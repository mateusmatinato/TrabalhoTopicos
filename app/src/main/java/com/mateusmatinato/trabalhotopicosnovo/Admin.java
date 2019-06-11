package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Admin extends AppCompatActivity {

    private SQLiteDatabase bd;
    private TextView tvNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Oculta título do app
        setContentView(R.layout.activity_admin);

        bd = openOrCreateDatabase("trabalhoTopicos",MODE_PRIVATE, null);
        Intent intent = getIntent();

        String emailUsuario = intent.getStringExtra("emailLogado");
        Cursor cursor = bd.rawQuery("SELECT * FROM usuarios where email = ?",new String[]{emailUsuario});
        cursor.moveToFirst();
        String nomeUsuario = cursor.getString(cursor.getColumnIndex("nome"));
        tvNome = findViewById(R.id.tvNome);
        tvNome.setText("Bem vindo, "+nomeUsuario);
    }
}
