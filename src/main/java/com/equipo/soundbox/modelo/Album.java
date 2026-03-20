package com.equipo.soundbox.modelo;

/**
 * Superclase abstracta que representa un álbum musical.
 * Implementa las interfaces Reproducible y Puntuable.
 *
 * @author José y Ruben
 * @version 1.0
 */
public abstract class Album implements Reproducible, Puntuable {

    private String titulo;
    private String artista;
    private int año;
    private String[] etiquetas;
    private double puntuacion;

    /**
     * Constructor de Album con validaciones.
     *
     * @param titulo  título del álbum
     * @param artista nombre del artista
     * @param año    año de publicación
     * @throws IllegalArgumentException si algún campo no es válido
     */
    public Album(String titulo, String artista, int año) {
        if (titulo == null || titulo.isBlank())
            throw new IllegalArgumentException("El título no puede estar vacío");
        if (artista == null || artista.isBlank())
            throw new IllegalArgumentException("El artista no puede estar vacío");
        if (año < 1900 || año > 2025)
            throw new IllegalArgumentException("Año no válido");
        this.titulo = titulo;
        this.artista = artista;
        this.año = año;
        this.etiquetas = new String[0];
        this.puntuacion = 0.0;
    }

    /**
     * Devuelve el tipo de álbum.
     *
     * @return tipo como String
     */
    public abstract String getTipo();

    /**
     * Devuelve una descripción con los datos propios del álbum.
     *
     * @return descripción del álbum
     */
    public abstract String getDescripcion();

    /**
     * Establece la puntuación del álbum.
     *
     * @param puntuacion valor entre 0.0 y 10.0
     * @throws IllegalArgumentException si está fuera de rango
     */
    @Override
    public void setPuntuacion(double puntuacion) {
        if (puntuacion < 0.0 || puntuacion > 10.0)
            throw new IllegalArgumentException("Puntuación debe estar entre 0 y 10");
        this.puntuacion = puntuacion;
    }

    /**
     * Devuelve la puntuación del álbum.
     *
     * @return puntuación actual
     */
    @Override
    public double getPuntuacion() {
        return puntuacion;
    }

    /**
     * Devuelve el título del álbum.
     *
     * @return título
     */
    public String getTitulo() { return titulo; }

    /**
     * Devuelve el artista del álbum.
     *
     * @return artista
     */
    public String getArtista() { return artista; }

    /**
     * Devuelve el año del álbum.
     *
     * @return año
     */
    public int getAño() { return año; }

    /**
     * Devuelve las etiquetas del álbum.
     *
     * @return array de etiquetas
     */
    public String[] getEtiquetas() { return etiquetas; }

    /**
     * Establece las etiquetas del álbum.
     *
     * @param etiquetas array de etiquetas
     */
    public void setEtiquetas(String[] etiquetas) {
        this.etiquetas = etiquetas;
    }

    /**
     * Representación en texto del álbum.
     *
     * @return cadena con los datos principales
     */
    @Override
    public String toString() {
        return "[" + getTipo() + "] " + titulo
                + " - " + artista
                + " (" + año + ")"
                + " | " + getDescripcion()
                + " | Puntuación: " + puntuacion;
    }
}