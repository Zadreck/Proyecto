package com.equipo.soundbox.gui;

import com.equipo.soundbox.colecciones.GestorAlbumes;
import com.equipo.soundbox.modelo.Album;
import com.equipo.soundbox.persistencia.GestorFicheros;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

/**
 * Ventana principal de la aplicación SoundBox con interfaz gráfica Swing.
 *
 * @author José y Ruben
 * @version 3.0
 */
public class VentanaPrincipal extends JFrame {

    private final GestorAlbumes gestor;
    private final GestorFicheros ficheros;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JTextField campoBusqueda;

    /**
     * Constructor de la ventana principal.
     *
     * @param gestor   gestor de álbumes
     * @param ficheros gestor de ficheros
     */
    public VentanaPrincipal(GestorAlbumes gestor, GestorFicheros ficheros) {
        this.gestor = gestor;
        this.ficheros = ficheros;
        initUI();
    }

    /**
     * Inicializa todos los componentes de la interfaz gráfica a mano.
     */
    private void initUI() {
        setTitle("SoundBox v3.0");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior: buscador
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel etiquetaBuscar = new JLabel("Buscar:");
        campoBusqueda = new JTextField(30);
        JButton btnBuscar = new JButton("Buscar");
        panelSuperior.add(etiquetaBuscar);
        panelSuperior.add(campoBusqueda);
        panelSuperior.add(btnBuscar);
        add(panelSuperior, BorderLayout.NORTH);

        // ActionListener con LAMBDA — botón buscar
        btnBuscar.addActionListener(e -> filtrarTabla());

        // Filtrado en tiempo real al escribir con clase anónima
        campoBusqueda.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
            }
        );

        // Tabla central 
        String[] columnas = {"Título", "Artista", "Año", "Tipo", "Descripción", "Puntuación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        // Panel lateral: botones de acción 
        JPanel panelLateral = new JPanel();
        panelLateral.setLayout(new BoxLayout(panelLateral, BoxLayout.Y_AXIS));
        panelLateral.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        JButton btnAnadir          = new JButton("Añadir álbum");
        JButton btnEliminar        = new JButton("Eliminar álbum");
        JButton btnPuntuar         = new JButton("Puntuar álbum");
        JButton btnMejorValorado   = new JButton("Mejor valorado");
        JButton btnFiltrarTipo     = new JButton("Filtrar por tipo");
        JButton btnOrdenarTitulo   = new JButton("Ordenar por título");
        JButton btnAgruparArtista  = new JButton("Agrupar por artista");
        JButton btnMedia           = new JButton("Media puntuaciones");
        JButton btnReproducir      = new JButton("Reproducir álbum");
        JButton btnExportarJSON    = new JButton("Exportar JSON");
        JButton btnImportarJSON    = new JButton("Importar JSON");
        JButton btnGuardar         = new JButton("Guardar");

        // Mismo ancho para todos los botones
        Dimension dimBoton = new Dimension(170, 32);
        for (JButton btn : new JButton[]{
                btnAnadir, btnEliminar, btnPuntuar, btnMejorValorado,
                btnFiltrarTipo, btnOrdenarTitulo, btnAgruparArtista,
                btnMedia, btnReproducir, btnExportarJSON, btnImportarJSON, btnGuardar}) {
            btn.setMaximumSize(dimBoton);
            btn.setPreferredSize(dimBoton);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelLateral.add(btn);
            panelLateral.add(Box.createVerticalStrut(6));
        }

        add(panelLateral, BorderLayout.EAST);

        // Listeners de todos los botones

        // ActionListener con CLASE ANÓNIMA — Añadir
        btnAnadir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DialogoFormulario dialogo = new DialogoFormulario(VentanaPrincipal.this);
                Album nuevo = dialogo.mostrar();
                if (nuevo != null) {
                    gestor.añadir(nuevo);
                    refrescarTabla();
                }
            }
        });

        // ActionListener con LAMBDA — Eliminar
        btnEliminar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un álbum para eliminar.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String titulo = (String) modeloTabla.getValueAt(fila, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar \"" + titulo + "\"?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gestor.eliminar(titulo);
                refrescarTabla();
            }
        });

        // LAMBDA — Puntuar
        btnPuntuar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un álbum para puntuar.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String titulo = (String) modeloTabla.getValueAt(fila, 0);
            String input = JOptionPane.showInputDialog(this,
                    "Puntuación para \"" + titulo + "\" (0.0 - 10.0):",
                    "Puntuar álbum", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return;
            try {
                double puntuacion = Double.parseDouble(input.replace(",", "."));
                gestor.getCatalogo().stream()
                        .filter(a -> a.getTitulo().equalsIgnoreCase(titulo))
                        .findFirst()
                        .ifPresent(a -> {
                            try {
                                a.setPuntuacion(puntuacion);
                                refrescarTabla();
                                JOptionPane.showMessageDialog(this,
                                        "Puntuación actualizada correctamente.",
                                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            } catch (IllegalArgumentException ex) {
                                JOptionPane.showMessageDialog(this,
                                        "Error: " + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Introduce un número válido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // LAMBDA — Mejor valorado
        btnMejorValorado.addActionListener(e -> {
            if (gestor.getCatalogo().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El catálogo está vacío.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Album mejor = GestorAlbumes.getMejorValorado(gestor.getCatalogo());
            JOptionPane.showMessageDialog(this,
                    "Mejor valorado:\n" + mejor.toString(),
                    "Mejor valorado", JOptionPane.INFORMATION_MESSAGE);
        });

        // LAMBDA — Filtrar por tipo
        btnFiltrarTipo.addActionListener(e -> {
            String[] tipos = {"Físico", "Digital"};
            String tipo = (String) JOptionPane.showInputDialog(this,
                    "Selecciona el tipo a filtrar:",
                    "Filtrar por tipo", JOptionPane.QUESTION_MESSAGE,
                    null, tipos, tipos[0]);
            if (tipo == null) return;
            List<Album> filtrados = gestor.filtrarPorTipo(tipo);
            mostrarListaEnDialogo("Álbumes de tipo: " + tipo, filtrados);
        });

        // LAMBDA — Ordenar por título
        btnOrdenarTitulo.addActionListener(e -> {
            List<Album> ordenados = gestor.ordenarPorTitulo();
            modeloTabla.setRowCount(0);
            for (Album a : ordenados) {
                modeloTabla.addRow(new Object[]{
                    a.getTitulo(), a.getArtista(), a.getAño(),
                    a.getTipo(), a.getDescripcion(),
                    String.format("%.1f", a.getPuntuacion())
                });
            }
        });

        // LAMBDA — Agrupar por artista
        btnAgruparArtista.addActionListener(e -> {
            Map<String, List<Album>> grupos = gestor.agruparPorArtista();
            if (grupos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El catálogo está vacío.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            StringBuilder sb = new StringBuilder();
            grupos.forEach((artista, albums) -> {
                sb.append(artista).append(":\n");
                albums.forEach(a -> sb.append("  • ").append(a.getTitulo()).append("\n"));
                sb.append("\n");
            });
            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JOptionPane.showMessageDialog(this,
                    new JScrollPane(area),
                    "Agrupados por artista", JOptionPane.INFORMATION_MESSAGE);
        });

        // LAMBDA — Media puntuaciones
        btnMedia.addActionListener(e -> {
            if (gestor.getCatalogo().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El catálogo está vacío.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            double media = gestor.mediaPuntuaciones();
            JOptionPane.showMessageDialog(this,
                    String.format("Media de puntuaciones: %.2f", media),
                    "Media", JOptionPane.INFORMATION_MESSAGE);
        });

        // LAMBDA — Reproducir álbum
        btnReproducir.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona un álbum para reproducir.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String titulo = (String) modeloTabla.getValueAt(fila, 0);
            Album album = gestor.getCatalogo().stream()
                    .filter(a -> a.getTitulo().equalsIgnoreCase(titulo))
                    .findFirst()
                    .orElse(null);
            if (album != null) {
                // Usar la interfaz Reproducible para reproducir
                album.reproducir();
                int duracionSegundos = album.getDuracionTotal();
                int minutos = duracionSegundos / 60;
                int segundos = duracionSegundos % 60;
                String mensaje = String.format("Duración: %d:%02d%nPuntuación: %.1f/10.0",
                        minutos, segundos, album.getPuntuacion());
                JOptionPane.showMessageDialog(this, mensaje,
                        "Reproduciendo: " + titulo, JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // LAMBDA — Exportar JSON
        btnExportarJSON.addActionListener(e -> {
            String json = gestor.exportarJSON();
            ficheros.exportarJSON(json, "datos/catalogo.json");
            JOptionPane.showMessageDialog(this,
                    "JSON exportado a datos/catalogo.json",
                    "Exportado", JOptionPane.INFORMATION_MESSAGE);
        });

        // LAMBDA — Importar JSON
        btnImportarJSON.addActionListener(e -> {
            String ruta = JOptionPane.showInputDialog(this,
                    "Ruta del fichero JSON:",
                    "datos/catalogo.json");
            if (ruta == null || ruta.isBlank()) return;
            List<Album> cargados = ficheros.cargarJSON(ruta);
            if (cargados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se cargaron álbumes desde el JSON.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            gestor.importarDesdeJSON(cargados);
            refrescarTabla();
            JOptionPane.showMessageDialog(this,
                    cargados.size() + " álbum(es) importado(s) correctamente.",
                    "Importado", JOptionPane.INFORMATION_MESSAGE);
        });

        // LAMBDA — Guardar
        btnGuardar.addActionListener(e -> {
            ficheros.guardar(gestor.getCatalogo());
            JOptionPane.showMessageDialog(this,
                    "Catálogo guardado correctamente.",
                    "Guardado", JOptionPane.INFORMATION_MESSAGE);
        });

        //  WindowListener — intercepta el cierre 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int resp = JOptionPane.showConfirmDialog(
                        VentanaPrincipal.this,
                        "¿Deseas guardar antes de salir?",
                        "Salir", JOptionPane.YES_NO_CANCEL_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    ficheros.guardar(gestor.getCatalogo());
                    dispose();
                } else if (resp == JOptionPane.NO_OPTION) {
                    dispose();
                }
            }
        });

        refrescarTabla();
    }

    /**
     * Refresca la tabla con todos los álbumes del catálogo.
     */
    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (Album a : gestor.getCatalogo()) {
            modeloTabla.addRow(new Object[]{
                a.getTitulo(),
                a.getArtista(),
                a.getAño(),
                a.getTipo(),
                a.getDescripcion(),
                String.format("%.1f", a.getPuntuacion())
            });
        }
    }

    /**
     * Filtra la tabla en tiempo real según el texto del buscador.
     */
    private void filtrarTabla() {
        String texto = campoBusqueda.getText().toLowerCase().trim();
        modeloTabla.setRowCount(0);
        for (Album a : gestor.getCatalogo()) {
            if (a.getTitulo().toLowerCase().contains(texto)
                    || a.getArtista().toLowerCase().contains(texto)
                    || a.getTipo().toLowerCase().contains(texto)) {
                modeloTabla.addRow(new Object[]{
                    a.getTitulo(),
                    a.getArtista(),
                    a.getAño(),
                    a.getTipo(),
                    a.getDescripcion(),
                    String.format("%.1f", a.getPuntuacion())
                });
            }
        }
    }

    /**
     * Muestra una lista de álbumes en un diálogo informativo.
     *
     * @param titulo   título del diálogo
     * @param albumes  lista de álbumes a mostrar
     */
    private void mostrarListaEnDialogo(String titulo, List<Album> albumes) {
        if (albumes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron álbumes.",
                    titulo, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Album a : albumes) {
            sb.append(a.toString()).append("\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this,
                new JScrollPane(area),
                titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}