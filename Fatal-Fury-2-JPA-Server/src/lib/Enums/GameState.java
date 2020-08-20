package lib.Enums;

/**
 * The enum Game state.
 */
// ESTADO DEL CONTROLADOR DEL JUEGO
// Navegando por menús, jugando, dándole al escape en partida
public enum GameState {
    /**
     * Opening 1 game state.
     */
    OPENING_1,
    /**
     * Opening 2 game state.
     */
    OPENING_2,
    /**
     * Navigation game state.
     */
    NAVIGATION,
    /**
     * Players game state.
     */
    PLAYERS,
    /**
     * Map game state.
     */
    MAP,
    /**
     * Fight game state.
     */
    FIGHT,
    /**
     * Escape game state.
     */
    ESCAPE,
    /**
     * Ranking game state.
     */
    RANKING,
    /**
     * Typing game state.
     */
    TYPING,
    /**
     * Options game state.
     */
    OPTIONS,
    /**
     * Story game state.
     */
    STORY,
    /**
     * Story fight game state.
     */
    STORY_FIGHT,
    /**
     * Story loading game state.
     */
    STORY_LOADING,
    /**
     * Story menu game state.
     */
    STORY_MENU,
    /**
     * Story end game state.
     */
    STORY_END,
    /**
     * Story difficulty game state.
     */
    STORY_DIFFICULTY,
    /**
     * Demo game state.
     */
    DEMO,
    /**
     * Difficulty game state.
     */
    DIFFICULTY,
    /**
     * How to play game state.
     */
    HOW_TO_PLAY,
    /**
     * Sure game state.
     */
    SURE,
    // ESTADOS ESPECIALES PARA EL MODO ONLINE
    /**
     * Online fight game state.
     */
    ONLINE_FIGHT,
    /**
     * Online mode game state.
     */
    ONLINE_MODE,
    /**
     * Online searching fight game state.
     */
    ONLINE_SEARCHING_FIGHT,
    LOGIN_REGISTER,
    REGISTER,
    LOGIN,
    PRINCIPAL_GUI,
    PROFILE_GUI,
    CHARACTER_SELECTION,
    ONLINE_RANKING,
    SERVER_PROBLEM,
    GAME_END
}