package lib.objects;

import lib.sound.menu_audio;
import lib.utils.Pair;
import lib.Enums.*;
import lib.input.controlListener;
import lib.maps.scenary;
import lib.menus.menu;
import lib.menus.menu_generator;
import lib.sound.audio_manager;
import lib.sound.fight_audio;

import javax.swing.*;
import java.io.*;
import java.util.Map;

public class story_mode {
    private menu actualMenu;
    private menu winMenu = menu_generator.generate_story_win();
    private menu loseMenu = menu_generator.generate_story_lose();
    private menu difficulty = menu_generator.generate_story_difficulty();
    private screenObject loads[];
    private screenObject ends[];
    private ia_loader.dif lvlIa = ia_loader.dif.EASY;
    private int score = 0;
    private int stage = 0;
    private Playable_Character charac = Playable_Character.TERRY;
    private fight_controller fight;
    private character_controller player, enemy;
    private GameState state = GameState.STORY_DIFFICULTY;
    private scenary scene = new scenary(Scenario_type.USA);;
    private hitBox mapLimit = new hitBox(0,0,1280,720,box_type.HURTBOX);;
    private long timeReference = System.currentTimeMillis();
    private boolean playing = false;
    private Scenario_type scenarys[] = {Scenario_type.CHINA, Scenario_type.CHINA, Scenario_type.CHINA
                                        , Scenario_type.AUSTRALIA, Scenario_type.AUSTRALIA,Scenario_type.AUSTRALIA,
                                        Scenario_type.USA, Scenario_type.USA,Scenario_type.USA};
    private Playable_Character enemies[] = {Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI,
                                            Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI,
                                            Playable_Character.TERRY, Playable_Character.ANDY, Playable_Character.MAI};
    private menu_audio.indexes audioEnemies[] = { menu_audio.indexes.Terry, menu_audio.indexes.Andy,menu_audio.indexes.Mai,
                                                    menu_audio.indexes.Terry,menu_audio.indexes.Andy,menu_audio.indexes.Mai,
                                                    menu_audio.indexes.Terry,menu_audio.indexes.Andy,menu_audio.indexes.Mai};

    public story_mode(){
        loadGame();
        loadLoadScreens();
    }

    public story_mode(ia_loader.dif lvl){
        this.lvlIa = lvl;
        loadLoadScreens();
    }

    void loadGame(){
        String path = "/files/last_game.txt";
        boolean newGame = true;
        try {
            InputStream f = this.getClass().getResourceAsStream(path);
            BufferedReader b = new BufferedReader(new InputStreamReader(f));
            String aux = "";
            if ((aux = b.readLine()) != null) {
                lvlIa = ia_loader.dif.valueOf(aux);
                if ((aux = b.readLine()) != null) {
                    stage = Integer.valueOf(aux);
                    newGame = false;
                }
                else{
                    newGame = true;
                }
            }
            else{
                newGame = true;
            }
            b.close();
            if(!newGame) {
                state = GameState.STORY_LOADING;
            }
            else{
                state = GameState.STORY_DIFFICULTY;
                actualMenu = difficulty;
                actualMenu.updateTime();
            }
        }
        catch (Exception e){
            state = GameState.STORY_DIFFICULTY;
            actualMenu = difficulty;
            actualMenu.updateTime();
        }
    }

