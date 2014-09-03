package com.krld.steamapi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrey on 9/3/2014.
 */
public class PlayerInMatch {

    private final int accountId;
    private final int playerSlot;
    private final int heroId;
    private final int item0;
    private final int item1;
    private final int item2;
    private final int item3;
    private final int item4;
    private final int item5;
    private final int kills;
    private final int deaths;
    private final int assists;
    private final int leaverStatus;
    private final int gold;
    private final int lastHits;
    private final int denies;
    private final int goldSpent;
    private final int heroDamage;
    private final int towerDamage;
    private final int heroHealing;
    private final int level;
    private final int matchId;
    private final int goldPerMin;
    private final int xpPerMin;
    private List<Map<String, Object>> abilityUpgrades;

    public PlayerInMatch(int accountId, int playerSlot, int heroId, int item0, int item1, int item2,
                         int item3, int item4, int item5, int kills, int deaths, int assists,
                         int leaverStatus, int gold, int lastHits, int denies, int goldSpent,
                         int heroDamage, int towerDamage, int heroHealing, int level, int matchId,
                         int goldPerMin, int xpPerMin) {
        this.accountId = accountId;
        this.playerSlot = playerSlot;
        this.heroId = heroId;
        this.item0 = item0;
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.item5 = item5;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.leaverStatus = leaverStatus;
        this.gold = gold;
        this.lastHits = lastHits;
        this.denies = denies;
        this.goldSpent = goldSpent;
        this.heroDamage = heroDamage;
        this.towerDamage = towerDamage;
        this.heroHealing = heroHealing;
        this.level = level;
        this.matchId = matchId;
        this.goldPerMin = goldPerMin;
        this.xpPerMin = xpPerMin;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getGoldPerMin() {
        return goldPerMin;
    }

    public int getXpPerMin() {
        return xpPerMin;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getPlayerSlot() {
        return playerSlot;
    }

    public int getHeroId() {
        return heroId;
    }

    public int getItem0() {
        return item0;
    }

    public int getItem1() {
        return item1;
    }

    public int getItem2() {
        return item2;
    }

    public int getItem3() {
        return item3;
    }

    public int getItem4() {
        return item4;
    }

    public int getItem5() {
        return item5;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }

    public int getLeaverStatus() {
        return leaverStatus;
    }

    public int getGold() {
        return gold;
    }

    public int getLastHits() {
        return lastHits;
    }

    public int getDenies() {
        return denies;
    }

    public int getGoldSpent() {
        return goldSpent;
    }

    public int getHeroDamage() {
        return heroDamage;
    }

    public int getTowerDamage() {
        return towerDamage;
    }

    public int getHeroHealing() {
        return heroHealing;
    }

    public int getLevel() {
        return level;
    }

    public void setAbilityUpgrades(List<Map<String, Object>> abilityUpgrades) {
        this.abilityUpgrades = abilityUpgrades;
    }

    public List<Map<String, Object>> getAbilityUpgrades() {
        return abilityUpgrades;
    }
}
