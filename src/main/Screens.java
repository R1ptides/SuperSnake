package main;

public class Screens {
    public enum GameState {
        HOME, PLAYING, PAUSED, GAME_OVER
    }

    private GameState currentState;

    public Screens() {
        currentState = GameState.HOME; // start at home
    }

    public GameState getState() {
        return currentState;
    }

    public void setState(GameState state) {
        currentState = state;
    }

    public boolean isHome() { return currentState == GameState.HOME; }
    public boolean isPlaying() { return currentState == GameState.PLAYING; }
    public boolean isPaused() { return currentState == GameState.PAUSED; }
    public boolean isGameOver() { return currentState == GameState.GAME_OVER; }
}
