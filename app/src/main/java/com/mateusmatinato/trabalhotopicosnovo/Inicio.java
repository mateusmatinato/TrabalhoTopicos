package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mateusmatinato.trabalhotopicosnovo.adapter.AdapterRestaurantes;
import com.mateusmatinato.trabalhotopicosnovo.model.Restaurante;

import java.util.ArrayList;
import java.util.List;

public class Inicio extends AppCompatActivity {

    private RecyclerView recyclerRestaurantes;
    private List<Restaurante> restaurantes = new ArrayList<>();
    private SQLiteDatabase bd;
    private TextView tvNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Oculta título do app
        setContentView(R.layout.activity_inicio);
        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);
        recyclerRestaurantes = findViewById(R.id.rvRestaurantes);
        Intent intent = getIntent();
        //Busca no banco e insere na lista restaurantes
        Cursor cursor = bd.rawQuery("SELECT * FROM restaurantes",null);
        cursor.moveToFirst();
        int count = 0;
        while(count < cursor.getCount()){
            Restaurante r = new Restaurante();
            r.setNome(cursor.getString(cursor.getColumnIndex("nome")));
            r.setEndereco(cursor.getString(cursor.getColumnIndex("endereco")));
            r.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
            r.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("idRestaurante"))));
            restaurantes.add(r);

            cursor.moveToNext();
            count++;
        }


        String emailUsuario = intent.getStringExtra("emailLogado");
        cursor = bd.rawQuery("SELECT * FROM usuarios where email = ?",new String[]{emailUsuario});
        cursor.moveToFirst();
        String nomeUsuario = cursor.getString(cursor.getColumnIndex("nome"));
        tvNome = findViewById(R.id.tvNome);
        tvNome.setText("Bem vindo, "+nomeUsuario);


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
                        Toast.makeText(getApplicationContext(), "Clique em " + restaurantes.get(position).getNome(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(), "Clique longo em " + restaurantes.get(position).getNome(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                )
        );
    }
}
