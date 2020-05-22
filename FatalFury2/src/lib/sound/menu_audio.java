package lib.sound;

import javax.sound.sampled.*;
import java.io.IOException;

// Clase encargada de cargar los audios que seran usados en la menus
public class menu_audio {

    public Clip[] clips;

    private static String Music_Route = "/assets/sound/music/Menu.wav";
    private static String Back_route = "/assets/sound/special_effects/Back.wav";
    private static String Move_cursor_route = "/assets/sound/special_effects/Move_cursor.wav";
    private static String Option_selected_route = "/assets/sound/special_effects/Option_selected.wav";
    private static String Fight_selected_route = "/assets/sound/special_effects/Fight_selected.wav";
    private static String Versus_route = "/assets/sound/voice/Announcer/Versus.wav";
    private static String Andy_route = "/assets/sound/voice/Announcer/Andy.wav";
    private static String Terry_route = "/assets/sound/voice/Announcer/Terry.wav";
    private static String Mai_route = "/assets/sound/voice/Announcer/Mai.wav";
    private static String Error_route = "/assets/sound/special_effects/Error.wav";

    public enum indexes {
        menu_theme, back, move_cursor, option_selected, fight_selected, Versus, Andy, Terry, Mai, error
    }

    // Carga los clips de audio
    menu_audio(){
        try {
            load();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void load() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        clips = new Clip[indexes.values().length];

        clips[indexes.menu_theme.ordinal()] = AudioSystem.getClip();
        clips[indexes.menu_theme.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(Music_Route) ));

        clips[indexes.back.ordinal()] = AudioSystem.getClip();
        clips[indexes.back.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(Back_route) ));

        clips[indexes.move_cursor.ordinal()] = AudioSystem.getClip();
        clips[indexes.move_cursor.ordinal()].open(AudioSystem.getAudioInputStream(this.getClass().getResource(Move_cursor_route) ));

        clips[indexes.option_selected.ordinal()] = AudioSystem.getClip();
        clips[indexes.option_selected.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(Option_selected_route) ));

        clips[indexes.fight_selected.ordinal()] = AudioSystem.getClip();
        clips[indexes.fight_selected.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(Fight_selected_route) ));

        clips[indexes.Versus.ordinal()] = AudioSystem.getClip();
        clips[indexes.Versus.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(Versus_route) ));

        clips[indexes.Andy.ordinal()] = AudioSystem.getClip();
        clips[indexes.Andy.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(Andy_route) ));

        clips[indexes.Terry.ordinal()] = AudioSystem.getClip();
        clips[indexes.Terry.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(Terry_route) ));

        clips[indexes.Mai.ordinal()] = AudioSystem.getClip();
        clips[indexes.Mai.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(Mai_route) ));

        clips[indexes.error.ordinal()] = AudioSystem.getClip();
        clips[indexes.error.ordinal()].open(AudioSystem.getAudioInputStream( this.getClass().getResource(Error_route) ));
    }


    // Cierra los clips de audio
    public void close(){
        for(int i = 0; i < indexes.values().length; i++){
            clips[i].close();
        }
    }

    // Actualiza los valores de volumen
    public void update_init(double mus, double sfx, double voices){

        FloatControl gainControl = (FloatControl) clips[indexes.menu_theme.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(mus));

        gainControl = (FloatControl) clips[indexes.back.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(sfx));

        gainControl = (FloatControl) clips[indexes.move_cursor.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(sfx));

        gainControl = (FloatControl) clips[indexes.option_selected.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(sfx));

        gainControl = (FloatControl) clips[indexes.fight_selected.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(sfx));

        gainControl = (FloatControl) clips[indexes.Versus.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(voices));

        gainControl = (FloatControl) clips[indexes.Andy.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(voices));

        gainControl = (FloatControl) clips[indexes.Terry.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(voices));

        gainControl = (FloatControl) clips[indexes.Mai.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(voices ));

        gainControl = (FloatControl) clips[indexes.error.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(sfx ));
    }

    // Actualiza los valores de volumen
    public void update(double mus, double sfx, double voices){
        stop(indexes.menu_theme);
        FloatControl gainControl = (FloatControl) clips[indexes.menu_theme.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(mus));
        resume(indexes.menu_theme);

        stop(indexes.back);
        gainControl = (FloatControl) clips[indexes.back.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(sfx));
        resume(indexes.back);

        stop(indexes.move_cursor);
        gainControl = (FloatControl) clips[indexes.move_cursor.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(sfx));
        resume(indexes.move_cursor);

        stop(indexes.option_selected);
        gainControl = (FloatControl) clips[indexes.option_selected.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(sfx));
        resume(indexes.option_selected);

        stop(indexes.fight_selected);
        gainControl = (FloatControl) clips[indexes.fight_selected.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(sfx));
        resume(indexes.fight_selected);

        stop(indexes.Versus);
        gainControl = (FloatControl) clips[indexes.Versus.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(voices));
        resume(indexes.Versus);

        stop(indexes.Andy);
        gainControl = (FloatControl) clips[indexes.Andy.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(voices));
        resume(indexes.Andy);

        stop(indexes.Terry);
        gainControl = (FloatControl) clips[indexes.Terry.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(voices));
        resume(indexes.Terry);

        stop(indexes.Mai);
        gainControl = (FloatControl) clips[indexes.Mai.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(voices ));
        resume(indexes.Mai);

        stop(indexes.error);
        gainControl = (FloatControl) clips[indexes.error.ordinal()].getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(sfx ));
        resume(indexes.error);
    }

    // Reproduce el clip con indice i
    public void play(indexes i){
        try {
            clips[i.ordinal()].setFramePosition(0);
            clips[i.ordinal()].start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // detiene el clip con indice i
    public void stop(indexes i){
        if(clips[i.ordinal()].isRunning()){
            clips[i.ordinal()].stop();
        }
    }

    // Continua la reproducción del clip con indice i
    public void resume(indexes i){
        if(!clips[i.ordinal()].isRunning() && clips[i.ordinal()].getFramePosition() != 0){
            clips[i.ordinal()].start();
        }
    }

    // Reproduce en bucle el clip con indice i
    public void loop(indexes i){
        try {
            clips[i.ordinal()].setFramePosition(0);
            clips[i.ordinal()].loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
