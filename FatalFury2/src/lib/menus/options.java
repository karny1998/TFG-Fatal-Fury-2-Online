package lib.menus;


import lib.Enums.Item_Type;
import lib.input.controlListener;
import lib.objects.screenObject;
import lib.sound.audio_manager;
import lib.sound.menu_audio;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class options {

    //La opcion en mayusculas representa la actual
    private String[] titulos = {"AUDIO", "player 1", "player 2"};
    private int[] xTitulos = {170, 150 + 340, 150 + 340 + 360};

    private String[] elementos_vol =  {
            "general", "music", "voices", "special effects"
    };
    private String[] elementos_p1 ={
            "up", "down", "left", "right", "weak punch", "weak kick", "strong punch", "strong kick", "ok", "pause/back"
    };
    private String[] elementos_p2 ={
            "up", "down", "left", "right", "weak punch", "weak kick", "strong punch", "strong kick", "ok", "pause/back"
    };

    private String[][] elementos = {elementos_vol, elementos_p1, elementos_p2};

    private int[] valores_vol = new int[elementos_vol.length];
    private int[] valores_p1 = new int[elementos_p1.length];
    private int[] valores_p2 = new int[elementos_p2.length];
    private int[][] valores = {valores_vol, valores_p1, valores_p2};


    private Boolean exit;
    private long referenceTime;


    private String filePath = System.getProperty("user.dir") + "/.files/options.xml";
    private URL imgPath_1 = this.getClass().getResource("/assets/sprites/menu/options/menu_vol.png");
    private URL imgPath_2 = this.getClass().getResource("/assets/sprites/menu/options/menu_p1.png");
    private URL imgPath_3 = this.getClass().getResource("/assets/sprites/menu/options/menu_p2.png");


    private InputStream fontStream_1 = this.getClass().getResourceAsStream("/files/fonts/m04b.TTF");
    private InputStream fontStream_2 = this.getClass().getResourceAsStream("/files/fonts/m04b.TTF");
    public void updateTime() {
        referenceTime = System.currentTimeMillis();
    }

    private Font f_1, f_2;
    private screenObject fondo, vol, p1, p2;

    private enum pag {
        PAGINA_VOLUMEN(0),
        PAGINA_P1(1),
        PAGINA_P2(2),
        PAGINA_P1_MAPEO(1),
        PAGINA_P2_MAPEO(2);

        private int val;
        pag(int i) {
            this.val = i;
        }
        public int getVal(){
            return val;
        }
    }

    private pag pagina;

    private void readOptionsFile() {
        try {


            File input = new File(filePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();



            Node opciones = doc.getChildNodes().item(0);
            NodeList vol = doc.getElementsByTagName("volumen").item(0).getChildNodes();
            NodeList p1 = doc.getElementsByTagName("controles_jugador_1").item(0).getChildNodes();
            NodeList p2 = doc.getElementsByTagName("controles_jugador_2").item(0).getChildNodes();

            int indice = 0;

            for (int i = 0; i < vol.getLength(); i++) {
                Node node = vol.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    valores_vol[indice] = Integer.parseInt(node.getTextContent());
                    indice++;
                }
            }

            indice = 0;
            for (int i = 0; i < p1.getLength(); i++) {
                Node node = p1.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    valores_p1[indice] = Integer.parseInt(node.getTextContent());
                    indice++;
                }
            }

            indice = 0;
            for (int i = 0; i < p2.getLength(); i++) {
                Node node = p2.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    valores_p2[indice] = Integer.parseInt(node.getTextContent());
                    indice++;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveOptions() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("opciones");

            int index = 0;
            doc.appendChild(rootElement);

                Element vol = doc.createElement("volumen");

                    Element vol_general = doc.createElement("vol_general");
                    vol_general.setTextContent(String.valueOf(valores_vol[index]));
                    vol.appendChild(vol_general);
                    index++;

                    Element vol_musica = doc.createElement("vol_musica");
                    vol_musica.setTextContent(String.valueOf(valores_vol[index]));
                    vol.appendChild(vol_musica);
                    index++;

                    Element vol_voces = doc.createElement("vol_voces");
                    vol_voces.setTextContent(String.valueOf(valores_vol[index]));
                    vol.appendChild(vol_voces);
                    index++;

                    Element vol_efectos = doc.createElement("vol_efectos");
                    vol_efectos.setTextContent(String.valueOf(valores_vol[index]));
                    vol.appendChild(vol_efectos);

                rootElement.appendChild(vol);


                index = 0;
                Element p1 = doc.createElement("controles_jugador_1");

                    Element arriba = doc.createElement("arriba");
                    arriba.setTextContent(String.valueOf(valores_p1[index]));
                    p1.appendChild(arriba);
                    index++;

                    Element abajo = doc.createElement("abajo");
                    abajo.setTextContent(String.valueOf(valores_p1[index]));
                    p1.appendChild(abajo);
                    index++;

                    Element izquierda = doc.createElement("izquierda");
                    izquierda.setTextContent(String.valueOf(valores_p1[index]));
                    p1.appendChild(izquierda);
                    index++;

                    Element derecha = doc.createElement("derecha");
                    derecha.setTextContent(String.valueOf(valores_p1[index]));
                    p1.appendChild(derecha);
                    index++;

                    Element punetazo_debil = doc.createElement("punetazo_debil");
                    punetazo_debil.setTextContent(String.valueOf(valores_p1[index]));
                    p1.appendChild(punetazo_debil);
                    index++;

                    Element patada_debil = doc.createElement("patada_debil");
                    patada_debil.setTextContent(String.valueOf(valores_p1[index]));
                    p1.appendChild(patada_debil);
                    index++;

                    Element punetazo_fuerte = doc.createElement("punetazo_fuerte");
                    punetazo_fuerte.setTextContent(String.valueOf(valores_p1[index]));
                    p1.appendChild(punetazo_fuerte);
                    index++;

                    Element patada_fuerte = doc.createElement("patada_fuerte");
                    patada_fuerte.setTextContent(String.valueOf(valores_p1[index]));
                    p1.appendChild(patada_fuerte);
                    index++;

                    Element aceptar = doc.createElement("aceptar");
                    aceptar.setTextContent(String.valueOf(valores_p1[index]));
                    p1.appendChild(aceptar);
                    index++;

                    Element pausa_atras = doc.createElement("pausa_atras");
                    pausa_atras.setTextContent(String.valueOf(valores_p1[index]));
                    p1.appendChild(pausa_atras);

                rootElement.appendChild(p1);

                index = 0;
                Element p2 = doc.createElement("controles_jugador_2");

                    arriba = doc.createElement("arriba");
                    arriba.setTextContent(String.valueOf(valores_p2[index]));
                    p2.appendChild(arriba);
                    index++;

                    abajo = doc.createElement("abajo");
                    abajo.setTextContent(String.valueOf(valores_p2[index]));
                    p2.appendChild(abajo);
                    index++;

                    izquierda = doc.createElement("izquierda");
                    izquierda.setTextContent(String.valueOf(valores_p2[index]));
                    p2.appendChild(izquierda);
                    index++;

                    derecha = doc.createElement("derecha");
                    derecha.setTextContent(String.valueOf(valores_p2[index]));
                    p2.appendChild(derecha);
                    index++;

                    punetazo_debil = doc.createElement("punetazo_debil");
                    punetazo_debil.setTextContent(String.valueOf(valores_p2[index]));
                    p2.appendChild(punetazo_debil);
                    index++;

                    patada_debil = doc.createElement("patada_debil");
                    patada_debil.setTextContent(String.valueOf(valores_p2[index]));
                    p2.appendChild(patada_debil);
                    index++;

                    punetazo_fuerte = doc.createElement("punetazo_fuerte");
                    punetazo_fuerte.setTextContent(String.valueOf(valores_p2[index]));
                    p2.appendChild(punetazo_fuerte);
                    index++;

                    patada_fuerte = doc.createElement("patada_fuerte");
                    patada_fuerte.setTextContent(String.valueOf(valores_p2[index]));
                    p2.appendChild(patada_fuerte);
                    index++;

                    aceptar = doc.createElement("aceptar");
                    aceptar.setTextContent(String.valueOf(valores_p2[index]));
                    p2.appendChild(aceptar);
                    index++;

                    pausa_atras = doc.createElement("pausa_atras");
                    pausa_atras.setTextContent(String.valueOf(valores_p2[index]));
                    p2.appendChild(pausa_atras);


                rootElement.appendChild(p2);



            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult file = new StreamResult(new File(filePath));


            transformer.transform(source, file);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateValues() {
        controlListener.update();
        audio_manager.update();
    }

    public options(){
        exit = false;
        pagina = pag.PAGINA_VOLUMEN;
        vol = new screenObject(0, 0,  1280, 720, new ImageIcon(imgPath_1).getImage(), Item_Type.MENU);
        p1 = new screenObject(0, 0,  1280, 720, new ImageIcon(imgPath_2).getImage(), Item_Type.MENU);
        p2 = new screenObject(0, 0,  1280, 720, new ImageIcon(imgPath_3).getImage(), Item_Type.MENU);
        fondo = vol;
        referenceTime = System.currentTimeMillis();

        readOptionsFile();

        try {
            this.f_1 = Font.createFont(Font.TRUETYPE_FONT, fontStream_1).deriveFont(36f);
            this.f_2 = Font.createFont(Font.ROMAN_BASELINE, fontStream_2).deriveFont(24f);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }


    public Boolean gestionMenu(Map<Item_Type, screenObject> screenObjects){
        long current = System.currentTimeMillis();
        if(current - referenceTime > 125){
            if(controlListener.getStatus(0, controlListener.ESC_INDEX)){
                saveOptions();
                updateValues();
                exit = true;
                //preguntar si quieres guardar
            }

            boolean init;
            int index;
            switch (pagina){
                case PAGINA_VOLUMEN:
                    if(controlListener.getStatus(0, controlListener.DE_INDEX)){
                        init = false;
                        index = 0;
                        for(int i = 0; i < elementos_vol.length; i++){
                            if(Character.isUpperCase(elementos_vol[i].charAt(0))){
                                init = true;
                                index = i;
                                break;
                            }
                        }
                        if(init){
                            // Subir volumen
                            int res = valores_vol[index] + 10;
                            if(res  > 100){
                                res = 100;
                            }
                            valores_vol[index] = res;
                        }else{
                            pagina = pag.PAGINA_P1;
                            fondo = p1;
                            titulos = new String[]{"audio", "PLAYER 1", "player 2"};
                        }
                        audio_manager.menu.play(menu_audio.indexes.move_cursor);
                    }
                    else if(controlListener.getStatus(0, controlListener.IZ_INDEX)){
                        init = false;
                        index = 0;
                        for(int i = 0; i < elementos_vol.length; i++){
                            if(Character.isUpperCase(elementos_vol[i].charAt(0))){
                                init = true;
                                index = i;
                                break;
                            }
                        }
                        if(init){
                            // Bajar volumen
                            int res = valores_vol[index] - 10;
                            if(res  < 0){
                                res = 0;
                            }
                            valores_vol[index] = res;
                            audio_manager.menu.play(menu_audio.indexes.move_cursor);
                        } else {
                            audio_manager.menu.play(menu_audio.indexes.error);
                        }
                    }
                    else if(controlListener.getStatus(0, controlListener.ENT_INDEX)) {
                        audio_manager.menu.play(menu_audio.indexes.error);
                    }
                    elementos_vol = moveCursor(elementos_vol);
                    break;
                case PAGINA_P1:
                    if(controlListener.getStatus(0, controlListener.DE_INDEX)){
                        init = false;
                        for(int i = 0; i < elementos_p1.length; i++){
                            if(Character.isUpperCase(elementos_p1[i].charAt(0))){
                                init = true;
                                break;
                            }
                        }
                        if(!init) {
                            pagina = pag.PAGINA_P2;
                            fondo = p2;
                            titulos = new String[]{"audio", "player 1", "PLAYER 2"};
                            audio_manager.menu.play(menu_audio.indexes.move_cursor);
                        } else {
                            audio_manager.menu.play(menu_audio.indexes.error);
                        }
                    }
                    else if(controlListener.getStatus(0, controlListener.IZ_INDEX)){
                        init = false;
                        for(int i = 0; i < elementos_p1.length; i++){
                            if(Character.isUpperCase(elementos_p1[i].charAt(0))){
                                init = true;
                                break;
                            }
                        }
                        if(!init) {
                            pagina = pag.PAGINA_VOLUMEN;
                            fondo = vol;
                            titulos = new String[]{"AUDIO", "player 1", "player 2"};
                            audio_manager.menu.play(menu_audio.indexes.move_cursor);
                        } else {
                            audio_manager.menu.play(menu_audio.indexes.error);
                        }
                    }
                    else if(controlListener.getStatus(0, controlListener.ENT_INDEX)){
                        init = false;
                        for(int i = 0; i < elementos_p1.length; i++){
                            if(Character.isUpperCase(elementos_p1[i].charAt(0))){
                                init = true;
                                break;
                            }
                        }
                        if(!init){
                            audio_manager.menu.play(menu_audio.indexes.error);
                        } else {
                            pagina = pag.PAGINA_P1_MAPEO;
                            audio_manager.menu.play(menu_audio.indexes.option_selected);
                        }
                    }
                    elementos_p1 = moveCursor(elementos_p1);
                    break;

                case PAGINA_P2:
                    if(controlListener.getStatus(0, controlListener.IZ_INDEX)){
                        init = false;
                        for(int i = 0; i < elementos_p2.length; i++){
                            if(Character.isUpperCase(elementos_p2[i].charAt(0))){
                                init = true;
                                break;
                            }
                        }
                        if(!init) {
                            pagina = pag.PAGINA_P1;
                            fondo = p1;
                            titulos = new String[]{"audio", "PLAYER 1", "player 2"};
                            audio_manager.menu.play(menu_audio.indexes.move_cursor);
                        } else {
                            audio_manager.menu.play(menu_audio.indexes.error);
                        }
                    }
                    else if(controlListener.getStatus(0, controlListener.ENT_INDEX)){
                        init = false;
                        for(int i = 0; i < elementos_p2.length; i++){
                            if(Character.isUpperCase(elementos_p2[i].charAt(0))){
                                init = true;
                                break;
                            }
                        }
                        if(!init){
                            audio_manager.menu.play(menu_audio.indexes.error);
                        } else {
                            pagina = pag.PAGINA_P2_MAPEO;
                            audio_manager.menu.play(menu_audio.indexes.option_selected);
                        }
                    }
                    elementos_p2 = moveCursor(elementos_p2);
                    break;
                case PAGINA_P1_MAPEO:
                    init = false;
                    index = 0;
                    for(int i = 0; i < elementos_p1.length; i++){
                        if(Character.isUpperCase(elementos_p1[i].charAt(0))){
                            init = true;
                            index = i;
                            break;
                        }
                    }
                    if(controlListener.anyKeyPressed()){
                        audio_manager.menu.play(menu_audio.indexes.option_selected);
                        int tecla = controlListener.getLastKey(0);
                        if(!alreadyUsed(tecla)){
                            valores_p1[index] = tecla;
                            audio_manager.menu.play(menu_audio.indexes.option_selected);
                        } else {
                            audio_manager.menu.play(menu_audio.indexes.error);
                        }
                        pagina = pag.PAGINA_P1;
                        audio_manager.menu.play(menu_audio.indexes.option_selected);
                    }

                    break;
                case PAGINA_P2_MAPEO:
                    init = false;
                    index = 0;
                    for(int i = 0; i < elementos_p2.length; i++){
                        if(Character.isUpperCase(elementos_p2[i].charAt(0))){
                            init = true;
                            index = i;
                            break;
                        }
                    }
                    if(controlListener.anyKeyPressed()){
                        int tecla = controlListener.getLastKey(0);
                        if(!alreadyUsed(tecla)){
                            valores_p2[index] = tecla;
                            audio_manager.menu.play(menu_audio.indexes.option_selected);
                        } else {
                            audio_manager.menu.play(menu_audio.indexes.error);
                        }
                        pagina = pag.PAGINA_P2;
                    }
                    break;
            }
            referenceTime = current;
        }

        screenObjects.put(Item_Type.MENU, fondo);
        return exit;
    }

    private boolean alreadyUsed(int key){
        for(int i = 0; i < valores_p1.length; i++){
            if(valores_p1[i] == key || valores_p2[i] == key){
                return true;
            }
        }
        return false;
    }

    private String[] moveCursor(String[] elem){
        //Gestion de subir
        if(controlListener.getStatus(0, controlListener.AB_INDEX)){
            boolean init = false;
            int index = 0;
            for(int i = 0; i < elem.length; i++){
                if(Character.isUpperCase(elem[i].charAt(0))){
                    init = true;
                    index = i;
                    break;
                }
            }
            if(init){
                menu_audio.indexes sonido = menu_audio.indexes.move_cursor;
                if(index + 1 >=elem.length){
                    index--;
                    sonido = menu_audio.indexes.error;
                }
                String aux = elem[index].toLowerCase();
                elem[index] = aux;
                aux = elem[index+1].toUpperCase();
                elem[index+1] = aux;
                audio_manager.menu.play(sonido);
            } else {
                String aux = elem[0].toUpperCase();
                elem[0] = aux;
                audio_manager.menu.play(menu_audio.indexes.move_cursor);
            }
        }
        //Gestion de bajar
        if(controlListener.getStatus(0, controlListener.AR_INDEX)){
            boolean init = false;
            int index = 0;
            for(int i = 0; i < elem.length; i++){
                if(Character.isUpperCase(elem[i].charAt(0))){
                    init = true;
                    index = i;
                    break;
                }
            }
            if(init){
                if(index == 0){
                    String aux = elem[index].toLowerCase();
                    elem[index] = aux;
                } else {
                    String aux = elem[index].toLowerCase();
                    elem[index] = aux;
                    aux = elem[index-1].toUpperCase();
                    elem[index-1] = aux;
                }
            }
            audio_manager.menu.play(menu_audio.indexes.move_cursor);
        }
        return elem;
    }


    public void printOptions(Graphics2D g){

        g.setFont(f_1);
        for(int i = 0; i < titulos.length; i++){
            if(Character.isUpperCase(titulos[i].charAt(0))){
                g.setColor(new Color(255, 221, 0));
            } else {
                g.setColor(new Color(140, 120, 0));
            }
            g.drawString(titulos[i], xTitulos[i], 135);
        }

        int y = 215;
        String[] elementos_mostrar = elementos[pagina.getVal()];
        int[] valores_mostrar = valores[pagina.getVal()];
        for(int i = 0; i < elementos_mostrar.length; i++){
            g.setFont(f_2);
            if(Character.isUpperCase(elementos_mostrar[i].charAt(0))){
                if(pagina == pag.PAGINA_P1_MAPEO || pagina == pag.PAGINA_P2_MAPEO){
                    g.setColor(new Color(100,255 , 0));
                } else {

                    g.setColor(new Color(255, 221, 0));
                }
            } else {
                g.setColor(new Color(140, 120, 0));
            }
            g.drawString(elementos_mostrar[i], 150, y);


            if(pagina == pag.PAGINA_VOLUMEN){
                g.drawString(String.valueOf(valores_mostrar[i]), 650 + 4*valores_mostrar[i], y);
                y+=100;
            } else {
                g.drawString(KeyEvent.getKeyText(valores_mostrar[i]), 650, y);
                y+=45;
            }
        }

    }

}
