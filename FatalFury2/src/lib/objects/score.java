package lib.objects;


import lib.Enums.Animation_type;
import lib.Enums.Item_Type;
import lib.input.controlListener;
import lib.sound.audio_manager;
import lib.sound.fight_audio;
import lib.sound.menu_audio;
import lib.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Clase que gestiona todo lo relacionado con los scores durante las peleas vs IA
// y el ranking
public class score {
    // Puntos del jugador
    private int points = 0;
    // Puntero en el ranking
    private int pointer = 0;
    // Ruta del fichero del rank
    private String path = System.getProperty("user.dir") + "/.files/rank_scores.txt";
    // Si se ha cargado la animación del ranking
    private boolean animLoaded = false;
    // Animaciones del ranking
    private animation anim[] = {null, null, null};
    // La fuente
    private Font f = null;
    // Lista de scores y nombres
    private List<Pair<String, Integer>> rank;
    // Tiempo de referencia
    private long timeReference = 0;
    // Cargamos el fichero de la fuente
    private InputStream fontStream = this.getClass().getResourceAsStream("/files/fonts/m04b.TTF");
    // Multiplicador de los puntos en base a la dificultad
    private double multiplier = 1.0;

    public score(ia_loader.dif d){{
        // Ajusta el multiplicador en base al nivel de la IA
        switch (d){
            case EASY:
                multiplier = 1.0;
                break;
            case NORMAL:
                multiplier = 1.25;
                break;
            case HARD:
                multiplier = 1.5;
                break;
            case VERY_HARD:
                multiplier = 2.0;
                break;
        }
    }}

    // Suma score en base al daño hecho
    public void addHit(int p){
        points += (p*multiplier);
    }

    // Aplica bonus de puntos en base a la vida y tiempo restantes
    public void applyBonus(int lf, int secs){
        points += (((lf + secs) * 100)*multiplier);
    }

    // Devuelve el score capado a 99999 (no se deberia poder alcanzar)
    public int getScore(){
        if(this.points >= 99999){
            return 99999;
        }
        return this.points;
    }

