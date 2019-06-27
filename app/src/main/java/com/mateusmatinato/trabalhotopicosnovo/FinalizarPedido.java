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

    private DecimalFormat df2 = new DecimalFormat("#,##0.00");

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
                    Intent perfil = new Intent(getApplicationContext(), Perfil.class);
                    perfil.putExtra("idUsuario", idUsuario);
                    finish();
                    startActivity(perfil);
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizarpedido);

        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);
        Cursor cursor;

        /* Pega informações da intent: id do usuário, do restaurante e a lista de itens da sacola */
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", 0);
        idRestaurante = intent.getIntExtra("idRestaurante", 0);
        listaItens = (HashMap<Integer, Integer>) intent.getSerializableExtra("listaPedidos");

        nomeRestaurante = findViewById(R.id.tvTitulo);
        enderecoUsuario = findViewById(R.id.tvEndereco);
        tempoEntrega = findViewById(R.id.tvHoraPedido);
        subTotal = findViewById(R.id.tvSubTotal);
        taxaEntrega = findViewById(R.id.tvSubTotal2);
        totalPedido = findViewById(R.id.tvPrecoTotalFinalizar);
        itensPedido = findViewById(R.id.tvItens);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        rgPagamento = findViewById(R.id.rgPagamento);
        troco = findViewById(R.id.textLayoutTroco);
        observacao = findViewById(R.id.textLayoutObs);

        String pedido = "";
        Double valorItens = 0.00;
        Double valorTotal = 0.00;
        Double taxaEntregaDouble = 0.00;
        int cont = 0;
        /* Para cada um dos itens na sacola, busca o nome no banco e calcula o valor (preço * qtd) */
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

        /* Pega informações sobre o restaurante para mostrar na tela */
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

        /* Mostra informações buscadas no banco e valor do pedido */
        subTotal.setText("R$ " + df2.format(valorItens));
        taxaEntrega.setText("R$ " + df2.format(taxaEntregaDouble));
        valorTotal = valorItens + taxaEntregaDouble;
        totalPedido.setText("R$ " + df2.format(valorTotal));

        itensPedido.setText(pedido);

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Verifica se selecionou pelo menos um dos radio box e ai insere na tabela pedidos */
                if (rgPagamento.getCheckedRadioButtonId() != -1) {

                    /* Pega a data atual e formata */
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    String dataAtual = dateFormat.format(date);

                    /* Converte o campo troco para double */
                    Double trocoDouble;
                    if (troco.getEditText().getText().length() > 0) {
                        trocoDouble = Double.parseDouble(troco.getEditText().getText().toString());
                    } else {
                        trocoDouble = 0.00;
                    }
                    String obs = observacao.getEditText().getText().toString();

                    /* Converte o valor total para double */
                    Double valorTotal = Double.parseDouble(totalPedido.getText().toString().substring(3).replace(",", "."));

                    /* Pega qual o método de pagamento */
                    int metodoPagamento = rgPagamento.getCheckedRadioButtonId();

                    boolean erro = false;
                    try {
                        /* Deve inserir na tabela de pedidos */
                        String sql = "INSERT INTO pedidos (idRestaurante, idUsuario, status, observacao," +
                                " data, troco, precoTotal, metodoPagamento) VALUES (" + idRestaurante + "," + idUsuario + ",'Em andamento','" + obs + "','" +
                                "" + dataAtual + "'," + trocoDouble + "," + valorTotal + "," + metodoPagamento +")";

                        bd.execSQL(sql);

                        /* Já que o pedido tem id autoincrement, deve buscar na tabela do SQLite o último id
                        * que foi autoincrementado para inserir na relação de pedido com produtos */
                        Cursor cursor = bd.rawQuery("SELECT seq FROM sqlite_sequence WHERE name = ?", new String[]{"pedidos"});
                        cursor.moveToFirst();
                        int idPedido = cursor.getInt(cursor.getColumnIndex("seq"));

                        /* Insere na tabela itensPedido todos os itens que foram comprados */
                        for (Map.Entry item : listaItens.entrySet()) {
                            /* Percorre os itens do pedido salvando na tabela */

                            sql = "INSERT INTO itensPedido (idPedido, idProduto,quantidade) " +
                                    "VALUES (" + idPedido + "," + item.getKey().toString() + "," + item.getValue().toString()+")";
                            bd.execSQL(sql);
                        }

                    } catch (SQLiteException e) {
                        Toast.makeText(FinalizarPedido.this, "Erro ao efetuar o pedido, tente novamente", Toast.LENGTH_SHORT).show();
                        erro = true;
                    }

                    if(!erro){
                        /* Se não houve erro, deve ir pra activity de pedidos */
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
