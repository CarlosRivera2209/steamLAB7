package steam_lab7;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegistroSteam extends JFrame {

    private JTextField txtUsername, txtNombre;
    private JPasswordField txtPassword;
    private JDateChooser dateChooser;
    private JComboBox<String> cmbTipoUsuario;
    private JButton btnCargarFoto, btnRegistrar;
    private JLabel lblFoto;

    public RegistroSteam() {
        super("Registro de Usuario Steam");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 5, 5));

        // Crear componentes
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtNombre = new JTextField();
        dateChooser = new JDateChooser();
        cmbTipoUsuario = new JComboBox<>(new String[]{"Normal", "Admin"});
        btnCargarFoto = new JButton("Cargar Foto");
        lblFoto = new JLabel();
        btnRegistrar = new JButton("Registrar");

        // Agregar componentes al formulario
        add(new JLabel("Username:"));
        add(txtUsername);
        add(new JLabel("Password:"));
        add(txtPassword);
        add(new JLabel("Nombre:"));
        add(txtNombre);
        add(new JLabel("Fecha de Nacimiento:"));
        add(dateChooser);
        add(new JLabel("Tipo de Usuario:"));
        add(cmbTipoUsuario);
        add(btnCargarFoto);
        add(lblFoto);
        add(new JLabel()); // Espacio en blanco para mantener el layout
        add(btnRegistrar);

        // Manejar eventos
        btnCargarFoto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(RegistroSteam.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    // Mostrar la imagen seleccionada
                    lblFoto.setIcon(new ImageIcon(selectedFile.getAbsolutePath()));
                }
            }
        });

        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
            }
        });
    }

    private void registrarUsuario() {
        try {
            // Obtener datos del formulario
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            String nombre = txtNombre.getText();
            Calendar fechaNacimiento = dateChooser.getCalendar();
            String tipoUsuario = (String) cmbTipoUsuario.getSelectedItem();

            // Obtener imagen de la etiqueta (si se cargó alguna)
            Icon fotoIcon = lblFoto.getIcon();
            byte[] fotoBytes = null;
            if (fotoIcon != null) {
                fotoBytes = Files.readAllBytes(new File(fotoIcon.toString()).toPath());
            }

            // Crear nuevo jugador en Steam
            Steam steam = new Steam();
            int codigo = steam.getNextPlayerCode(); // Obtener el próximo código de jugador disponible
            steam.addPlayer(codigo, username, password, nombre, fechaNacimiento, fotoBytes, tipoUsuario);
            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente");
            dispose(); // Cerrar la ventana de registro
            LoginSteam login = new LoginSteam();
            login.setVisible(true); // Abrir la ventana de login
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RegistroSteam registro = new RegistroSteam();
                registro.setVisible(true);
            }
        });
    }
}
