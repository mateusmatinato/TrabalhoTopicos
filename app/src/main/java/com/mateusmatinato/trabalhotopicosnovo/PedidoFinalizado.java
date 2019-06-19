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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PedidoFinalizado extends AppCompatActivity {

    private TextView tvNome;
    private SQLiteDatabase bd;

    private DecimalFormat df2 = new DecimalFormat("#.##");

    private int idUsuario;
    private int idPedido;
    private HashMap<Integer, Integer> listaItens = new HashMap<>();

    private SQLiteDatabase db;

    private TextView tvPrecoTotal, tvTroco, tvTitulo, tvNomeRestaurante, tvEndereco,
            tvHoraPedido, tvItens, tvEntrega, tvValorItens;
    private RadioGroup rgPagamento;

    private EditText etObservacao;

    private Button btnVoltar;



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
                    Intent pedidos = new Intent(getApplicationContext(), Pedidos.class);
                    pedidos.putExtra("idUsuario", idUsuario);
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
        //overridePendingTransition(R.layout.slide_up, R.layout.slide_down);
        setContentView(R.layout.activity_pedidofinalizado);

        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);
        Cursor cursor;

        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", 0);
        idPedido = intent.getIntExtra("idPedido", 0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        tvPrecoTotal = findViewById(R.id.tvPrecoTotalFinalizar);
        rgPagamento = findViewById(R.id.rgPagamento);
        tvTroco = findViewById(R.id.tvTroco);
        tvTitulo = findViewById(R.id.tvTitulo);
        tvNomeRestaurante = findViewById(R.id.tvNomeRestaurante);
        tvEndereco = findViewById(R.id.tvEndereco);
        tvHoraPedido = findViewById(R.id.tvHoraPedido);
        etObservacao = findViewById(R.id.etObs);
        tvItens = findViewById(R.id.tvItens);
        btnVoltar = findViewById(R.id.btnVoltar);
        tvEntrega = findViewById(R.id.tvSubTotal2);
        tvValorItens = findViewById(R.id.tvSubTotal);

        double valorEntrega = 0;
        double valorItens = 0;

        try {
            cursor = bd.rawQuery("SELECT u.endereco, r.nome, r.taxaEntrega, p.data, " +
                            "p.status, p.observacao, p.metodoPagamento, " +
                            "p.precoTotal, p.Troco FROM pedidos p JOIN restaurantes r " +
                            "ON r.idRestaurante = p.idRestaurante JOIN usuarios u " +
                            "ON u.idUsuario = p.idUsuario WHERE p.idPedido = "+idPedido
                    ,null);
            cursor.moveToFirst();

            tvTitulo.setText("Pedido "+cursor.getString(cursor.getColumnIndex("status")));
            tvEndereco.setText("Entregar em: "+cursor.getString(cursor.getColumnIndex("endereco")));

            Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(cursor.getColumnIndex("data")));
            DateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
            tvHoraPedido.setText("Realizado em: "+dateFormat.format(date1));

            tvNomeRestaurante.setText(cursor.getString(cursor.getColumnIndex("nome")));
            tvEntrega.setText("R$: "+df2.format(cursor.getDouble(cursor.getColumnIndex("taxaEntrega"))));
            valorEntrega = cursor.getDouble(cursor.getColumnIndex("taxaEntrega"));

            tvPrecoTotal.setText("R$: "+df2.format(cursor.getDouble(cursor.getColumnIndex("precoTotal"))));
            rgPagamento.check(cursor.getInt(cursor.getColumnIndex("metodoPagamento")));

            if(!cursor.getString(cursor.getColumnIndex("observacao")).equals("")) {
                etObservacao.setText(cursor.getString(cursor.getColumnIndex("observacao")));
                etObservacao.setFocusable(false);
            }
            else{
                etObservacao.setVisibility(View.GONE);
            }
            if(rgPagamento.getCheckedRadioButtonId() == R.id.radioDinheiro){
                tvTroco.setText("Troco: R$ "+ df2.format(cursor.getDouble(cursor.getColumnIndex("troco"))));
                tvTroco.setVisibility(View.VISIBLE);
            }
            for(int i = 0 ; i < rgPagamento.getChildCount(); i++){
                if(rgPagamento.getChildAt(i).getId() != cursor.getInt(cursor.getColumnIndex("metodoPagamento"))){
                    rgPagamento.getChildAt(i).setVisibility(View.GONE);
                }
            }

            btnVoltar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        catch (SQLiteException|ParseException e){
            Toast.makeText(this, "Erro ao buscar pedido, tente novamente.", Toast.LENGTH_SHORT).show();
        }

        try{
            cursor = bd.rawQuery("SELECT p.nome, p.preco, ip.quantidade FROM itensPedido ip JOIN produtos p " +
                    "ON p.idProduto = ip.idProduto WHERE ip.idPedido = "+idPedido,null);
            cursor.moveToFirst();
            String listaItens = "";
            for(int i = 0 ; i < cursor.getCount() ; i++){
                listaItens += cursor.getInt(cursor.getColumnIndex("quantidade")) +"x ";
                listaItens += cursor.getString(cursor.getColumnIndex("nome"));
                if(i != cursor.getCount()-1) listaItens += "\n";
                valorItens += cursor.getDouble(cursor.getColumnIndex("preco"))
                        * cursor.getInt(cursor.getColumnIndex("quantidade"));
            }
            tvItens.setText(listaItens);
            tvValorItens.setText("R$: "+df2.format(valorItens));

        }
        catch(SQLiteException e){
            Toast.makeText(this, "Erro ao buscar itens do pedido, tente novamente.", Toast.LENGTH_SHORT).show();
        }


    }
}
