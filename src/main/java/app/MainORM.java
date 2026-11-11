package app;

import conexion.JpaUtil;
import vista.VentanaPrincipalORM;

import javax.swing.*;

public class MainORM {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(
                new Thread(JpaUtil::cerrar)
        );

        SwingUtilities.invokeLater(
                () -> new VentanaPrincipalORM().setVisible(true)
        );
    }
}
