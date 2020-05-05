package lib.Enums;

// ESTADO DEL CONTROLADOR DEL JUEGO
// Navegando por menús, jugando, dándole al escape en partida
public enum GameState {
    OPENING_1, OPENING_2,
    NAVIGATION, PLAYERS, MAP, FIGHT, ESCAPE, RANKING, TYPING, OPTIONS,
    STORY, STORY_FIGHT, STORY_LOADING, STORY_MENU, STORY_END, STORY_DIFFICULTY
}
