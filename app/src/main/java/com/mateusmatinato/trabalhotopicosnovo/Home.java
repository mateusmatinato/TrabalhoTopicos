package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mateusmatinato.trabalhotopicosnovo.adapter.AdapterRestaurantes;
import com.mateusmatinato.trabalhotopicosnovo.model.Restaurante;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerRestaurantes;
    private List<Restaurante> restaurantes = new ArrayList<>();
    private List<Restaurante> restaurantesCopia = new ArrayList<>();
    private List<Restaurante> restaurantesFavoritos = new ArrayList<>();
    private SQLiteDatabase bd;
    private TextView tvNome;
    private EditText etBuscar;

    private AdapterRestaurantes adapter;
    private ImageView ivFavoritos;

    private int idUsuario;

    private Button btnBuscar;

    boolean searchFavorite = false;

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
                    Intent perfil = new Intent(getApplicationContext(), Perfil.class);
                    perfil.putExtra("idUsuario", idUsuario);
                    startActivity(perfil);
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /* Configurações do Bottom Navigation */
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /* Inicia a base de dados*/
        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);

        /* Pega informações da intent (id do usuário logado) */
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", 0);

        /* Deve buscar no banco todos os restaurantes cadastrados e insere nas duas listas */
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
            restaurantesCopia.add(r);

            cursor.moveToNext();
            count++;
        }

        /* Pega informações do usuário */
        cursor = bd.rawQuery("SELECT * FROM usuarios where idUsuario = " + idUsuario, null);
        cursor.moveToFirst();
        String nomeUsuario = cursor.getString(cursor.getColumnIndex("nome"));
        tvNome = findViewById(R.id.tvTitulo);
        tvNome.setText("Bem vindo, " + nomeUsuario);

        /*Configurações do recycler view e adapter dos restaurantes */
        adapter = new AdapterRestaurantes(restaurantes);

        recyclerRestaurantes = findViewById(R.id.rvRestaurantes);
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
                        /* Abre intent de restaurante passando o id do restaurante e o usuário logado */
                        Intent restaurante = new Intent(getApplicationContext(), RestauranteActivity.class);
                        restaurante.putExtra("idRestaurante",restaurantes.get(position).getId());
                        restaurante.putExtra("idUsuario",idUsuario);
                        startActivity(restaurante);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
                )
        );

        /* Lógica para filtrar os restaurantes do recycler view */
        btnBuscar = findViewById(R.id.btnBuscar);
        etBuscar = findViewById(R.id.etBuscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etBuscar.getText().length() == 0){
                    restaurantes.clear();
                    restaurantes.addAll(restaurantesCopia);
                }
                else{
                    String busca = etBuscar.getText().toString().toLowerCase();
                    restaurantes.clear();
                    for(int i = 0 ; i < restaurantesCopia.size() ; i++){
                        String nomeRestaurante = restaurantesCopia.get(i).getNome().toLowerCase();
                        if(nomeRestaurante.contains(busca)){
                            restaurantes.add(restaurantesCopia.get(i));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        /* Lógica para mostrar somente os restaurantes favoritos */
        ivFavoritos = findViewById(R.id.ivFavoritos);
        ivFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchFavorite){
                    //Estava selecionado os favoritos, agora deve voltar todos os restaurantes
                    searchFavorite = false;
                    ivFavoritos.setImageResource(R.drawable.btn_star_big_off);
                    restaurantes.clear();
                    restaurantes.addAll(restaurantesCopia);
                }
                else{
                    //Estava selecionado todos os restaurantes, agora deve mostrar só os favoritos
                    searchFavorite = true;
                    ivFavoritos.setImageResource(R.drawable.btn_star_big_on);

                    try {
                        Cursor cursor = bd.rawQuery("SELECT * FROM restaurantes r JOIN restaurantesFavoritos rf" +
                                " on rf.idRestaurante = r.idRestaurante JOIN categoriaRestaurantes cr " +
                                "ON r.idCategoria = cr.idCategoria WHERE rf.idUsuario = "+idUsuario+"",null);
                        cursor.moveToFirst();
                        int count = 0;
                        restaurantesFavoritos.clear();
                        while (count < cursor.getCount()) {
                            Restaurante r = new Restaurante();
                            r.setNome(cursor.getString(cursor.getColumnIndex("nome")));
                            r.setEndereco(cursor.getString(cursor.getColumnIndex("endereco")));
                            r.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
                            r.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("idRestaurante"))));
                            r.setImagem(cursor.getInt(cursor.getColumnIndex("imagem")));
                            restaurantesFavoritos.add(r);

                            cursor.moveToNext();
                            count++;
                        }
                    }
                    catch(SQLiteException e){
                        Toast.makeText(Home.this, "Falha ao buscar favoritos.", Toast.LENGTH_SHORT).show();
                    }

                    restaurantes.clear();
                    restaurantes.addAll(restaurantesFavoritos);

                }
                adapter.notifyDataSetChanged();
            }
        });
    }

}
