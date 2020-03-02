package lib.sound;

import lib.Enums.Playable_Character;
import lib.Enums.Voice_Index;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class Sound {
    private static String soundRoute = "assets/sound/";
    private Playable_Character character;
    private static String[] voiceRoute;
    private static String[] sfxRoute;

    public Sound(Playable_Character c){

    }
//TODO stop, pause, reset, etc.
//https://www.geeksforgeeks.org/play-audio-file-using-java/

    public synchronized void play(final String url) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream =  AudioSystem.getAudioInputStream(new File(soundRoute+url).getAbsoluteFile());
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }



    public synchronized void playVoice(Voice_Index index){
        play(soundRoute + voiceRoute[index.ordinal()]);
    }

    public synchronized void playSfx(Voice_Index index){
        play(soundRoute + sfxRoute[index.ordinal()]);
    }


}
