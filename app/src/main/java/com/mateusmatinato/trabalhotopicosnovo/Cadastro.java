package com.mateusmatinato.trabalhotopicosnovo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Cadastro extends AppCompatActivity {

    private TextInputLayout tlNome, tlEmail, tlSenha, tlTelefone, tlEndereco;
    private SQLiteDatabase bd;
    private Button btnCadastrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        bd = openOrCreateDatabase("trabalhoTopicos",MODE_PRIVATE,null);

        btnCadastrar = findViewById(R.id.btnCadastrar);

        tlNome = findViewById(R.id.textLayoutNome);
        tlEmail = findViewById(R.id.textLayoutEmail);
        tlSenha = findViewById(R.id.textLayoutSenha);
        tlTelefone = findViewById(R.id.textLayoutTelefone);
        tlEndereco = findViewById(R.id.textLayoutEndereco);

        /* Lógica para cadastro do usuário */
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean erro = false;
                /* Expressão para validação de email */
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                /* Verificar se todos os campos foram preenchidos */
                if (tlNome.getEditText().getText().length() == 0 ||
                        tlEmail.getEditText().getText().length() == 0 ||
                        tlSenha.getEditText().getText().length() == 0 ||
                        tlTelefone.getEditText().getText().length() == 0 ||
                        tlEndereco.getEditText().getText().length() == 0) {
                    //Não digitou algum dos campos
                    erro = true;
                    Toast.makeText(Cadastro.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                } else {
                    /* Preencheu todos os campos, deve validar o email pela expressão regular */
                    if (!tlEmail.getEditText().getText().toString().trim().matches(emailPattern)) {
                        erro = true;
                        Toast.makeText(Cadastro.this, "Insira um email válido.", Toast.LENGTH_SHORT).show();
                    } else {
                        /* Valida tamanho da senha */
                        if (tlSenha.getEditText().getText().length() < 4) {
                            erro = true;
                            Toast.makeText(Cadastro.this, "A sua senha deve conter 4 caracteres ou mais.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                if(!erro){
                    /* Validações iniciais foram realizadas, deve verificar se o email inserido
                    * já existe no banco. */
                    try{
                        String email = tlEmail.getEditText().getText().toString();
                        Cursor cursor = bd.rawQuery("SELECT * FROM usuarios WHERE email = ?", new String[]{email});
                        if(cursor.getCount() == 0){
                            /* O email não existe, então pode cadastrar (salva na tabela usuarios) */
                            String nome = tlNome.getEditText().getText().toString();
                            String senha = tlSenha.getEditText().getText().toString();
                            String telefone = tlTelefone.getEditText().getText().toString();
                            String endereco = tlEndereco.getEditText().getText().toString();
                            bd.execSQL("INSERT INTO usuarios (nome, email, senha, telefone, endereco) VALUES (?,?,?,?,?)"
                            , new String[]{nome,email,senha,telefone,endereco});
                            Toast.makeText(Cadastro.this, "Cadastro efetuado com sucesso.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            Toast.makeText(Cadastro.this, "Esse email já possui cadastro.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(SQLiteException e){
                        Toast.makeText(Cadastro.this, "Erro ao cadastrar usuário, tente novamente.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


    }
}
