package app;

import vista.VentanaPrincipal;

import javax.swing.*;

/**
 * Punto de entrada de la aplicación.
 *
 * La interfaz se crea en el Event Dispatch Thread (EDT), que es
 * el hilo que Swing utiliza para gestionar la interfaz gráfica.
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
