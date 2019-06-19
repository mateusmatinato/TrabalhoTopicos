package com.mateusmatinato.trabalhotopicosnovo;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {

    public void criaBase(SQLiteDatabase bd){
        //Cria tabela de usuários
        bd.execSQL("CREATE TABLE IF NOT EXISTS usuarios (idUsuario INTEGER PRIMARY KEY AUTOINCREMENT, email VARCHAR UNIQUE, senha VARCHAR, nome VARCHAR, " +
                "telefone VARCHAR, endereco VARCHAR, admin INTEGER)");

        //Cria tabela de categorias de restaurantes
        bd.execSQL("CREATE TABLE IF NOT EXISTS categoriaRestaurantes (idCategoria INTEGER PRIMARY KEY," +
                "descricao VARCHAR, imagem INTEGER)");

        //Cria tabela de restaurantes
        bd.execSQL("CREATE TABLE IF NOT EXISTS restaurantes (idRestaurante INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome VARCHAR, telefone VARCHAR, endereco VARCHAR, tempoEntrega VARCHAR ,idCategoria INTEGER, taxaEntrega DOUBLE," +
                "CONSTRAINT fk_categoria FOREIGN KEY (idCategoria) REFERENCES categoriaRestaurantes(idCategoria))");

        //Cria tabela de produtos
        bd.execSQL("CREATE TABLE IF NOT EXISTS produtos (idProduto INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idRestaurante INTEGER, nome VARCHAR, preco DOUBLE, descricao VARCHAR, imagem INTEGER," +
                "CONSTRAINT fk_restaurante FOREIGN KEY (idRestaurante) REFERENCES restaurante(idRestaurante))");

        //Cria tabela de pedidos
        bd.execSQL("CREATE TABLE IF NOT EXISTS pedidos (idPedido INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idRestaurante INTEGER, idUsuario INTEGER, status TEXT, observacao VARCHAR, data TEXT, " +
                "troco DOUBLE, precoTotal DOUBLE, metodoPagamento INTEGER," +
                "CONSTRAINT fk_restaurante FOREIGN KEY (idRestaurante) REFERENCES restaurante(idRestaurante)," +
                "CONSTRAINT fk_usuario FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario))");

        //Cria tabela de itens de cada pedido
        bd.execSQL("CREATE TABLE IF NOT EXISTS itensPedido (idItem INTEGER PRIMARY KEY AUTOINCREMENT, idPedido INTEGER, " +
                "idProduto INTEGER, quantidade INTEGER, CONSTRAINT fk_pedido " +
                "FOREIGN KEY (idPedido) REFERENCES pedidos(idPedido), CONSTRAINT fk_produto " +
                "FOREIGN KEY (idProduto) REFERENCES produtos(idProduto))");

        bd.execSQL("CREATE TABLE IF NOT EXISTS restaurantesFavoritos (idRestaurante INTEGER, idUsuario INTEGER," +
                "CONSTRAINT pk_favorito PRIMARY KEY (idRestaurante, idUsuario), " +
                "CONSTRAINT fk_restaurante FOREIGN KEY (idRestaurante) REFERENCES restaurantes(idRestaurante)," +
                "CONSTRAINT fk_usuario FOREIGN KEY (idUsuario) REFERENCES usuarios(idUsuario))");

    }

    public void insereDadosIniciais(SQLiteDatabase bd){

        int imgLanchonetes = R.drawable.hamburgueria;
        int imgPizzaria = R.drawable.pizzaria;
        int imgSorveteria = R.drawable.sorveteria;
        int imgItaliana = R.drawable.italiana;
        int imgChurrascaria = R.drawable.churrascaria;
        int imgJaponesa = R.drawable.japonesa;

        try {
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (1,'Lanchonete',"+imgLanchonetes+") ");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (2,'Sorveteria',"+imgSorveteria+") ");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (3,'Pizzaria',"+imgPizzaria+") ");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (4,'Comida Italiana',"+imgItaliana+") ");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (5,'Churrascaria',"+imgChurrascaria+") ");
            bd.execSQL("INSERT INTO categoriaRestaurantes(idCategoria,descricao,imagem) VALUES (6,'Comida Japonesa',"+imgJaponesa+") ");

            bd.execSQL("INSERT INTO restaurantes(idRestaurante, nome,endereco,telefone, tempoEntrega, taxaEntrega, idCategoria) VALUES (1,'Hashtag Lanches','Rua Teste, 287, Bela Vista','(17) 3229-4999','30 a 40 min',7.00,1)");
            bd.execSQL("INSERT INTO restaurantes(idRestaurante, nome,endereco,telefone, tempoEntrega, taxaEntrega, idCategoria) VALUES (2,'Burguette Burger','Rua Teste, 287, Bela Vista','(17) 99771-2491','60 a 70 min',8.00,1)");
            bd.execSQL("INSERT INTO restaurantes(idRestaurante, nome,endereco,telefone, tempoEntrega, taxaEntrega, idCategoria) VALUES (3,'Sorveteria Cremoso','Rua Teste 2, 2873, Bela Desaparecida','(17) 3912-4002','70 a 90 min',0.00,2)");
            bd.execSQL("INSERT INTO restaurantes(idRestaurante, nome,endereco,telefone, tempoEntrega, taxaEntrega, idCategoria) VALUES (4,'Pizzaria Vesúvio','Rua Teste 3, 9912, Jardim Nazareth','(17) 3221-4112','25 a 50 min',4.00,3)");
            bd.execSQL("INSERT INTO restaurantes(idRestaurante, nome,endereco,telefone, tempoEntrega, taxaEntrega, idCategoria) VALUES (5,'Mamma Mia Massas','Rua Teste 4, 2201, Bela Adormecida','(17) 3292-4992','30 a 60 min',10.00,4)");
            bd.execSQL("INSERT INTO restaurantes(idRestaurante, nome,endereco,telefone, tempoEntrega, taxaEntrega, idCategoria) VALUES (6,'Coxilha dos Pampas','Rua Teste 5, 10, Bela Adormecida','(17) 3221-4112','30 a 40 min',12.00,5)");
            bd.execSQL("INSERT INTO restaurantes(idRestaurante, nome,endereco,telefone, tempoEntrega, taxaEntrega, idCategoria) VALUES (7,'Sushidô','Rua Teste 6, 10, Centro','(17) 3221-4112','30 a 40 min',9.00,6)");

            //Insere produtos no restaurante 1
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'X-Salada',14.00,'Pão, hamburguer, queijo , batata palha, molho, alface e tomate')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'X-Salada Bacon',16.90,'Pão, hamburguer, queijo, bacon, batata palha, alface e tomate')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'X-Egg Bacon',17.00,'Pão, hamburguer, queijo, bacon, ovo e batata palha')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'X-Salada Egg',15.00,'Pão, hamburguer, queijo, ovo, batata palha, alface e tomate')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'X-Tudo',18.90,'Pão, hamburguer, queijo, bacon, ovo, batata palha, catupiry, milho, ervilha, alface e tomate')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'X-Salada Duplo',21.00,'Pão, 2 hamburgueres, dobro de queijo, batata palha, alface e tomate')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'Especial #Hashtag',25.00,'Pão, hambúrguer recheado com queijo, bacon, presunto, queijo, cebola rocha, molho especial, milho e batata palha')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'Porção de Batata Frita',18.50,'500 gramas de batata frita')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'Porção de Anel de Cebola Empanado',19.50,'Anel de cebola empanado 400 gramas acompanha molho')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'Coca Cola - 2.5 litros',11.00,'Coca 2.5 litros gelada')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'Coca Cola - Lata 310 ml',4.00,'Coca lata 310ml gelada')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (1,'Fanta - Lata 310 ml',4.00,'Fanta lata 310ml gelada')");

            //Insere produtos no restaurante 2


            //Insere produtos no restaurante 3

            //Insere produtos no restaurante 4

            //Insere produtos no restaurante 5

            //Insere produtos no restaurante 6

            //bd.execSQL("INSERT INTO usuarios (email,senha,nome,telefone,endereco, admin) VALUES ('mateus.matinato@unesp.br','1234','Administrador','997712491','Teste',0)");
            //bd.execSQL("INSERT INTO usuarios (email,senha,nome,telefone,endereco, admin) VALUES ('mateusmatinato@gmail.com','1234','Mateus Matinato','997712491','Teste',1)");

        } catch (SQLException e) {
            Log.d("ERRO DADOS INICIAIS",""+e.getMessage());
        }
    }
}
