package comunicacion;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import technow.com.blogtechnow.Noticias;
import technow.com.blogtechnow.R;

/**
 * clase que se encarga de realiza la comunicacion
 * con el servidor y recibir el contenido de JSON.
 * Lo parsea y obtiene los datos que serán cargados en
 * el Objeto Noticas
 * Created by Tautvydas on 16/03/2016.
 */
public class Categorias extends AsyncTask<Void,Integer,Boolean>{

    private HttpsURLConnection connection;
    private URL url;
    private String algoritmo,id,titulo;
    private SSLContext sslContext;
    private InputStream in;
    private InputStreamReader rd;
    private KeyStore keyStore;
    private TrustManagerFactory trustManagerFactory;
    private Context context;
    private ArrayList<Noticias> noticias;
    private RecyclerView recyclerView;
    private Spanned spanned;
    private obtenerImagen obtenerImagen;
    private String categoria;
    private String page;
    private String contenido;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Semaphore semaphore;

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

    /**
     * Constructor que recibe 5 parametros
     * @param context contexto
     * @param noticias lista dinámica de las noticias
     * @param recyclerView reciclador que será encargado de ir añadiendo cada noticia
     * @param categoria el tipo de categoria que queremos ver
     * @param page número de página
     * @param swipeRefreshLayout barra de carga para actualizar la información
     */
    public Categorias(Context context, ArrayList<Noticias> noticias, RecyclerView recyclerView, String categoria, String page, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.noticias = noticias;
        this.recyclerView = recyclerView;
        this.categoria = categoria;
        this.page = page;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.semaphore=semaphore;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean bandera = true;
        //si se ha realizado la comunicacion correctamente
        try {
            semaphore.acquire();
            if(comunicacion()){
                try {
                    JsonReader jsonReader = new JsonReader(rd);
                    leerNoticias(jsonReader);
                    rd.close();
                    in.close();
                } catch (IOException e) {
                    bandera=false;
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
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
                        default:
                            jsonReader.skipValue();
                            break;
                    }
                }
                noticias.add(new Noticias(id, titulo, contenido, obtenerImagen.getImagenCreator()));
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
        }if(swipeRefreshLayout!=null){
            swipeRefreshLayout.setRefreshing(false);
        }
        semaphore.release();
    }

    /**
     * Método que realiza la comunicación con el servidor web
     * @return true|false si la comunicacion se pudo o no establecer
     */
    private boolean  comunicacion(){
        boolean b=true;
        //obtenemos el certificado
        InputStream entrada = context.getResources().openRawResource(R.raw.ca);
        //creamos la factoria de certificados
        CertificateFactory certificateFactory = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate ca;
            //generamos el certificado con los datos leidos de la entrada
            ca = certificateFactory.generateCertificate(entrada);
            entrada.close();
            //obtenemos el almacen de llaves y añadimos el certificado
            String key = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(key);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            //obtenemos el algoritmo utilizado
            algoritmo = KeyManagerFactory.getDefaultAlgorithm();
            //obtenemos la factoria de certificados con el algoritmo
            trustManagerFactory = TrustManagerFactory.getInstance(algoritmo);
            //iniciamos el certificado con el almacen de llaves
            trustManagerFactory.init(keyStore);
            //obtenemos el contexto SSL
            sslContext = SSLContext.getInstance("TLS");
            //instanciamos el contexto SSL
            sslContext.init(null,trustManagerFactory.getTrustManagers(),null);
            //obtenemos la URL de la página con el certificado
            url = new URL("https://www.technow.es/blog/wp-json/wp/v2/posts?filter[category_name]="+categoria+"&page="+page);
            //realizamos la comunicacion
            connection = (HttpsURLConnection) url.openConnection();
            //le pasamos el contexto SSL para que pueda comprobar el certificado
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            //obtenemos los flujos
            in = connection.getInputStream();
            rd = new InputStreamReader(in);
        } catch (CertificateException e) {
            e.printStackTrace();
            b=false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            b=false;
        } catch (KeyManagementException e) {
            e.printStackTrace();
            b=false;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            b=false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            b=false;
        } catch (IOException e) {
            e.printStackTrace();
            b=false;
        }
        return b;
    }
}
