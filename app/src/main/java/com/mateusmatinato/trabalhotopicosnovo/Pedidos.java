package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class Pedidos extends AppCompatActivity {

    private TextView tvNome;
    private SQLiteDatabase bd;

    private int idUsuario;

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

                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide(); // Oculta título do app
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario",0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_pedidos).setChecked(true);

        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);



    }
}
