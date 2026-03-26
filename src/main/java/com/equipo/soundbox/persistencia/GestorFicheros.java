package com.equipo.soundbox.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.equipo.soundbox.modelo.Album;
import com.equipo.soundbox.modelo.AlbumDigital;
import com.equipo.soundbox.modelo.AlbumFisico;

/**
 * Gestiona la persistencia de álbumes en ficheros CSV.
 * Usa BufferedReader para lectura, FileWriter para escritura
 * y RandomAccessFile para acceso por posición.
 *
 * @author José y Ruben
 * @version 1.0
 */
public class GestorFicheros {

    private final String rutaCSV;

    /**
     * Constructor de GestorFicheros.
     *
     * @param rutaCSV ruta al fichero CSV de datos
     */
    public GestorFicheros(String rutaCSV) {
        this.rutaCSV = rutaCSV;
    }

    /**
     * Carga los álbumes desde el fichero CSV.
     * Formato: tipo,titulo,artista,anio,puntuacion,dato1,dato2
     *
     * @return lista de álbumes cargados
     */
    public List<Album> cargar() {
        List<Album> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaCSV))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] p = linea.split(",");
                if (p[0].equals("Fisico")) {
                    AlbumFisico a = new AlbumFisico(
                            p[1], p[2], Integer.parseInt(p[3]), p[5], Integer.parseInt(p[6]));
                    a.setPuntuacion(Double.parseDouble(p[4]));
                    lista.add(a);
                } else if (p[0].equals("Digital")) {
                    AlbumDigital a = new AlbumDigital(
                            p[1], p[2], Integer.parseInt(p[3]), p[5], Integer.parseInt(p[6]));
                    a.setPuntuacion(Double.parseDouble(p[4]));
                    lista.add(a);
                }
            }
        } catch (IOException e) {
            System.out.println("Fichero no encontrado, se iniciará vacío.");
        }
        return lista;
    }

    /**
     * Guarda la lista de álbumes en el fichero CSV.
     *
     * @param lista lista de álbumes a guardar
     */
    public void guardar(List<Album> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaCSV))) {
            for (Album a : lista) {
                if (a instanceof AlbumFisico af) {
                    bw.write("Fisico," + af.getTitulo() + "," + af.getArtista()
                            + "," + af.getAño() + "," + af.getPuntuacion()
                            + "," + af.getFormato() + "," + af.getNumDiscos());
                } else if (a instanceof AlbumDigital ad) {
                    bw.write("Digital," + ad.getTitulo() + "," + ad.getArtista()
                            + "," + ad.getAño() + "," + ad.getPuntuacion()
                            + "," + ad.getPlataforma() + "," + ad.getBitrate());
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }

    /**
     * Busca una línea del CSV por número de línea usando RandomAccessFile.
     *
     * @param numeroLinea número de línea a buscar (desde 0)
     * @return contenido de la línea o null si no existe
     */
    public String buscarPorLinea(int numeroLinea) {
        try (RandomAccessFile raf = new RandomAccessFile(rutaCSV, "r")) {
            int lineaActual = 0;
            String linea;
            while ((linea = raf.readLine()) != null) {
                if (lineaActual == numeroLinea) return linea;
                lineaActual++;
            }
        } catch (IOException e) {
            System.out.println("Error al leer: " + e.getMessage());
        }
        return null;
    }

    /**
     * Exporta el JSON a un fichero.
     *
     * @param json    contenido JSON a exportar
     * @param rutaJSON ruta del fichero de salida
     */
    public void exportarJSON(String json, String rutaJSON) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaJSON))) {
            bw.write(json);
        } catch (IOException e) {
            System.out.println("Error al exportar JSON: " + e.getMessage());
        }
    }

    /**
     * Carga los álbumes desde un fichero JSON.
     * Parser manual sin dependencias externas.
     * 
     * @param rutaJSON ruta al fichero JSON
     * @return lista de álbumes cargados desde JSON
     */
    public List<Album> cargarJSON(String rutaJSON) {
        List<Album> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaJSON))) {
            StringBuilder contenido = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea);
            }
            
            String json = contenido.toString().trim();
            
            // Si es un array JSON
            if (json.startsWith("[")) {
                lista.addAll(parseJsonArray(json));
            }
            // Si es un objeto JSON
            else if (json.startsWith("{")) {
                Album album = parseJsonObject(json);
                if (album != null) {
                    lista.add(album);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar JSON: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Parsea un array JSON y retorna lista de álbumes.
     */
    private List<Album> parseJsonArray(String json) {
        List<Album> lista = new ArrayList<>();
        int inicio = json.indexOf("[") + 1;
        int fin = json.lastIndexOf("]");
        
        if (inicio > 0 && fin > inicio) {
            String contenido = json.substring(inicio, fin);
            
            // Dividir por objetos JSON (buscar { } balanceados)
            int nivel = 0;
            int startObj = -1;
            
            for (int i = 0; i < contenido.length(); i++) {
                char c = contenido.charAt(i);
                
                if (c == '{') {
                    if (nivel == 0) startObj = i;
                    nivel++;
                } else if (c == '}') {
                    nivel--;
                    if (nivel == 0 && startObj >= 0) {
                        String objeto = contenido.substring(startObj, i + 1);
                        Album album = parseJsonObject(objeto);
                        if (album != null) {
                            lista.add(album);
                        }
                        startObj = -1;
                    }
                }
            }
        }
        
        return lista;
    }

    /**
     * Parsea un objeto JSON y retorna un Album.
     */
    private Album parseJsonObject(String json) {
        try {
            String tipo = extraerValor(json, "tipo");
            String titulo = extraerValor(json, "titulo");
            String artista = extraerValor(json, "artista");
            int año = extraerEntero(json, "año");
            double puntuacion = extraerDouble(json, "puntuacion");

            Album album = null;

            if ("Fisico".equals(tipo)) {
                String formato = extraerValor(json, "formato");
                int numDiscos = extraerEntero(json, "numDiscos");
                if (formato == null) formato = "CD";
                if (numDiscos <= 0) numDiscos = 1;
                album = new AlbumFisico(titulo, artista, año, formato, numDiscos);
            } else if ("Digital".equals(tipo)) {
                String plataforma = extraerValor(json, "plataforma");
                int bitrate = extraerEntero(json, "bitrate");
                if (plataforma == null) plataforma = "Spotify";
                if (bitrate <= 0) bitrate = 128;
                album = new AlbumDigital(titulo, artista, año, plataforma, bitrate);
            }

            if (album != null) {
                album.setPuntuacion(puntuacion);
            }

            return album;
        } catch (Exception e) {
            System.out.println("Error al parsear álbum JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extrae un valor String de un JSON.
     */
    private String extraerValor(String json, String clave) {
        String patron = "\"" + clave + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(patron).matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Extrae un valor entero de un JSON.
     */
    private int extraerEntero(String json, String clave) {
        String patron = "\"" + clave + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(patron).matcher(json);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    /**
     * Extrae un valor double de un JSON.
     */
    private double extraerDouble(String json, String clave) {
        String patron = "\"" + clave + "\"\\s*:\\s*([\\d.]+)";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(patron).matcher(json);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0.0;
    }

    /**
     * Devuelve la ruta del fichero CSV.
     *
     * @return ruta CSV
     */
    public String getRutaCSV() {
        return rutaCSV;
    }
}