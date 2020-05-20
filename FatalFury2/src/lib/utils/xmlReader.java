package lib.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class xmlReader {
    public static Document open(InputStream is) throws SAXException, IOException,
            ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setNamespaceAware(true);
        // dbf.setCoalescing(true);
        // dbf.setExpandEntityReferences(true);

        DocumentBuilder db = null;
        db = dbf.newDocumentBuilder();
        db.setEntityResolver(new NullResolver());

        // db.setErrorHandler( new MyErrorHandler());

        return db.parse(is);
    }
    private static String optionsFilePath = System.getProperty("user.dir") + "/.files/options.xml";

    public static boolean IsFullscren(){
        try {

            File input = new File(optionsFilePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(input);
            doc.getDocumentElement().normalize();


            NodeList p1 = doc.getElementsByTagName("general").item(0).getChildNodes();


            int vols[] = new int[5];
            int indice = 0;
            for (int i = 0; i < p1.getLength(); i++) {
                Node node = p1.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    vols[indice] = Integer.parseInt(node.getTextContent());
                    indice++;
                }
            }

            if(vols[4] == 1){
                return  true;
            }
            return false;



        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
