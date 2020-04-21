package lib.menus;


import lib.Enums.Item_Type;
import lib.input.controlListener;
import lib.objects.screenObject;
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
import java.util.Map;

public class options {

//TODO METER MAQUINA DE ESTADOS, CAMBIAR UN POCO ORGANIZACIÓN DE LA FUNCIÓN PRINCIPAL

    private String[] elementos = {
            "Volumen", "Volumen general", "Volumen musica", "Volumen voces", "Volumen efectos especiales",
            "Controles jugador 1", "Arriba", "Abajo", "Izquierda", "Derecha", "Puñetazo débil", "Patada débil", "Puñetazo fuerte", "Patada fuerte", "Aceptar", "Pausa/Atrás",
            "Controles jugador 2", "Arriba", "Abajo", "Izquierda", "Derecha", "Puñetazo débil", "Patada débil", "Puñetazo fuerte", "Patada fuerte", "Aceptar", "Pausa/Atrás"
    };

    private String salir = "Descartar cambios";
    private String guardar = "Guardar cambios";


    private Boolean exit;
    private Boolean[] mostrando;
    private String[] valores;
    private int actual, posicion;
    private int maxElementos = 8;

    private long referenceTime;
    private String filePath = "files/options.xml";
    private String imgPath = "assets/sprites/menu/options/";

    public void updateTime() {
        referenceTime = System.currentTimeMillis();
    }

    private Font f_1, f_2, f_3;
    private screenObject fondo;

    private enum estado_options {NAVEGACION_PRINCIPAL, NAVEGACION_SECUNDARIA, MAPEO_TECLA, CAMBIO_VOLUMEN, SALIR, GUARDAR, OPCION_SALIR, OPCION_GUARDAR}

    private estado_options estado, opcion;

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

            int indice = 1;

