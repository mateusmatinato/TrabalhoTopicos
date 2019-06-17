package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private RadioGroup rgPagamento;

    private TextInputLayout troco, observacao;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent home = new Intent(getApplicationContext(), Home.class);
                    home.putExtra("idUsuario", idUsuario);
                    finish();
                    startActivity(home);
                    break;
                case R.id.navigation_pedidos:
                    Intent pedidos = new Intent(getApplicationContext(),Pedidos.class);
                    pedidos.putExtra("idUsuario",idUsuario);
                    startActivity(pedidos);
                    finish();
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
        //overridePendingTransition(R.layout.slide_up, R.layout.slide_down);
        setContentView(R.layout.activity_finalizarpedido);

        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);
        Cursor cursor;

        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", 0);
        idRestaurante = intent.getIntExtra("idRestaurante", 0);
        listaItens = (HashMap<Integer, Integer>) intent.getSerializableExtra("listaPedidos");

        nomeRestaurante = findViewById(R.id.tvTitulo);
        enderecoUsuario = findViewById(R.id.tvEndereco);
        tempoEntrega = findViewById(R.id.tvEntrega);
        subTotal = findViewById(R.id.tvSubTotal);
        taxaEntrega = findViewById(R.id.tvSubTotal2);
        totalPedido = findViewById(R.id.tvPrecoTotalFinalizar);
        itensPedido = findViewById(R.id.tvStatus);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        rgPagamento = findViewById(R.id.rgPagamento);
        troco = findViewById(R.id.textLayoutTroco);
        observacao = findViewById(R.id.textLayoutObs);

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

            cursor = bd.rawQuery("SELECT endereco FROM usuarios WHERE idUsuario = "+idUsuario,null);
            cursor.moveToFirst();
            enderecoUsuario.setText("Entregar em: "+cursor.getString(cursor.getColumnIndex("endereco")));
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
                if (rgPagamento.getCheckedRadioButtonId() != -1) {
                    //Selecionou um método de pagamento

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    String dataAtual = dateFormat.format(date);

                    Double trocoDouble;
                    if (troco.getEditText().getText().length() > 0) {
                        trocoDouble = Double.parseDouble(troco.getEditText().getText().toString());
                    } else {
                        trocoDouble = 0.00;
                    }
                    String obs = observacao.getEditText().getText().toString();

                    Double valorTotal = Double.parseDouble(totalPedido.getText().toString().substring(3).replace(",", "."));

                    boolean erro = false;
                    try {
                        //Salva o pedido
                        String sql = "INSERT INTO pedidos (idRestaurante, idUsuario, status, observacao," +
                                " data, troco, precoTotal) VALUES (" + idRestaurante + "," + idUsuario + ",'Em andamento','" + obs + "','" +
                                "" + dataAtual + "'," + trocoDouble + "," + valorTotal + ")";
                        //Log.d("SQL", sql);
                        bd.execSQL(sql);
                        Cursor cursor = bd.rawQuery("SELECT seq FROM sqlite_sequence WHERE name = ?", new String[]{"pedidos"});
                        cursor.moveToFirst();
                        int idPedido = cursor.getInt(cursor.getColumnIndex("seq"));

                        //Salva os itens do pedido
                        for (Map.Entry item : listaItens.entrySet()) {
                            //Percorre todos os itens inserindo na itensPedido

                            sql = "INSERT INTO itensPedido (idPedido, idProduto,quantidade) " +
                                    "VALUES (" + idPedido + "," + item.getKey().toString() + "," + item.getValue().toString()+")";
                            //Log.d("SQL", sql);

                        }

                    } catch (SQLiteException e) {
                        Toast.makeText(FinalizarPedido.this, "Erro ao efetuar o pedido, tente novamente", Toast.LENGTH_SHORT).show();
                        erro = true;
                    }

                    if(!erro){
                        //Salvou o pedido e os itens
                        Intent pedidos = new Intent(getApplicationContext(),Pedidos.class);
                        pedidos.putExtra("idUsuario",idUsuario);
                        startActivity(pedidos);
                        finish();
                    }
                    else{
                        Intent restaurante = new Intent(getApplicationContext(),RestauranteActivity.class);
                        startActivity(restaurante);

                        finish();
                    }

                } else {
                    Toast.makeText(FinalizarPedido.this, "Selecione um método de pagamento", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* Deve mostrar o Troco somente se selecionou o radiobutton de dinheiro */
        rgPagamento.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioDinheiro) {
                    //Selecionou dinheiro, então mostra o troco
                    troco.setVisibility(View.VISIBLE);
                } else {
                    troco.setVisibility(View.GONE);
                }
            }
        });


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }
}
