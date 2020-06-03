package lib.sound;

import lib.Enums.Playable_Character;
import lib.Enums.Scenario_type;

import javax.sound.sampled.*;
import java.io.IOException;

/**
 * The type Fight audio.
 */
// Clase encargada de cargar los audios que seran usados en la pelea
public class fight_audio {

    /**
     * The P 1 voices.
     */
    Clip p1_voices[];
    /**
     * The P 2 voices.
     */
    Clip p2_voices[];
    /**
     * The Sfx.
     */
    Clip sfx[];
    /**
     * The Music.
     */
    Clip music[];
    /**
     * The Announcer.
     */
    Clip announcer[];
    /**
     * The Crowd.
     */
    Clip crowd[];

    /**
     * The enum Voice indexes.
     */
// DONE
    public enum voice_indexes {
        /**
         * Hit 1 voice indexes.
         */
        Hit_1,
        /**
         * Hit 2 voice indexes.
         */
        Hit_2,
        /**
         * Hit 3 voice indexes.
         */
        Hit_3,
        /**
         * Hurt 1 voice indexes.
         */
        Hurt_1,
        /**
         * Hurt 2 voice indexes.
         */
        Hurt_2,
        /**
         * Hurt 3 voice indexes.
         */
        Hurt_3,
        /**
         * Special 1 voice indexes.
         */
        Special_1,
        /**
         * Special 2 voice indexes.
         */
        Special_2,
        /**
         * Special 3 voice indexes.
         */
        Special_3,
        /**
         * Win voice indexes.
         */
//Special_4,
        //Desperation_Move,
        Win,
        /**
         * Defeat voice indexes.
         */
        Defeat,
        /**
         * Throw voice indexes.
         */
//Taunt,
        Throw
    }

    /**
     * The enum Sfx indexes.
     */
// DONE
    public enum sfx_indexes{
        /**
         * Pause sfx indexes.
         */
        Pause,
        /**
         * Hit 1 sfx indexes.
         */
        Hit_1,
        /**
         * Hit 2 sfx indexes.
         */
        Hit_2,
        /**
         * Final hit 1 sfx indexes.
         */
        Final_hit_1,
        /**
         * Final hit 2 sfx indexes.
         */
        Final_hit_2,
        /**
         * Final hit 3 sfx indexes.
         */
        Final_hit_3,
        /**
         * Move cursor sfx indexes.
         */
        Move_cursor,
        /**
         * Option selected sfx indexes.
         */
        Option_selected
    }

    /**
     * The enum Music indexes.
     */
// Done
    public enum music_indexes {
        /**
         * Map theme music indexes.
         */
        map_theme,
        /**
         * Win theme music indexes.
         */
        win_theme,
        /**
         * Lose theme music indexes.
         */
        lose_theme
    }

    /**
     * The enum Announcer indexes.
     */
// Done
    public enum announcer_indexes{
        /**
         * Fight announcer indexes.
         */
        Fight,
        /**
         * Round one announcer indexes.
         */
        Round_One,
        /**
         * Round two announcer indexes.
         */
        Round_Two,
        /**
         * Round three announcer indexes.
         */
        Round_Three,
        /**
         * Final round announcer indexes.
         */
        Final_Round,
        /**
         * Bonus game announcer indexes.
         */
        Bonus_Game,
        /**
         * Double ko announcer indexes.
         */
        Double_KO,
        /**
         * Draw game announcer indexes.
         */
        Draw_Game,
        /**
         * Perfect announcer indexes.
         */
        Perfect,
        /**
         * Ready announcer indexes.
         */
        Ready,
        /**
         * Time up announcer indexes.
         */
        Time_Up,
    }


    /**
     * The P 1.
     */
    private Playable_Character p1, /**
     * The P 2.
     */
    p2;
    /**
     * The Map.
     */
    private Scenario_type map;

    /**
     * Instantiates a new Fight audio.
     *
     * @param p1_  the p 1
     * @param p2_  the p 2
     * @param map_ the map
     */
// Inicializa todos los audios
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


