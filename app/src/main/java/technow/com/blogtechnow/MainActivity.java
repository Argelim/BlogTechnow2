package technow.com.blogtechnow;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;

import comunicacion.Categorias;
import comunicacion.CheckRed;
import comunicacion.Paginacion;
import comunicacion.compruebaNoticia;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static RecyclerView recycler;
    private static Object[] objetos;
    private static ArrayList<Noticias> items;
    private RecyclerView.LayoutManager lManager;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int contador = 5;
    private int contadorCurrentPage = 1;
    private Adaptador adaptador;
    private String categoria;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Semaphore semaphore;
    private String TAG = "estados";
    private int posicionScrol;


    public static ArrayList<Noticias> getItems() {
        return items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "create");
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
        objetos[0] = new Paginacion(getApplicationContext(), items, recycler, String.valueOf(contadorCurrentPage), semaphore).execute();
        categoria = "Ultimas noticias";
        //agregamos un listener para que este atento para ir almacenando más noticias
        recycler.addOnScrollListener(new ScrollListener());

        //obtenemos la barra de carga, se lo pasaremos al asytask que carga las noticias
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refrescar);
        swipeRefreshLayout.setOnRefreshListener(new actualizacionMensaje(recycler));


    }

    /**
     * Método que se ejecuta cuando se ha pulsado los botones si en caso
     * de que tenga teclado, en este caso nos interesa el de arriba y hacia abajo
     * para el paso de noticias con el talckback
     * @param keyCode código del bóton pulsado
     * @param event evento ejecutado
     * @return true o false si ejecutamos la acción
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        if (keyCode == 19) {
            if (posicionScrol <= 0) {
                return false;
            }
            posicionScrol -= 1;
            recycler.getLayoutManager().scrollToPosition(posicionScrol);
        } else if (keyCode == 20) {
            if (posicionScrol >= items.size()) {
                return false;
            }
            posicionScrol += 1;
            recycler.getLayoutManager().scrollToPosition(posicionScrol);
        }
        return true;
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
//        getMenuInflater().inflate(R.menu.main, menu);
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



    public static Object[] getObjetos() {
        return objetos;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        contador = 5;
        contadorCurrentPage = 1;
        CheckRed red = new CheckRed(getApplicationContext());
        red.start();
        try {
            red.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (red.bandera) {
            reloadReciclador();
            categoria = item.getTitle().toString();
            if (categoria.contains(" ")) {
                if (categoria.equals("Redes e Internet")) {
                    categoria = "redes%20internet";
                } else if (categoria.equals("Fotografía y Video")) {
                    categoria = "fotografia%20video";
                } else {
                    StringTokenizer st = new StringTokenizer(categoria);
                    String aux = "";
                    while (st.hasMoreTokens()) {
                        aux += st.nextToken() + "%20";
                        Log.d("TOKENS", aux);
                    }
                    categoria = aux;
                }
            }
            if (categoria.equals("Ultimas noticias")) {
                objetos[0] = new Paginacion(getApplicationContext(), items, recycler, String.valueOf(contadorCurrentPage), semaphore).execute();
            } else {
                objetos[0] = new Categorias(getApplicationContext(), items, recycler, categoria, String.valueOf(contadorCurrentPage), semaphore).execute();
            }
            categoria = item.getTitle().toString();
        } else {
            Toast.makeText(getApplicationContext(), "Sin conexión", Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * clase que se encarga de escuchar los cambios del scrolling del reciclador
     */
    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            posicionScrol = layoutManager.findLastCompletelyVisibleItemPosition();

            Log.d("POSICION", String.valueOf(posicionScrol + "," + layoutManager.findFirstVisibleItemPosition()));
            //position starts at 0
            if (posicionScrol >= contador) {
                if (objetos[0] instanceof Paginacion) {
                    contador += 10;
                    contadorCurrentPage++;
                    new Paginacion(getApplicationContext(), items, recycler, String.valueOf(contadorCurrentPage), semaphore).execute();
                } else {
                    contador += 10;
                    contadorCurrentPage++;
                    new Categorias(getApplicationContext(), items, recycler, categoria, String.valueOf(contadorCurrentPage), semaphore).execute();
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

            swipeRefreshLayout.setEnabled(verticalOffset == 0);

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
            CheckRed red = new CheckRed(getApplicationContext());
            red.start();
            try {
                red.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (red.bandera) {
                if (objetos[0] instanceof Paginacion) {
                    new compruebaNoticia(getApplicationContext(), swipeRefreshLayout, "1", recyclerView, semaphore).execute();
                } else {
                    new compruebaNoticia(getApplicationContext(), categoria, "1", swipeRefreshLayout, recyclerView, semaphore).execute();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Sin conexión", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Método que se encarga de matar los hilos en ejecución
     * y limpiar la lista de noticias
     */
    public static void reloadReciclador() {
        //matamos los hilos que pueden estén ejecutandose
        if (objetos[0] instanceof Paginacion) {
            Paginacion p = (Paginacion) objetos[0];
            p.cancel(true);
        } else {
            Categorias c = (Categorias) objetos[0];
            c.cancel(true);
        }
        items.clear();
        recycler.getAdapter().notifyDataSetChanged();
    }
}
