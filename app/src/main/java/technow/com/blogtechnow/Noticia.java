package technow.com.blogtechnow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

/**
 * clase que se encarga de mostrara las noticias en una
 * actividad nueva
 */
public class Noticia extends AppCompatActivity {

    private Bundle bundle;
    private WebView vista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vista = (WebView) findViewById(R.id.vistaWeb);
        //en caso de que si hay algo en el bundle
        bundle=getIntent().getExtras();
        if(bundle!=null){
            //obtenemos el id que será la posicion del array de items para mostrar la información
            int id = bundle.getInt("id");
            //cargamos los datos de la noticia con la id que nos ha pasado
            vista.loadData(MainActivity.getItems().get(id).getContenido(), "text/html; charset=utf-8", "utf-8");
            /*
            Realizamos todas las configuraciones para que se adapte a la
            pantalla del móvil la vista
            */
            vista.getSettings().setJavaScriptEnabled(true);
            vista.getSettings().setDisplayZoomControls(true);
            vista.getSettings().setLoadWithOverviewMode(true);
            vista.getSettings().setUseWideViewPort(true);
        }
    }
}
