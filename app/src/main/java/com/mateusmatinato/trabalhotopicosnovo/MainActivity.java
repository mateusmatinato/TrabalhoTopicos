package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        setContentView(R.layout.activity_main);
        bd = openOrCreateDatabase("trabalhoTopicos", MODE_PRIVATE, null);

        //Cria tabela de usuários
        bd.execSQL("CREATE TABLE IF NOT EXISTS usuarios (email VARCHAR PRIMARY KEY, senha VARCHAR, nome VARCHAR, " +
                "telefone VARCHAR, endereco VARCHAR, admin INTEGER)");

        //Cria tabela de restaurantes
        bd.execSQL("CREATE TABLE IF NOT EXISTS restaurantes (idRestaurante INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome VARCHAR, telefone VARCHAR, endereco VARCHAR, imagem blob)");

        //Cria tabela de produtos
        bd.execSQL("CREATE TABLE IF NOT EXISTS produtos (idProduto INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idRestaurante INTEGER, nome VARCHAR, preco DOUBLE, descricao VARCHAR, imagem blob," +
                "CONSTRAINT fk_restaurante FOREIGN KEY (idRestaurante) REFERENCES restaurante(idRestaurante))");

        //Cria tabela de pedidos
        bd.execSQL("CREATE TABLE IF NOT EXISTS pedidos (idPedido INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idRestaurante INTEGER, idUsuario INTEGER, " +
                "CONSTRAINT fk_restaurante FOREIGN KEY (idRestaurante) REFERENCES restaurante(idRestaurante)," +
                "CONSTRAINT fk_usuario FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario))");

        //Cria tabela de itens de cada pedido
        bd.execSQL("CREATE TABLE IF NOT EXISTS itensPedido (idItem INTEGER PRIMARY KEY AUTOINCREMENT, idPedido INTEGER, " +
                "idProduto INTEGER, quantidade INTEGER, precoItem double, CONSTRAINT fk_pedido " +
                "FOREIGN KEY (idPedido) REFERENCES pedidos(idPedido), CONSTRAINT fk_produto " +
                "FOREIGN KEY (idProduto) REFERENCES produtos(idProduto))");

        try {
            bd.execSQL("INSERT INTO usuarios (email,senha,nome,telefone,endereco, admin) VALUES ('mateus.matinato@unesp.br','1234','Administrador','997712491','Teste',1)");
            bd.execSQL("INSERT INTO usuarios (email,senha,nome,telefone,endereco, admin) VALUES ('mateusmatinato@gmail.com','1234','Mateus Matinato','997712491','Teste',1)");
            bd.execSQL("INSERT INTO restaurantes(nome,endereco,telefone) VALUES ('Lanchonete do Papai','Rua Teste, 287, Bela Vista','1632536846')");
            bd.execSQL("INSERT INTO restaurantes(nome,endereco,telefone) VALUES ('Lanchonete do Papai 2','Rua Teste 2, 2873, Bela Desaparecida','16997712491')");
            bd.execSQL("INSERT INTO restaurantes(nome,endereco,telefone) VALUES ('Lanchonete do Papai 3','Rua Teste 3, 2817, Bela Adormecida','16322104311')");
        } catch (SQLException e) {

        }
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
                    int isAdmin = cursor.getInt(cursor.getColumnIndex("admin"));
                    if (isAdmin == 1) {
                        Intent admin = new Intent(getApplicationContext(),Admin.class);
                        admin.putExtra("emailLogado",email);
                        startActivity(admin);

                    } else {
                        Intent inicio = new Intent(getApplicationContext(), Inicio.class);
                        inicio.putExtra("emailLogado", email);
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