    /**
     * Load.
     *
     * @throws LineUnavailableException      the line unavailable exception
     * @throws IOException                   the io exception
     * @throws UnsupportedAudioFileException the unsupported audio file exception
     */
    private void load() throws LineUnavailableException, IOException, UnsupportedAudioFileException {

        String ruta_p1 = "";
        String ruta_p2 = "";
        String ruta_Musica = "";
        String ruta_win = "/assets/sound/music/Win.wav";
        String ruta_lose = "/assets/sound/music/Lose.wav";
        String ruta_sfx = "/assets/sound/special_effects";
        String ruta_announcer = "/assets/sound/voice/Announcer";
        String ruta_crowd = "/assets/sound/voice/Crowd";

        switch (p1){
            case ANDY:
                ruta_p1 = "/assets/sound/voice/Andy";
                break;
            case MAI:
                ruta_p1 = "/assets/sound/voice/Mai";
                break;
            case TERRY:
                ruta_p1 = "/assets/sound/voice/Terry";
                break;
        }

        switch (p2){
            case ANDY:
                ruta_p2 = "/assets/sound/voice/Andy";
                break;
            case MAI:
                ruta_p2 = "/assets/sound/voice/Mai";
                break;
            case TERRY:
                ruta_p2 = "/assets/sound/voice/Terry";
                break;
        }
        switch (map){
            case USA:
                ruta_Musica = "/assets/sound/music/Usa.wav";
                break;
            case CHINA:
                ruta_Musica = "/assets/sound/music/China.wav";
                break;
            case AUSTRALIA:
                ruta_Musica = "/assets/sound/music/Australia.wav";
                break;
        }


        // CARGAR MUSICA
        music = new Clip[music_indexes.values().length];

        music[music_indexes.map_theme.ordinal()] = AudioSystem.getClip();
        music[music_indexes.map_theme.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(ruta_Musica) ));

        music[music_indexes.win_theme.ordinal()] = AudioSystem.getClip();
        music[music_indexes.win_theme.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(ruta_win) ));

        music[music_indexes.lose_theme.ordinal()] = AudioSystem.getClip();
        music[music_indexes.lose_theme.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(ruta_lose) ));

        // CARGAR EFECTOS ESPECIALES
        sfx = new Clip[sfx_indexes.values().length];

        for(int i = 0; i < sfx.length; i++){
            String ruta_sfx_aux = ruta_sfx +"/"+ sfx_indexes.values()[i] + ".wav";
            sfx[i] = AudioSystem.getClip();
            sfx[i].open(AudioSystem.getAudioInputStream( this.getClass().getResource(ruta_sfx_aux) ));
        }

        // CARGAR VOCES DEL NARRADOR
        announcer = new Clip[announcer_indexes.values().length];

        for(int i = 0; i < announcer.length; i++){
            String ruta_announcer_aux = ruta_announcer +"/"+ announcer_indexes.values()[i] + ".wav";
            announcer[i] = AudioSystem.getClip();
            announcer[i].open(AudioSystem.getAudioInputStream( this.getClass().getResource(ruta_announcer_aux) ));
        }

        // CARGAR VOCES DEL JUGADOR 1
        p1_voices = new Clip[voice_indexes.values().length];

        for(int i = 0; i < p1_voices.length; i++){
            String ruta_voices_aux = ruta_p1 +"/"+ voice_indexes.values()[i] + ".wav";
            p1_voices[i] = AudioSystem.getClip();
            p1_voices[i].open(AudioSystem.getAudioInputStream( this.getClass().getResource(ruta_voices_aux) ));
        }
        // CARGAR VOCES DEL JUGADOR 2
        if(p1 == p2){
            p2_voices = p1_voices;
        } else {
            p2_voices = new Clip[voice_indexes.values().length];


            for(int i = 0; i < p2_voices.length; i++){
                String ruta_voices_aux = ruta_p2 +"/"+ voice_indexes.values()[i] + ".wav";
                p2_voices[i] = AudioSystem.getClip();
                p2_voices[i].open(AudioSystem.getAudioInputStream( this.getClass().getResource(ruta_voices_aux) ));
            }
        }



    }

    /**
     * Play voice.
     *
     * @param isP1 the is p 1
     * @param i    the
     */
