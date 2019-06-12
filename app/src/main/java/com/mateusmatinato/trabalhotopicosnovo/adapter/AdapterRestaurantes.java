package com.mateusmatinato.trabalhotopicosnovo.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mateusmatinato.trabalhotopicosnovo.R;
import com.mateusmatinato.trabalhotopicosnovo.model.Restaurante;
import java.util.List;

public class AdapterRestaurantes extends RecyclerView.Adapter<AdapterRestaurantes.MyViewHolder>{
    private List<Restaurante> listaRestaurantes;

    public AdapterRestaurantes(List<Restaurante> listaRestaurantes) {
        this.listaRestaurantes = listaRestaurantes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Este método cria a View para serem exibidos os elementos

        // Converte o layout XML para uma View
        View listaItens = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_restaurantes, viewGroup,false);

        return new MyViewHolder(listaItens);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Este método atualiza a visualização e mostra os elementos
        // i representa cada posição no RecyclerView
        // Como setamos a quantidade de elementos para lista.size()
        // i vai de 0 a lista.size

        myViewHolder.nome.setText(listaRestaurantes.get(i).getNome());
        myViewHolder.endereco.setText(listaRestaurantes.get(i).getEndereco());
        myViewHolder.telefone.setText(listaRestaurantes.get(i).getTelefone());
        myViewHolder.imagem.setImageResource(listaRestaurantes.get(i).getImagem());


    }

    @Override
    public int getItemCount() {
        // Retorna a quantidade de itens que serão exibidos
        return listaRestaurantes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        //cria elementos gráficos que estarão no modelo
        TextView nome;
        TextView endereco;
        TextView telefone;
        ImageView imagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //linka os elementos do layout aos atributos da classe
            nome = itemView.findViewById(R.id.tvNomeProduto);
            endereco = itemView.findViewById(R.id.tvEndereco);
            telefone = itemView.findViewById(R.id.tvTelefone);
            imagem = itemView.findViewById(R.id.imgRestaurante);
            imagem.setImageResource(R.drawable.hamburgueria);
        }
    }
}
