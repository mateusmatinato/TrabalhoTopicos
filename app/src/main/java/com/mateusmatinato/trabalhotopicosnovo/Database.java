package com.mateusmatinato.trabalhotopicosnovo;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {
    /* A função dessa classe é somente para criar o banco e inserir os dados iniciais */

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
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Cheese Bacon',16.00,'Hambúrguer, queijo mozarela, alface, tomate e bacon.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Jerseys',16.00,'Hambúrguer, queijo mozarela, cebola grelhada na chapa, ovo frito e bacon.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Volcano',17.00,'Hambúrguer, queijo mozarela, onion rings, cheddar cremoso e bacon.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Cheese Catupiry',16.00,'Hambúrguer, queijo mozarela, Catupiry Original e alho frito.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Bacon Lover',17.00,'Hambúrguer, queijo mozarela e muito bacon.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Corner',16.00,'Hambúrguer, queijo cheddar, picles de pepino e cebola roxa.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Dog Tudo',16.00,'Pão de hambúrguer, salsicha, carne, frango ou misto, bacon, presunto, muçarela, bacon, ketchup, maionese e batata palha!')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Dog Tradicional',11.00,'Pão de hotdog, salsicha, (carne, , frango ou misto) ketchup, maionese e batata palha!')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Batata Palito',6.00,'Porção de Batata Palito')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Coca Cola 2 Litros',10.00,'')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Fanta 2 Litros',10.00,'')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (2,'Cerveja Heineken Long Neck',7.00,'')");

            //Insere produtos no restaurante 3
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (3,'Frappuccinno 300ml',13.00,'Café + chocolate suíço + leite Ninho + sorvete de creme + biscoito cookies.  ')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (3,'Sundae 250ml',14.00,'Sorvete de creme com cobertura de chocolate. Acompanha farofa doce e amendoim.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (3,'Smoothie de iogurte natural com sorvete e polpa de frutas 300ml',12.00,'50% de iogurte natural, 50% de sorvete + polpa de frutas de sua preferência')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (3,'Escondidinho de morango',14.00,'Morangos picados cobertos de chocolate com avelã, sorvete de creme, leite Ninho e leite condensado')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (3,'Arrumadinho de morango',12.00,'Farofa doce feita com paçoca e leite em pó, morangos (fruta), sorvete de creme, cobertura de chocolate')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (3,'Frapê de maracujá com chocolate',14.00,'Maracujá batido com sorvete de creme e cobertura de chocolate')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (3,'Fondue de morango com chocolate',14.00,'100 gramas de morango, acompanhado de chocolate derretido quentinho.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (3,'Milk shake Nutella 500 ml',18.00,'Sorvete de creme com Nutella')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (3,'Sundae com nutella 250 ml',15.00,'Sorvete de creme, nutella, farofa doce, amendoim.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (3,'Água mineral 510ml',6.00,'Garrafa 510 ml')");

            //Insere produtos no restaurante 4
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (4,'Combo hunger família',68.00,'1 pizza família 35cm + 1 crostini + 1 pizza doce individual + 1 refri ou suco grande')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (4,'Combo casal',42.00,'2 pizzas + 1 crostini + 1 refri 600ml. O combo ideal para duas pessoas.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (4,'Pizza pollo maiale',42.00,'Molho de tomate pelado, queijo muçarela, frango desfiado, Catupiry, fatias de bacon, azeitona preta e orégano.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (4,'Pizza da calábria',38.00,'Molho de tomate pelado, queijo muçarela, calabresa em rodelas, azeitonas pretas, orégano e cebola roxa.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (4,'Pizza la mia donna',47.00,'Molho de tomate pelado, queijo muçarela, Catupiry, parmesão, bacon, tomates cereja e orégano.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (4,'Pizza canadense',52.00,'Molho de tomate pelado, lombo canadense fatiado, queijo muçarela, Catupiry, azeitonas pretas e orégano.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (4,'Pizza di napoli',49.00,'Molho de tomate pelado, presunto em lascas, queijo muçarela, bacon, tomates em cubos e orégano.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (4,'Pizza la portuga',50.00,'Molho de tomate pelado, presunto em lascas, queijo muçarela, ovo de codorna, ervilhas frescas, orégano e cebola roxa.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (4,'Coca-Cola 2 Litros',10.00,'')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (4,'Fanta 2 Litros',10.00,'')");

            //Insere produtos no restaurante 5
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (5,'Lasanha de Peito de Peru',18.20,'Lasanha de peito de peru com ricota e manjericão feita com farinha de arroz sem glúten')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (5,'Tutti il Bene',15.80,'Alface americano, Carpaccio, Tomate salada, Palmito, Ervilha fresca, Molho especial de mostarda, Queijo parmesão, Tomate seco, Alcaparras e Azeitonas pretas')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (5,'Macarrão à bolonhesa',17.00,'Macarrão grano duro, molho sugo, carne bovina e parmesão. Sugestão de molho: para este produto sugerimos o molho branco.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (5,'Macarrão de carne seca c/ pimenta.',22.00,'Macarrão grano duro, molho, carne seca, pimenta biquinho e queijo parmesão. Sugestão de molho: para este produto sugerimos o molho sugo.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (5,'Macarrão brócolis e bacon',19.00,'Macarrão grano duro, molho, brócolis, bacon e parmesão. Sugestão de molho: para este produto sugerimos o molho bechamel ou molho alfredo.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (5,'Nhoque à bolonhesa',21.00,'Nhoque de batata, molho sugo, carne bovina e parmesão.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (5,'Nhoque filé mignon',25.00,'Nhoque de batata, molho sugo, filé mignon em tiras e parmesão.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (5,'Calzone de filé mignon',24.00,'Molho don, filé mignon, alho poró, tomate seco, mussarela e Catupiry.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (5,'Coca Cola 2 Litros',10.00,'')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (5,'Fanta 2 Litros',10.00,'')");

            //Insere produtos no restaurante 6
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (6,'Combo - bisteca suína 130 gr + refrigerante lata + sobremesa',31.00,'Acompanha arroz, feijão (carioca ou preto) e fritas ou purê.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (6,'Combo - calabresa fatiada 140 gr + refrigerante lata + sobremesa',31.00,'Acompanha arroz, feijão (carioca ou preto) e fritas ou purê.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (6,'Combo - iscas de carne 120 gr + refrigerante lata + sobremesa',34.00,'Acompanha arroz, feijão (carioca ou preto) salada e fritas ou purê.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (6,'Combo-frango á passarinho+ refrigerante lata + sobremesa',35.00,'200 gr de frango à passarinho aproximadamente')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (6,'Combo - contra filé grelhado 120 gr + refrigerante lata + sobremesa',39.00,'Acompanha arroz, feijão (carioca ou preto) salada e fritas ou purê.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (6,'Contra filé grelhado 180 gr + 3 acompanhamentos + saladinha',38.00,'3 acompanhamentos + saladinha.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (6,'Salmão 200 gr + 3 acompanhamentos + saladinha',50.00,'3 acompanhamentos + saladinha.  Atenção, salmão e tilápia demoram cerca de 15 minutos a mais para que fique pronto, então sua entrega demandará cerca de 15 minutos à mais do tempo informado!')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (6,'2 hambúrgueres com ovo frito 110 gr',24.00,'Acompanha arroz, feijão (carioca ou preto) e fritas ou purê.')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (6,'Coca Cola 2 Litros',10.00,'')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (6,'Fanta 2 Litros',10.00,'')");

            //Insere produtos no restaurante 7
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (7,'Combinado 40 unidades + 2 mini temaki',69.00,'4 sashimi salmão + 4 sashimi peixe branco + 4 joy salmão + 4 uramaki salmão + 4 uramaki skin + 10 hot Philadelphia salmão + 3 niguiri salmão + 3 niguiri skin + 4 hossomaki salmão ( 40 unidades + brinde )')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (7,'Combo 2 ( 16 Peças )',29.40,'4 Sashimi Salmão + 4 Hot Holl + 4 Joy + 4 Uramaki Salmão ')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (7,'Combo gold ( 15 uni )',25.00,'5 sashimi salmão + 4 joy + 6 niguiri salmão')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (7,'Hot holl ( 20 unidades )',28.00,'20 unidades da melhor porção de hot holl')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (7,'Joy queijo ( 8 unidades )',15.00,'Joy de queijo: bolinho de arroz envolto por uma fatia de queijo aviação flambado, coberto por salmão grelhado e cream cheese')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (7,'Combo 1 ( 30 Peças )',59.99,'10 Sashimi Salmão + 4 Joy Salmão + 6 Niguiri Salmão + 6 Uramaki Salmão + 4 Hossomaki Salmão')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (7,'Compre 2 Temaki 180gr Grelhado e leve 3',42.99,'Arroz, Alga, Salmão Grelhado, Cream chesse e cebolinha')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (7,'Uramaki skin',15.00,'Couro do salmão grelhado + cream cheese')");
            bd.execSQL("INSERT INTO produtos(idRestaurante,nome,preco,descricao) VALUES (7,'Temaki camarão',24.99,'Camarão empnado e frito + cream cheese + cebolinha')");

            bd.execSQL("INSERT INTO usuarios (email,senha,nome,telefone,endereco, admin) VALUES ('mateus.matinato@unesp.br','1234','Mateus Matinato','997712491','Rua Cristóvão Colombo, 1125 - Jardim Nazareth',0)");
            Log.d("DADOS INICIAIS", "DADOS INICIAIS INSERIDOS COM SUCESSO");
        } catch (SQLException e) {
            Log.d("DADOS INICIAIS","ERRO: "+e.getMessage());
        }
    }
}
