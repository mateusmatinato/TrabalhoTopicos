package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mateusmatinato.trabalhotopicosnovo.adapter.AdapterRestaurantes;
import com.mateusmatinato.trabalhotopicosnovo.model.Restaurante;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerRestaurantes;
    private List<Restaurante> restaurantes = new ArrayList<>();
    private SQLiteDatabase bd;
    private TextView tvNome;

    private int idUsuario;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
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
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);
        recyclerRestaurantes = findViewById(R.id.rvRestaurantes);
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", 0);

        //Busca no banco e insere na lista restaurantes
        Cursor cursor = bd.rawQuery("SELECT * FROM restaurantes r JOIN categoriaRestaurantes cr ON r.idCategoria = cr.idCategoria", null);
        cursor.moveToFirst();
        int count = 0;
        while (count < cursor.getCount()) {
            Restaurante r = new Restaurante();
            r.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            r.setEndereco(cursor.getString(cursor.getColumnIndex("endereco")));
            r.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
            r.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("idRestaurante"))));
            r.setImagem(cursor.getInt(cursor.getColumnIndex("imagem")));
            restaurantes.add(r);

            cursor.moveToNext();
            count++;
        }


        cursor = bd.rawQuery("SELECT * FROM usuarios where idUsuario = " + idUsuario, null);
        cursor.moveToFirst();
        String nomeUsuario = cursor.getString(cursor.getColumnIndex("nome"));
        tvNome = findViewById(R.id.tvNomeRestaurante);
        tvNome.setText("Bem vindo, " + nomeUsuario);


        AdapterRestaurantes adapter = new AdapterRestaurantes(restaurantes);

        // Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerRestaurantes.setLayoutManager(layoutManager);
        // fixa o tamanho para otimizar
        recyclerRestaurantes.setHasFixedSize(true);
        // adiciona linha separadora dos elementos
        recyclerRestaurantes.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        recyclerRestaurantes.setAdapter(adapter);

        // Adicionando eventos de clique a partir de classe já estabelecida
        recyclerRestaurantes.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(), recyclerRestaurantes, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent restaurante = new Intent(getApplicationContext(), RestauranteActivity.class);
                        restaurante.putExtra("idRestaurante",restaurantes.get(position).getId());
                        restaurante.putExtra("idUsuario",idUsuario);
                        startActivity(restaurante);
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

}
