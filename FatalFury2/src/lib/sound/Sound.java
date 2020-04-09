package lib.sound;

import javafx.scene.media.MediaPlayer;
import lib.Enums.*;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sound {
    private static String Voice_Route = "assets/sound/voice/";
    private static String Music_Route = "assets/sound/music/";
    private static String Special_Effects_Route = "assets/sound/special_effects/";

    private float music_audio = 1.0f;
    private float voice_audio = 1.0f;
    private float sfx_audio = 1.0f;

    private Playable_Character character;
    private Clip[] clips;


    private MediaPlayer mp;

    private static int Character_Voices_Size = Character_Voices.values().length;
    private static int Announcer_Voices_Size = Announcer_voices.values().length;
    private static int Special_Effects_Size = Special_Effects.values().length;
    private static int Crowd_Voices_Size = Crowd_Voices.values().length;
    private static int Music_Size = Music.values().length;




    public Sound(Audio_Type type){
        try {
          this.loadAudio(type);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void loadAudio(Audio_Type type) throws IllegalStateException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        String routes[];
        //TODO METER RESTO SONIDOS
        //TODO Load from folder instead ºfrom route
        switch ( type ){
            case Terry_audio:
                routes = new String[Character_Voices_Size];
                routes[Character_Voices.Hit_1.ordinal()] = Voice_Route + "Terry/Hit_1.wav";
                routes[Character_Voices.Hit_2.ordinal()] = Voice_Route + "Terry/Hit_2.wav";
                routes[Character_Voices.Hit_3.ordinal()] = Voice_Route + "Terry/Hit_3.wav";
                routes[Character_Voices.Hurt_1.ordinal()] = Voice_Route + "Terry/Hurt_1.wav";
                routes[Character_Voices.Hurt_2.ordinal()] = Voice_Route + "Terry/Hurt_2.wav";
                routes[Character_Voices.Hurt_3.ordinal()] = Voice_Route + "Terry/Hurt_3.wav";
                routes[Character_Voices.Special_1.ordinal()] = Voice_Route + "Terry/Special_1.wav";
                routes[Character_Voices.Special_2.ordinal()] = Voice_Route + "Terry/Special_2.wav";
                routes[Character_Voices.Special_3.ordinal()] = Voice_Route + "Terry/Special_3.wav";
                routes[Character_Voices.Special_4.ordinal()] = Voice_Route + "Terry/Special_4.wav";
                routes[Character_Voices.Desperation_Move.ordinal()] = Voice_Route + "Terry/Desperation_Move.wav";
                routes[Character_Voices.Win.ordinal()] = Voice_Route + "Terry/Win.wav";
                routes[Character_Voices.Defeat.ordinal()] = Voice_Route + "Terry/Defeat.wav";
                routes[Character_Voices.Taunt.ordinal()] = Voice_Route + "Terry/Taunt.wav";
                routes[Character_Voices.Throw.ordinal()] = Voice_Route + "Terry/Throw.wav";
                break;
            case Andy_audio:
                routes = new String[Character_Voices_Size];
                routes[Character_Voices.Hit_1.ordinal()] = Voice_Route + "Andy/Hit_1.wav";
                routes[Character_Voices.Hit_2.ordinal()] = Voice_Route + "Andy/Hit_2.wav";
                routes[Character_Voices.Hit_3.ordinal()] = Voice_Route + "Andy/Hit_3.wav";
                routes[Character_Voices.Hurt_1.ordinal()] = Voice_Route + "Andy/Hurt_1.wav";
                routes[Character_Voices.Hurt_2.ordinal()] = Voice_Route + "Andy/Hurt_2.wav";
                routes[Character_Voices.Hurt_3.ordinal()] = Voice_Route + "Andy/Hurt_3.wav";
                routes[Character_Voices.Special_1.ordinal()] = Voice_Route + "Andy/Special_1.wav";
                routes[Character_Voices.Special_2.ordinal()] = Voice_Route + "Andy/Special_2.wav";
                routes[Character_Voices.Special_3.ordinal()] = Voice_Route + "Andy/Special_3.wav";
                routes[Character_Voices.Special_4.ordinal()] = Voice_Route + "Andy/Special_4.wav";
                routes[Character_Voices.Desperation_Move.ordinal()] = Voice_Route + "Andy/Desperation_Move.wav";
                routes[Character_Voices.Win.ordinal()] = Voice_Route + "Andy/Win.wav";
                routes[Character_Voices.Defeat.ordinal()] = Voice_Route + "Andy/Defeat.wav";
                routes[Character_Voices.Taunt.ordinal()] = Voice_Route + "Andy/Taunt.wav";
                routes[Character_Voices.Throw.ordinal()] = Voice_Route + "Andy/Throw.wav";
                break;
            case Mai_audio:
                routes = new String[Character_Voices_Size];
                routes[Character_Voices.Hit_1.ordinal()] = Voice_Route + "Mai/Hit_1.wav";
                routes[Character_Voices.Hit_2.ordinal()] = Voice_Route + "Mai/Hit_2.wav";
                routes[Character_Voices.Hit_3.ordinal()] = Voice_Route + "Mai/Hit_3.wav";
                routes[Character_Voices.Hurt_1.ordinal()] = Voice_Route + "Mai/Hurt_1.wav";
                routes[Character_Voices.Hurt_2.ordinal()] = Voice_Route + "Mai/Hurt_2.wav";
                routes[Character_Voices.Hurt_3.ordinal()] = Voice_Route + "Mai/Hurt_3.wav";
                routes[Character_Voices.Special_1.ordinal()] = Voice_Route + "Mai/Special_1.wav";
                routes[Character_Voices.Special_2.ordinal()] = Voice_Route + "Mai/Special_2.wav";
                routes[Character_Voices.Special_3.ordinal()] = Voice_Route + "Mai/Special_3.wav";
                routes[Character_Voices.Special_4.ordinal()] = Voice_Route + "Mai/Special_4.wav";
                routes[Character_Voices.Desperation_Move.ordinal()] = Voice_Route + "Mai/Desperation_Move.wav";
                routes[Character_Voices.Win.ordinal()] = Voice_Route + "Mai/Win.wav";
                routes[Character_Voices.Defeat.ordinal()] = Voice_Route + "Mai/Defeat.wav";
                routes[Character_Voices.Taunt.ordinal()] = Voice_Route + "Mai/Taunt.wav";
                routes[Character_Voices.Throw.ordinal()] = Voice_Route + "Mai/Throw.wav";
                break;
            case Special_Effects_Audio:
                routes = new String[Special_Effects_Size];
                break;
            case Announcer_Audio:
                routes = new String[Announcer_Voices_Size];
                routes[Announcer_voices.Round_One.ordinal()] = Voice_Route + "Announcer/Round_One.wav";
                routes[Announcer_voices.Round_Two.ordinal()] = Voice_Route + "Announcer/Round_Two.wav";
                routes[Announcer_voices.Round_Three.ordinal()] = Voice_Route + "Announcer/Round_Three.wav";
                routes[Announcer_voices.Final_Round.ordinal()] = Voice_Route + "Announcer/Final_Round.wav";
                routes[Announcer_voices.Bonus_Game.ordinal()] = Voice_Route + "Announcer/Bonus_Game.wav";
                routes[Announcer_voices.Double_KO.ordinal()] = Voice_Route + "Announcer/Double_KO.wav";
                routes[Announcer_voices.Draw_Game.ordinal()] = Voice_Route + "Announcer/Draw_Game.wav";
                routes[Announcer_voices.Perfect.ordinal()] = Voice_Route + "Announcer/Perfect.wav";
                routes[Announcer_voices.Ready.ordinal()] = Voice_Route + "Announcer/Ready.wav";
                routes[Announcer_voices.Time_Up.ordinal()] = Voice_Route + "Announcer/Time_Up.wav";
                routes[Announcer_voices.Versus.ordinal()] = Voice_Route + "Announcer/Versus.wav";
                routes[Announcer_voices.Andy.ordinal()] = Voice_Route + "Announcer/Andy.wav";
                routes[Announcer_voices.Terry.ordinal()] = Voice_Route + "Announcer/Terry.wav";
                routes[Announcer_voices.Mai.ordinal()] = Voice_Route + "Announcer/Mai.wav";
                break;
            case Crowd_Audio:
                routes = new String[Crowd_Voices_Size];
                routes[Crowd_Voices.Cheer_1.ordinal()] = Voice_Route + "Crowd/Cheer_1.wav";
                routes[Crowd_Voices.Cheer_2.ordinal()] = Voice_Route + "Crowd/Cheer_2.wav";
                break;
            case Music_Audio:
                routes = new String[Music_Size];
                routes[Music.TEST.ordinal()] = Music_Route + "Rivers.wav";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        clips = new Clip[routes.length];
        for(int i = 0; i < routes.length; i++){
           clips[i] = AudioSystem.getClip();
           clips[i].open(AudioSystem.getAudioInputStream(new File(routes[i]).getAbsoluteFile()));

        }

    }
//TODO stop, pause, reset, etc.
//https://www.geeksforgeeks.org/play-audio-file-using-java/

    private void play(int index) {
        try {
            clips[index].setFramePosition(0);
            clips[index].start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    private void loop(int index) {
        try {
            clips[index].setFramePosition(0);
            clips[index].loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    private void stop(int index){
        if(clips[index].isRunning()){
            clips[index].stop();
        }
    }

    private void resume(int index){
        if(!clips[index].isRunning() && clips[index].getFramePosition() != 0){
            clips[index].start();
        }
    }

    private void setVolume(float volume, int index){
        FloatControl gainControl = (FloatControl) clips[index].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    public void musicVolumeDown() {
        music_audio -= 0.1;
        if(music_audio < 0.0) { music_audio = 0.0f; }
        for(int i = 0; i < clips.length; i++){
           setVolume(music_audio, i);
        }
    }

    public void musicVolumeUp() {
        music_audio += 0.1;
        if(music_audio > 1.0) { music_audio = 1.0f; }
        for(int i = 0; i < clips.length; i++){
            setVolume(music_audio, i);
        }
    }


    public void voicesVolumeDown() {
        voice_audio -= 0.1;
        if(voice_audio < 0.0) { voice_audio = 0.0f; }
        for(int i = 0; i < clips.length; i++){
            setVolume(voice_audio, i);
        }
    }

    public void voicesVolumeUp() {
        voice_audio += 0.1;
        if(voice_audio > 1.0) { voice_audio = 1.0f; }
        for(int i = 0; i < clips.length; i++){
            setVolume(voice_audio, i);
        }
    }

    public void sfxVolumeDown() {
        sfx_audio -= 0.1;
        if(sfx_audio < 0.0) { sfx_audio = 0.0f; }
        for(int i = 0; i < clips.length; i++){
            setVolume(sfx_audio, i);
        }
    }

    public void sfxVolumeUp() {
        sfx_audio += 0.1;
        if(sfx_audio > 1.0) { sfx_audio = 1.0f; }
        for(int i = 0; i < clips.length; i++){
            setVolume(sfx_audio, i);
        }
    }

    public void playCharacterVoice(Character_Voices index){ play(index.ordinal()); }
    public void stopCharacterVoice(Character_Voices index){ stop(index.ordinal()); }
    public void resumeCharacterVoices(Character_Voices index){ resume(index.ordinal()); }


    public void playAnnouncerVoice(Announcer_voices index){ play(index.ordinal()); }
    public void stopAnnouncerVoice(Announcer_voices index){ stop(index.ordinal()); }
    public void resumeAnnouncerVoice(Announcer_voices index){ resume(index.ordinal()); }


    public void playMusic(Music index){ play(index.ordinal()); }
    public void loopMusic(Music index){ loop(index.ordinal()); }
    public void stopMusic(Music index){ stop(index.ordinal()); }
    public void resumeMusic(Music index){ resume(index.ordinal()); }



    public void playSfx(Character_Voices index){ play(index.ordinal()); }
    public void stopSfx(Character_Voices index){ stop(index.ordinal()); }
    public void resumeSfx(Character_Voices index){ resume(index.ordinal()); }

}
