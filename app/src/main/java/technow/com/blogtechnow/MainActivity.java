package technow.com.blogtechnow;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import comunicacion.Categorias;
import comunicacion.Paginacion;
import comunicacion.compruebaNoticia;
import talkback.Talkback;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static RecyclerView recycler;
    private static Object [] objetos;
    private static ArrayList<Noticias> items;
    private RecyclerView.LayoutManager lManager;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int contador=5;
    private int contadorCurrentPage=1;
    private Adaptador adaptador;
    private String categoria;
    private SwipeRefreshLayout swipeRefreshLayout;
    private compruebaNoticia compruebaNoticia;
    private Semaphore semaphore;
    private String TAG ="estados";
    private int posicionScrol;
    private final int CHECK_TTS = 1;
    private Talkback talkback;


    public static ArrayList<Noticias> getItems() {
        return items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"create");
        //cargamos el toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        //cargamos el Drawer, menu de la derecha
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //recuperamos la lista de navegación y le añadimos un escuchador para que este atento en caso
        //de que se ha clicado en alguna opcion del menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //recuperamos el layout de la bara de navegación y le añadimos un escuchador cuando se cambie de estado
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayoutChange());

        items = new ArrayList<>();
        objetos = new Object[1];
        semaphore = new Semaphore(1);

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
        adaptador = new Adaptador(items, recycler);
        recycler.setAdapter(adaptador);
        //almacenamos el objeto en la primera posicion para el control de instancias
        objetos[0] = new Paginacion(getApplicationContext(), items, recycler, String.valueOf(contadorCurrentPage),semaphore).execute();
        categoria="Ultimas noticias";
        //agregamos un listener para que este atento para ir almacenando más noticias
        recycler.addOnScrollListener(new ScrollListener());

        //obtenemos la barra de carga, se lo pasaremos al asytask que carga las noticias
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refrescar);
        swipeRefreshLayout.setOnRefreshListener(new actualizacionMensaje(recycler));


        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, CHECK_TTS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CHECK_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                talkback = new Talkback(this);
            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            for (int i = 0; i < items.size(); i++) {
//                talkback.comunicar(items.get(i).getTitulo());
//            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "resume");
        recycler.getLayoutManager().scrollToPosition(posicionScrol);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "restart");
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, CHECK_TTS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        talkback.shutdown();
        Log.d(TAG, "Destroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(talkback != null){
            talkback.shutdown();
        }
        Log.d(TAG, "Pause");
    }


    public static Object[] getObjetos() {
        return objetos;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        contador=5;
        contadorCurrentPage=1;
        reloadReciclador();
        categoria =item.getTitle().toString();
        if(categoria.equals("Ultimas noticias")){
            objetos[0] = new Paginacion(getApplicationContext(), items, recycler, String.valueOf(contadorCurrentPage),semaphore).execute();
        }else{
            objetos[0] = new Categorias(getApplicationContext(),items,recycler,categoria,String.valueOf(contadorCurrentPage),semaphore).execute();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * clase que se encarga de escuchar los cambios del scrolling del reciclador
     */
    private class ScrollListener  extends  RecyclerView.OnScrollListener{
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            posicionScrol =layoutManager.findFirstCompletelyVisibleItemPosition();
            Log.d("POSICION", String.valueOf(posicionScrol + "," + layoutManager.findFirstVisibleItemPosition()));
            talkback.comunicar(items.get(layoutManager.findFirstVisibleItemPosition()).getTitulo());
            //position starts at 0
            if (posicionScrol >= contador) {
                if (objetos[0] instanceof Paginacion) {
                    contador += 10;
                    contadorCurrentPage++;
                    new Paginacion(getApplicationContext(), items, recycler, String.valueOf(contadorCurrentPage),semaphore).execute();
                } else {
                    contador += 10;
                    contadorCurrentPage++;
                    new Categorias(getApplicationContext(), items, recycler, categoria, String.valueOf(contadorCurrentPage),semaphore).execute();
                }
            }
        }
    }

    /**
     * clase que se encarga de cambair el estado de appBarlayout cuando cambie de estado
     */
    private class AppBarLayoutChange implements AppBarLayout.OnOffsetChangedListener {

        private boolean verifica = false;
        private int scrollRange = -1;

        public AppBarLayoutChange() {
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.ctlLayout);
        }

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            
            swipeRefreshLayout.setEnabled(verticalOffset==0);

            if (scrollRange == -1) {
                scrollRange = appBarLayout.getTotalScrollRange();
            }
            if (scrollRange + verticalOffset == 0) {
                collapsingToolbarLayout.setTitle(categoria);
                verifica = true;

            } else if (verifica) {
                collapsingToolbarLayout.setTitle("");
                verifica = false;

            }
        }
    }

    /**
     * clase que esta a la esucha cuando el usuario quiere actualizar las notcias
     */
    private class actualizacionMensaje implements SwipeRefreshLayout.OnRefreshListener {
        private RecyclerView recyclerView;

        public actualizacionMensaje(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        public void onRefresh() {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            //si esta en la posicion 0, es decir en la primera noticia
            if(layoutManager.findFirstVisibleItemPosition()==0){
                if (objetos[0] instanceof Paginacion){
                    new compruebaNoticia(getApplicationContext(),swipeRefreshLayout,"1",recyclerView,semaphore).execute();
                }else{
                    new compruebaNoticia(getApplicationContext(),categoria,"1",swipeRefreshLayout,recyclerView,semaphore).execute();
                }
            }else{
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    public static void reloadReciclador(){
        //matamos los hilos que pueden estén ejecutandose
        if(objetos[0] instanceof Paginacion){
            Paginacion p = (Paginacion) objetos[0];
            p.cancel(true);
        }else{
            Categorias c = (Categorias) objetos[0];
            c.cancel(true);
        }
        items.clear();
        recycler.getAdapter().notifyDataSetChanged();
    }
}