    void saveGame(){
        String path = "/files/last_game.txt";
        File f= new File(path);
        f.delete();
        f= new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(lvlIa.toString()+"\n");
            bw.write(Integer.toString(stage));
            bw.close();
        }
        catch (Exception e){}
    }

    void loadLoadScreens(){
        String path =  "/assets/sprites/menu/story/story_";
        loads = new screenObject[9];
        for(int i = 1; i < 10; ++i){
            loads[i-1] = new screenObject(0, 0,  1280, 720, new ImageIcon(this.getClass().getResource(path  + i + ".png")).getImage(), Item_Type.MENU);
        }
        path =  "/assets/sprites/menu/story/end_story_";
        ends = new screenObject[3];
        for(int i = 1; i < 4; ++i){
            ends[i-1] = new screenObject(0, 0,  1280, 720, new ImageIcon(this.getClass().getResource(path  + i + ".png")).getImage(), Item_Type.MENU);
        }
    }

    void generateFight(){
        player = new user_controller(charac, 1);
        enemy = new enemy_controller(enemies[stage], 2);
        enemy.getIa().setDif(lvlIa);
        enemy.setRival(player.getPlayer());
        enemy.getPlayer().setMapLimit(mapLimit);
        player.setRival(enemy.getPlayer());
        player.getPlayer().setMapLimit(mapLimit);
        scene = new scenary(scenarys[stage]);
        fight = new fight_controller(player,enemy,scene);
        fight.setMapLimit(mapLimit);
        fight.setVsIa(true);
    }

    public Pair<Boolean, GameState> getAnimation(Map<Item_Type, screenObject> screenObjects){
        Boolean exit = false;
        long current = System.currentTimeMillis();
        if(state == GameState.STORY_DIFFICULTY){
            screenObjects.put(Item_Type.MENU,actualMenu.getFrame());
            Pair<menu, Selectionable> p = actualMenu.select();
            if(controlListener.getStatus(1, controlListener.ENT_INDEX) && p.getValue() != Selectionable.NONE) {
                if (p.getKey() == null) {
                    switch (p.getValue()) {
                        case EASY:
                            lvlIa = ia_loader.dif.EASY;
                            break;
                        case NORMAL:
                            lvlIa = ia_loader.dif.NORMAL;
                            break;
                        case HARD:
                            lvlIa = ia_loader.dif.HARD;
                            break;
                        case VERY_HARD:
                            lvlIa = ia_loader.dif.VERY_HARD;
                            break;
                    }
                    audio_manager.menu.play(menu_audio.indexes.option_selected);
                    audio_manager.menu.stop(menu_audio.indexes.menu_theme);
                    state = GameState.STORY_LOADING;
                    timeReference = current;
                }
            }
        }
        else if(state == GameState.STORY_LOADING){
            if(current - timeReference < 3000.0 && (!playing || current - timeReference < 300.0)){
                screenObjects.put(Item_Type.MENU, loads[stage]);
                playing = true;
            }
            else if(current - timeReference < 3000.0 && current - timeReference > 200.0 && playing){
                audio_manager.menu.play(menu_audio.indexes.Terry);
                try {
                    Thread.sleep(1000);
                    audio_manager.menu.play(menu_audio.indexes.Versus);
                    Thread.sleep(1000);
                    audio_manager.menu.play(audioEnemies[stage]);
                    Thread.sleep(1000);
                }catch (Exception e){}
                playing = false;
            }
            else{
                generateFight();
                audio_manager.startFight(player.getPlayer().getCharac(), enemy.getPlayer().getCharac(), scenarys[stage]);
                audio_manager.fight.loopMusic(fight_audio.music_indexes.map_theme);
                state = GameState.STORY_FIGHT;
                generateFight();
                screenObjects.remove(Item_Type.MENU);
                fight.getAnimation(screenObjects);
            }
        }
        else if(state == GameState.STORY_FIGHT){
            fight.getAnimation(screenObjects);
            if (fight.getEnd()) {
                Fight_Results resultado = fight.getFight_result();
                audio_manager.fight.stopMusic(fight_audio.music_indexes.map_theme);
                switch (resultado){
                    case UNFINISHED:
                    case PLAYER2_WIN:
                    case TIE:
                        audio_manager.fight.loopMusic(fight_audio.music_indexes.lose_theme);
                        actualMenu = loseMenu;
                        actualMenu.updateTime();
                        break;
                    case PLAYER1_WIN:
                        audio_manager.fight.loopMusic(fight_audio.music_indexes.win_theme);
                        actualMenu = winMenu;
                        actualMenu.updateTime();
                        break;
                }
                state = GameState.STORY_MENU;
                if(actualMenu == winMenu && stage == 0){
                    state = GameState.STORY_END;
                    timeReference = current;
                }
            }
        }
        else if(state == GameState.STORY_MENU){
            screenObjects.put(Item_Type.MENU,actualMenu.getFrame());
            Pair<menu, Selectionable> p = actualMenu.select();
            if(controlListener.getStatus(1, controlListener.ENT_INDEX) && p.getValue() != Selectionable.NONE) {
                if (p.getKey() == null) {
                    switch (p.getValue()) {
                        case LOSE_EXIT:
                            exit = true;
                            break;
                        case LOSE_RETRY:
                            generateFight();
                            break;
                        case WIN_CONTINUE:
                            ++stage;
                            saveGame();
                            state = GameState.STORY_LOADING;
                            timeReference = System.currentTimeMillis();
                            break;
                        case WIN_SAVE:
                            ++stage;
                            saveGame();
                            exit = true;
                            break;
                    }
                    audio_manager.menu.stop(menu_audio.indexes.menu_theme);
                    audio_manager.endFight();
                }
            }
        }
        else if(state == GameState.STORY_END){
            if(current - timeReference < 4000.0){
                screenObjects.put(Item_Type.MENU, ends[0]);
            }
            else if(current - timeReference > 12000.0){
                exit = true;
                audio_manager.fight.stopMusic(fight_audio.music_indexes.win_theme);
                audio_manager.fight.stopMusic(fight_audio.music_indexes.lose_theme);
                audio_manager.menu.play(menu_audio.indexes.menu_theme);
            }
            else{
                double aux = current - timeReference - 2000.0;
                int aux2 = (int)(aux / 200.0) % 2;
                screenObjects.put(Item_Type.MENU, ends[aux2+1]);
            }
        }
        return new Pair<>(exit, state);
    }

    public menu getActualMenu() {
        return actualMenu;
    }

    public void setActualMenu(menu actualMenu) {
        this.actualMenu = actualMenu;
    }

    public menu getWinMenu() {
        return winMenu;
    }

    public void setWinMenu(menu winMenu) {
        this.winMenu = winMenu;
    }

    public menu getLoseMenu() {
        return loseMenu;
    }

    public void setLoseMenu(menu loseMenu) {
        this.loseMenu = loseMenu;
    }

    public screenObject[] getLoads() {
        return loads;
    }

    public void setLoads(screenObject[] loads) {
        this.loads = loads;
    }

    public ia_loader.dif getLvlIa() {
        return lvlIa;
    }

    public void setLvlIa(ia_loader.dif lvlIa) {
        this.lvlIa = lvlIa;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public Playable_Character getCharac() {
        return charac;
    }

    public void setCharac(Playable_Character charac) {
        this.charac = charac;
    }

    public fight_controller getFight() {
        return fight;
    }

    public void setFight(fight_controller fight) {
        this.fight = fight;
    }

    public character_controller getPlayer() {
        return player;
    }

    public void setPlayer(character_controller player) {
        this.player = player;
    }

    public character_controller getEnemy() {
        return enemy;
    }

    public void setEnemy(character_controller enemy) {
        this.enemy = enemy;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public scenary getScene() {
        return scene;
    }

    public void setScene(scenary scene) {
        this.scene = scene;
    }

    public hitBox getMapLimit() {
        return mapLimit;
    }

    public void setMapLimit(hitBox mapLimit) {
        this.mapLimit = mapLimit;
    }

    public long getTimeReference() {
        return timeReference;
    }

    public void setTimeReference(long timeReference) {
        this.timeReference = timeReference;
    }

    public Scenario_type[] getScenarys() {
        return scenarys;
    }

    public void setScenarys(Scenario_type[] scenarys) {
        this.scenarys = scenarys;
    }

    public Playable_Character[] getEnemies() {
        return enemies;
    }

    public void setEnemies(Playable_Character[] enemies) {
        this.enemies = enemies;
    }
}
