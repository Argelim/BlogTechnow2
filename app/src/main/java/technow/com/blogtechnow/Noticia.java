package technow.com.blogtechnow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

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
        vista.getSettings().setJavaScriptEnabled(true);
        vista.getSettings().setDisplayZoomControls(true);

        bundle=getIntent().getExtras();
        if(bundle!=null){
            int id = bundle.getInt("id");
            vista.loadData(MainActivity.getItems().get(id).getContenido(), "text/html; charset=utf-8", "utf-8");

        }
    }
}
