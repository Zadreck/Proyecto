package com.equipo.soundbox.modelo;

/**
 * Representa un álbum en formato físico (CD, Vinilo, Cassette).
 *
 * @author José y Ruben
 * @version 1.0
 */
public class AlbumFisico extends Album {

    private String formato;
    private int numDiscos;

    /**
     * Constructor de AlbumFisico.
     *
     * @param titulo    título del álbum
     * @param artista   nombre del artista
     * @param anio      año de publicación
     * @param formato   formato físico (CD, Vinilo, Cassette)
     * @param numDiscos número de discos
     * @throws IllegalArgumentException si el número de discos es menor que 1
     */
    public AlbumFisico(String titulo, String artista, int anio,
                       String formato, int numDiscos) {
        super(titulo, artista, anio);
        if (numDiscos < 1)
            throw new IllegalArgumentException("Debe tener al menos 1 disco");
        this.formato = formato;
        this.numDiscos = numDiscos;
    }

    /**
     * Devuelve el tipo de álbum.
     *
     * @return "Físico"
     */
    @Override
    public String getTipo() { return "Físico"; }

    /**
     * Devuelve la descripción con formato y número de discos.
     *
     * @return descripción del álbum físico
     */
    @Override
    public String getDescripcion() {
        return formato + " · " + numDiscos + " disco(s)";
    }

    /**
     * Simula la reproducción del álbum físico por consola.
     */
    @Override
    public void reproducir() {
        System.out.println("▶ Reproduciendo " + getTitulo()
                + " [" + formato + "]");
    }

    /**
     * Devuelve la duración total estimada en segundos.
     *
     * @return duración en segundos (2400 por disco)
     */
    @Override
    public int getDuracionTotal() { return numDiscos * 2400; }

    /**
     * Devuelve el formato del álbum.
     *
     * @return formato físico
     */
    public String getFormato() { return formato; }

    /**
     * Devuelve el número de discos.
     *
     * @return número de discos
     */
    public int getNumDiscos() { return numDiscos; }
}