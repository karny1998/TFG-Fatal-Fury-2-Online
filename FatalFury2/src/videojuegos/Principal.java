package videojuegos;

import lib.input.controlListener;
import lib.objects.Screen;
import lib.sound.audio_manager;
import lib.utils.fileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class Principal extends JFrame {

    public Principal() {
        // TODO -> MOSTRAR OPENING


        String ruta =  System.getProperty("user.dir") + "/.files";
        String origen = "/files/";
        File f = new File(ruta);
        if(f.exists() && f.isDirectory()) {
            // comprobar si existen los ficheros
            f = new File(ruta+"/options.xml");
            if(!f.exists()) {
                // Crear copia del fichero de opciones
                try {
                    fileUtils.copy(origen+"options_bak.xml", ruta+"/options.xml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            f = new File(ruta+"/rank_scores.txt");
            if(!f.exists()) {
                // Crear copia del fichero de opciones
                try {
                    fileUtils.copy(origen+"rank_scores.txt", ruta+"/rank_scores.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            f = new File(ruta+"/last_name.txt");
            if(!f.exists()) {
                // Crear copia del fichero de opciones
                try {
                    fileUtils.copy(origen+"last_name.txt", ruta+"/last_name.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            new File(ruta).mkdirs();
            // Crear copia del fichero de opciones
            try {
                fileUtils.copy(origen+"options_bak.xml", ruta+"/options.xml");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Crear copia del fichero de rankings
            try {
                fileUtils.copy(origen+"rank_scores.txt", ruta+"/rank_scores.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Crear copia del fichero de nombres
            try {
                fileUtils.copy(origen+"last_name.txt", ruta+"/last_name.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        // TODO -> LEER XML PARA EL CARGADOR DE IA
        // TODO -> ANTIREBOTES
        controlListener control = new controlListener();

        audio_manager audio = new audio_manager();

        addKeyListener(control);

        setFocusable(true);

        initUI();
    }

    private void initUI() {
        add(new Screen());
        pack();
        setTitle("Fatal Fury 2");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {


                Principal ex = new Principal();
                ex.setVisible(true);
            }
        });
    }
}