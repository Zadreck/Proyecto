package com.equipo.soundbox.modelo;

import java.util.ArrayList;

/**
 * Clase utilitaria con métodos genéricos sobre colecciones de álbumes.
 *
 * @author José y Ruben
 * @version 1.0
 */
public class GestorAlbumes {

    /**
     * Devuelve el álbum con mayor puntuación de una lista.
     *
     * @param <T>   tipo que extiende Album
     * @param lista lista de álbumes a evaluar
     * @return el álbum mejor valorado
     * @throws IllegalArgumentException si la lista está vacía
     */
    public static <T extends Album> T getMejorValorado(ArrayList<T> lista) {
        if (lista == null || lista.isEmpty())
            throw new IllegalArgumentException("La lista no puede estar vacía");
        T mejor = lista.get(0);
        for (T a : lista) {
            if (a.getPuntuacion() > mejor.getPuntuacion()) mejor = a;
        }
        return mejor;
    }
}