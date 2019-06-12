package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RestauranteActivity extends AppCompatActivity {
    private TextView tvNome, tvTempoEntrega;
    private SQLiteDatabase bd;
    private ImageView imgLike;

    private int idUsuario, idRestaurante;

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
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_restaurante);

        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario",0);
        idRestaurante = intent.getIntExtra("idRestaurante",0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);
        Cursor cursor = bd.rawQuery("SELECT * FROM restaurantes where idRestaurante = " + idRestaurante, null);
        cursor.moveToFirst();
        String nomeRestaurante = cursor.getString(cursor.getColumnIndex("nome"));
        String tempoEntrega = cursor.getString(cursor.getColumnIndex("tempoEntrega"));
        tvNome = findViewById(R.id.tvNome);
        tvTempoEntrega = findViewById(R.id.tvTempoEntrega);
        tvNome.setText(""+nomeRestaurante);
        tvTempoEntrega.setText("Entrega em: "+tempoEntrega);

        imgLike = findViewById(R.id.imgStar);
        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgLike.setImageResource(R.drawable.btn_star_big_on);
            }
        });
    }
}
