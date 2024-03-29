package com.mateusmatinato.trabalhotopicosnovo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.mateusmatinato.trabalhotopicosnovo.adapter.AdapterProdutos;
import com.mateusmatinato.trabalhotopicosnovo.model.Produto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestauranteActivity extends AppCompatActivity {
    private TextView tvNome, tvTempoEntrega;
    private SQLiteDatabase bd;
    private ImageView imgLike;

    private int idUsuario, idRestaurante;

    private RecyclerView rvProdutos;
    private List<Produto> produtos = new ArrayList<>();

    private boolean isFavorite;

    private TextView precoTotal;
    private Toolbar tbFinalizarPedido;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent home = new Intent(getApplicationContext(), Home.class);
                    home.putExtra("idUsuario", idUsuario);
                    finish();
                    //startActivity(home);
                    break;
                case R.id.navigation_pedidos:
                    Intent pedidos = new Intent(getApplicationContext(), Pedidos.class);
                    pedidos.putExtra("idUsuario", idUsuario);
                    startActivity(pedidos);
                    break;
                case R.id.navigation_perfil:

                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante);

        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", 0);
        idRestaurante = intent.getIntExtra("idRestaurante", 0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        precoTotal = findViewById(R.id.tvPrecoTotal);
        tbFinalizarPedido = findViewById(R.id.tbFinalizarPedido);

        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);
        Cursor cursor = bd.rawQuery("SELECT * FROM restaurantes where idRestaurante = " + idRestaurante, null);
        cursor.moveToFirst();
        String nomeRestaurante = cursor.getString(cursor.getColumnIndex("nome"));
        String tempoEntrega = cursor.getString(cursor.getColumnIndex("tempoEntrega"));
        tvNome = findViewById(R.id.tvTitulo);
        tvTempoEntrega = findViewById(R.id.tvTempoEntrega);
        tvNome.setText("" + nomeRestaurante);
        tvTempoEntrega.setText("Entrega em: " + tempoEntrega);

        /* Deve buscar se já é um restaurante favorito */
        cursor = bd.rawQuery("SELECT * FROM restaurantesFavoritos WHERE idRestaurante = " + idRestaurante + " AND idUsuario = " + idUsuario, null);
        cursor.moveToFirst();


        imgLike = findViewById(R.id.imgStar);
        if (cursor.getCount() != 0) {
            //Caso for favorito seta a variável de controle e muda a imagem
            isFavorite = true;
            imgLike.setImageResource(R.drawable.btn_star_big_on);
        } else {
            //Caso contrário seta a variável como falsa e muda a imagem
            isFavorite = false;
            imgLike.setImageResource(R.drawable.btn_star_big_off);
        }

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    // Se já for favorito e estiver clicando, muda a imagem, deleta da tabela e muda a variável
                    try {
                        bd.execSQL("DELETE FROM restaurantesFavoritos WHERE idRestaurante = " + idRestaurante + " AND idUsuario = " + idUsuario);
                        isFavorite = false;
                        imgLike.setImageResource(R.drawable.btn_star_big_off);
                    } catch (SQLiteException e) {
                        Toast.makeText(RestauranteActivity.this, "Erro ao efetuar a operação, tente novamente.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Não é um restaurante favorito, muda a imagem para on, insere na tabela e muda a variável
                    try {
                        bd.execSQL("INSERT INTO restaurantesFavoritos (idRestaurante, idUsuario) VALUES (" + idRestaurante + "," + idUsuario + ")");
                        isFavorite = true;
                        imgLike.setImageResource(R.drawable.btn_star_big_on);
                    } catch (SQLiteException e) {
                        Toast.makeText(RestauranteActivity.this, "Erro ao efetuar a operação, tente novamente.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        rvProdutos = findViewById(R.id.rvProdutos);
        //Percorre todos os produtos e adiciona no array
        cursor = bd.rawQuery("SELECT p.idProduto, p.nome, p.descricao, p.preco, cr.imagem FROM produtos p JOIN restaurantes r ON p.idRestaurante = r.idRestaurante " +
                "JOIN categoriaRestaurantes cr ON r.idCategoria = cr.idCategoria WHERE p.idRestaurante = " + idRestaurante + "", null);
        cursor.moveToFirst();
        int count = 0;
        while (count < cursor.getCount()) {
            Produto p = new Produto();
            p.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            p.setDescricao(cursor.getString(cursor.getColumnIndex("descricao")));
            p.setPreco(cursor.getDouble(cursor.getColumnIndex("preco")));
            p.setImagem(cursor.getInt(cursor.getColumnIndex("imagem")));
            p.setIdProduto(cursor.getInt(cursor.getColumnIndex("idProduto")));
            p.setQuantidade(0);
            produtos.add(p);

            cursor.moveToNext();
            count++;
        }
        AdapterProdutos adapter = new AdapterProdutos(produtos, this);

        // Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvProdutos.setLayoutManager(layoutManager);
        // fixa o tamanho para otimizar
        rvProdutos.setHasFixedSize(true);
        // adiciona linha separadora dos elementos
        rvProdutos.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        rvProdutos.setAdapter(adapter);

        tbFinalizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent finalizarPedido = new Intent(getApplicationContext(), FinalizarPedido.class);
                finalizarPedido.putExtra("idUsuario", idUsuario);
                finalizarPedido.putExtra("idRestaurante", idRestaurante);

                HashMap<Integer, Integer> listaPedido = new HashMap<Integer, Integer>();
                TextView itemAtual, qtdAtual, idItemAtual;
                for (int i = 0; i < produtos.size(); i++) {
                    int qtd = produtos.get(i).getQuantidade();
                    if (qtd != 0) {
                        //Esse produto foi selecionado, adiciona na lista do pedido
                        listaPedido.put(produtos.get(i).getIdProduto(), qtd);
                    }
                }
                finalizarPedido.putExtra("listaPedidos", listaPedido);
                if (listaPedido.isEmpty()) {
                    Toast.makeText(RestauranteActivity.this, "Você precisa selecionar pelo menos um produto!", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(finalizarPedido, ActivityOptions.makeSceneTransitionAnimation(RestauranteActivity.this).toBundle());
                }
            }
        });


    }

    public void alteraPrecoTotal(Double precoItem, int flag) {
        DecimalFormat df2 = new DecimalFormat("#,##0.00");
        String precoAtualString = precoTotal.getText().toString().substring(3).replace(",", ".");
        double precoAtual = Double.parseDouble(precoAtualString);
        if (flag == 1) {
            //soma
            precoAtual += precoItem;
        } else {
            precoAtual -= precoItem;
        }
        precoTotal.setText("R$ " + df2.format(precoAtual));
    }
}
