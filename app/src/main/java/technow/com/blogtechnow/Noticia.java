package technow.com.blogtechnow;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
    private String titulo, contenido;

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
            contenido = MainActivity.getItems().get(id).getContenido();
            titulo = MainActivity.getItems().get(id).getTitulo();

            vista.loadData(getHTML(titulo, contenido), "text/html; charset=utf-8", "utf-8");
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


    private String getHTML(String titulo, String contenido){
        String html =   "<html>" +
                            "<head>" +
                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                                "<style>" +
                                    "img{display: inline;height: auto;max-width: 100%;}" +
                                    "iframe{display: inline;height: auto;max-width: 100%;}" +
                                    "h1{color: #607D8B;}" +
                                    "a{color: #dd9933;}" +
                                 "</style>" +
                            "</head>" +
                            "<body>" +
                                "<h1>"+titulo+"</h1>"
                                + contenido +
                            "</body>" +
                        "</html>";
        return html;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
