package videojuegos;

import lib.Enums.GameState;
import lib.input.controlListener;
import lib.objects.Screen;
import lib.objects.networking.gui.gameGUI;
import lib.objects.networking.gui.guiItems;
import lib.objects.networking.gui.guiListener;
import lib.sound.audio_manager;
import lib.utils.ScreenOptions;
import lib.utils.fileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


/**
 * The type Principal.
 */
public class Principal extends JFrame {
    /**
     * The Card layout.
     */
    CardLayout cardLayout;
    /**
     * The Main panel.
     */
    JPanel mainPanel, /**
     * The Game.
     */
    game, /**
     * The Gui.
     */
    gui;

    /**
     * Instantiates a new Principal.
     */
    public Principal() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("/assets/sprites/menu/story/are_you_sure1.png"));
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

    /**
     * Init ui.
     */
    private void initUI() {
        gui = new gameGUI(this);
        game = new Screen(this);
        mainPanel.add(game, "game");
        mainPanel.add(gui, "gui");
        add(mainPanel);
        pack();
        setTitle("Fatal Fury 2");
        ScreenOptions.init(this);
        ScreenOptions.update();
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if(((Screen)game).getGame().getState() == GameState.ONLINE_MODE){
                    guiOn();
                    new guiListener(((Screen)game).getGame().getOnline().getGui(),guiItems.QUIT_BUTTON).actionPerformed(null);
                }
                else{
                    System.exit(0);
                }
            }
        });
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new crashReport();
                Principal ex = new Principal();
                ex.setVisible(true);
            }
        });
    }

    /**
     * Game on.
     */
    public void gameOn(){
        cardLayout.show(mainPanel, "game");
    }

    /**
     * Gui on.
     */
    public void guiOn(){
        cardLayout.show(mainPanel, "gui");
    }

    /**
     * Gets game.
     *
     * @return the game
     */
    public JPanel getGame() {
        return game;
    }

    /**
     * Sets game.
     *
     * @param game the game
     */
    public void setGame(JPanel game) {
        this.game = game;
    }

    /**
     * Gets gui.
     *
     * @return the gui
     */
    public JPanel getGui() {
        return gui;
    }

    /**
     * Sets gui.
     *
     * @param gui the gui
     */
    public void setGui(JPanel gui) {
        this.gui = gui;
    }
}