    // Guarda el nombre como el último introducido, para precargarlo la siguiente vez
    public void saveLastName(String name){
        String path = System.getProperty("user.dir") + "/.files/last_name.txt";
        File f= new File(path);
        f.delete();
        f= new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(name);
            bw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // Añade al ranking el score actual con el nombre name
    public void writeRankScore(String name){
        // Guarda el nombre
        saveLastName(name);
        // Lee el ranking anterior
        List<Pair<String, Integer>> list = readRankScores(false);

        File f= new File(path);
        f.delete();
        f= new File(path);
        try {
            Boolean write = true;
            FileOutputStream fos = new FileOutputStream(f);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            // Revisa todo el ranking
            for (int i = 0; i < list.size(); i++) {
                // Si es un nombre ya existente, actualiza el score si es mejor que en anterior, sino lo deja igual
                if(list.get(i).getKey().equals(name)){
                    write = this.points > list.get(i).getValue();
                    if(!write){
                        bw.write(list.get(i).getKey());
                        bw.newLine();
                        bw.write(list.get(i).getValue().toString());
                        if(i < list.size()-1){bw.newLine();}
                    }
                }
                // Si es un nombre no existente,
                else {
                    bw.write(list.get(i).getKey());
                    bw.newLine();
                    bw.write(list.get(i).getValue().toString());
                    if(i < list.size()-1){bw.newLine();}
                }
            }
            if(write){
                bw.newLine();
                bw.write(name);
                bw.newLine();
                bw.write(Integer.toString(this.points));
            }
            bw.close();
        }
        catch (Exception e){}
    }

    // Lee el fichero del ranking y lo ordena o no
    public List<Pair<String, Integer>> readRankScores(Boolean ordered){
        List<Pair<String, Integer>> list = new ArrayList<Pair<String, Integer>>();
        try {
            // Lee el fichero de nombres y scores
            String name = "";
            FileReader f = new FileReader(path);
            BufferedReader b = new BufferedReader(f);
            while((name = b.readLine())!=null) {
                int p = Integer.valueOf(b.readLine());
                list.add(new Pair<>(name,p));
            }
            b.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        // Lo ordena si corresponde
        if(ordered) {
            Collections.sort(list, new scoreComparator());
        }
        return list;
    }

    // Muestra por pantalla el ranking
    public void printRanking(Graphics2D g){
        // Si no se habían cargado las animaciones, se cargan
        if(!animLoaded){
            loadAnim();
            timeReference = System.currentTimeMillis();

            try {
                this.f =  Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(60f);
            } catch (FontFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            rank = readRankScores(true);
            animLoaded = true;
        }
        long current = System.currentTimeMillis();
        // Se gestiona el subir y bajar en el scroll del ranking
        if(current - timeReference > 100.0){
            if(controlListener.getStatus(1, controlListener.AR_INDEX) && pointer > 0){
                audio_manager.menu.play(menu_audio.indexes.move_cursor);
                --pointer;
            }
            else if(controlListener.getStatus(1, controlListener.AB_INDEX) && pointer < rank.size()-1){
                audio_manager.menu.play(menu_audio.indexes.move_cursor);
                ++pointer;
            }
            timeReference = current;
        }
        // Se muestra la animación correspondiente según el puntero del ranking
        if(pointer == 0){
            g.drawImage(anim[0].getFrame(0,0,1).getImg(), 0, 0, 1280,720, null);
        }
        else if(pointer == rank.size()-1){
            g.drawImage(anim[2].getFrame(0,0,1).getImg(), 0, 0, 1280,720, null);
        }
        else{
            g.drawImage(anim[1].getFrame(0,0,1).getImg(), 0, 0, 1280,720, null);
        }
        g.setFont(f);
        g.setColor(Color.YELLOW);
        int x = 180;
        int y = 220;
        // Se escriben los 7 correspondientes scores por pantalla
        for(int i = pointer; i-pointer < 7 && i < rank.size(); ++i){
            g.drawString((i+1) + ". " , x, y);
            g.drawString(rank.get(i).getKey() , x+46*4, y);
            g.drawString(rank.get(i).getValue().toString(), x+46*6+8*60, y);
            y += 70;
        }
    }

    // Recarga el ranking
    public void reloadRanking(){
        timeReference = System.currentTimeMillis();
        rank = readRankScores(true);
    }

    // Carga las animaciones del ranking
    public void loadAnim(){
        anim[0] = new animation();
        anim[0].setHasEnd(false);
        anim[0].setType(Animation_type.ENDLESS);

        anim[0].setHasSound(false);
        anim[0].setSoundType(fight_audio.voice_indexes.Win);

        screenObject s = new screenObject(0, 0,  1280, 720, new ImageIcon(this.getClass().getResource("/assets/sprites/menu/ranking/ranking_base.png")).getImage(), Item_Type.SCENARY_2);
        anim[0].addFrame(s,250.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(this.getClass().getResource("/assets/sprites/menu/ranking/ranking_down.png")).getImage(), Item_Type.SCENARY_2);
        anim[0].addFrame(s,250.0,0,0);

        anim[1] = new animation();
        anim[1].setHasEnd(false);
        anim[1].setType(Animation_type.ENDLESS);
        anim[0].setHasSound(false);
        anim[1].setSoundType(fight_audio.voice_indexes.Win);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(this.getClass().getResource("/assets/sprites/menu/ranking/ranking_base.png")).getImage(), Item_Type.SCENARY_2);
        anim[1].addFrame(s,250.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(this.getClass().getResource("/assets/sprites/menu/ranking/ranking_both.png")).getImage(), Item_Type.SCENARY_2);
        anim[1].addFrame(s,250.0,0,0);

        anim[2] = new animation();
        anim[2].setHasEnd(false);
        anim[2].setType(Animation_type.ENDLESS);
        anim[0].setHasSound(false);
        anim[2].setSoundType(fight_audio.voice_indexes.Win);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(this.getClass().getResource("/assets/sprites/menu/ranking/ranking_base.png")).getImage(), Item_Type.SCENARY_2);
        anim[2].addFrame(s,250.0,0,0);
        s = new screenObject(0, 0,  1280, 720, new ImageIcon(this.getClass().getResource("/assets/sprites/menu/ranking/ranking_up.png")).getImage(), Item_Type.SCENARY_2);
        anim[2].addFrame(s,250.0,0,0);
    }

    // Comparador de scores para ordenar la lista
    class scoreComparator implements Comparator {
        public int compare(Object o1,Object o2){
            Pair<String, Integer> s1=(Pair<String, Integer>)o1;
            Pair<String, Integer> s2=(Pair<String, Integer>)o2;
            if(s1.getValue() == s2.getValue()){return 0;}
            else if (s1.getValue() < s2.getValue()){return 1;}
            else{return -1;}
        }
    }
}

