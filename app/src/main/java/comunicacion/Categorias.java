package comunicacion;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.JsonReader;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;


import technow.com.blogtechnow.Noticias;

/**
 * clase que se encarga de realiza la comunicacion
 * con el servidor y recibir el contenido de JSON.
 * Lo parsea y obtiene los datos que serán cargados en
 * el Objeto Noticas
 * Created by Tautvydas on 16/03/2016.
 */
public class Categorias extends AsyncTask<Void,Integer,Boolean>{

    private String id,titulo,fecha;
    private Context context;
    private ArrayList<Noticias> noticias;
    private RecyclerView recyclerView;
    private Spanned spanned;
    private obtenerImagen obtenerImagen;
    private String categoria;
    private String page;
    private String contenido;
    private Semaphore semaphore;
    private socketSSL socketSSL;

    /**
     * Constructor que recibe 5 parametros
     * @param context contexto
     * @param noticias lista dinámica de las noticias
     * @param recyclerView reciclador que será encargado de ir añadiendo cada noticia
     * @param categoria el tipo de categoria que queremos ver
     * @param page número de página
     */
    public Categorias(Context context, ArrayList<Noticias> noticias, RecyclerView recyclerView, String categoria,String page,Semaphore semaphore) {
        this.context=context;
        this.noticias=noticias;
        this.recyclerView=recyclerView;
        this.categoria=categoria;
        this.page=page;
        this.semaphore=semaphore;
    }

    @Override
    protected void onPreExecute() {
        socketSSL = new socketSSL(categoria,page,context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean bandera = true;
        //si se ha realizado la comunicacion correctamente
        try {
            semaphore.acquire();
            if(socketSSL.comunicacion()){
                JsonReader jsonReader = new JsonReader(socketSSL.getRd());
                leerNoticias(jsonReader);
                socketSSL.cerrarSocket();
            }else{
                Toast.makeText(context, "Sin conexión", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            bandera=false;
            e.printStackTrace();
        }
        return bandera;
    }

    /**
     * Método que parsea el JSON
     * @param jsonReader el parser para poder leer el archivo JSON
     */
    public void leerNoticias(JsonReader jsonReader){
        try {
            jsonReader.beginArray();

            while (jsonReader.hasNext()){

                jsonReader.beginObject();

                while (jsonReader.hasNext()){

                    String nombre = jsonReader.nextName();

                    switch (nombre){
                        case "id":
                            id=jsonReader.nextString();
                            break;
                        case "title":
                            titulo=Html.fromHtml(leerObjeto(jsonReader)).toString();
                            break;
                        case "content":
                            obtenerImagen =new obtenerImagen(context);
                            contenido = leerObjeto(jsonReader);
                            spanned = Html.fromHtml(contenido,obtenerImagen,null);
                            break;
                        case "date":
                            fecha=jsonReader.nextString();
                            break;
                        default:
                            jsonReader.skipValue();
                            break;
                    }
                }
                noticias.add(new Noticias(id, titulo, contenido, obtenerImagen.getImagenCreator(),fecha));
                publishProgress();
                jsonReader.endObject();
            }
            jsonReader.endArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(recyclerView.getAdapter()!=null){
            recyclerView.getAdapter().notifyItemInserted(noticias.size()-1);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     * método que lee el contenido dentro de un objeto
     * @param jsonReader Parser JSON
     * @return cadena leida
     */
    public String leerObjeto(JsonReader jsonReader){
        String content="";
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()){
                String name = jsonReader.nextName();
                if(name.equals("rendered")){
                    content=jsonReader.nextString();
                }else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if(aBoolean){
            if(recyclerView.getAdapter()!=null){
                recyclerView.getAdapter().notifyItemInserted(noticias.size()-1);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
        semaphore.release();
    }
}
