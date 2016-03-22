package technow.com.blogtechnow;

import com.squareup.picasso.RequestCreator;

/**
 * Created by Technow i3 on 15/03/2016.
 */
public class Noticias{

    private RequestCreator image;
    private String titulo, descripcion,id;
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

    public RequestCreator getImagen() {
        return image;
    }

    public String getTitulo() {
        return titulo;
    }

}
