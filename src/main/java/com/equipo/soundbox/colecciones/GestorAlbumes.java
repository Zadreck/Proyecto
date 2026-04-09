package com.equipo.soundbox.colecciones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.equipo.soundbox.modelo.Album;
import com.equipo.soundbox.modelo.AlbumDigital;
import com.equipo.soundbox.modelo.AlbumFisico;

/**
 * Gestor de colecciones de álbumes con métodos para añadir, eliminar, buscar y filtrar.
 *
 * @author José y Ruben
 * @version 2.0
 */
public class GestorAlbumes {

    private final ArrayList<Album> catalogo;
    private final HashMap<String, List<Album>> porArtista;

    /**
     * Constructor sin parámetros que inicializa el catálogo y el índice.
     */
    public GestorAlbumes() {
        this.catalogo = new ArrayList<>();
        this.porArtista = new HashMap<>();
    }

    /**
     * Obtiene el catálogo de álbumes.
     *
     * @return lista de álbumes
     */
    public ArrayList<Album> getCatalogo() {
        return catalogo;
    }

    /**
     * Añade un álbum al catálogo y al índice por artista.
     *
     * @param album el álbum a añadir
     */
    public void añadir(Album album) {
        if (album != null) {
            catalogo.add(album);
            porArtista
                .computeIfAbsent(album.getArtista(), k -> new ArrayList<>())
                .add(album);
        }
    }

    /**
     * Elimina un álbum del catálogo por título usando Iterator explícito.
     *
     * @param titulo el título del álbum a eliminar
     * @return true si se eliminó, false si no se encontró
     */
    public boolean eliminar(String titulo) {
        Iterator<Album> it = catalogo.iterator();
        while (it.hasNext()) {
            Album a = it.next();
            if (a.getTitulo().equalsIgnoreCase(titulo)) {
                it.remove();
                List<Album> lista = porArtista.get(a.getArtista());
                if (lista != null) lista.remove(a);
                return true;
            }
        }
        return false;
    }

    /**
     * Busca álbumes por artista usando el HashMap como índice secundario.
     *
     * @param artista el nombre del artista
     * @return lista de álbumes del artista o lista vacía
     */
    public List<Album> buscarPorArtista(String artista) {
        return porArtista.getOrDefault(artista, new ArrayList<>());
    }

    /**
     * Filtra álbumes por tipo usando Stream filter.
     *
     * @param tipo el tipo a filtrar ("Físico" o "Digital")
     * @return lista de álbumes del tipo especificado
     */
    public List<Album> filtrarPorTipo(String tipo) {
        return catalogo.stream()
                .filter(a -> a.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve los álbumes ordenados por título usando Stream sorted.
     *
     * @return lista ordenada alfabéticamente
     */
    public List<Album> ordenarPorTitulo() {
        return catalogo.stream()
                .sorted((a, b) -> a.getTitulo().compareToIgnoreCase(b.getTitulo()))
                .collect(Collectors.toList());
    }

    /**
     * Agrupa los álbumes por artista usando Stream groupingBy.
     *
     * @return mapa con artista como clave y lista de álbumes como valor
     */
    public Map<String, List<Album>> agruparPorArtista() {
        return catalogo.stream()
                .collect(Collectors.groupingBy(Album::getArtista));
    }

    /**
     * Calcula la media de puntuaciones usando Stream averagingDouble.
     *
     * @return media de puntuaciones o 0.0 si el catálogo está vacío
     */
    public double mediaPuntuaciones() {
        return catalogo.stream()
                .collect(Collectors.averagingDouble(Album::getPuntuacion));
    }

    /**
     * Exporta el catálogo a formato JSON construido manualmente con StringBuilder.
     *
     * @return string con el formato JSON válido
     */
    public String exportarJSON() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < catalogo.size(); i++) {
            Album a = catalogo.get(i);
            json.append("  {\n");
            json.append("    \"titulo\": \"").append(a.getTitulo()).append("\",\n");
            json.append("    \"artista\": \"").append(a.getArtista()).append("\",\n");
            json.append("    \"año\": ").append(a.getAño()).append(",\n");
            json.append("    \"puntuacion\": ").append(a.getPuntuacion()).append(",\n");
            json.append("    \"tipo\": \"").append(a.getTipo()).append("\"");
            if (a instanceof AlbumFisico af) {
                json.append(",\n    \"formato\": \"").append(af.getFormato()).append("\",\n");
                json.append("    \"numDiscos\": ").append(af.getNumDiscos());
            } else if (a instanceof AlbumDigital ad) {
                json.append(",\n    \"plataforma\": \"").append(ad.getPlataforma()).append("\",\n");
                json.append("    \"bitrate\": ").append(ad.getBitrate());
            }
            json.append("\n  }");
            if (i < catalogo.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Importa álbumes desde JSON y los agrega al catálogo.
     *
     * @param nuevos lista de álbumes cargados desde JSON
     */
    public void importarDesdeJSON(List<Album> nuevos) {
        for (Album album : nuevos) {
            if (album != null) {
                catalogo.add(album);
                porArtista
                    .computeIfAbsent(album.getArtista(), k -> new ArrayList<>())
                    .add(album);
            }
        }
    }

    /**
     * Devuelve el álbum con mayor puntuación de una lista usando método genérico.
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