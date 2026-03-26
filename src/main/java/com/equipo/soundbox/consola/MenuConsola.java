package com.equipo.soundbox.consola;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.equipo.soundbox.colecciones.GestorAlbumes;
import com.equipo.soundbox.modelo.Album;
import com.equipo.soundbox.modelo.AlbumDigital;
import com.equipo.soundbox.modelo.AlbumFisico;
import com.equipo.soundbox.persistencia.GestorFicheros;

/**
 * Menú de consola principal de SoundBox.
 * Gestiona la interacción con el usuario mediante printf formateado.
 *
 * @author José y Ruben
 * @version 2.0
 */
public class MenuConsola {

    private final GestorAlbumes gestor;
    private final GestorFicheros ficheros;
    private final Scanner sc;

    /**
     * Constructor de MenuConsola.
     *
     * @param gestor   gestor de la colección de álbumes
     * @param ficheros gestor de persistencia en ficheros
     */
    public MenuConsola(GestorAlbumes gestor, GestorFicheros ficheros) {
        this.gestor = gestor;
        this.ficheros = ficheros;
        this.sc = new Scanner(System.in);
    }

    /**
     * Muestra el menú principal con formato tabular usando printf.
     */
    public void mostrarMenu() {
        System.out.println();
        System.out.printf("       SOUNDBOX  v2.0         %n");
        System.out.printf("  %-4s %-25s%n", "1.", "Añadir álbum");
        System.out.printf("  %-4s %-25s%n", "2.", "Listar todos");
        System.out.printf("  %-4s %-25s%n", "3.", "Buscar por artista");
        System.out.printf("  %-4s %-25s%n", "4.", "Filtrar por tipo");
        System.out.printf("  %-4s %-25s%n", "5.", "Eliminar álbum");
        System.out.printf("  %-4s %-25s%n", "6.", "Ver mejor valorado");
        System.out.printf("  %-4s %-25s%n", "7.", "Puntuar álbum");
        System.out.printf("  %-4s %-25s%n", "8.", "Ordenar por título");
        System.out.printf("  %-4s %-25s%n", "9.", "Agrupar por artista");
        System.out.printf("  %-4s %-25s%n", "10.", "Media puntuaciones");
        System.out.printf("  %-4s %-25s%n", "11.", "Exportar JSON");
        System.out.printf("  %-4s %-25s%n", "12.", "Importar JSON");
        System.out.printf("  %-4s %-25s%n", "13.", "Guardar y salir");
        System.out.print("  Opción: ");
    }

