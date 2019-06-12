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

        //Cria tabela de usuários
        bd.execSQL("CREATE TABLE IF NOT EXISTS usuarios (idUsuario INTEGER PRIMARY KEY AUTOINCREMENT, email VARCHAR UNIQUE, senha VARCHAR, nome VARCHAR, " +
                "telefone VARCHAR, endereco VARCHAR, admin INTEGER)");

        //Cria tabela de categorias de restaurantes
        bd.execSQL("CREATE TABLE IF NOT EXISTS categoriaRestaurantes (idCategoria INTEGER PRIMARY KEY," +
                "descricao VARCHAR, imagem INTEGER)");

        //Cria tabela de restaurantes
        bd.execSQL("CREATE TABLE IF NOT EXISTS restaurantes (idRestaurante INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome VARCHAR, telefone VARCHAR, endereco VARCHAR, tempoEntrega VARCHAR ,idCategoria INTEGER, " +
                "CONSTRAINT fk_categoria FOREIGN KEY (idCategoria) REFERENCES categoriaRestaurantes(idCategoria))");

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

        int imgLanchonetes = R.drawable.hamburgueria;
        int imgPizzaria = R.drawable.pizzaria;
        int imgSorveteria = R.drawable.sorveteria;
        int imgItaliana = R.drawable.italiana;
        int imgChurrascaria = R.drawable.churrascaria;
        int imgJaponesa = R.drawable.japonesa;


        try {
            bd.execSQL("INSERT INTO usuarios (email,senha,nome,telefone,endereco, admin) VALUES ('mateus.matinato@unesp.br','1234','Administrador','997712491','Teste',0)");
            bd.execSQL("INSERT INTO usuarios (email,senha,nome,telefone,endereco, admin) VALUES ('mateusmatinato@gmail.com','1234','Mateus Matinato','997712491','Teste',1)");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (1,'Lanchonete',"+imgLanchonetes+") ");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (2,'Sorveteria',"+imgSorveteria+") ");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (3,'Pizzaria',"+imgPizzaria+") ");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (4,'Comida Italiana',"+imgItaliana+") ");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (5,'Churrascaria',"+imgChurrascaria+") ");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (6,'Comida Japonesa',"+imgJaponesa+") ");

            bd.execSQL("INSERT INTO restaurantes(nome,endereco,telefone, tempoEntrega, idCategoria) VALUES ('Lanchonete do Papai','Rua Teste, 287, Bela Vista','(17) 3229-4999','30 a 40 min',1)");
            bd.execSQL("INSERT INTO restaurantes(nome,endereco,telefone, tempoEntrega, idCategoria) VALUES ('Burguette Burger','Rua Teste, 287, Bela Vista','(17) 99771-2491','60 a 70 min',1)");
            bd.execSQL("INSERT INTO restaurantes(nome,endereco,telefone, tempoEntrega, idCategoria) VALUES ('Sorveteria Cremoso','Rua Teste 2, 2873, Bela Desaparecida','(17) 3912-4002','70 a 90 min',2)");
            bd.execSQL("INSERT INTO restaurantes(nome,endereco,telefone, tempoEntrega, idCategoria) VALUES ('Pizzaria Vesúvio','Rua Teste 3, 9912, Jardim Nazareth','(17) 3221-4112','25 a 50 min',3)");
            bd.execSQL("INSERT INTO restaurantes(nome,endereco,telefone, tempoEntrega, idCategoria) VALUES ('Mamma Mia Massas','Rua Teste 4, 2201, Bela Adormecida','(17) 3292-4992','30 a 60 min',4)");
            bd.execSQL("INSERT INTO restaurantes(nome,endereco,telefone, tempoEntrega, idCategoria) VALUES ('Coxilha dos Pampas','Rua Teste 5, 10, Bela Adormecida','(17) 3221-4112','30 a 40 min',5)");
            bd.execSQL("INSERT INTO restaurantes(nome,endereco,telefone, tempoEntrega, idCategoria) VALUES ('Sushidô','Rua Teste 6, 10, Centro','(17) 3221-4112',6)");
        } catch (SQLException e) {
            Log.d("ERRO",""+e.getMessage());
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
