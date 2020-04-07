package lib.objects;

import lib.Enums.Animation_type;
import lib.Enums.Character_Voices;
import lib.Enums.Item_Type;
import lib.Enums.Movement;
import lib.sound.Sound;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

public class load_character {
    public void generateMovs(String charac, Map<String, Movement> combos, Map<Movement, movement> movs, Sound sounds) {
        String path = "assets/sprites/characters/" + charac + "/";
        try {
            String value;
            FileReader f = new FileReader(path + "load.txt");
            BufferedReader b = new BufferedReader(f);
            int nMovs = Integer.valueOf(b.readLine()), i = 0;
            while((value = b.readLine())!=null && i < nMovs) {
                String fold = value;
                Movement movId = Movement.valueOf(b.readLine());
                String combo = b.readLine();
                Character_Voices sound = Character_Voices.valueOf(b.readLine());
                Boolean hasEnd = Boolean.valueOf(b.readLine());
                Animation_type aType = Animation_type.valueOf(b.readLine());
                int dmg = Integer.valueOf(b.readLine());
                Boolean hasHit = Boolean.valueOf(b.readLine());
                int xHit = 0, yHit = 0, xHurt = 0, yHurt = 0,
                        wHit = 0, hHit = 0, wHurt = 0, hHurt = 0,
                        nFrames = 0;
                if (hasHit){
                    xHit = Integer.valueOf(b.readLine());
                    yHit = Integer.valueOf(b.readLine());
                    wHit = Integer.valueOf(b.readLine());
                    hHit = Integer.valueOf(b.readLine());
                }
                xHurt = Integer.valueOf(b.readLine());
                yHurt = Integer.valueOf(b.readLine());
                wHurt = Integer.valueOf(b.readLine());
                hHurt = Integer.valueOf(b.readLine());
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

                int j = 1;
                screenObject s;
                int xF = 0, yF = 0, wF = 0, hF = 0,
                        ixF = 0, iyF = 0, wxF = 0, wyF = 0;
                double tF = 0.0;
                Boolean stop = false;
                while(j <= nFrames){
                    xF = Integer.valueOf(b.readLine());
                    yF = Integer.valueOf(b.readLine());
                    wF = Integer.valueOf(b.readLine());
                    hF = Integer.valueOf(b.readLine());
                    tF = Double.valueOf(b.readLine());
                    ixF = Integer.valueOf(b.readLine());
                    iyF = Integer.valueOf(b.readLine());
                    wxF = Integer.valueOf(b.readLine());
                    wyF = Integer.valueOf(b.readLine());
                    stop = Boolean.valueOf(b.readLine());
                    String ssda = path + "/" + fold + "/" +j + ".png";
                    s = new screenObject(xF, yF,  wF, hF, new ImageIcon(path + fold + "/" + j + ".png").getImage(), Item_Type.PLAYER);
                    anim.addFrame(s, tF, ixF, iyF, wxF, wyF, stop);
                    ++j;
                }

                movement mov = new movement(movId, anim);
                mov.setDamage(dmg);
                movs.put(movId,mov);
                combos.put(combo, movId);

                ++i;
            }
            b.close();
        }
        catch (Exception e){}
    }
}
