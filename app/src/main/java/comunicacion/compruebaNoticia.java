package comunicacion;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Semaphore;


import technow.com.blogtechnow.MainActivity;

/**
 * Clase que se encarga de comprobar de que si existen noticas nuevas
 * en la categoria que estes.
 * Created by Tautvydas on 21/03/2016.
 */
public class compruebaNoticia extends AsyncTask<Void, Integer, Boolean> {

    private Context context;
    private URL url;
    private String categoria;
    private String id = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private String TAG = "nombre";
    private Semaphore semaphore;
    private socketSSL socketSSL;

    /**
     * Constructor que recibe por parámetro
     * @param context contexto
     * @param swipeRefreshLayout componente UI que se activa al actualizar las noticias
     * @param page página la que queremos actualizar
     * @param recyclerView reciclador de vistas
     * @param semaphore control de semaforo
     */
    public compruebaNoticia(Context context, SwipeRefreshLayout swipeRefreshLayout, String page, RecyclerView recyclerView, Semaphore semaphore) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.recyclerView = recyclerView;
        this.semaphore = semaphore;
        try {
            this.url = new URL("https://www.technow.es/blog/wp-json/wp/v2/posts?page=" + page);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor que recibe por parámetro
     * @param context contexto en el que trabaja
     * @param categoria categoria que queremos actualizar de la notica
     * @param page página que queremos actualizar dentro de una categoria
     * @param swipeRefreshLayout componente UI que se activa al actualizar las noticias
     * @param recyclerView reciclador de vistas
     * @param semaphore control de semaforo
     */
    public compruebaNoticia(Context context, String categoria, String page, SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView, Semaphore semaphore) {
        this.context = context;
        this.categoria = categoria;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.recyclerView = recyclerView;
        this.semaphore = semaphore;
        try {
            this.url = new URL("https://www.technow.es/blog/wp-json/wp/v2/posts?filter[category_name]=" + categoria + "&page=" + page);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        socketSSL = new socketSSL(url, context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean bandera = true;
        if (socketSSL.comunicacion()) {
            JsonReader jsonReader = new JsonReader(socketSSL.getRd());
            obtenerID(jsonReader);
            socketSSL.cerrarSocket();
        } else {
            bandera = false;
        }
        return bandera;
    }

    /**
     * Comprobamos el id de la noticia, si no hay noticias nuevas
     * le indicamos mediante un toast al usuario, en cambio volvenmos a
     * acualizar la información con el método reload()
     * @param b si se pudo realizar la comunicación true si no false
     */
    @Override
    protected void onPostExecute(Boolean b) {
        //si el id es igual entonces no necesitamos actualizar la informacion
        Log.d("COMPROBACION", String.valueOf(b));
        if (b) {
            if(!MainActivity.getItems().isEmpty()) {
                Log.d("ID", String.valueOf(id + ", " + MainActivity.getItems().get(0).getId()));
                if (id.equals(MainActivity.getItems().get(0).getId())) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(context, "No hay noticias nuevas", Toast.LENGTH_LONG).show();
                } else {
                    reload();
                }
            }else{
                reload();
            }
        } else {
            Toast.makeText(context, "Sin conexión", Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }
        semaphore.release();
    }

    /**
     * Método que parsea el JSON
     *
     * @param jsonReader el parser para poder leer el archivo JSON
     */
    public void obtenerID(JsonReader jsonReader) {
        try {
            jsonReader.beginArray();
            if(jsonReader.hasNext()){
                jsonReader.beginObject();
                while(jsonReader.hasNext()){
                    String nombre = jsonReader.nextName();
                    switch (nombre) {
                        case "id":
                            id = jsonReader.nextString();
                            Log.d(TAG, id);
                            break;
                        default:
                            jsonReader.skipValue();
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que recarga de nuevo el listado de noticias
     */
    private void reload(){
        MainActivity.reloadReciclador();
        if (categoria == null) {
            MainActivity.getObjetos()[0] = new Paginacion(context, MainActivity.getItems(), recyclerView, "1", semaphore).execute();
        } else {
            MainActivity.getObjetos()[0] = new Categorias(context, MainActivity.getItems(), recyclerView, categoria, "1", semaphore).execute();
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
