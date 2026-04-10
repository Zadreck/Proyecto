package com.equipo.soundbox.modelo;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.equipo.soundbox.colecciones.GestorAlbumes;
import com.equipo.soundbox.consola.MenuConsola;
import com.equipo.soundbox.gui.VentanaPrincipal;
import com.equipo.soundbox.persistencia.GestorFicheros;

/**
 * Punto de entrada de la aplicación SoundBox.
 *
 * @author José y Ruben
 * @version 3.0
 */
public class Main {

    /**
     * Método principal que lanza la aplicación.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        GestorAlbumes gestor = new GestorAlbumes();
        GestorFicheros ficheros = new GestorFicheros("datos/catalogo.csv");
        gestor.getCatalogo().addAll(ficheros.cargar());

        String[] opciones = {"Interfaz gráfica (GUI)", "Consola"};
        int eleccion = JOptionPane.showOptionDialog(
                null,
                "¿Cómo deseas usar SoundBox?",
                "SoundBox v3.0",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

        if (eleccion == 0) {
            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventana = new VentanaPrincipal(gestor, ficheros);
                ventana.setVisible(true);
            });
        } else {
            MenuConsola menu = new MenuConsola(gestor, ficheros);
            menu.ejecutar();
        }
    }
}