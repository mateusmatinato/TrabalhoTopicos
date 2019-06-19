package com.mateusmatinato.trabalhotopicosnovo;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mateusmatinato.trabalhotopicosnovo.adapter.AdapterPedidos;
import com.mateusmatinato.trabalhotopicosnovo.adapter.AdapterProdutos;
import com.mateusmatinato.trabalhotopicosnovo.model.Pedido;
import com.mateusmatinato.trabalhotopicosnovo.model.Produto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Pedidos extends AppCompatActivity {

    private TextView tvNome;
    private SQLiteDatabase bd;

    private int idUsuario;

    private RecyclerView rvPedidos;

    private ArrayList<Pedido> pedidos = new ArrayList<>();


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent home = new Intent(getApplicationContext(), Home.class);
                    home.putExtra("idUsuario",idUsuario);
                    finish();
                    //startActivity(home);
                    break;
                case R.id.navigation_pedidos:

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
        setContentView(R.layout.activity_pedidos);
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario",0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_pedidos).setChecked(true);

        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);
        Cursor cursor = null;

        rvPedidos = findViewById(R.id.rvPedidos);
        try{
            String sql = "SELECT * FROM pedidos p JOIN restaurantes r ON r.idRestaurante = p.idRestaurante " +
                    "WHERE p.idUsuario = "+idUsuario+" ORDER BY datetime(p.data) DESC";
            cursor = bd.rawQuery(sql,null);

        }
        catch(SQLiteException e){
            Toast.makeText(this, "Problema ao buscar pedidos, tente novamente", Toast.LENGTH_SHORT).show();

        }
        int count = 0;

        cursor.moveToFirst();
        while (count < cursor.getCount()) {
            Pedido p = new Pedido();
            p.setNomeRestaurante(cursor.getString(cursor.getColumnIndex("nome")));
            p.setPrecoTotal(cursor.getDouble(cursor.getColumnIndex("precoTotal")));
            p.setIdRestaurante(cursor.getInt(cursor.getColumnIndex("idRestaurante")));
            p.setIdPedido(cursor.getInt(cursor.getColumnIndex("idPedido")));
            String data = (cursor.getString(cursor.getColumnIndex("data")));
            String status = cursor.getString(cursor.getColumnIndex("status"));
            try{
                Date dataPedido=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data);
                p.setData(dataPedido);
            }
            catch(Exception e){
                p.setData(null);
            }

            if(status.equalsIgnoreCase("Em andamento")){
                status = atualizaStatus(cursor, bd, p);
            }
            p.setStatus(status);

            pedidos.add(p);

            cursor.moveToNext();
            count++;
        }
        AdapterPedidos adapter = new AdapterPedidos(pedidos);

        if(pedidos.size() == 0){
            //não possui pedidos, mostra o textView
            findViewById(R.id.tvZeroPedidos).setVisibility(View.VISIBLE);
        }

        // Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvPedidos.setLayoutManager(layoutManager);
        // fixa o tamanho para otimizar
        rvPedidos.setHasFixedSize(true);
        // adiciona linha separadora dos elementos
        rvPedidos.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        rvPedidos.setAdapter(adapter);

        rvPedidos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(), rvPedidos, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent pedidofinalizado = new Intent(getApplicationContext(), PedidoFinalizado.class);
                        pedidofinalizado.putExtra("idPedido",pedidos.get(position).getIdPedido());
                        pedidofinalizado.putExtra("idUsuario",idUsuario);
                        startActivity(pedidofinalizado);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        /* Aqui talvez aparecer um menu para adicionar/remover dos favoritos */
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                )
        );
    }

    public String atualizaStatus(Cursor cursor, SQLiteDatabase bd, Pedido p){
        /* Aqui vai a lógica para mudar o status do pedido */
        /* Deve pegar o tempo de entrega do restaurante e gerar um número aleatório
         * que esteja no intervalo do tempo de entrega. Deve verificar se:
         *   horaAtual >= horaPedido + tempoEntregaAleatorio -> Em andamento
         *   horaAtual <= horaPedido + tempoEntregaAleatorio -> Finalizado
         * */

        String tempoEntregaString[] = cursor.getString(cursor.getColumnIndex("tempoEntrega")).split(" ");
        int tempoInicial = Integer.parseInt(tempoEntregaString[0]);
        int tempoFinal = Integer.parseInt(tempoEntregaString[2]);
        Random random = new Random();
        int minutosEntrega = random.nextInt((tempoFinal - tempoInicial) + 1) + tempoInicial;
        Calendar cal = Calendar.getInstance();
        cal.setTime(p.getData());
        cal.add(Calendar.MINUTE, minutosEntrega);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(cal.getTime());

        Date dataAtual = new Date();
        Log.d("HORA ATUAL",""+dataAtual.toString());
        Log.d("HORA DO PEDIDO ENTREGUE",""+strDate);
        if(dataAtual.compareTo(cal.getTime()) > 0){
            //Já entregou
            try{
                //Atualiza o status do pedido para Finalizado
                bd.execSQL("UPDATE pedidos SET status = 'Finalizado' WHERE idPedido = "+p.getIdPedido());
            }
            catch(SQLiteException e){
                Toast.makeText(this, "Erro!", Toast.LENGTH_SHORT).show();
            }
            return "Finalizado";
        }
        else{
            return "Em andamento";
        }

    }
}
