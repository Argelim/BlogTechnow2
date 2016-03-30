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
 * Created by Tautvydas on 21/03/2016.
 */
public class compruebaNoticia extends AsyncTask<Void,Integer,String> {

    private Context context;
    private URL url;
    private String categoria,page;
    private String id;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private String TAG ="nombre";
    private Semaphore semaphore;
    private socketSSL socketSSL;

    public compruebaNoticia(Context context, SwipeRefreshLayout swipeRefreshLayout, String page,RecyclerView recyclerView,Semaphore semaphore) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.page = page;
        this.recyclerView=recyclerView;
        this.semaphore=semaphore;
        try {
            this.url = new URL("https://www.technow.es/blog/wp-json/wp/v2/posts?page="+page);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public compruebaNoticia(Context context, String categoria, String page, SwipeRefreshLayout swipeRefreshLayout,RecyclerView recyclerView,Semaphore semaphore) {
        this.context = context;
        this.categoria = categoria;
        this.page = page;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.recyclerView=recyclerView;
        this.semaphore=semaphore;
        try {
            this.url = new URL("https://www.technow.es/blog/wp-json/wp/v2/posts?filter[category_name]="+categoria+"&page="+page);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        socketSSL = new socketSSL(url,context);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (socketSSL.comunicacion()){
            JsonReader jsonReader = new JsonReader(socketSSL.getRd());
            obtenerID(jsonReader);
            socketSSL.cerrarSocket();
        }else{
            Toast.makeText(context,"Sin conexión",Toast.LENGTH_LONG).show();
        }
        return this.id;
    }

    @Override
    protected void onPostExecute(String s) {
        //si el id es igual entonces no necesitamos actualizar la informacion
        if (s.equals(MainActivity.getItems().get(0).getId())){
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(context,"No hay noticias nuevas",Toast.LENGTH_LONG).show();
        }else{
            MainActivity.reloadReciclador();
            if (categoria==null){
                MainActivity.getObjetos()[0]=new Paginacion(context,MainActivity.getItems(),recyclerView,"1",semaphore).execute();
            }else{
                MainActivity.getObjetos()[0]=new Categorias(context,MainActivity.getItems(),recyclerView,categoria,"1",semaphore).execute();
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        semaphore.release();
    }

    /**
     * Método que parsea el JSON
     * @param jsonReader el parser para poder leer el archivo JSON
     */
    public void obtenerID(JsonReader jsonReader){
        try {
            jsonReader.beginArray();
            jsonReader.beginObject();
            String nombre = jsonReader.nextName();
            switch (nombre){
                case "id":
                    id=jsonReader.nextString();
                    Log.d(TAG,id);
                    break;
                default:
                    jsonReader.skipValue();
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
