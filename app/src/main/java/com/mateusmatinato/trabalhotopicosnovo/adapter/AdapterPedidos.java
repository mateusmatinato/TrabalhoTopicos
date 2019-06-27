package com.mateusmatinato.trabalhotopicosnovo.adapter;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mateusmatinato.trabalhotopicosnovo.R;
import com.mateusmatinato.trabalhotopicosnovo.model.Pedido;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdapterPedidos extends RecyclerView.Adapter<AdapterPedidos.MyViewHolder>{
    private List<Pedido> listaPedidos;
    private SQLiteDatabase bd;

    private DecimalFormat df2 = new DecimalFormat("#,##0.00");

    public AdapterPedidos(List<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Este método cria a View para serem exibidos os elementos

        // Converte o layout XML para uma View
        View listaItens = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_pedidos, viewGroup,false);

        return new MyViewHolder(listaItens);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Este método atualiza a visualização e mostra os elementos
        // i representa cada posição no RecyclerView
        // Como setamos a quantidade de elementos para lista.size()
        // i vai de 0 a lista.size

        myViewHolder.nomeRestaurante.setText(listaPedidos.get(i).getNomeRestaurante());
        myViewHolder.status.setText(listaPedidos.get(i).getStatus());
        myViewHolder.precoTotal.setText("R$ "+df2.format(listaPedidos.get(i).getPrecoTotal()));

        DateFormat df = new SimpleDateFormat ("dd");
        String diaString = df.format(listaPedidos.get(i).getData());
        myViewHolder.dia.setText(diaString);

        df = new SimpleDateFormat ("MMMM", new Locale("pt", "BR"));
        String mesString = df.format(listaPedidos.get(i).getData());
        mesString = mesString.substring(0,1).toUpperCase() + mesString.substring(1,3);
        myViewHolder.mes.setText(mesString);


    }

    @Override
    public int getItemCount() {
        // Retorna a quantidade de itens que serão exibidos
        return listaPedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        //cria elementos gráficos que estarão no modelo
        TextView nomeRestaurante;
        TextView status;
        TextView precoTotal;
        TextView dia;
        TextView mes;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //linka os elementos do layout aos atributos da classe
            nomeRestaurante = itemView.findViewById(R.id.tvTitulo);
            status = itemView.findViewById(R.id.tvItens);
            precoTotal = itemView.findViewById(R.id.tvPrecoPedido);
            dia = itemView.findViewById(R.id.tvDia);
            mes = itemView.findViewById(R.id.tvMes);
        }
    }
}
