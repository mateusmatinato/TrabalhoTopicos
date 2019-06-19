package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase bd;
    private TextView tvCadastro;
    private Button btnLogar;
    private EditText etSenha, etEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Verifica se tem alguém logado
        final SharedPreferences sp=getSharedPreferences("LoginTrabalhoTopicos", MODE_PRIVATE);
        int idUsuario = sp.getInt("idUsuarioLogado",-1);
        int permissaoUsuario = sp.getInt("nivelUsuario",-1);
        Log.d("LOGIN","ID USUARIO: "+idUsuario+" - NIVEL: "+permissaoUsuario);

        /* Para resetar o aplicativo tire essa parte comentada
        deleteDatabase("trabalhoTopicos");
        SharedPreferences.Editor Ed=sp.edit();
        Ed.putInt("idUsuarioLogado", -1);
        Ed.putInt("nivelUsuario",-1);
        Ed.commit();
        */
        if(idUsuario != -1){
            if(permissaoUsuario == 0){
                //User
                Intent inicio = new Intent(getApplicationContext(), Home.class);
                inicio.putExtra("idUsuario",idUsuario);
                finish();
                startActivity(inicio);
            }
            else if(permissaoUsuario == 1){
                //Admin
                Intent admin = new Intent(getApplicationContext(),Admin.class);
                admin.putExtra("idUsuario",idUsuario);
                finish();
                startActivity(admin);
            }
        }

        // Não tem ninguém logado, então pode abrir página de login
        setContentView(R.layout.activity_main);
        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);

        new Database().criaBase(bd);
        new Database().insereDadosIniciais(bd);

        btnLogar = findViewById(R.id.btnLogar);
        etSenha = findViewById(R.id.etSenha);
        etEmail = findViewById(R.id.etEmail);

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, senha;
                email = etEmail.getText().toString();
                senha = etSenha.getText().toString();
                Cursor cursor = bd.rawQuery("SELECT * FROM usuarios WHERE email = ? and senha = ?", new String[]{email, senha});
                cursor.moveToFirst();
                if (cursor.getCount() != 0) {
                    //Usuário pode logar
                    int idUsuario = cursor.getInt(cursor.getColumnIndex("idUsuario"));
                    int isAdmin = cursor.getInt(cursor.getColumnIndex("admin"));
                    SharedPreferences.Editor Ed=sp.edit();
                    Ed.putInt("idUsuarioLogado", idUsuario);
                    Ed.putInt("nivelUsuario",isAdmin);
                    Ed.commit();

                    if (isAdmin == 1) {
                        Intent admin = new Intent(getApplicationContext(),Admin.class);
                        admin.putExtra("idUsuario",idUsuario);
                        finish();
                        startActivity(admin);

                    } else {
                        Intent inicio = new Intent(getApplicationContext(), Home.class);
                        inicio.putExtra("idUsuario",idUsuario);
                        finish();
                        startActivity(inicio);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Email ou senha inválidos, tente novamente.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        tvCadastro = findViewById(R.id.tvCadastro);
        tvCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cadastro = new Intent(getApplicationContext(), Cadastro.class);
                startActivity(cadastro);
            }
        });
    }
}
