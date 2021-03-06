package technow.com.blogtechnow;

import com.squareup.picasso.RequestCreator;

/**
 * Clase que contiene toda la información necesaria
 * de una noticia
 * Created by Technow i3 on 15/03/2016.
 */
public class Noticias{

    private RequestCreator image;
    private String titulo,id,fecha;
    private String contenido;

    public Noticias(String id, String titulo,String contenido,RequestCreator image,String fecha) {
        this.titulo = titulo;
        this.contenido =contenido;
        this.id = id;
        this.image=image;
        this.fecha=fecha;
    }

    public String getContenido() {
        return contenido;
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

    public String getFecha() {
        return fecha;
    }
}
