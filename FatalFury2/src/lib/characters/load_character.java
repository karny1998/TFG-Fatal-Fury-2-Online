package lib.characters;

import lib.Enums.Animation_type;
import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.objects.animation;
import lib.objects.movement;
import lib.objects.screenObject;
import lib.sound.fight_audio;

import javax.swing.*;
import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Random;

/**
 * The type Load character.
 */
public class load_character {

    /**
     * Filter image image.
     *
     * @param img    the img
     * @param charac the charac
     * @param method the method
     * @return the image
     */
    public static Image filterImage(Image img, String charac, int method) {
        if (img == null) {
            return img;
        }
        final ImageFilter filter = new RGBImageFilter() {
            @Override
            public final int filterRGB(final int x, final int y, final int rgb) {
                Color currentColor = new Color(rgb);
                if((rgb & 0xFF000000) == 0x00000000){
                    return rgb;
                }
                if(
                        ((charac.equals("terry") || charac.equals("mai")) && ((double)currentColor.getBlue()/(double)currentColor.getRed()) < 0.5 && ((double)currentColor.getGreen()/(double)currentColor.getRed()) < 0.5)
                ){
                    Color c;
                    if(method == 2) {
                        c = new Color(currentColor.getGreen(), currentColor.getRed(), currentColor.getBlue(), currentColor.getAlpha());
                    }
                    else if(method == 3) {
                        c = new Color(currentColor.getRed(), currentColor.getRed(), currentColor.getBlue(), currentColor.getAlpha());
                    }
                    else if(method == 4) {
                        c = new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getRed(), currentColor.getAlpha());
                    }
                    else{
                        c = new Color(currentColor.getBlue(), currentColor.getGreen(), currentColor.getRed(), currentColor.getAlpha());
                    }
                    return c.getRGB();
                }
                if(charac.equals("andy") && ((double)currentColor.getBlue()/(double)currentColor.getRed()) > 0.6 && ((double)currentColor.getGreen()/(double)currentColor.getRed()) > 0.8
                        && ((double)currentColor.getBlue()/(double)currentColor.getRed()) < 0.9){
                    Color c;
                    if(method == 2) {
                        c = new Color(currentColor.getGreen(), 0, currentColor.getBlue(), currentColor.getAlpha());
                    }
                    else if(method == 3) {
                        c = new Color(currentColor.getRed(), currentColor.getRed(), 0, currentColor.getAlpha());
                    }
                    else if(method == 4) {
                        c = new Color(0, currentColor.getGreen(), currentColor.getRed(), currentColor.getAlpha());
                    }
                    else{
                        int aux = currentColor.getBlue() + 50;
                        if(aux > 255){
                            aux = 255;
                        }
                        c = new Color(0, aux, 0, currentColor.getAlpha());
                    }
                    return c.getRGB();
                }
                return rgb;
            }
        };

