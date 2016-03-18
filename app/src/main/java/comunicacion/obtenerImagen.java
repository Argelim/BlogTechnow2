package comunicacion;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;


import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.net.URLConnection;

import technow.com.blogtechnow.R;

/**
 * Created by Tautvydas on 16/03/2016.
 */
public class obtenerImagen implements Html.ImageGetter {

    private int contador=0;
    private Context context;
    private RequestCreator imagenCreator;
    private String TAG ="IMG";

    public obtenerImagen(Context context) {
        this.context = context;
    }

    @Override
    public Drawable getDrawable(String source) {
        if (contador==0){
            Log.d(TAG,source);
            imagenCreator = Picasso.with(context).load(source);
            contador++;
        }
        return null;
    }

    public RequestCreator getImagenCreator() {
        if(imagenCreator!=null){
            return imagenCreator;
        }else{
            return Picasso.with(context).load(R.drawable.noimagen);
        }

    }

    public void setImagenCreator(RequestCreator imagenCreator) {
        this.imagenCreator = imagenCreator;
    }
}
