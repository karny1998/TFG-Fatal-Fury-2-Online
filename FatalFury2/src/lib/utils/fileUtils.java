package lib.utils;

import java.io.*;
import java.net.URL;

/**
 * The type File utils.
 */
// Clase que implementa un método de copia de ficheros
public class fileUtils {
    /**
     * Copy.
     *
     * @param s the s
     * @param d the d
     * @throws IOException the io exception
     */
    public static void copy(String s, String d) throws IOException {
        File dest = new File(d);
        URL src = fileUtils.class.getResource(s);
        InputStream is = null;
        OutputStream os = null;
        try {
            is = src.openStream();
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

}
