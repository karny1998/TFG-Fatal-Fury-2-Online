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
    public void generateMovs(String charac, Map<String, Movement> combos, Map<Movement, movement> movs, Sound sounds, double multiplier) {
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
                    xHit = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    yHit = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    wHit = (int) (Integer.valueOf(b.readLine()) * multiplier);
                    hHit = (int) (Integer.valueOf(b.readLine()) * multiplier);
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
                    if(wxF != -1){wxF *= multiplier;}
                    if(wyF != -1){wyF *= multiplier;}
                    stop = Boolean.valueOf(b.readLine());
                    s = new screenObject(xF, yF,  wF, hF, new ImageIcon(path + fold + "/" + j + ".png").getImage(), Item_Type.PLAYER);
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
