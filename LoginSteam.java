/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package steam_lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginSteam extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginSteam() {
        super("Login de Usuario Steam");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1, 5, 5));

        // Crear componentes
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("Iniciar sesión");

        // Agregar componentes al formulario
        add(new JLabel("Nombre de Usuario:"));
        add(txtUsername);
        add(new JLabel("Contraseña:"));
        add(txtPassword);
        add(btnLogin);

        // Manejar eventos
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                // Verificar las credenciales
                boolean credencialesValidas = verificarCredenciales(username, password);
                if (credencialesValidas) {
                    // Obtener el tipo de usuario y abrir el menú correspondiente
                    String tipoUsuario = obtenerTipoUsuario(username);
                    abrirMenu(tipoUsuario);
                } else {
                    JOptionPane.showMessageDialog(LoginSteam.this, "Nombre de usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private boolean verificarCredenciales(String username, String password) {
        // Aquí se debería implementar la lógica para verificar las credenciales en la base de datos
        // Por ahora, solo se simulará un usuario admin con contraseña "admin"
        return username.equals("admin") && password.equals("admin");
    }

    private String obtenerTipoUsuario(String username) {
        // Aquí se debería consultar la base de datos para obtener el tipo de usuario asociado al nombre de usuario
        // Por ahora, devolveremos un tipo de usuario estático
        return "admin";
    }

    private void abrirMenu(String tipoUsuario) {
        if (tipoUsuario.equals("admin")) {
            // Abre el menú de administrador
            JOptionPane.showMessageDialog(LoginSteam.this, "¡Bienvenido, admin! (Menú de administrador)");
        } else {
            // Abre el menú de usuario normal
            JOptionPane.showMessageDialog(LoginSteam.this, "¡Bienvenido! (Menú de usuario normal)");
        }
    }
}
