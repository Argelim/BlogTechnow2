package comunicacion;

import android.content.Context;

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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import technow.com.blogtechnow.R;

/**
 * Created by Technow i3 on 30/03/2016.
 */
public class CheckRed extends Thread {

    public static boolean bandera;
    private HttpsURLConnection connection;
    private KeyStore keyStore;
    private String algoritmo;
    private TrustManagerFactory trustManagerFactory;
    private SSLContext sslContext;
    private Context context;
    private URL url;

    public CheckRed(Context c){
        this.context = c;
        try {
            url = new URL("https://www.technow.es/blog/wp-json/wp/v2/posts?page=1");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public boolean  comunicacion(){
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
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            //realizamos la comunicacion
            connection = (HttpsURLConnection) url.openConnection();
            //le pasamos el contexto SSL para que pueda comprobar el certificado
            connection.setSSLSocketFactory(sslContext.getSocketFactory());
            //obtenemos los flujos
            connection.disconnect();
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

    @Override
    public void run() {
        bandera = comunicacion();
    }
}
