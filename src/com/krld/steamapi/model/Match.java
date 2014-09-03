package com.krld.steamapi.model;

import com.krld.steamapi.model.PlayerInMatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey on 9/2/2014.
 */
public class Match {
    public static final int NULL = -1;
    private final int id;
    private final int radiantWin;
    private final int duration;
    private final int startTime;
    private final int gameMode;
    private final int lobbyType;
    private final int towerStatusRadiant;
    private final int towerStatusDire;
    private final int cluster;
    private final int firstBloodTime;
    private final int humanPlayers;
    private final int leagueId;
    private final int positiveVotes;
    private final int negativeVotes;
    private final int radiantCaptain;
    private final int direCaptain;
    private final int barracksStatusRadiant;
    private final int barracksStatusDire;
    private List<PlayerInMatch> playersInMatch;

    public Match(int id, int radiantWin, int duration, int startTime, int gameMode, int lobbyType) {
        this.id = id;
        this.radiantWin = radiantWin;
        this.duration = duration;
        this.startTime = startTime;
        this.gameMode = gameMode;
        this.lobbyType = lobbyType;
        this.towerStatusRadiant = NULL;
        this.towerStatusDire = NULL;
        this.cluster = NULL;
        this.firstBloodTime = NULL;
        this.humanPlayers = NULL;
        this.leagueId = NULL;
        this.positiveVotes = NULL;
        this.negativeVotes = NULL;
        this.radiantCaptain = NULL;
        this.direCaptain = NULL;
        this.barracksStatusRadiant = NULL;
        this.barracksStatusDire = NULL;
        playersInMatch = new ArrayList<PlayerInMatch>(10);
    }

    public Match(int matchId, int radiantWin, int duration, int startTime, int gameMode, int lobbyType,
                 int towerStatusRadiant, int towerStatusDire, int cluster, int firstBloodTime, int humanPlayers,
                 int leagueId, int positiveVotes, int negativeVotes, int radiantCaptain, int direCaptain, int barracksStatusRadiant, int barracksStatusDire) {
        this.id = matchId;
        this.radiantWin = radiantWin;
        this.duration = duration;
        this.startTime = startTime;
        this.gameMode = gameMode;
        this.lobbyType = lobbyType;
        this.towerStatusRadiant = towerStatusRadiant;
        this.towerStatusDire = towerStatusDire;
        this.cluster = cluster;
        this.firstBloodTime = firstBloodTime;
        this.humanPlayers = humanPlayers;
        this.leagueId = leagueId;
        this.positiveVotes = positiveVotes;
        this.negativeVotes = negativeVotes;
        this.radiantCaptain = radiantCaptain;
        this.direCaptain = direCaptain;
        this.barracksStatusRadiant = barracksStatusRadiant;
        this.barracksStatusDire = barracksStatusDire;
        playersInMatch = new ArrayList<PlayerInMatch>(10);
    }

    public List<PlayerInMatch> getPlayersInMatch() {
        return playersInMatch;
    }

    public int getId() {
        return id;
    }

    public int getRadiantWin() {
        return radiantWin;
    }

    public int getDuration() {
        return duration;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getGameMode() {
        return gameMode;
    }

    public int getLobbyType() {
        return lobbyType;
    }

    public int getDireCaptain() {
        return direCaptain;
    }

    public int getTowerStatusRadiant() {
        return towerStatusRadiant;
    }

    public int getTowerStatusDire() {
        return towerStatusDire;
    }

    public int getCluster() {
        return cluster;
    }

    public int getFirstBloodTime() {
        return firstBloodTime;
    }

    public int getHumanPlayers() {
        return humanPlayers;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public int getPositiveVotes() {
        return positiveVotes;
    }

    public int getNegativeVotes() {
        return negativeVotes;
    }

    public int getRadiantCaptain() {
        return radiantCaptain;
    }

    public int getBarracksStatusDire() {
        return barracksStatusDire;
    }

    public int getBarracksStatusRadiant() {
        return barracksStatusRadiant;
    }

    public void addPlayer(PlayerInMatch player) {
        playersInMatch.add(player);
    }
}
