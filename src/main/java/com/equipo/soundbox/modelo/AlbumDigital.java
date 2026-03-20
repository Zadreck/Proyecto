package com.equipo.soundbox.modelo;

/**
 * Representa un álbum en formato digital.
 *
 * @author José y Ruben
 * @version 1.0
 */
public class AlbumDigital extends Album {

    private String plataforma;
    private int bitrate;

    /**
     * Constructor de AlbumDigital.
     *
     * @param titulo     título del álbum
     * @param artista    nombre del artista
     * @param anio       año de publicación
     * @param plataforma plataforma de distribución
     * @param bitrate    calidad de audio en kbps
     * @throws IllegalArgumentException si el bitrate no es válido
     */
    public AlbumDigital(String titulo, String artista, int anio,
                        String plataforma, int bitrate) {
        super(titulo, artista, anio);
        if (bitrate != 128 && bitrate != 192 && bitrate != 256 && bitrate != 320)
            throw new IllegalArgumentException("Bitrate no válido");
        this.plataforma = plataforma;
        this.bitrate = bitrate;
    }

    /**
     * Devuelve el tipo de álbum.
     *
     * @return "Digital"
     */
    @Override
    public String getTipo() { return "Digital"; }

    /**
     * Devuelve la descripción con plataforma y bitrate.
     *
     * @return descripción del álbum digital
     */
    @Override
    public String getDescripcion() {
        return plataforma + " · " + bitrate + " kbps";
    }

    /**
     * Simula la reproducción del álbum digital por consola.
     */
    @Override
    public void reproducir() {
        System.out.println("▶ Reproduciendo " + getTitulo()
                + " en " + plataforma
                + " [" + bitrate + " kbps]");
    }

    /**
     * Devuelve la duración total estimada en segundos.
     *
     * @return duración en segundos
     */
    @Override
    public int getDuracionTotal() { return 2700; }

    /**
     * Devuelve la plataforma de distribución.
     *
     * @return plataforma
     */
    public String getPlataforma() { return plataforma; }

    /**
     * Devuelve el bitrate del álbum.
     *
     * @return bitrate en kbps
     */
    public int getBitrate() { return bitrate; }
}