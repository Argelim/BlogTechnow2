package technow.com.blogtechnow;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
<<<<<<< HEAD
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

=======
>>>>>>> refs/remotes/origin/master
import java.util.ArrayList;
import comunicacion.Categorias;
import comunicacion.Paginacion;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recycler;
    private RecyclerView.LayoutManager lManager;
    private String[] cuatrocosas = {"Cuatro", "Cosas"};
    private String TAG = "LISTENER";
    private static ArrayList<Noticias> items;
    private int contador=5;
    private int contadorCurrentPage=1;
<<<<<<< HEAD
    private ListView categorias;
=======
    private Adaptador adaptador;
    private Object [] objetos;
    private String categoria;
>>>>>>> refs/remotes/origin/master

    public static ArrayList<Noticias> getItems() {
        return items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        Menu m = navigationView.getMenu();
//        m.add("cuatro");
//        m.add("cosas");
        items = new ArrayList<>();
<<<<<<< HEAD
//        categorias = (ListView) findViewById(R.id.listview);
//        categorias.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list, cuatrocosas));


=======
        objetos = new Object[1];
>>>>>>> refs/remotes/origin/master

        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        adaptador = new Adaptador(items,recycler);
        recycler.setAdapter(adaptador);
        //almacenamos el objeto en la primera posicion para el control de instancias
        objetos[0]=new Paginacion(getApplicationContext(),items,recycler,String.valueOf(contadorCurrentPage)).execute();

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                Log.d(TAG, String.valueOf(layoutManager.findLastVisibleItemPosition()));
                //position starts at 0
                if (layoutManager.findFirstVisibleItemPosition() == contador) {
<<<<<<< HEAD
                    contador += 10;
                    contadorCurrentPage++;
                    new Paginacion(getApplicationContext(), items, recycler, String.valueOf(contadorCurrentPage)).execute();
                }
            }
        });

        new Paginacion(getApplicationContext(),items,recycler,String.valueOf(contadorCurrentPage)).execute();
=======
                    if(objetos[0] instanceof Paginacion){
                        contador += 10;
                        contadorCurrentPage++;
                        new Paginacion(getApplicationContext(), items, recycler, String.valueOf(contadorCurrentPage)).execute();
                    }else{
                        contador += 10;
                        contadorCurrentPage++;
                        new Categorias(getApplicationContext(),items,recycler,categoria,String.valueOf(contadorCurrentPage)).execute();
                    }
                }
            }
        });
>>>>>>> refs/remotes/origin/master
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
<<<<<<< HEAD
//        int id = item.getItemId();
//
//        if (id == R.id.accesibilidad) {
//            ArrayList<Noticias> not = new ArrayList<>();
//            recycler.setAdapter(new Adaptador(not, recycler));
//            new Categorias(getApplicationContext(),not,recycler,item.getTitle().toString()).execute();
//        } else if (id == R.id.almacenamiento) {
//            new Categorias(getApplicationContext(),items,recycler,"almacenamiento").execute();
//        } else if (id == R.id.app) {
//            new Categorias(getApplicationContext(),items,recycler,"app").execute();
//        } else if (id == R.id.apple) {
//            new Categorias(getApplicationContext(),items,recycler,"apple").execute();
//        } else if (id == R.id.inicio){
//            contador = 5;
//            contadorCurrentPage = 1;
//            new Paginacion(getApplicationContext(), items, recycler, String.valueOf(contadorCurrentPage)).execute();
//        }
//        contador = 5;
//        contadorCurrentPage = 1;
//        if(item.getTitle().equals("inicio")){
//            new Paginacion(getApplicationContext(), items, recycler, String.valueOf(contadorCurrentPage)).execute();
//        }else{
//            new Categorias(getApplicationContext(),items,recycler,item.getTitle().toString()).execute();
//        }
=======
        int id = item.getItemId();
        contador=5;
        contadorCurrentPage=1;
        if (id == R.id.nav_camera) {
            items.clear();
            recycler.getAdapter().notifyDataSetChanged();
            categoria ="accesibilidad";
            objetos[0]=new Categorias(getApplicationContext(),items,recycler,"accesibilidad",String.valueOf(contadorCurrentPage)).execute();
        } else if (id == R.id.nav_gallery) {
            items.clear();
            recycler.getAdapter().notifyDataSetChanged();
            categoria ="almacenamiento";
            objetos[0]=new Categorias(getApplicationContext(),items,recycler,"almacenamiento",String.valueOf(contadorCurrentPage)).execute();
        } else if (id == R.id.nav_slideshow) {
            items.clear();
            recycler.getAdapter().notifyDataSetChanged();
            categoria ="app";
            objetos[0]=new Categorias(getApplicationContext(),items,recycler,"app",String.valueOf(contadorCurrentPage)).execute();
        } else if (id == R.id.nav_manage) {
            items.clear();
            categoria ="apple";
            recycler.getAdapter().notifyDataSetChanged();
            objetos[0]=new Categorias(getApplicationContext(),items,recycler,"apple",String.valueOf(contadorCurrentPage)).execute();
        }

>>>>>>> refs/remotes/origin/master
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
