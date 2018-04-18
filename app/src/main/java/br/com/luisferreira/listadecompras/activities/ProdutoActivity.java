package br.com.luisferreira.listadecompras.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import br.com.luisferreira.listadecompras.R;
import br.com.luisferreira.listadecompras.model.Produto;

public class ProdutoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText textNomeProduto;
    private EditText textCodigoDeBarras;
    private EditText textQuantidade;
    private EditText textPrecoUnitario;

    private Button btnEditarCadastrar;

    protected ProgressBar progressBar;

    private Produto produto;

    private FirebaseFirestore firebaseFirestore;

    boolean isUpdating;
    String id;
    String nomeProduto;
    String codidoDeBarras;
    String quantidadeProduto;
    String precoUnitario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isUpdating = getIntent().getBooleanExtra("isUpdating", false);
        btnEditarCadastrar = findViewById(R.id.btnEditarCadastrar);

        if (isUpdating) {
            getSupportActionBar().setTitle("Editar Produto");
            btnEditarCadastrar.setText("Salvar");

            id = getIntent().getStringExtra("idProduto");
        } else {
            getSupportActionBar().setTitle("Inserir Produto");
            btnEditarCadastrar.setText("Cadastrar");
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        btnEditarCadastrar.setOnClickListener(this);

        initViews();
    }

    private void initViews() {
        textNomeProduto = findViewById(R.id.textNomeProduto);
        textCodigoDeBarras = findViewById(R.id.textCodigoDeBarras);
        textQuantidade = findViewById(R.id.textQuantidade);
        textPrecoUnitario = findViewById(R.id.textPrecoUnitario);

        progressBar = findViewById(R.id.cadastro_progress);

        if (isUpdating) {
            nomeProduto = getIntent().getStringExtra("textViewNomeProduto");
            codidoDeBarras = getIntent().getStringExtra("textViewCodigoBarras");
            quantidadeProduto = getIntent().getStringExtra("textViewQuantidade");
            precoUnitario = getIntent().getStringExtra("textViewPrecoUnitario");

            textNomeProduto.setText(nomeProduto);
            textNomeProduto.setSelection(textNomeProduto.getText().length());
            textCodigoDeBarras.setText(codidoDeBarras);
            textQuantidade.setText(quantidadeProduto);
            textPrecoUnitario.setText(precoUnitario);
        }
    }

    protected void initProduto() {
        produto = new Produto();
        produto.setNome(textNomeProduto.getText().toString());
        produto.setCodigoDeBarras(textCodigoDeBarras.getText().toString());
    }

    @Override
    public void onClick(View v) {

        initProduto();

        String nome = textNomeProduto.getText().toString();
        String codigoDeBarras = textCodigoDeBarras.getText().toString();
        String quantidade = textQuantidade.getText().toString();
        String precoUnitario = textPrecoUnitario.getText().toString();

        boolean ok = true;

        if (nome.isEmpty()) {
            textNomeProduto.setError(getString(R.string.msg_erro_nome_empty));
            ok = false;
        }

        if (codigoDeBarras.isEmpty()) {
            textCodigoDeBarras.setError(getString(R.string.msg_erro_codigoDeBarras_empty));
            ok = false;
        }

        if (quantidade.isEmpty()) {
            textQuantidade.setError(getString(R.string.msg_erro_quantidade_empty));
            ok = false;
        } else {
            produto.setQuantidade(Integer.parseInt(textQuantidade.getText().toString()));
        }

        if (precoUnitario.isEmpty()) {
            textPrecoUnitario.setError(getString(R.string.msg_erro_precoUnitario_empty));
            ok = false;
        } else {
            produto.setPrecoUnitario(Double.parseDouble(textPrecoUnitario.getText().toString()));
        }

        produto.setPrecoTotalItem(produto.getPrecoUnitario() * produto.getQuantidade());

        if (ok) {
            btnEditarCadastrar.setEnabled(false);

            openProgressBar();

            if (isUpdating) {
                updateProduto(id, produto.getNome(), produto.getCodigoDeBarras(), produto.getQuantidade(), produto.getPrecoUnitario(), produto.getPrecoTotalItem());
            } else {
                addProduto(produto.getNome(), produto.getCodigoDeBarras(), produto.getQuantidade(), produto.getPrecoUnitario(), produto.getPrecoTotalItem());
            }

            closeProgressBar();
        } else {
            closeProgressBar();
            btnEditarCadastrar.setEnabled(true);
        }
    }

    private void updateProduto(String id, String nome, String codigoDeBarras, int quantidade, double precoUnitario, double precoTotalItem) {
        Map<String, Object> produtoMap = (new Produto(id, nome, codigoDeBarras, quantidade, precoUnitario, precoTotalItem)).toMap();

        firebaseFirestore.collection("produtos")
                .document(id)
                .set(produtoMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Produto atualizado com sucesso!");

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        showToast("Não foi possível atualizar o produto!");
                    }
                });
    }

    private void addProduto(String nome, String codigoDeBarras, int quantidade, double precoUnitario, double precoTotalItem) {
        Map<String, Object> produtoMap = new Produto(nome, codigoDeBarras, quantidade, precoUnitario, precoTotalItem).toMap();

        firebaseFirestore.collection("produtos")
                .add(produtoMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        showToast("Produto cadastrado com sucesso!");

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        showToast("O produto não foi cadastrado!");
                    }
                });
    }

    protected void openProgressBar() {
        progressBar.setFocusable(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void closeProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    protected void showToast(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
