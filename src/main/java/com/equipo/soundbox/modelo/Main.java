package com.equipo.soundbox.modelo;

import java.util.Scanner;

import com.equipo.soundbox.colecciones.GestorAlbumes;
import com.equipo.soundbox.consola.MenuConsola;
import com.equipo.soundbox.persistencia.GestorFicheros;

/**
 * Punto de entrada de la aplicación SoundBox.
 *
 * @author José y Ruben
 * @version 2.0
 */
public class Main {

    /**
     * Método principal que lanza el menú de consola.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        // Pedir ruta al usuario
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║        Bienvenido a SoundBox v2.0      ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        System.out.print("Introduce la ruta donde guardar los archivos\n(default: datos/catalogo.csv): ");
        String rutaUsuario = sc.nextLine().trim();
        
        // Si está vacío, usar ruta por defecto
        if (rutaUsuario.isBlank()) {
            rutaUsuario = "datos/catalogo.csv";
        }
        
        // Asegurar que la ruta acabe en .csv
        if (!rutaUsuario.endsWith(".csv")) {
            rutaUsuario = rutaUsuario + ".csv";
        }
        
        GestorAlbumes gestor = new GestorAlbumes();
        GestorFicheros ficheros = new GestorFicheros(rutaUsuario);
        MenuConsola menu = new MenuConsola(gestor, ficheros);
        menu.ejecutar();
        
        sc.close();
    }
}