            for (int i = 0; i < vol.getLength(); i++) {
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
            for (int i = 0; i < p2.getLength(); i++) {
                Node node = p2.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    valores[indice] = node.getTextContent();
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
            index++;
            doc.appendChild(rootElement);

                Element vol = doc.createElement("volumen");

                    Element vol_general = doc.createElement("vol_general");
                    vol_general.setTextContent(valores[index]);
                    vol.appendChild(vol_general);
                    index++;

                    Element vol_musica = doc.createElement("vol_musica");
                    vol_musica.setTextContent(valores[index]);
                    vol.appendChild(vol_musica);
                    index++;

                    Element vol_voces = doc.createElement("vol_voces");
                    vol_voces.setTextContent(valores[index]);
                    vol.appendChild(vol_voces);
                    index++;

                    Element vol_efectos = doc.createElement("vol_efectos");
                    vol_efectos.setTextContent(valores[index]);
                    vol.appendChild(vol_efectos);
                    index++;

                rootElement.appendChild(vol);

                index++;

                Element p1 = doc.createElement("controles_jugador_1");

                    Element arriba = doc.createElement("arriba");
                    arriba.setTextContent(valores[index]);
                    p1.appendChild(arriba);
                    index++;

                    Element abajo = doc.createElement("abajo");
                    abajo.setTextContent(valores[index]);
                    p1.appendChild(abajo);
                    index++;

                    Element izquierda = doc.createElement("izquierda");
                    izquierda.setTextContent(valores[index]);
                    p1.appendChild(izquierda);
                    index++;

                    Element derecha = doc.createElement("derecha");
                    derecha.setTextContent(valores[index]);
                    p1.appendChild(derecha);
                    index++;

                    Element punetazo_debil = doc.createElement("punetazo_debil");
                    punetazo_debil.setTextContent(valores[index]);
                    p1.appendChild(punetazo_debil);
                    index++;

                    Element patada_debil = doc.createElement("patada_debil");
                    patada_debil.setTextContent(valores[index]);
                    p1.appendChild(patada_debil);
                    index++;

                    Element punetazo_fuerte = doc.createElement("punetazo_fuerte");
                    punetazo_fuerte.setTextContent(valores[index]);
                    p1.appendChild(punetazo_fuerte);
                    index++;

                    Element patada_fuerte = doc.createElement("patada_fuerte");
                    patada_fuerte.setTextContent(valores[index]);
                    p1.appendChild(patada_fuerte);
                    index++;

                    Element aceptar = doc.createElement("aceptar");
                    aceptar.setTextContent(valores[index]);
                    p1.appendChild(aceptar);
                    index++;

                    Element pausa_atras = doc.createElement("pausa_atras");
                    pausa_atras.setTextContent(valores[index]);
                    p1.appendChild(pausa_atras);
                    index++;


                rootElement.appendChild(p1);

                index++;

                Element p2 = doc.createElement("controles_jugador_2");

                    arriba = doc.createElement("arriba");
                    arriba.setTextContent(valores[index]);
                    p2.appendChild(arriba);
                    index++;

                    abajo = doc.createElement("abajo");
                    abajo.setTextContent(valores[index]);
                    p2.appendChild(abajo);
                    index++;

                    izquierda = doc.createElement("izquierda");
                    izquierda.setTextContent(valores[index]);
                    p2.appendChild(izquierda);
                    index++;

                    derecha = doc.createElement("derecha");
                    derecha.setTextContent(valores[index]);
                    p2.appendChild(derecha);
                    index++;

                    punetazo_debil = doc.createElement("punetazo_debil");
                    punetazo_debil.setTextContent(valores[index]);
                    p2.appendChild(punetazo_debil);
                    index++;

                    patada_debil = doc.createElement("patada_debil");
                    patada_debil.setTextContent(valores[index]);
                    p2.appendChild(patada_debil);
                    index++;

                    punetazo_fuerte = doc.createElement("punetazo_fuerte");
                    punetazo_fuerte.setTextContent(valores[index]);
                    p2.appendChild(punetazo_fuerte);
                    index++;

                    patada_fuerte = doc.createElement("patada_fuerte");
                    patada_fuerte.setTextContent(valores[index]);
                    p2.appendChild(patada_fuerte);
                    index++;

                    aceptar = doc.createElement("aceptar");
                    aceptar.setTextContent(valores[index]);
                    p2.appendChild(aceptar);
                    index++;

                    pausa_atras = doc.createElement("pausa_atras");
                    pausa_atras.setTextContent(valores[index]);
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
        //volumen.update
    }

    public options(){
        exit = false;
        estado = estado_options.NAVEGACION_PRINCIPAL;
        fondo = new screenObject(0, 0,  1280, 720, new ImageIcon(imgPath  + "menu.png").getImage(), Item_Type.MENU);
        referenceTime = System.currentTimeMillis();
        actual = 1;
        posicion = 0;

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
        //dependiendo del estado devolver una cosa u otra
        return exit;
    }



    public void printOptions(Graphics2D g){

        long current = System.currentTimeMillis();
        if(current - referenceTime > 100.0){
            switch (estado){
                case NAVEGACION_PRINCIPAL:
                    if(controlListener.getStatus(1, controlListener.AR_INDEX)) {
                        if(posicion > 0){
                            posicion--;
                            mostrando[posicion] = true;
                            mostrando[posicion + maxElementos] = false;
                        }
                        if(actual > 0){
                            actual--;
                            if( elementos[actual].equals("Volumen")){
                                actual++;
                            } else if( elementos[actual].equals("Controles jugador 1") || elementos[actual].equals("Controles jugador 2") ){
                                actual--;
                            }
                        }
                    } else if(controlListener.getStatus(1, controlListener.AB_INDEX)){
                        if(posicion + maxElementos < elementos.length){
                            mostrando[posicion] = false;
                            mostrando[posicion + maxElementos] = true;
                            posicion++;
                        }
                        if(actual < elementos.length - 1){
                            actual++;
                            if( elementos[actual].equals("Volumen") || elementos[actual].equals("Controles jugador 1") || elementos[actual].equals("Controles jugador 2") ){
                                actual++;
                            }
                        }
                    } else if(controlListener.getStatus(1, controlListener.IZ_INDEX)) {
                        estado = estado_options.NAVEGACION_SECUNDARIA;
                        opcion = estado_options.OPCION_SALIR;
                    } else if(controlListener.getStatus(1, controlListener.DE_INDEX)){
                        estado = estado_options.NAVEGACION_SECUNDARIA;
                        opcion = estado_options.OPCION_GUARDAR;
                    } else if(controlListener.getStatus(1, controlListener.ENT_INDEX)){
                        if(actual < 5){
                            estado = estado_options.CAMBIO_VOLUMEN;
                        } else {
                            estado = estado_options.MAPEO_TECLA;
                        }
                    }
                    break;
                case NAVEGACION_SECUNDARIA:
                    if(controlListener.getStatus(1, controlListener.AR_INDEX)) {
                        estado = estado_options.NAVEGACION_PRINCIPAL;
                    } else if(controlListener.getStatus(1, controlListener.AB_INDEX)){
                        estado = estado_options.NAVEGACION_PRINCIPAL;
                    } else if(controlListener.getStatus(1, controlListener.IZ_INDEX)) {
                        opcion = estado_options.OPCION_SALIR;
                    } else if(controlListener.getStatus(1, controlListener.DE_INDEX)){
                        opcion = estado_options.OPCION_GUARDAR;
                    } else if(controlListener.getStatus(1, controlListener.ENT_INDEX)){
                        if(opcion == estado_options.OPCION_GUARDAR){
                            estado = estado_options.GUARDAR;
                        } else if(opcion == estado_options.OPCION_SALIR){
                            estado = estado_options.SALIR;
                        }
                    }
                    break;
                case MAPEO_TECLA:
                    if(controlListener.anyKeyPressed()){
                        int tecla = controlListener.getLastKey(0);
                        valores[actual] = String.valueOf(tecla);
                        estado = estado_options.NAVEGACION_PRINCIPAL;
                    }
                    break;
                case CAMBIO_VOLUMEN:
                    int aux = Integer.parseInt(valores[actual]);
                    if(controlListener.getStatus(1, controlListener.ENT_INDEX)){
                        estado = estado_options.NAVEGACION_PRINCIPAL;
                    } else if(controlListener.getStatus(1, controlListener.IZ_INDEX)) {
                        aux--;
                    } else if(controlListener.getStatus(1, controlListener.DE_INDEX)) {
                        aux++;
                    }
                    if (aux > 100) { aux = 100; }
                    else if (aux < 0) { aux = 0; }
                    valores[actual] = String.valueOf(aux);
                    break;
                case SALIR:
                    exit = true;
                    break;
                case GUARDAR:
                    saveOptions();
                    updateValues();
                    estado = estado_options.SALIR;
                    break;
            }



            referenceTime = current;
        }




        int x = 120;
        int y = 160;


        switch (estado){
            case NAVEGACION_PRINCIPAL:
                g.setFont(f_3);
                g.setColor(Color.GREEN);
                g.drawString(salir, 50, 695);
                g.drawString(guardar, 935, 695);

                for(int i = 0; i < elementos.length; i++) {
                    if( mostrando[i] ){
                        if( elementos[i].equals("Volumen")  ||elementos[i].equals("Controles jugador 1")  || elementos[i].equals("Controles jugador 2")  ){
                            g.setFont(f_1);
                            g.setColor(Color.YELLOW);
                            g.drawString(elementos[i], x, y);
                        } else {
                            g.setFont(f_2);
                            if(i == actual){
                                g.setColor(Color.RED);
                            } else {
                                g.setColor(Color.GREEN);
                            }
                            g.drawString(elementos[i], x+130, y);
                            if( i > 5){
                                g.drawString(KeyEvent.getKeyText(Integer.parseInt(valores[i])), x+800, y);
                            } else {
                                g.drawString(valores[i], x+800, y);
                            }
                        }

                        y+=60;
                    }
                }
                break;
            case NAVEGACION_SECUNDARIA:
                g.setFont(f_3);
                if(opcion == estado_options.OPCION_GUARDAR) {
                    g.setColor(Color.GREEN);
                    g.drawString(salir, 50, 695);
                    g.setColor(Color.RED);
                    g.drawString(guardar, 935, 695);
                } else if(opcion == estado_options.OPCION_SALIR) {
                    g.setColor(Color.RED);
                    g.drawString(salir, 50, 695);
                    g.setColor(Color.GREEN);
                    g.drawString(guardar, 935, 695);
                }
                for(int i = 0; i < elementos.length; i++) {
                    if( mostrando[i] ){
                        if( elementos[i].equals("Volumen")  ||elementos[i].equals("Controles jugador 1")  || elementos[i].equals("Controles jugador 2")  ){
                            g.setFont(f_1);
                            g.setColor(Color.YELLOW);
                            g.drawString(elementos[i], x, y);
                        } else {
                            g.setFont(f_2);
                            g.setColor(Color.GREEN);
                            g.drawString(elementos[i], x+130, y);
                            if( i > 5){
                                g.drawString(KeyEvent.getKeyText(Integer.parseInt(valores[i])), x+800, y);
                            } else {
                                g.drawString(valores[i], x+800, y);
                            }
                        }
                        y+=60;
                    }
                }
                break;
            case MAPEO_TECLA:
                g.setFont(f_3);
                g.setColor(Color.GREEN);
                g.drawString(salir, 50, 695);
                g.drawString(guardar, 935, 695);

                for(int i = 0; i < elementos.length; i++) {
                    if( mostrando[i] ){
                        if( elementos[i].equals("Volumen") || elementos[i].equals("Controles jugador 1")  || elementos[i].equals("Controles jugador 2")  ){
                            g.setFont(f_1);
                            g.setColor(Color.YELLOW);
                            g.drawString(elementos[i], x, y);
                        } else {
                            g.setFont(f_2);
                            if(i == actual){
                                g.setColor(Color.RED);
                                g.drawString("...", x+800, y);
                            } else {
                                g.setColor(Color.GREEN);
                                if( i > 5){
                                    g.drawString(KeyEvent.getKeyText(Integer.parseInt(valores[i])), x+800, y);
                                } else {
                                    g.drawString(valores[i], x+800, y);
                                }
                            }
                            g.drawString(elementos[i], x+130, y);
                        }
                        y+=60;
                    }
                }
                break;

            case CAMBIO_VOLUMEN:
                g.setFont(f_3);
                g.setColor(Color.GREEN);
                g.drawString(salir, 50, 695);
                g.drawString(guardar, 935, 695);
                for(int i = 0; i < elementos.length; i++) {
                    if( mostrando[i] ){
                        if( elementos[i].equals("Volumen") || elementos[i].equals("Controles jugador 1")  || elementos[i].equals("Controles jugador 2")  ){
                            g.setFont(f_1);
                            g.setColor(Color.YELLOW);
                            g.drawString(elementos[i], x, y);
                        } else {
                            g.setFont(f_2);
                            if(i == actual){
                                g.setColor(Color.RED);
                                g.drawString("-  " + valores[i] + "  +", x+800, y);
                            } else {
                                g.setColor(Color.GREEN);
                                if( i > 5){
                                    g.drawString(KeyEvent.getKeyText(Integer.parseInt(valores[i])), x+800, y);
                                } else {
                                    g.drawString(valores[i], x+800, y);
                                }
                            }
                            g.drawString(elementos[i], x+130, y);
                        }
                        y+=60;
                    }
                }
                break;
        }





    }

}
