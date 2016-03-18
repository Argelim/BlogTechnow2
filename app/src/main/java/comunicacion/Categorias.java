package comunicacion;


import android.content.Context;
import android.os.AsyncTask;
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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import technow.com.blogtechnow.Noticias;
import technow.com.blogtechnow.R;


/**
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
    private String TAG="DEBUG";
    private ArrayList<Noticias> noticias;
    private RecyclerView recyclerView;
    private Spanned spanned;
    private obtenerImagen obtenerImagen;
    private String categoria;
    private String page;
    private String contenido;

    public Categorias(Context context, ArrayList<Noticias> noticias, RecyclerView recyclerView, String categoria,String page) {
        this.context=context;
        this.noticias=noticias;
        this.recyclerView=recyclerView;
        this.categoria=categoria;
        this.page=page;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean bandera = true;
        try {
            //obtenemos el certificado
            InputStream entrada = context.getResources().openRawResource(R.raw.ca);
            //creamos la factoria de certificados
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
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

            JsonReader jsonReader = new JsonReader(rd);
            leerNoticias(jsonReader);

            rd.close();
            in.close();

        } catch (NoSuchAlgorithmException e) {
            bandera=false;
            e.printStackTrace();
        } catch (KeyStoreException e) {
            bandera=false;
            e.printStackTrace();
        } catch (KeyManagementException e) {
            bandera=false;
            e.printStackTrace();
        } catch (MalformedURLException e) {
            bandera=false;
            e.printStackTrace();
        } catch (IOException e) {
            bandera=false;
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return bandera;
    }


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
                noticias.add(new Noticias(id, titulo,contenido, obtenerImagen.getImagenCreator()));
                Log.d(TAG,String.valueOf(noticias.size()));
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
    }

}