// Reproduce un audio del tipo voice
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


    /**
     * Play announcer.
     *
     * @param i the
     */
// Reproduce un audio del tipo announcer
    public void playAnnouncer(announcer_indexes i){
        try {
            announcer[i.ordinal()].setFramePosition(0);
            announcer[i.ordinal()].start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Play sfx.
     *
     * @param i the
     */
// Reproduce un audio del tipo sfx
    public void playSfx(sfx_indexes i){
        try {
            sfx[i.ordinal()].setFramePosition(0);
            sfx[i.ordinal()].start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Loop music.
     *
     * @param i the
     */
// Reproduce un audio del tipo music en bucle
    public void loopMusic(music_indexes i){
        try {
            music[i.ordinal()].setFramePosition(0);
            music[i.ordinal()].loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Stop music.
     *
     * @param i the
     */
    public void stopMusic(music_indexes i){
        if(music[i.ordinal()].isRunning()){
            music[i.ordinal()].stop();
        }
    }

    /**
     * Resume music.
     *
     * @param i the
     */
    public void resumeMusic(music_indexes i){
        if(!music[i.ordinal()].isRunning() && music[i.ordinal()].getFramePosition() != 0){
            music[i.ordinal()].start();
        }
    }

    /**
     * Update.
     *
     * @param mus    the mus
     * @param sfx    the sfx
     * @param voices the voices
     */
    public void update(double mus, double sfx, double voices){
        updateMusic(mus);
        updateSfx(sfx);
        updateVoices(voices);
    }

    /**
     * Update music.
     *
     * @param mus the mus
     */
// Actualiza el volumen de la musica
    private void updateMusic(double mus){

        FloatControl gainControl = (FloatControl) music[music_indexes.map_theme.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(mus));

        gainControl = (FloatControl) music[music_indexes.win_theme.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(mus));

        gainControl = (FloatControl) music[music_indexes.lose_theme.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(mus));

    }

    /**
     * Update sfx.
     *
     * @param sfx_ the sfx
     */
// Actualiza el volumen de los efectos especiales
    private void updateSfx(double sfx_){
        for(int i = 0; i < sfx_indexes.values().length; i++){
            FloatControl gainControl = (FloatControl) sfx[i].getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(sfx_));
        }
    }

    /**
     * Update voices.
     *
     * @param voices the voices
     */
// Actualiza el volumen de las voces
    private void updateVoices(double voices){

        for(int i = 0; i < announcer_indexes.values().length; i++){
            FloatControl gainControl = (FloatControl) announcer[i].getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(voices));
        }
        if(p1 == p2){
            for(int i = 0; i < voice_indexes.values().length; i++){
                FloatControl gainControl = (FloatControl) p1_voices[i].getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(20f * (float) Math.log10(voices));

            }
        } else {
            for(int i = 0; i < voice_indexes.values().length; i++){
                FloatControl gainControl = (FloatControl) p1_voices[i].getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(20f * (float) Math.log10(voices));

                FloatControl gainControl_2 = (FloatControl) p2_voices[i].getControl(FloatControl.Type.MASTER_GAIN);
                gainControl_2.setValue(20f * (float) Math.log10(voices));

            }
        }
    }

    /**
     * Close.
     */
// Cierra todos los clips de audio abiertos
    public void close(){
        for(int i = 0; i < music_indexes.values().length; i++){
            music[i].close();
        }

        for(int i = 0; i < sfx_indexes.values().length; i++){
            sfx[i].close();
        }

        for(int i = 0; i < announcer_indexes.values().length; i++){
            announcer[i].close();
        }


        if(p1 == p2){
            for(int i = 0; i < voice_indexes.values().length; i++){
                p1_voices[i].close();
            }
        } else {
            for(int i = 0; i < voice_indexes.values().length; i++){
                p1_voices[i].close();
                p2_voices[i].close();
            }
        }
    }

}
