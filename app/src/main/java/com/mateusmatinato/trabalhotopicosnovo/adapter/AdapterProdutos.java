package com.mateusmatinato.trabalhotopicosnovo.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mateusmatinato.trabalhotopicosnovo.R;
import com.mateusmatinato.trabalhotopicosnovo.model.Produto;
import com.mateusmatinato.trabalhotopicosnovo.model.Restaurante;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterProdutos extends RecyclerView.Adapter<AdapterProdutos.MyViewHolder>{
    private List<Produto> listaProdutos;
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public AdapterProdutos(List<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Este método cria a View para serem exibidos os elementos

        // Converte o layout XML para uma View
        View listaItens = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_produtos, viewGroup,false);


        return new MyViewHolder(listaItens);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        // Este método atualiza a visualização e mostra os elementos
        // i representa cada posição no RecyclerView
        // Como setamos a quantidade de elementos para lista.size()
        // i vai de 0 a lista.size

        myViewHolder.nome.setText(listaProdutos.get(i).getNome());
        myViewHolder.descricao.setText(listaProdutos.get(i).getDescricao());
        myViewHolder.preco.setText("R$: "+df2.format(listaProdutos.get(i).getPreco()));

        myViewHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(myViewHolder.btnMinus.getContext(), "CLICOU NO MENOS", Toast.LENGTH_SHORT).show();
            }
        });

        myViewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(myViewHolder.btnAdd.getContext(), "CLICOU NO MAIS", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public int getItemCount() {
        // Retorna a quantidade de itens que serão exibidos
        return listaProdutos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        //cria elementos gráficos que estarão no modelo
        TextView nome;
        TextView descricao;
        TextView preco;
        ImageView btnMinus;
        ImageView btnAdd;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //linka os elementos do layout aos atributos da classe
            nome = itemView.findViewById(R.id.tvNomeProduto);
            descricao = itemView.findViewById(R.id.tvDescricao);
            preco = itemView.findViewById(R.id.tvPreco);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}
