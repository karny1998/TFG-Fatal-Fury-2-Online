package lib.utils.sendableObjects.simpleObjects;

import lib.utils.sendableObjects.sendableObject;

import java.util.ArrayList;

public class profile extends sendableObject {
    private String user;
    private int points;
    private int normalWins, normalLoses, rankedWins, rankedLoses;
    private ArrayList<game> games;

    public profile(String user, int points, int normalWins, int normalLoses, int rankedWins, int rankedLoses, ArrayList<game> games) {
        this.user = user;
        this.points = points;
        this.normalWins = normalWins;
        this.normalLoses = normalLoses;
        this.rankedWins = rankedWins;
        this.rankedLoses = rankedLoses;
        this.games = games;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getNormalWins() {
        return normalWins;
    }

    public void setNormalWins(int normalWins) {
        this.normalWins = normalWins;
    }

    public int getNormalLoses() {
        return normalLoses;
    }

    public void setNormalLoses(int normalLoses) {
        this.normalLoses = normalLoses;
    }

    public int getRankedWins() {
        return rankedWins;
    }

    public void setRankedWins(int rankedWins) {
        this.rankedWins = rankedWins;
    }

    public int getRankedLoses() {
        return rankedLoses;
    }

    public void setRankedLoses(int rankedLoses) {
        this.rankedLoses = rankedLoses;
    }

    public ArrayList<game> getGames() {
        return games;
    }

    public void setGames(ArrayList<game> games) {
        this.games = games;
    }
}