    /**
     * Ejecuta el bucle principal del menú.
     */
    public void ejecutar() {
        gestor.getCatalogo().addAll(ficheros.cargar());
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero();
            switch (opcion) {
                case 1  -> añadirAlbum();
                case 2  -> listarTodos();
                case 3  -> buscarPorArtista();
                case 4  -> filtrarPorTipo();
                case 5  -> eliminarAlbum();
                case 6  -> verMejorValorado();
                case 7  -> puntuarAlbum();
                case 8  -> mostrarOrdenadosPorTitulo();
                case 9  -> mostrarAgrupadosPorArtista();
                case 10 -> mostrarMediaPuntuaciones();
                case 11 -> exportarJSON();
                case 12 -> importarJSON();
                case 13 -> guardarYSalir();
                default -> System.out.println("  Opción no válida.");
            }
        } while (opcion != 13);
    }

    /**
     * Pide datos al usuario y añade un álbum nuevo con validación por regex.
     */
    private void añadirAlbum() {
        System.out.println("\n── Añadir álbum ──");
        System.out.print("Título: ");
        String titulo = sc.nextLine();

        System.out.print("Artista: ");
        String artista = sc.nextLine();

        System.out.print("Año (1900-2025): ");
        int año = leerEntero();

        System.out.print("Tipo (1=Físico / 2=Digital): ");
        int tipo = leerEntero();

        try {
            Album album = null;

            if (tipo == 1) {
                System.out.print("Formato (CD/Vinilo/Cassette): ");
                String formato = sc.nextLine();
                if (!validarConRegex(formato, "CD|Vinilo|Cassette"))
                    throw new IllegalArgumentException("Formato no válido");
                System.out.print("Número de discos: ");
                int discos = leerEntero();
                album = new AlbumFisico(titulo, artista, año, formato, discos);
            } else if (tipo == 2) {
                System.out.print("Plataforma: ");
                String plataforma = sc.nextLine();
                System.out.print("Bitrate (128/192/256/320): ");
                int bitrate = leerEntero();
                album = new AlbumDigital(titulo, artista, año, plataforma, bitrate);
            } else {
                System.out.println("  Tipo no válido.");
                return;
            }

            System.out.print("Puntuación (0.0 - 10.0): ");
            double puntuacion = leerDouble();
            album.setPuntuacion(puntuacion);

            gestor.añadir(album);
            System.out.println("  Álbum añadido correctamente.");

        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    /**
     * Lista todos los álbumes con formato tabular usando printf.
     */
    private void listarTodos() {
        System.out.println();
        System.out.printf("%-30s %-20s %-6s %-10s %-5s%n",
                "Título", "Artista", "Año", "Tipo", "Punt.");
        System.out.println("─".repeat(76));
        for (Album a : gestor.getCatalogo()) {
            System.out.printf("%-30s %-20s %-6d %-10s %-5.1f%n",
                    a.getTitulo(), a.getArtista(),
                    a.getAño(), a.getTipo(), a.getPuntuacion());
        }
        if (gestor.getCatalogo().isEmpty())
            System.out.println("  No hay álbumes.");
    }

    /**
     * Busca álbumes por artista usando el HashMap.
     */
    private void buscarPorArtista() {
        System.out.print("\nArtista a buscar: ");
        String artista = sc.nextLine();
        List<Album> resultado = gestor.buscarPorArtista(artista);
        if (resultado.isEmpty()) {
            System.out.println("  No se encontraron álbumes.");
        } else {
            resultado.forEach(a -> System.out.println("  " + a));
        }
    }

    /**
     * Filtra álbumes por tipo usando Stream filter.
     */
    private void filtrarPorTipo() {
        System.out.print("\nTipo (Físico/Digital): ");
        String tipo = sc.nextLine();
        List<Album> resultado = gestor.filtrarPorTipo(tipo);
        if (resultado.isEmpty()) {
            System.out.println("  No hay álbumes de ese tipo.");
        } else {
            resultado.forEach(a -> System.out.println("  " + a));
        }
    }

    /**
     * Elimina un álbum por título usando Iterator explícito.
     */
    private void eliminarAlbum() {
        System.out.print("\nTítulo a eliminar: ");
        String titulo = sc.nextLine();
        boolean ok = gestor.eliminar(titulo);
        System.out.println(ok ? "  Álbum eliminado." : "  No encontrado.");
    }

    /**
     * Muestra el álbum mejor valorado del catálogo.
     */
    private void verMejorValorado() {
        if (gestor.getCatalogo().isEmpty()) {
            System.out.println("  El catálogo está vacío.");
            return;
        }
        Album mejor = GestorAlbumes.getMejorValorado(gestor.getCatalogo());
        System.out.println("\n  Mejor valorado: " + mejor);
    }

    /**
     * Puntúa un álbum del catálogo.
     */
    private void puntuarAlbum() {
        System.out.println();
        listarTodos();
        System.out.print("\nTítulo del álbum a puntuar: ");
        String titulo = sc.nextLine();

        Album album = gestor.getCatalogo().stream()
                .filter(a -> a.getTitulo().equalsIgnoreCase(titulo))
                .findFirst()
                .orElse(null);

        if (album == null) {
            System.out.println("  Álbum no encontrado.");
            return;
        }

        System.out.print("Puntuación (0.0 - 10.0): ");
        double puntuacion = leerDouble();

        try {
            album.setPuntuacion(puntuacion);
            System.out.println("  Puntuación asignada correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    /**
     * Muestra los álbumes ordenados por título usando Stream sorted.
     */
    private void mostrarOrdenadosPorTitulo() {
        System.out.println("\n── Álbumes ordenados por título ──");
        List<Album> ordenados = gestor.ordenarPorTitulo();
        if (ordenados.isEmpty()) {
            System.out.println("  El catálogo está vacío.");
            return;
        }
        ordenados.forEach(a -> System.out.printf("  %-30s %-20s %-5.1f%n",
                a.getTitulo(), a.getArtista(), a.getPuntuacion()));
    }

    /**
     * Muestra los álbumes agrupados por artista usando Stream groupingBy.
     */
    private void mostrarAgrupadosPorArtista() {
        System.out.println("\n── Álbumes agrupados por artista ──");
        Map<String, List<Album>> grupos = gestor.agruparPorArtista();
        if (grupos.isEmpty()) {
            System.out.println("  El catálogo está vacío.");
            return;
        }
        grupos.forEach((artista, albums) -> {
            System.out.println("  " + artista + ":");
            albums.forEach(a -> System.out.println("    - " + a.getTitulo()));
        });
    }

    /**
     * Muestra la media de puntuaciones usando Stream averagingDouble.
     */
    private void mostrarMediaPuntuaciones() {
        if (gestor.getCatalogo().isEmpty()) {
            System.out.println("  El catálogo está vacío.");
            return;
        }
        System.out.printf("%n  Media de puntuaciones: %.2f%n",
                gestor.mediaPuntuaciones());
    }

    /**
     * Exporta el catálogo a JSON y lo guarda en fichero.
     */
    private void exportarJSON() {
        String json = gestor.exportarJSON();
        ficheros.exportarJSON(json, "datos/catalogo.json");
        System.out.println("  JSON exportado a datos/catalogo.json");
    }

    /**
     * Importa álbumes desde un fichero JSON.
     */
    private void importarJSON() {
        System.out.print("\n  Ruta del fichero JSON a importar: ");
        String rutaJSON = sc.nextLine().trim();

        if (rutaJSON.isBlank()) {
            System.out.println("  Ruta inválida.");
            return;
        }

        List<Album> albumsImportados = ficheros.cargarJSON(rutaJSON);

        if (albumsImportados.isEmpty()) {
            System.out.println("  No se pudieron cargar álbumes del JSON.");
            return;
        }

        // Preguntar si desea agregar o reemplazar
        System.out.printf("  Se encontraron %d álbumes.%n", albumsImportados.size());
        System.out.print("  ¿Desea agregar (1) o reemplazar (2) el catálogo actual?: ");
        int opcion = leerEntero();

        if (opcion == 1) {
            // Agregar los nuevos álbumes
            for (Album album : albumsImportados) {
                try {
                    gestor.añadir(album);
                } catch (IllegalArgumentException e) {
                    System.out.println("  Aviso: " + album.getTitulo() + " - " + e.getMessage());
                }
            }
            System.out.printf("  %d álbumes agregados correctamente.%n", albumsImportados.size());
        } else if (opcion == 2) {
            // Reemplazar el catálogo
            gestor.getCatalogo().clear();
            for (Album album : albumsImportados) {
                try {
                    gestor.añadir(album);
                } catch (IllegalArgumentException e) {
                    System.out.println("  Aviso: " + album.getTitulo() + " - " + e.getMessage());
                }
            }
            System.out.printf("  Catálogo reemplazado con %d álbumes.%n", albumsImportados.size());
        } else {
            System.out.println("  Opción no válida.");
        }
    }

    /**
     * Guarda el catálogo en CSV y termina la aplicación.
     */
    private void guardarYSalir() {
        ficheros.guardar(gestor.getCatalogo());
        System.out.println("  Catálogo guardado. ¡Hasta luego!");
    }

    /**
     * Valida una cadena usando expresión regular.
     *
     * @param texto  texto a validar
     * @param patron patrón regex a aplicar
     * @return true si el texto coincide con el patrón
     */
    public boolean validarConRegex(String texto, String patron) {
        return texto != null && texto.matches(patron);
    }

    /**
     * Lee un entero por consola gestionando errores de formato.
     *
     * @return entero introducido por el usuario o -1 si hay error
     */
    private int leerEntero() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Lee un double por consola gestionando errores de formato.
     *
     * @return double introducido por el usuario o 0.0 si hay error
     */
    private double leerDouble() {
        try {
            return Double.parseDouble(sc.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("  Valor no válido, se usará 0.0.");
            return 0.0;
        }
    }
}