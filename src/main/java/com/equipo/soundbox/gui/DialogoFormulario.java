package com.equipo.soundbox.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.equipo.soundbox.modelo.Album;
import com.equipo.soundbox.modelo.AlbumDigital;
import com.equipo.soundbox.modelo.AlbumFisico;

/**
 * Diálogo modal para añadir un nuevo álbum.
 * Construido a mano sin editor visual.
 *
 * @author José y Ruben
 * @version 3.0
 */
public class DialogoFormulario extends JDialog {

    private Album albumResultado;

    private JTextField txtTitulo;
    private JTextField txtArtista;
    private JTextField txtAño;
    private JComboBox<String> cmbTipo;
    private JTextField txtDato1;  // formato o plataforma
    private JTextField txtDato2;  // numDiscos o bitrate
    private JTextField txtPuntuacion;
    private JLabel lblDato1;
    private JLabel lblDato2;

    /**
     * Constructor del diálogo modal.
     *
     * @param padre ventana padre
     */
    public DialogoFormulario(JFrame padre) {
        super(padre, "Añadir álbum", true);
        setSize(400, 380);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout());
        initComponentes();
    }

    /**
     * Inicializa los componentes del formulario.
     */
    private void initComponentes() {
        JPanel panelForm = new JPanel(new GridLayout(8, 2, 8, 8));
        panelForm.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        txtTitulo     = new JTextField();
        txtArtista    = new JTextField();
        txtAño        = new JTextField();
        cmbTipo       = new JComboBox<>(new String[]{"Físico", "Digital"});
        txtDato1      = new JTextField();
        txtDato2      = new JTextField();
        txtPuntuacion = new JTextField();
        lblDato1      = new JLabel("Formato (CD/Vinilo/Cassette):");
        lblDato2      = new JLabel("Núm. discos:");

        panelForm.add(new JLabel("Título:"));
        panelForm.add(txtTitulo);
        panelForm.add(new JLabel("Artista:"));
        panelForm.add(txtArtista);
        panelForm.add(new JLabel("Año:"));
        panelForm.add(txtAño);
        panelForm.add(new JLabel("Tipo:"));
        panelForm.add(cmbTipo);
        panelForm.add(lblDato1);
        panelForm.add(txtDato1);
        panelForm.add(lblDato2);
        panelForm.add(txtDato2);
        panelForm.add(new JLabel("Puntuación (0-10):"));
        panelForm.add(txtPuntuacion);

        add(panelForm, BorderLayout.CENTER);

        // Cambiar etiquetas al cambiar tipo
        cmbTipo.addActionListener(e -> actualizarEtiquetas());

        // Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar  = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        // ActionListener con LAMBDA — Aceptar
        btnAceptar.addActionListener(e -> {
            if (validarYCrear()) dispose();
        });

        // ActionListener con LAMBDA — Cancelar
        btnCancelar.addActionListener(e -> {
            albumResultado = null;
            dispose();
        });
    }

    /**
     * Actualiza las etiquetas del formulario según el tipo seleccionado.
     */
    private void actualizarEtiquetas() {
        if (cmbTipo.getSelectedItem().equals("Físico")) {
            lblDato1.setText("Formato (CD/Vinilo/Cassette):");
            lblDato2.setText("Núm. discos:");
        } else {
            lblDato1.setText("Plataforma:");
            lblDato2.setText("Bitrate (128/192/256/320):");
        }
    }

    /**
     * Valida los campos y crea el álbum si todo es correcto.
     *
     * @return true si la validación fue exitosa
     */
    private boolean validarYCrear() {
        String titulo  = txtTitulo.getText().trim();
        String artista = txtArtista.getText().trim();

        if (titulo.isBlank() || artista.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Título y artista son obligatorios.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int año = Integer.parseInt(txtAño.getText().trim());
            double puntuacion = Double.parseDouble(
                    txtPuntuacion.getText().trim().replace(",", "."));
            String tipo = (String) cmbTipo.getSelectedItem();

            if ("Físico".equals(tipo)) {
                String formato = txtDato1.getText().trim();
                if (!formato.matches("CD|Vinilo|Cassette")) {
                    JOptionPane.showMessageDialog(this,
                            "Formato debe ser CD, Vinilo o Cassette.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                int numDiscos = Integer.parseInt(txtDato2.getText().trim());
                albumResultado = new AlbumFisico(titulo, artista, año, formato, numDiscos);
            } else {
                String plataforma = txtDato1.getText().trim();
                int bitrate = Integer.parseInt(txtDato2.getText().trim());
                albumResultado = new AlbumDigital(titulo, artista, año, plataforma, bitrate);
            }

            albumResultado.setPuntuacion(puntuacion);
            return true;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Año, discos/bitrate y puntuación deben ser números.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Muestra el diálogo y devuelve el álbum creado.
     *
     * @return álbum creado o null si se canceló
     */
    public Album mostrar() {
        setVisible(true);
        return albumResultado;
    }

    /**
     * Devuelve el álbum resultado del formulario.
     *
     * @return álbum creado o null
     */
    public Album getAlbumResultado() {
        return albumResultado;
    }
}