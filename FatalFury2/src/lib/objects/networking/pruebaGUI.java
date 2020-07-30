package lib.objects.networking;

import lib.Enums.Item_Type;
import lib.menus.menu_generator;
import lib.objects.game_controller;
import videojuegos.Principal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import static lib.Enums.Item_Type.*;

public class pruebaGUI extends JPanel {
    Principal principal;
    static int resX = 1280, resY = 720;
    private Font f;
    {
        try {
            f = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/files/fonts/m04b.TTF")).deriveFont(25f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public pruebaGUI(Principal principal) {
        this.principal = principal;
        setSurfaceSize();
        this.setLayout(null);
        JButton login = new JButton("Login");
        login.setFont(f);
        login.setForeground(Color.YELLOW);
        login.setBackground(new Color(33,32,57));
        login.setBounds(370,560, 250,60);
        JTextField username = new JTextField("Username");
        username.setEditable(true);
        username.setFont(f);
        username.setForeground(Color.YELLOW);
        username.setBackground(new Color(33,32,57));
        username.setBounds(660,560, 250,60);
        this.add(login);
        this.add(username);
    }

    private void setSurfaceSize() {
        Dimension d = new Dimension();
        d.width = resX;
        d.height = resY;
        setPreferredSize(d);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Ssssssssssss");
        g.drawImage(new ImageIcon(menu_generator.class.getResource("/assets/sprites/menu/base_2.png")).getImage(),0,0, null);
    }
}
