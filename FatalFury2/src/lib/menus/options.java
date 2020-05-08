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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class options {

    private String[] titulos = {"VOLUMEN", "jugador 1", "jugador 2"};
    private int[] xTitulos = {150, 135 + 340, 135 + 340 + 360};

    private String[] elementos_vol =  {
            "general", "Music", "Voices", "Special effects"
    };
    private String[] elementos_control ={
            "Arriba", "Abajo", "Izquierda", "Derecha", "Puñetazo débil", "Patada débil", "Puñetazo fuerte", "Patada fuerte", "Aceptar", "Pausa/Atrás"
    };

    private String[][] elementos = {elementos_vol, elementos_control, elementos_control};

    private int[] valores_vol = new int[elementos_vol.length];
    private int[] valores_p1 = new int[elementos_control.length];
    private int[] valores_p2 = new int[elementos_control.length];
    private int[][] valores = {valores_vol, valores_p1, valores_p2};


    private Boolean exit;
    private long referenceTime;


    private String filePath = System.getProperty("user.dir") + "/.files/options.xml";
    private URL imgPath_1 = this.getClass().getResource("/assets/sprites/menu/options/menu_vol.png");
    private URL imgPath_2 = this.getClass().getResource("/assets/sprites/menu/options/menu_p1.png");
    private URL imgPath_3 = this.getClass().getResource("/assets/sprites/menu/options/menu_p2.png");


    private InputStream fontStream_1 = this.getClass().getResourceAsStream("/files/fonts/m04b.TTF");
    private InputStream fontStream_2 = this.getClass().getResourceAsStream("/files/fonts/m04.TTF");
    public void updateTime() {
        referenceTime = System.currentTimeMillis();
    }

    private Font f_1, f_2;
    private screenObject fondo, vol, p1, p2;

    private enum pag {PAGINA_VOLUMEN, PAGINA_P1, PAGINA_P2}

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
        if(current - referenceTime > 150.0){
            switch (pagina){
                case PAGINA_VOLUMEN:
                    if(controlListener.getStatus(0, controlListener.DE_INDEX)){
                        pagina = pag.PAGINA_P1;
                        fondo = p1;
                        titulos = new String[]{"volumen", "JUGADOR 1", "jugador 2"};
                        audio_manager.menu.play(menu_audio.indexes.move_cursor);
                    }
                    break;
                case PAGINA_P1:
                    if(controlListener.getStatus(0, controlListener.DE_INDEX)){
                        pagina = pag.PAGINA_P2;
                        fondo = p2;
                        titulos = new String[]{"volumen", "jugador 1", "JUGADOR 2"};
                        audio_manager.menu.play(menu_audio.indexes.move_cursor);
                    } if(controlListener.getStatus(0, controlListener.IZ_INDEX)){
                        pagina = pag.PAGINA_VOLUMEN;
                        fondo = vol;
                        titulos = new String[]{"VOLUMEN", "jugador 1", "jugador 2"};
                        audio_manager.menu.play(menu_audio.indexes.move_cursor);
                    }
                    break;
                case PAGINA_P2:
                    if(controlListener.getStatus(0, controlListener.IZ_INDEX)){
                        pagina = pag.PAGINA_P1;
                        fondo = p1;
                        titulos = new String[]{"volumen", "JUGADOR 1", "jugador 2"};
                        audio_manager.menu.play(menu_audio.indexes.move_cursor);
                    }
                    break;
            }
            referenceTime = current;
        }

        screenObjects.put(Item_Type.MENU, fondo);
        return exit;
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

        int y = 235;
        String[] mostrar = elementos[pagina.ordinal()];
        for(int i = 0; i < mostrar.length; i++){
            g.setFont(f_2);
            g.drawString(mostrar[i], 150, y);
            y+=100;
        }

    }

}
