package com.equipo.soundbox.modelo;

import java.util.ArrayList;

/**
 * Clase principal de prueba del Hito 1.
 *
 * @author José y Ruben
 * @version 1.0
 */
public class Main {

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        AlbumFisico a1 = new AlbumFisico(
                "Dark Side of the Moon", "Pink Floyd", 1973, "Vinilo", 1);
        AlbumDigital a2 = new AlbumDigital(
                "Thriller", "Michael Jackson", 1982, "Spotify", 320);

        a1.setPuntuacion(9.5);
        a2.setPuntuacion(8.0);

        System.out.println(a1);
        System.out.println(a2);

        a1.reproducir();
        a2.reproducir();

        ArrayList<Album> lista = new ArrayList<>();
        lista.add(a1);
        lista.add(a2);

        Album mejor = GestorAlbumes.getMejorValorado(lista);
        System.out.println("Mejor valorado: " + mejor.getTitulo());
    }
}
