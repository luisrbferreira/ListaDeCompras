package br.com.luisferreira.listadecompras.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.luisferreira.listadecompras.R;
import br.com.luisferreira.listadecompras.adapter.RecyclerAdapter;
import br.com.luisferreira.listadecompras.model.Produto;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;

    private FloatingActionButton fabNovoProduto;
    protected ProgressBar progressBar;

    private List<Produto> produtoList = new ArrayList<>();

    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;

    private Paint paint = new Paint();

    private double valorCompras;
    private TextView totalCompras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_produtos);

        firebaseFirestore = FirebaseFirestore.getInstance();

        initViews();
        fetchData();

        listenerRegistration = firebaseFirestore.collection("produtos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            Produto produto = documentSnapshot.toObject(Produto.class);
                            produto.setId(documentSnapshot.getId());
                            produtoList.add(produto);
                        }

                        recyclerView.setAdapter(recyclerAdapter);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void initViews() {
        recyclerAdapter = new RecyclerAdapter(produtoList, getApplicationContext(), firebaseFirestore);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        fabNovoProduto = findViewById(R.id.fabNovoProduto);

        progressBar = findViewById(R.id.main_progress);

        totalCompras = (TextView) findViewById(R.id.totalCompras);

        initSwipe();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        fetchData();
    }

    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        listenerRegistration.remove();
    }

    private void fetchData() {
        openProgressBar();

        valorCompras = 0.00;

        firebaseFirestore.collection("produtos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            produtoList.removeAll(produtoList);

                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Produto produto = documentSnapshot.toObject(Produto.class);
                                produto.setId(documentSnapshot.getId());
                                produtoList.add(produto);

                                valorCompras += produto.getPrecoTotalItem();
                            }

                            String valorFormatado = NumberFormat.getCurrencyInstance().format(valorCompras);
                            totalCompras.setText("Total: R" + valorFormatado);

                            recyclerView.setAdapter(recyclerAdapter);
                            recyclerAdapter.notifyDataSetChanged();
                        } else {
                            showSnackbar("Erro ao buscar os dados: " + task.getException());
                        }

                        closeProgressBar();
                    }
                });
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    recyclerAdapter.deleteProduto(String.valueOf(produtoList.get(position).getId()), position);
                    onRestart();
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    recyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        paint.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_sweep_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, paint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void callInsert(View view) {
        Intent intent = new Intent(this, ProdutoActivity.class);
        startActivity(intent);
    }

    protected void openProgressBar() {
        progressBar.setFocusable(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void closeProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    protected void showSnackbar(String message) {
        Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
