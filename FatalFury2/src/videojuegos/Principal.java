package videojuegos;

import lib.input.controlListener;
import lib.objects.Screen;
import lib.sound.audio_manager;
import lib.utils.crashReport;
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
                    fileUtils.copy(origen+"options.xml", ruta+"/options.xml");
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

            f = new File(ruta+"/last_game.txt");
            if(!f.exists()) {
                // Crear copia del fichero de opciones
                try {
                    fileUtils.copy(origen+"last_game.txt", ruta+"/last_game.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            new File(ruta).mkdirs();
            //TODO PREGUNTAR SI FUNCIONA
            /*
            try {
                Files.setAttribute(Paths.get(ruta), "dos:hidden", true);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            // Crear copia del fichero de opciones
            try {
                fileUtils.copy(origen+"options.xml", ruta+"/options.xml");
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

            try {
                fileUtils.copy(origen+"last_game.txt", ruta+"/last_game.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        controlListener control = new controlListener();

        new audio_manager();

        addKeyListener(control);



        initUI();
    }

    private void initUI() {
        add(new Screen());
        pack();
        setTitle("Fatal Fury 2");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dispose();
        setUndecorated(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setVisible(true);

        setResizable(false);
        setFocusable(true);
        setLocationRelativeTo(null);




    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new crashReport();
                Principal ex = new Principal();
                ex.setVisible(true);
            }
        });
    }
}
