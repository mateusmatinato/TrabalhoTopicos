package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mateusmatinato.trabalhotopicosnovo.adapter.AdapterPedidos;
import com.mateusmatinato.trabalhotopicosnovo.model.Pedido;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Perfil extends AppCompatActivity {

    private TextView tvNomeUsuario;
    private SQLiteDatabase bd;

    private int idUsuario;

    private TextInputLayout tlEmail, tlSenha, tlTelefone, tlEndereco;

    private Button btnSair, btnAtualizar;


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
                    Intent pedidos = new Intent(getApplicationContext(), Pedidos.class);
                    pedidos.putExtra("idUsuario", idUsuario);
                    finish();
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
        setContentView(R.layout.activity_perfil);
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("idUsuario", 0);

        final SharedPreferences sp = getSharedPreferences("LoginTrabalhoTopicos", MODE_PRIVATE);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_perfil).setChecked(true);

        tvNomeUsuario = findViewById(R.id.tvNomeUsuario);
        tlEmail = findViewById(R.id.textLayoutEmail);
        tlSenha = findViewById(R.id.textLayoutSenha);
        tlTelefone = findViewById(R.id.textLayoutTelefone);
        tlEndereco = findViewById(R.id.textLayoutEndereco);

        btnSair = findViewById(R.id.btnSair);
        btnAtualizar = findViewById(R.id.btnAtualizar);

        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);

        Cursor cursor = null;
        try {
            cursor = bd.rawQuery("SELECT * FROM usuarios WHERE idUsuario = " + idUsuario, null);
            cursor.moveToFirst();
        } catch (SQLiteException e) {
            Toast.makeText(this, "Erro ao buscar usuário", Toast.LENGTH_SHORT).show();
        }
        if (cursor != null) {
            tvNomeUsuario.setText(cursor.getString(cursor.getColumnIndex("nome")));
            tlEmail.getEditText().setText(cursor.getString(cursor.getColumnIndex("email")));
            tlSenha.getEditText().setText(cursor.getString(cursor.getColumnIndex("senha")));
            tlTelefone.getEditText().setText(cursor.getString(cursor.getColumnIndex("telefone")));
            tlEndereco.getEditText().setText(cursor.getString(cursor.getColumnIndex("endereco")));
        }


        /* Lógica do botão sair (muda as preferencias para pedir email e senha) e redireciona para login */
        btnSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor Ed = sp.edit();
                Ed.putInt("idUsuarioLogado", -1);
                Ed.putInt("nivelUsuario", -1);
                Ed.commit();
                Intent login = new Intent(getApplicationContext(), MainActivity.class);
                finish();
                startActivity(login);
            }
        });

        /* Lógica para atualizar cadastro do usuário */
        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean erro = false;
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                //Está atualizando os dados, deve primeiro verificar se todos foram preenchidos
                if (tlEmail.getEditText().getText().length() == 0 ||
                        tlSenha.getEditText().getText().length() == 0 ||
                        tlTelefone.getEditText().getText().length() == 0 ||
                        tlEndereco.getEditText().getText().length() == 0) {
                    //Não digitou algum dos campos
                    erro = true;
                    Toast.makeText(Perfil.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                } else {
                    //Preencheu todos, deve verificar se o email é valido
                    if (!tlEmail.getEditText().getText().toString().trim().matches(emailPattern)) {
                        erro = true;
                        Toast.makeText(Perfil.this, "Insira um email válido.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Email é valido também, deve verificar se a senha possui mais que 4 caracteres
                        if (tlSenha.getEditText().getText().length() < 4) {
                            erro = true;
                            Toast.makeText(Perfil.this, "A sua senha deve conter 4 caracteres ou mais.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                if (!erro) {
                    //Aqui dá o update no banco e deve verificar se o email já existe
                    try {
                        String email = tlEmail.getEditText().getText().toString();
                        Cursor cursor = bd.rawQuery("SELECT * FROM usuarios WHERE email = ?", new String[]{email});
                        cursor.moveToFirst();
                        if (cursor.getCount() == 0) {
                            String senha = tlSenha.getEditText().getText().toString();
                            String telefone = tlTelefone.getEditText().getText().toString();
                            String endereco = tlEndereco.getEditText().getText().toString();
                            bd.execSQL("UPDATE usuarios SET email = ?, senha = ?, telefone = ?, endereco = ?" +
                                    "WHERE idUsuario = " + idUsuario, new String[]{email, senha, telefone, endereco});
                            Toast.makeText(Perfil.this, "Cadastro atualizado com sucesso.", Toast.LENGTH_SHORT).show();
                        } else if (cursor.getCount() > 0) {
                            int idUsuarioMail = cursor.getInt(cursor.getColumnIndex("idUsuario"));
                            if (idUsuarioMail == idUsuario) {
                                String senha = tlSenha.getEditText().getText().toString();
                                String telefone = tlTelefone.getEditText().getText().toString();
                                String endereco = tlEndereco.getEditText().getText().toString();
                                bd.execSQL("UPDATE usuarios SET email = ?, senha = ?, telefone = ?, endereco = ?" +
                                        "WHERE idUsuario = " + idUsuario, new String[]{email, senha, telefone, endereco});
                                Toast.makeText(Perfil.this, "Cadastro atualizado com sucesso.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Perfil.this, "Esse email já possui cadastro.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Perfil.this, "Esse email já possui cadastro.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (SQLiteException e) {
                        Toast.makeText(Perfil.this, "Erro ao atualizar os dados, tente novamente.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


    }
}
