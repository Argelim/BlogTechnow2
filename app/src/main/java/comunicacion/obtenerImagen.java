package comunicacion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;


import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.net.URLConnection;

/**
 * Created by Tautvydas on 16/03/2016.
 */
public class obtenerImagen implements Html.ImageGetter {

    private int contador=0;
    private Drawable drawable;
    private URLConnection connection;
    private Bitmap bitmap;
    private BitmapFactory bitmapFactory;
    private BitmapFactory.Options options;
    private Context context;
    private RequestCreator imagenCreator;

    public obtenerImagen(Context context) {
        this.context = context;
    }

    @Override
    public Drawable getDrawable(String source) {
        if (contador==0){
            imagenCreator = Picasso.with(context).load(source);
            contador++;
        }
        return null;
    }

    public RequestCreator getImagenCreator() {
        return imagenCreator;
    }

    public void setImagenCreator(RequestCreator imagenCreator) {
        this.imagenCreator = imagenCreator;
    }
}
