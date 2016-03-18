package technow.com.blogtechnow;

import android.graphics.drawable.Drawable;
import android.text.Spanned;

import com.squareup.picasso.RequestCreator;

import java.io.Serializable;

/**
 * Created by Technow i3 on 15/03/2016.
 */
public class Noticias{

    private RequestCreator image;
    private String titulo, descripcion,id;
    private Spanned spanned;
    private String contenido;

    public String getContenido() {
        return contenido;
    }

    public Noticias(String id, String titulo,String contenido,RequestCreator image) {
        this.titulo = titulo;
        this.contenido =contenido;
        this.id = id;
        this.image=image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Spanned getSpanned() {
        return spanned;
    }

    public void setSpanned(Spanned spanned) {
        this.spanned = spanned;
    }

    public RequestCreator getImagen() {
        return image;
    }

    public void setImagen(RequestCreator imagen) {
        this.image = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
