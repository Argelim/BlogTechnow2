package comunicacion;

import android.content.Context;;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.JsonReader;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import technow.com.blogtechnow.MainActivity;
import technow.com.blogtechnow.Noticias;


/**
 * Created by Tautvydas on 16/03/2016.
 */
public class Paginacion extends AsyncTask<Void, Integer, Boolean> {

    private String id, titulo,fecha;
    private Context context;
    private String TAG = "DEBUG";
    private ArrayList<Noticias> noticias;
    private RecyclerView recyclerView;
    private Spanned spanned;
    private obtenerImagen obtenerImagen;
    private String contenido;
    private String pagina;
    private Semaphore semaphore;
    private socketSSL socketSSL;

    public Paginacion(Context context, ArrayList<Noticias> noticias, RecyclerView recyclerView, String pagina, Semaphore semaphore) {
        this.context = context;
        this.noticias = noticias;
        this.recyclerView = recyclerView;
        this.pagina = pagina;
        this.semaphore = semaphore;
    }

    @Override
    protected void onPreExecute() {
        socketSSL = new socketSSL(pagina, context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean b = true;
        if (socketSSL.comunicacion()) {
            leerNoticias(new JsonReader(socketSSL.getRd()));
            socketSSL.cerrarSocket();
        } else {
            b = false;
        }
        return b;
    }


    public void leerNoticias(JsonReader jsonReader) {

        try {
            jsonReader.beginArray();

            while (jsonReader.hasNext()) {

                jsonReader.beginObject();

                while (jsonReader.hasNext()) {

                    String nombre = jsonReader.nextName();

                    switch (nombre) {
                        case "id":
                            id = jsonReader.nextString();
                            break;
                        case "title":
                            titulo = Html.fromHtml(leerObjeto(jsonReader)).toString();
                            break;
                        case "content":
                            obtenerImagen = new obtenerImagen(context);
                            contenido = leerObjeto(jsonReader);
                            spanned = Html.fromHtml(contenido, obtenerImagen, null);
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
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyItemInserted(noticias.size() - 1);
            recyclerView.getAdapter().notifyDataSetChanged();
        }

    }

    public String leerObjeto(JsonReader jsonReader) {
        String content = "";
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.equals("rendered")) {
                    content = jsonReader.nextString();
                } else {
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
        if (aBoolean) {
            if (recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyItemInserted(noticias.size() - 1);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }else{
            Toast.makeText(context, "Sin conexión", Toast.LENGTH_LONG).show();
        }
        semaphore.release();
    }

}
