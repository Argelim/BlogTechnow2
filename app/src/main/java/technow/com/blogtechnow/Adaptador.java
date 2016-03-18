package technow.com.blogtechnow;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Technow i3 on 15/03/2016.
 */
public class Adaptador extends RecyclerView.Adapter<Adaptador.AnimeViewHolder> implements View.OnClickListener {

    private ArrayList<Noticias> items;
    private RecyclerView recyclerView;
    private Bundle bundle;
    private String TAG="NULL";

    @Override
    public void onClick(View v) {
        int posicion = recyclerView.getChildLayoutPosition(v);
        Intent intent = new Intent(v.getContext(),Noticia.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt("id", posicion);
        intent.putExtras(bundle);
        v.getContext().startActivity(intent);
    }

    public static class AnimeViewHolder extends RecyclerView.ViewHolder {

        public ImageView imagen;
        public TextView nombre;

        public AnimeViewHolder(View v) {
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            nombre = (TextView) v.findViewById(R.id.nombre);
        }
    }

    public Adaptador(ArrayList<Noticias> items,RecyclerView recyclerView) {
        this.items = items;
        this.recyclerView=recyclerView;
        bundle = new Bundle();
    }

    @Override
    public int getItemCount() {
        if(items!=null){
            return items.size();
        }else{
            return 0;
        }

    }

    @Override
    public AnimeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final int i1=i;
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view, viewGroup, false);
        v.setOnClickListener(this);
        return new AnimeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AnimeViewHolder viewHolder, int i) {
        if(i<items.size()){
            //agregamos la imagen en el texto
            Log.d(TAG,String.valueOf(items.size()));
            items.get(i).getImagen().into(viewHolder.imagen);
            viewHolder.nombre.setText(items.get(i).getTitulo());
        }
    }
}