        final ImageProducer ip = new FilteredImageSource(img.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    /**
     * Read aux string.
     *
     * @param b the b
     * @return the string
     */
    public String readAux(BufferedReader b) {
        String lineaBuena = "";
        try {
            while ((lineaBuena = b.readLine()) != null) {
                if (lineaBuena.charAt(0) != '#') { break; }
            }
        }
        catch (Exception ignored) { }
        return lineaBuena;
    }


    /**
     * Generate movs.
     *
     * @param charac        the charac
     * @param nJ            the n j
     * @param combos        the combos
     * @param combosInverse the combos inverse
     * @param movs          the movs
     * @param multiplier    the multiplier
     */
    public void generateMovs(String charac, int nJ, Map<String, Movement> combos, Map<Movement,String> combosInverse, Map<Movement, movement> movs, double multiplier) {
        String path = "/assets/sprites/characters/" + charac + "/";
        try {
            String value;
            InputStream f = this.getClass().getResourceAsStream(path + "load.txt");
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            int nMovs = Integer.valueOf(b.readLine()), i = 0;
            Random random = new Random();
            int filterMethod = 1 + Math.abs(random.nextInt()) % 4;

            while((value = b.readLine())!=null && i < nMovs) {
                if (value.charAt(0) == '#') {
                    continue;
                }
                String fold = value;
                Movement movId = Movement.valueOf(readAux(b));
                String combo = readAux(b);
                String aux = readAux(b);
                boolean hasSound = !aux.equals("NONE");
                fight_audio.voice_indexes sound = fight_audio.voice_indexes.Hit_1;
                if(hasSound) {
                    sound = fight_audio.voice_indexes.valueOf(aux);
                }
                Boolean hasEnd = Boolean.valueOf(readAux(b));
                Animation_type aType = Animation_type.valueOf(readAux(b));
                int dmg = Integer.valueOf(readAux(b));
                Boolean hasHit = Boolean.valueOf(readAux(b));
                int xHit = 0, yHit = 0, xHurt = 0, yHurt = 0, xCover = 0, yCover = 0,
                        wHit = 0, hHit = 0, wHurt = 0, hHurt = 0, wCover = 0, hCover = 0,
                        nFrames = 0;
                if (hasHit){
                    xHit = (int) (Integer.valueOf(readAux(b)) * multiplier);
                    yHit = (int) (Integer.valueOf(readAux(b)) * multiplier);
                    wHit = (int) (Integer.valueOf(readAux(b)) * multiplier);
                    hHit = (int) (Integer.valueOf(readAux(b)) * multiplier);
                }
                Boolean hasCover = Boolean.valueOf(readAux(b));
                if (hasCover){
                    xCover = (int) (Integer.valueOf(readAux(b)) * multiplier);
                    yCover = (int) (Integer.valueOf(readAux(b)) * multiplier);
                    wCover = (int) (Integer.valueOf(readAux(b)) * multiplier);
                    hCover = (int) (Integer.valueOf(readAux(b)) * multiplier);
                }
                xHurt = (int) (Integer.valueOf(readAux(b)) * multiplier);
                yHurt = (int) (Integer.valueOf(readAux(b)) * multiplier);
                wHurt = (int) (Integer.valueOf(readAux(b)) * multiplier);
                hHurt = (int) (Integer.valueOf(readAux(b)) * multiplier);
                nFrames = Integer.valueOf(readAux(b));

                animation anim = new animation();
                anim.setType(aType);
                anim.setHasEnd(hasEnd);
                anim.setHasSound(hasSound);
                if(hasSound) {
                    anim.setSoundType(sound);
                }
                boolean player1 = false;
                switch (nJ){
                    case 1:
                        player1 = true;
                        break;
                    case 2:
                        player1 = false;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + nJ);
                }

                anim.setPlayer1(player1);
                anim.setHurtBox(xHurt,yHurt,wHurt,hHurt);

                if(hasHit){
                    anim.setHitbox(xHit,yHit,wHit,hHit);
                }
                if(hasCover){
                    anim.setCoverbox(xCover,yCover,wCover,hCover);
                }

                int j = 1;
                screenObject s;
                int xF = 0, yF = 0, wF = 0, hF = 0,
                        ixF = 0, iyF = 0, wxF = 0, wyF = 0;
                double tF = 0.0;
                Boolean stop = false;
                while(j <= nFrames){
                    xF = (int) (Integer.valueOf(readAux(b)) * multiplier);
                    yF = (int) (Integer.valueOf(readAux(b)) * multiplier);
                    wF = (int) (Integer.valueOf(readAux(b)) * multiplier);
                    hF = (int) (Integer.valueOf(readAux(b)) * multiplier);
                    tF = Double.valueOf(readAux(b));
                    ixF = (int) (Integer.valueOf(readAux(b)) );
                    iyF = (int) (Integer.valueOf(readAux(b)) );
                    wxF = Integer.valueOf(readAux(b));
                    wyF = Integer.valueOf(readAux(b));
                    //if(wxF != -1){wxF *= multiplier;}
                    //if(wyF != -1){wyF *= multiplier;}
                    stop = Boolean.valueOf(readAux(b));
                    Image img = new ImageIcon( this.getClass().getResource(path + fold + "/" + j + ".png")).getImage();
                    if(nJ == 2) {
                        img = filterImage(img, charac, filterMethod);
                    }
                    s = new screenObject(xF, yF,  wF, hF, img, Item_Type.PLAYER);
                    if(hasHit) {
                        anim.addFrame(s, tF, ixF, iyF, wxF, wyF, stop);
                    }
                    else{
                        anim.addFrame(s, tF, ixF, iyF, wxF, wyF, false);
                    }
                    ++j;
                }

                movement mov = new movement(movId, anim);
                mov.setDamage(dmg);
                if(combos.containsKey(combo)){
                    movs.get(combos.get(combo)).setSubMovement(mov);
                }
                else {
                    movs.put(movId, mov);
                    combos.put(combo, movId);
                    combosInverse.put(movId,combo);
                }
                ++i;
            }
            b.close();
        }
        catch (Exception e){}
    }
}
