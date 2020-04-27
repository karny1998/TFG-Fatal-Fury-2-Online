package lib.characters;

import lib.Enums.Animation_type;
import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.objects.animation;
import lib.objects.movement;
import lib.objects.screenObject;
import lib.sound.Sound;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.Random;

public class load_character {

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
                        (charac.equals("terry") && ((double)currentColor.getBlue()/(double)currentColor.getRed()) < 0.5 && ((double)currentColor.getGreen()/(double)currentColor.getRed()) < 0.5)
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
                return rgb;
            }
        };

        final ImageProducer ip = new FilteredImageSource(img.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

    public void generateMovs(String charac, int nJ, Map<String, Movement> combos, Map<Movement, movement> movs, Sound sounds, double multiplier) {
        String path = "assets/sprites/characters/" + charac + "/";
        try {
            String value;
            FileReader f = new FileReader(path + "load.txt");
            BufferedReader b = new BufferedReader(f);
            int nMovs = Integer.valueOf(b.readLine()), i = 0;
            Random random = new Random();
            int filterMethod = 1 + Math.abs(random.nextInt()) % 4;

            while((value = b.readLine())!=null && i < nMovs) {
                String fold = value;
                Movement movId = Movement.valueOf(b.readLine());
                String combo = b.readLine();
                Character_Voices sound = Character_Voices.valueOf(b.readLine());
                Boolean hasEnd = Boolean.valueOf(b.readLine());
                Animation_type aType = Animation_type.valueOf(b.readLine());
                int dmg = Integer.valueOf(b.readLine());
                Boolean hasHit = Boolean.valueOf(b.readLine());
                int xHit = 0, yHit = 0, xHurt = 0, yHurt = 0, xCover = 0, yCover = 0,
                        wHit = 0, hHit = 0, wHurt = 0, hHurt = 0, wCover = 0, hCover = 0,
                        nFrames = 0;
                if (hasHit){
                    xHit = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    yHit = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    wHit = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    hHit = (int) (Integer.valueOf(b.readLine()) * multiplier);
                }
                Boolean hasCover = Boolean.valueOf(b.readLine());
                if (hasCover){
                    xCover = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    yCover = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    wCover = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    hCover = (int) (Integer.valueOf(b.readLine()) * multiplier);
                }
                xHurt = (int) (Integer.valueOf(b.readLine()) * multiplier);
                yHurt = (int) (Integer.valueOf(b.readLine()) * multiplier);
                wHurt = (int) (Integer.valueOf(b.readLine()) * multiplier);
                hHurt = (int) (Integer.valueOf(b.readLine()) * multiplier);
                nFrames = Integer.valueOf(b.readLine());

                animation anim = new animation();
                anim.setType(aType);
                anim.setHasEnd(hasEnd);
                anim.setSound(sounds);
                anim.setSoundType(sound);
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
                    xF = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    yF = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    wF = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    hF = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    tF = Double.valueOf(b.readLine());
                    ixF = (int) (Integer.valueOf(b.readLine()) );
                    iyF = (int) (Integer.valueOf(b.readLine()) );
                    wxF = Integer.valueOf(b.readLine());
                    wyF = Integer.valueOf(b.readLine());
                    //if(wxF != -1){wxF *= multiplier;}
                    //if(wyF != -1){wyF *= multiplier;}
                    stop = Boolean.valueOf(b.readLine());
                    Image img = new ImageIcon(path + fold + "/" + j + ".png").getImage();
                    if(nJ == 2) {
                        img = filterImage(img, charac, filterMethod);
                    }
                    s = new screenObject(xF, yF,  wF, hF, img, Item_Type.PLAYER);
                    anim.addFrame(s, tF, ixF, iyF, wxF, wyF, stop);
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
                }
                ++i;
            }
            b.close();
        }
        catch (Exception e){}
    }
}
