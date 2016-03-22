package comunicacion;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

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
import java.util.concurrent.Semaphore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import technow.com.blogtechnow.MainActivity;
import technow.com.blogtechnow.R;

/**
 * Created by Tautvydas on 21/03/2016.
 */
public class compruebaNoticia extends AsyncTask<Void,Integer,String> {

    private Context context;
    private KeyStore keyStore;
    private String algoritmo;
    private TrustManagerFactory trustManagerFactory;
    private SSLContext sslContext;
    private URL url;
    private HttpsURLConnection connection;
    private InputStream in;
    private InputStreamReader rd;
    private String categoria,page;
    private String id;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private String TAG ="nombre";
    private Semaphore semaphore;

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
    protected String doInBackground(Void... params) {
        if (comunicacion()){
            JsonReader jsonReader = new JsonReader(rd);
            obtenerID(jsonReader);
            try {
                in.close();
                rd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
