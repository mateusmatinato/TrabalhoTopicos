package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class FinalizarPedido extends AppCompatActivity {

    private TextView tvNome;
    private SQLiteDatabase bd;

    private DecimalFormat df2 = new DecimalFormat("#.##");

    private int idUsuario;
    private int idRestaurante;
    private HashMap<Integer, Integer> listaItens = new HashMap<>();

    private SQLiteDatabase db;

    private TextView nomeRestaurante, enderecoUsuario, tempoEntrega,
            subTotal, taxaEntrega, totalPedido, itensPedido;

    private Button btnFinalizar;

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

                    break;
                case R.id.navigation_perfil:

                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide(); // Oculta título do app
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.layout.slide_up, R.layout.slide_down);
        setContentView(R.layout.activity_finalizarpedido);

        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);
        Cursor cursor;

        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", 0);
        idRestaurante = intent.getIntExtra("idRestaurante", 0);
        listaItens = (HashMap<Integer, Integer>) intent.getSerializableExtra("listaPedidos");

        nomeRestaurante = findViewById(R.id.tvNomeRestaurante);
        enderecoUsuario = findViewById(R.id.tvEndereco);
        tempoEntrega = findViewById(R.id.tvEntrega);
        subTotal = findViewById(R.id.tvSubTotal);
        taxaEntrega = findViewById(R.id.tvSubTotal2);
        totalPedido = findViewById(R.id.tvPrecoTotalFinalizar);
        itensPedido = findViewById(R.id.tvListaItens);
        btnFinalizar = findViewById(R.id.btnFinalizar);

        String pedido = "";
        Double valorItens = 0.00;
        Double valorTotal = 0.00;
        Double taxaEntregaDouble = 0.00;
        int cont = 0;
        for (Map.Entry item : listaItens.entrySet()) {
            try {
                cursor = bd.rawQuery("SELECT * FROM produtos WHERE idProduto = " + item.getKey(), null);
                cursor.moveToFirst();
                pedido += item.getValue() + " - " + cursor.getString(cursor.getColumnIndex("nome"));
                String qtd = "" + item.getValue();
                valorItens += (cursor.getDouble(cursor.getColumnIndex("preco")) * Integer.parseInt(qtd));
                if (cont != listaItens.size() - 1)
                    pedido += "\n";
            } catch (SQLiteException e) {
                Toast.makeText(this, "Não foi possível buscar o item: " + item.getKey(), Toast.LENGTH_SHORT).show();
            }
            cont++;
        }

        try {
            cursor = bd.rawQuery("SELECT * FROM restaurantes WHERE idRestaurante = " + idRestaurante, null);
            cursor.moveToFirst();
            taxaEntregaDouble = cursor.getDouble(cursor.getColumnIndex("taxaEntrega"));
            nomeRestaurante.setText(cursor.getString(cursor.getColumnIndex("nome")));
            tempoEntrega.setText("Previsão de entrega: " + cursor.getString(cursor.getColumnIndex("tempoEntrega")));
        } catch (SQLiteException e) {
            Toast.makeText(this, "Falha ao buscar o restaurante", Toast.LENGTH_SHORT).show();
        }

        subTotal.setText("R$ " + df2.format(valorItens));
        taxaEntrega.setText("R$ " + df2.format(taxaEntregaDouble));
        valorTotal = valorItens + taxaEntregaDouble;
        totalPedido.setText("R$ " + df2.format(valorTotal));

        itensPedido.setText(pedido);

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica se selecionou pelo menos um dos radio box e ai insere na tabela pedidos
            }
        });


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_home).setChecked(true);


    }
}
