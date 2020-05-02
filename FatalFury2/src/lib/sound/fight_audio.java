package lib.sound;

import lib.Enums.Playable_Character;
import lib.Enums.Scenario_type;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class fight_audio {

    Clip p1_voices[];
    Clip p2_voices[];
    Clip sfx[];
    Clip music[];
    Clip announcer[];
    Clip crowd[];

    //TODO
    public enum voice_indexes {
        Hit_1,
        Hit_2,
        Hit_3,
        Hurt_1,
        Hurt_2,
        Hurt_3,
        Special_1,
        Special_2,
        Special_3,
        Special_4,
        Desperation_Move,
        Win,
        Defeat,
        Taunt,
        Throw,
    }

    // DONE
    public enum sfx_indexes{
        Pause,
        Hit_1,
        Hit_2,
        Final_hit_1,
        Final_hit_2,
        Final_hit_3,
        //TODO -> FALTAN PROYECTILES
        Projectile_1,
        Projectile_2,
        Projectile_3,
    }

    //Done
    public enum music_indexes {
        map_theme,
        win_theme,
        lose_theme
    }

    // Done
    public enum announcer_indexes{
        Fight,
        Round_One,
        Round_Two,
        Round_Three,
        Final_Round,
        Bonus_Game,
        Double_KO,
        Draw_Game,
        Perfect,
        Ready,
        Time_Up,
    }

    //TODO
    public enum crowd_indexes{
        Cheer_1,
        Cheer_2
    }




    private Playable_Character p1, p2;
    private Scenario_type map;

    public fight_audio(Playable_Character p1_, Playable_Character p2_, Scenario_type map_){
        p1 = p1_;
        p2 = p2_;
        map = map_;
        try {
            load();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }


    private void load() throws LineUnavailableException, IOException, UnsupportedAudioFileException {

        String ruta_p1 = "";
        String ruta_p2 = "";
        String ruta_Musica = "";
        String ruta_win = "assets/sound/music/Win.wav";
        String ruta_lose = "assets/sound/music/Lose.wav";
        String ruta_sfx = "assets/sound/special_effects";
        String ruta_announcer = "assets/sound/voice/Announcer";
        String ruta_crowd = "assets/sound/voice/Crowd";

        switch (p1){
            case ANDY:
                ruta_p1 = "assets/sound/voice/Andy";
                break;
            case MAI:
                ruta_p1 = "assets/sound/voice/Mai";
                break;
            case TERRY:
                ruta_p1 = "assets/sound/voice/Terry";
                break;
        }

        switch (p2){
            case ANDY:
                ruta_p2 = "assets/sound/voice/Andy";
                break;
            case MAI:
                ruta_p2 = "assets/sound/voice/Mai";
                break;
            case TERRY:
                ruta_p2 = "assets/sound/voice/Terry";
                break;
        }
        switch (map){
            case USA:
                ruta_Musica = "assets/sound/music/Usa.wav";
                break;
            case CHINA:
                ruta_Musica = "assets/sound/music/China.wav";
                break;
            case AUSTRALIA:
                ruta_Musica = "assets/sound/music/Australia.wav";
                break;
        }


        // CARGAR MUSICA
        music = new Clip[music_indexes.values().length];

        music[music_indexes.map_theme.ordinal()] = AudioSystem.getClip();
        music[music_indexes.map_theme.ordinal()].open(AudioSystem.getAudioInputStream(new File(ruta_Musica).getAbsoluteFile()));

        music[music_indexes.win_theme.ordinal()] = AudioSystem.getClip();
        music[music_indexes.win_theme.ordinal()].open(AudioSystem.getAudioInputStream(new File(ruta_win).getAbsoluteFile()));

        music[music_indexes.lose_theme.ordinal()] = AudioSystem.getClip();
        music[music_indexes.lose_theme.ordinal()].open(AudioSystem.getAudioInputStream(new File(ruta_lose).getAbsoluteFile()));

        // CARGAR EFECTOS ESPECIALES
        sfx = new Clip[sfx_indexes.values().length];

        for(int i = 0; i < sfx.length; i++){
            String ruta_sfx_aux = ruta_sfx +"/"+ sfx_indexes.values()[i] + ".wav";
            sfx[i] = AudioSystem.getClip();
            sfx[i].open(AudioSystem.getAudioInputStream(new File(ruta_sfx_aux).getAbsoluteFile()));
        }

        // CARGAR VOCES DE LA AUDIENCIA
        crowd = new Clip[crowd_indexes.values().length];

        for(int i = 0; i < crowd.length; i++){
            String ruta_crowd_aux = ruta_crowd +"/"+ crowd_indexes.values()[i] + ".wav";
            crowd[i] = AudioSystem.getClip();
            crowd[i].open(AudioSystem.getAudioInputStream(new File(ruta_crowd_aux).getAbsoluteFile()));
        }

        // CARGAR VOCES DEL NARRADOR
        announcer = new Clip[announcer_indexes.values().length];

        for(int i = 0; i < announcer.length; i++){
            String ruta_announcer_aux = ruta_announcer +"/"+ announcer_indexes.values()[i] + ".wav";
            announcer[i] = AudioSystem.getClip();
            announcer[i].open(AudioSystem.getAudioInputStream(new File(ruta_announcer_aux).getAbsoluteFile()));
        }

        // CARGAR VOCES DEL JUGADOR 1
        p1_voices = new Clip[voice_indexes.values().length];

        for(int i = 0; i < p1_voices.length; i++){
            String ruta_voices_aux = ruta_p1 +"/"+ voice_indexes.values()[i] + ".wav";
            p1_voices[i] = AudioSystem.getClip();
            p1_voices[i].open(AudioSystem.getAudioInputStream(new File(ruta_voices_aux).getAbsoluteFile()));
        }
        // CARGAR VOCES DEL JUGADOR 2
        if(p1 == p2){
            p2_voices = p1_voices;
        } else {
            p2_voices = new Clip[voice_indexes.values().length];


            for(int i = 0; i < p2_voices.length; i++){
                String ruta_voices_aux = ruta_p2 +"/"+ voice_indexes.values()[i] + ".wav";
                p2_voices[i] = AudioSystem.getClip();
                p2_voices[i].open(AudioSystem.getAudioInputStream(new File(ruta_voices_aux).getAbsoluteFile()));
            }
        }



    }

    public void playVoice(boolean isP1, voice_indexes i){
        if(isP1){
            try {
                p1_voices[i.ordinal()].setFramePosition(0);
                p1_voices[i.ordinal()].start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            try {
                p2_voices[i.ordinal()].setFramePosition(0);
                p2_voices[i.ordinal()].start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }



    public void playAnnouncer(announcer_indexes i){
        try {
            announcer[i.ordinal()].setFramePosition(0);
            announcer[i.ordinal()].start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void playSfx(sfx_indexes i){
        try {
            sfx[i.ordinal()].setFramePosition(0);
            sfx[i.ordinal()].start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    public void playCrowd(crowd_indexes i){
        try {
            crowd[i.ordinal()].setFramePosition(0);
            crowd[i.ordinal()].start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }



    public void loopMusic(music_indexes i){
        try {
            music[i.ordinal()].setFramePosition(0);
            music[i.ordinal()].loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void stopMusic(music_indexes i){
        if(music[i.ordinal()].isRunning()){
            music[i.ordinal()].stop();
        }
    }

    public void resumeMusic(music_indexes i){
        if(!music[i.ordinal()].isRunning() && music[i.ordinal()].getFramePosition() != 0){
            music[i.ordinal()].start();
        }
    }

    public void update(double mus, double sfx, double voces){
        updateMusic(mus);
    }


    private void updateMusic(double mus){
        stopMusic(music_indexes.map_theme);
        FloatControl gainControl = (FloatControl) music[music_indexes.map_theme.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(mus));
        resumeMusic(music_indexes.map_theme);

        stopMusic(music_indexes.win_theme);
        gainControl = (FloatControl) music[music_indexes.win_theme.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(mus));
        resumeMusic(music_indexes.win_theme);

        stopMusic(music_indexes.lose_theme);
        gainControl = (FloatControl) music[music_indexes.lose_theme.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(mus));
        resumeMusic(music_indexes.lose_theme);
    }
    public void close(){

    }

}
