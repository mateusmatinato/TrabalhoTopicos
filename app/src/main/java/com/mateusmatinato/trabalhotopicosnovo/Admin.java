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

    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        bd = openOrCreateDatabase("trabalhoTopicos",MODE_PRIVATE, null);
        Intent intent = getIntent();
        int idUsuario = intent.getIntExtra("idUsuario",0);

        Cursor cursor = bd.rawQuery("SELECT * FROM usuarios where idUsuario = "+idUsuario,null);
        cursor.moveToFirst();
        String nomeUsuario = cursor.getString(cursor.getColumnIndex("nome"));
        tvNome = findViewById(R.id.tvTitulo);
        tvNome.setText("Bem vindo, "+nomeUsuario);
    }
}
