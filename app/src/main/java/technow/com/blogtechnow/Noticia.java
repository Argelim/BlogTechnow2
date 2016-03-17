package technow.com.blogtechnow;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Noticia extends AppCompatActivity {
    private TextView titulo,contenido;
    private ImageView imageView;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        titulo = (TextView) findViewById(R.id.textViewTitulo);
        contenido =(TextView) findViewById(R.id.textViewContenido);
        imageView = (ImageView) findViewById(R.id.imageViewNoticia);

        bundle=getIntent().getExtras();
        if(bundle!=null){
            int id = bundle.getInt("id");
            titulo.setText(String.valueOf(MainActivity.getItems().get(id).getTitulo()));
            MainActivity.getItems().get(id).getImagen().into(imageView);
            contenido.setText(MainActivity.getItems().get(id).getSpanned());
        }
    }

}
