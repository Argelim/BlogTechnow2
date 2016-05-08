package comunicacion;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import technow.com.blogtechnow.R;

/**
 * Clase que implementa Html.ImageGetter que se encarga de obtener las etiquetas
 * <img> para extraer las URL´s de las imagenes
 * Created by Tautvydas on 16/03/2016.
 */
public class obtenerImagen implements Html.ImageGetter {

    private int contador=0;
    private Context context;
    private RequestCreator imagenCreator;
    private String TAG ="IMG";

    /**
     * Constructor que recibe el contexto
     * @param context contexto
     */
    public obtenerImagen(Context context) {
        this.context = context;
    }

    /**
     * Método es invocado cuando encuentra una etiqueta <img></img> para
     * extraer la URL de la imgen, y despues es cargado con picasso. Incremento el contador
     * porque solo me interesa la primera imagen las demás que las ignore
     * @param source contenido
     * @return en este caso null
     */
    @Override
    public Drawable getDrawable(String source) {
        if (contador==0){
            Log.d(TAG,source);
            imagenCreator = Picasso.with(context).load(source);
            contador++;
        }
        return null;
    }

    /**
     * en caso de no encontrar la imagen cargamos una por defecto
     * @return imagen por defecto
     */
    public RequestCreator getImagenCreator() {
        if(imagenCreator!=null){
            return imagenCreator;
        }else{
            return Picasso.with(context).load(R.drawable.noimagen);
        }

    }

}
