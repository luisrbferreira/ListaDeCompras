package br.com.luisferreira.listadecompras.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import br.com.luisferreira.listadecompras.Interface.ItemClickListener;
import br.com.luisferreira.listadecompras.R;
import br.com.luisferreira.listadecompras.activities.ProdutoActivity;
import br.com.luisferreira.listadecompras.model.Produto;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Produto> produtoList;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public RecyclerAdapter(List<Produto> produtoList, Context context, FirebaseFirestore firebaseFirestore) {
        this.produtoList = produtoList;
        this.context = context;
        this.firebaseFirestore = firebaseFirestore;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int itemPosition = position;
        final Produto produto = produtoList.get(itemPosition);

        holder.textViewNomeProduto.setText(produto.getNome());
        holder.textViewCodigoBarras.setText(produto.getCodigoDeBarras());
        holder.textViewQuantidade.setText(String.valueOf(produto.getQuantidade()));
        holder.textViewPrecoUnitario.setText(String.valueOf(produto.getPrecoUnitario()));
        holder.textViewPrecoTotal.setText(String.valueOf(produto.getPrecoTotalItem()));

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(view.getContext(), ProdutoActivity.class);
                intent.putExtra("isUpdating", true);
                intent.putExtra("idProduto", produto.getId());
                intent.putExtra("textViewNomeProduto", holder.textViewNomeProduto.getText());
                intent.putExtra("textViewCodigoBarras", holder.textViewCodigoBarras.getText());
                intent.putExtra("textViewQuantidade", holder.textViewQuantidade.getText());
                intent.putExtra("textViewPrecoUnitario", holder.textViewPrecoUnitario.getText());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return produtoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewNomeProduto, textViewCodigoBarras, textViewQuantidade, textViewPrecoUnitario, textViewPrecoTotal;
        private ItemClickListener clickListener;

        public ViewHolder(View view) {
            super(view);

            textViewNomeProduto = view.findViewById(R.id.textViewNomeProduto);
            textViewCodigoBarras = view.findViewById(R.id.textViewCodigoBarras);
            textViewQuantidade = view.findViewById(R.id.textViewQuantidade);
            textViewPrecoUnitario = view.findViewById(R.id.textViewPrecoUnitario);
            textViewPrecoTotal = view.findViewById(R.id.textViewPrecoTotal);

            view.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }

    public void deleteProduto(String id, final int position) {
        firebaseFirestore.collection("produtos")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        produtoList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, produtoList.size());
                        Toast.makeText(context, "O produto foi removido!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}