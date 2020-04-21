package lib.menus;


import lib.Enums.Item_Type;
import lib.input.controlListener;
import lib.objects.screenObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class options {

//TODO METER MAQUINA DE ESTADOS, CAMBIAR UN POCO ORGANIZACIÓN DE LA FUNCIÓN PRINCIPAL

    private String[] elementos = {
            "Volumen",  "Volumen general",  "Volumen musica",   "Volumen voces",    "Volumen efectos especiales",
            "Controles jugador 1", "Arriba",   "Abajo",    "Izquierda",    "Derecha",  "Puñetazo débil",   "Patada débil", "Puñetazo fuerte",  "Patada fuerte",    "Aceptar",  "Pausa/Atrás",
            "Controles jugador 2", "Arriba",   "Abajo",    "Izquierda",    "Derecha",  "Puñetazo débil",   "Patada débil", "Puñetazo fuerte",  "Patada fuerte",    "Aceptar",  "Pausa/Atrás"
    };

    private String salir = "Descartar cambios";
    private String guardar = "Guardar cambios";

    private Boolean realizandoCambios = true;

    private Boolean[] mostrando;
    private String[] valores;
    private int actual, posicion;
    private int maxElementos = 8;
    private int opcion;

    private Boolean cambiando = false;

    private long referenceTime;
    private String filePath = "assets/sprites/menu/options/options.xml";
    private String imgPath = "assets/sprites/menu/options/";

    public void updateTime(){referenceTime = System.currentTimeMillis();}

    private Font f_1, f_2, f_3;
    private screenObject fondo;

    private void readOptionsFile(){
        try
        {
            File input = new File(filePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();

            Node opciones = doc.getChildNodes().item(0);
            NodeList vol = opciones.getChildNodes().item(1).getChildNodes();
            NodeList p1 = opciones.getChildNodes().item(3).getChildNodes();
            NodeList p2 = opciones.getChildNodes().item(5).getChildNodes();

            int indice = 1;

            for (int i = 0; i < vol.getLength(); i++){
                Node node = vol.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    valores[indice] = node.getTextContent();
                    indice++;
                }
            }
            indice++;
            for (int i = 0; i < p1.getLength(); i++) {
                Node node = p1.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    valores[indice] = node.getTextContent();
                    indice++;
                }
            }
            indice++;
            for (int i = 0; i < p2.getLength(); i++){
                Node node = p2.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    valores[indice] = node.getTextContent();
                    indice++;
                }
            }


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public options(){

        fondo = new screenObject(0, 0,  1280, 720, new ImageIcon(imgPath  + "menu.png").getImage(), Item_Type.MENU);
        referenceTime = System.currentTimeMillis();
        actual = 1;
        posicion = 0;
        opcion = 0;

        mostrando = new Boolean[elementos.length];
        for(int i = 0; i < maxElementos; i++) {
            mostrando[i] = true;
        }
        for(int i = maxElementos; i < elementos.length; i++) {
            mostrando[i] = false;
        }

        valores = new String[elementos.length];
        valores[0] = "";
        valores[5] = "";
        valores[16] = "";



        readOptionsFile();


        this.f_1 = new Font("Orbitron", Font.ROMAN_BASELINE, 48);
        this.f_2 = new Font("Orbitron", Font.ROMAN_BASELINE, 24);
        this.f_3 = new Font("Orbitron", Font.ROMAN_BASELINE, 32);


    }


    public Boolean gestionMenu(Map<Item_Type, screenObject> screenObjects){
        screenObjects.put(Item_Type.MENU, fondo);
        return false;
    }



    public void printOptions(Graphics2D g){

        long current = System.currentTimeMillis();
        if(current - referenceTime > 100.0){
            if(controlListener.getStatus(1, controlListener.AR_INDEX)) {
                if(!realizandoCambios){
                    realizandoCambios = true;
                }

                if(posicion > 0){
                    posicion--;
                    mostrando[posicion] = true;
                    mostrando[posicion + maxElementos] = false;
                }
                if(actual > 0){
                    actual--;
                    if( elementos[actual].equals("Volumen")){
                        actual++;
                    }
                    else if(    elementos[actual].equals("Controles jugador 1")  ||
                                elementos[actual].equals("Controles jugador 2")  ){
                        actual--;
                    }
                }
            }
            else if(controlListener.getStatus(1, controlListener.AB_INDEX)){
                if(!realizandoCambios){
                    realizandoCambios = true;
                }
                if(posicion + maxElementos < elementos.length){
                    mostrando[posicion] = false;
                    mostrando[posicion + maxElementos] = true;
                    posicion++;
                }
                if(actual < elementos.length - 1){
                    actual++;
                    if( elementos[actual].equals("Volumen")              ||
                        elementos[actual].equals("Controles jugador 1")  ||
                        elementos[actual].equals("Controles jugador 2")  ){
                        actual++;
                    }
                }
            }
            else if(controlListener.getStatus(1, controlListener.IZ_INDEX)){
                if(realizandoCambios){
                    realizandoCambios = false;
                }
                opcion = 0;

            }
            else if(controlListener.getStatus(1, controlListener.DE_INDEX)){
                if(realizandoCambios){
                    realizandoCambios = false;
                }
                opcion = 1;

            }else if(controlListener.getStatus(1, controlListener.ENT_INDEX)){
                if(realizandoCambios){
                    //PEDIR TECLA CONCRETA
                    if(cambiando){
                        cambiando = false;
                    } else {
                        cambiando = true;
                    }
                } else {
                    switch (opcion){
                        case 0:
                            //ESCRIBIR EN FICHERO Y SALIR
                            break;
                        case 1:
                            //SALIR
                            break;
                    }
                }

            }
            referenceTime = current;
        }




        int x = 120;
        int y = 160;


        if(!realizandoCambios){
            g.setFont(f_3);
            switch (opcion){
                case 0:
                    g.setColor(Color.RED);
                    g.drawString(guardar, 50, 695);
                    g.setColor(Color.GREEN);
                    g.drawString(salir, 935, 695);
                    break;
                case 1:
                    g.setColor(Color.GREEN);
                    g.drawString(guardar, 50, 695);
                    g.setColor(Color.RED);
                    g.drawString(salir, 935, 695);
                    break;
            }
        } else {
            g.setFont(f_3);
            g.setColor(Color.GREEN);
            g.drawString(guardar, 50, 695);
            g.drawString(salir, 935, 695);
        }

        for(int i = 0; i < elementos.length; i++) {
            if( mostrando[i] ){
                if( elementos[i].equals("Volumen")              ||
                    elementos[i].equals("Controles jugador 1")  ||
                    elementos[i].equals("Controles jugador 2")  ){
                    g.setFont(f_1);
                    g.setColor(Color.YELLOW);
                    g.drawString(elementos[i], x, y);
                } else {
                    g.setFont(f_2);
                    g.setColor(Color.GREEN);
                    g.drawString(elementos[i], x+130, y);
                }

                if(i == actual && realizandoCambios){
                    g.setColor(Color.RED);
                    g.drawString(elementos[i], x+130, y);
                }


                if(cambiando && i == actual){
                    g.drawString("INTRODUCE VALOR", x+800, y);
                } else{
                    g.drawString(valores[i], x+800, y);
                }


                y+=60;
            }

        }

    }

}
