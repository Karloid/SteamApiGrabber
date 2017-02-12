package com.krld.steamapi.model;

import com.krld.steamapi.jsonkeys.JsonResponseFormat;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.krld.steamapi.Utils.nvlInt;
import static com.krld.steamapi.jsonkeys.JsonResponseFormat.*;

/**
 * Created by Andrey on 8/31/2014.
 */
public class SQLiteModel implements Model {
    private Connection connection;

    public SQLiteModel(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            //connection.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveHero(String name, int id, String localizedName) {
        try {
            log("SAVE HERO " + name);
            PreparedStatement prep = connection.prepareStatement("insert into heroes values (?, ?, ?);");
            prep.setInt(1, id);
            prep.setString(2, name);
            prep.setString(3, localizedName);
            execute(prep);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void saveHero(Hero hero) {
        saveHero(hero.getName(), hero.getId(), hero.getLocalizedName());
    }

    @Override
    public List<Hero> getAllHeroes() {
        List<Hero> heroesList = new ArrayList<Hero>();
        try {
            Statement stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("select * from heroes;");
            while (rs.next()) {
                heroesList.add(new Hero(rs.getString(2), rs.getInt(1), rs.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return heroesList;
    }

    @Override
    public void saveMatches(ArrayList<Map<String, Object>> matches) {
        log("SAVE MATHES... count: " + matches.size());
        for (Map<String, Object> match : matches) {
            try {
                PreparedStatement prep = connection.prepareStatement("insert into matches (id, match_seq_num, start_time, lobby_type, radiant_team_id, dire_team_id) " +
                        " select ?, ?, ?, ?, ?, ? where not exists (select 1 from matches where id = ?);");
                prep.setInt(1, anInt(match.get(MATCH_ID))); // insert into ID field
                prep.setInt(2, anInt(match.get(MATCH_SEQ_NUM)));
                prep.setInt(3, anInt(match.get(START_TIME)));
                prep.setInt(4, anInt(match.get(LOBBY_TYPE)));
                prep.setInt(5, anInt(match.get(RADIANT_TEAM_ID)));
                prep.setInt(6, anInt(match.get(DIRE_TEAM_ID)));
                prep.setInt(7, anInt(match.get(MATCH_ID)));
                execute(prep);
                saveMatchPlayers(anInt(match.get(MATCH_ID)), (ArrayList<Map<String, Object>>) match.get(PLAYERS));
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public List<Player> getAllPlayers() {
        log("GET ALL PLAYERS");
        List<Player> players = new ArrayList<Player>();
        try {
            Statement stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("select account_id, persona_name from players;");
            while (rs.next()) {
                players.add(new Player(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    @Override
    public void updatePlayer(int id32, int communityVisibilityState, int profileState, String personaName, int lastLogoff,
                             String profileurl, String avatar, String avatarmedium, String avatarfull, int personastate,
                             String primaryclanid, int timecreated, int personastateflags) {
        PreparedStatement prep = null;
        try {
            prep = connection.prepareStatement("update players set persona_name = ?, " +
                    "community_visibility_state = ?," +
                    "profile_state = ?, " +
                    "last_logoff = ?, " +
                    "profile_url = ?, " +
                    "avatar = ?," +
                    "avatar_medium = ?, " +
                    "avatar_full = ?, " +
                    "persona_state = ?, " +
                    "primary_clan_id = ?, " +
                    "time_created = ?, " +
                    "persona_state_flags = ? " +
                    " where account_id = ?");

            prep.setString(1, personaName);
            prep.setInt(2, communityVisibilityState);
            prep.setInt(3, profileState);
            prep.setInt(4, lastLogoff);
            prep.setString(5, profileurl);
            prep.setString(6, avatar);
            prep.setString(7, avatarmedium);
            prep.setString(8, avatarfull);
            prep.setInt(9, personastate);
            prep.setString(10, primaryclanid);
            prep.setInt(11, timecreated);
            prep.setInt(12, personastateflags);
            prep.setInt(13, id32);
            execute(prep);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Match> getAllMatches() {
        log("GET ALL MATCHES");
        List<Match> matches = new ArrayList<Match>();
        try {
            Statement stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("select id, radiant_win, duration, start_time," +
                    "game_mode, lobby_type from matches;");
            while (rs.next()) {
                matches.add(new Match(rs.getInt("id"), rs.getInt("radiant_win"),
                        rs.getInt("duration"), rs.getInt("start_time"), rs.getInt("game_mode"),
                        rs.getInt("lobby_type")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    @Override
    public List<Match> getMatchesWithoutDetails() {
        log("GET MATCHES WITHOUT DETAILS");
        List<Match> matches = new ArrayList<Match>();
        try {
            Statement stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("select id, radiant_win, duration, start_time," +
                    "game_mode, lobby_type from matches where radiant_win is null order by id desc;");
            while (rs.next()) {
                matches.add(new Match(rs.getInt("id"), rs.getInt("radiant_win"),
                        rs.getInt("duration"), rs.getInt("start_time"), rs.getInt("game_mode"),
                        rs.getInt("lobby_type")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    @Override
    public void updateMatchDetails(Match match) {
        log("UPDATE MATCH DETAILS");
        PreparedStatement prep = null;
        try {
            prep = connection.prepareStatement("update matches set radiant_win = ?, " +
                    "duration = ?," +
                    "tower_status_radiant = ?, " +
                    "tower_status_dire = ?, " +
                    "barracks_status_radiant = ?, " +
                    "barracks_status_dire = ?," +
                    "cluster = ?, " +
                    "first_blood_time = ?, " +
                    "human_players = ?, " +
                    "league_id = ?, " +
                    "positive_votes = ?, " +
                    "negative_votes = ?, " +
                    "game_mode = ?, " +
                    "radiant_captain = ?, " +
                    "dire_captain = ? " +
                    " where id = ?");

            prep.setInt(1, match.getRadiantWin());
            prep.setInt(2, match.getDuration());
            prep.setInt(3, match.getTowerStatusRadiant());
            prep.setInt(4, match.getTowerStatusDire());
            prep.setInt(5, match.getBarracksStatusRadiant());
            prep.setInt(6, match.getBarracksStatusDire());
            prep.setInt(7, match.getCluster());
            prep.setInt(8, match.getFirstBloodTime());
            prep.setInt(9, match.getHumanPlayers());
            prep.setInt(10, match.getLeagueId());
            prep.setInt(11, match.getPositiveVotes());
            prep.setInt(12, match.getNegativeVotes());
            prep.setInt(13, match.getGameMode());
            prep.setInt(14, match.getRadiantCaptain());
            prep.setInt(15, match.getDireCaptain());
            prep.setInt(16, match.getId());
            execute(prep);
            log("prep update count: " + prep.getUpdateCount());
            updatePlayersInMatch(match.getPlayersInMatch());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayersInMatch(List<PlayerInMatch> playersInMatch) {
        log("UPDATE PLAYERS IN MATCH ");
        for (PlayerInMatch playerInMatch : playersInMatch) {
            PreparedStatement prep = null;
            try {
                prep = connection.prepareStatement("update players_in_matches set " +
                        "item_0 = ?," +
                        "item_1 = ?, " +
                        "item_2 = ?, " +
                        "item_3 = ?, " +
                        "item_4 = ?, " +
                        "item_5 = ?, " +
                        "kills = ?, " +
                        "deaths = ?, " +
                        "assists = ?, " +
                        "leaver_status = ?, " +
                        "gold = ?, " +
                        "last_hits = ?, " +
                        "denies = ?, " +
                        "gold_per_min = ?, " +
                        "xp_per_min = ?, " +
                        "gold_spent = ?, " +               //16
                        "hero_damage = ?, " +              //17
                        "tower_damage = ?, " +             //18
                        "hero_healing = ? " +              //19
                        " where match_id = ? " +           //20
                        " and account_id = ? " +           //21
                        " and player_slot = ? " +          //22
                        " and hero_id = ? ;");             //23

                prep.setInt(1, playerInMatch.getItem0());
                prep.setInt(2, playerInMatch.getItem1());
                prep.setInt(3, playerInMatch.getItem2());
                prep.setInt(4, playerInMatch.getItem3());
                prep.setInt(5, playerInMatch.getItem4());
                prep.setInt(6, playerInMatch.getItem5());
                prep.setInt(7, playerInMatch.getKills());
                prep.setInt(8, playerInMatch.getDeaths());
                prep.setInt(9, playerInMatch.getAssists());
                prep.setInt(10, playerInMatch.getLeaverStatus());
                prep.setInt(11, playerInMatch.getGold());
                prep.setInt(12, playerInMatch.getLastHits());
                prep.setInt(13, playerInMatch.getDenies());
                prep.setInt(14, playerInMatch.getGoldPerMin());
                prep.setInt(15, playerInMatch.getXpPerMin());
                prep.setInt(16, playerInMatch.getGoldSpent());
                prep.setInt(17, playerInMatch.getHeroDamage());
                prep.setInt(18, playerInMatch.getTowerDamage());
                prep.setInt(19, playerInMatch.getHeroHealing());
                prep.setInt(20, playerInMatch.getMatchId());
                prep.setInt(21, playerInMatch.getAccountId());
                prep.setInt(22, playerInMatch.getPlayerSlot());
                prep.setInt(23, playerInMatch.getHeroId());
                execute(prep);
                log("prep update count: " + prep.getUpdateCount());
                updateAbilityesPlayersInMatch(playerInMatch);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    private void updateAbilityesPlayersInMatch(PlayerInMatch playerInMatch) {
        PreparedStatement prep = null;
        try {
            prep = connection.prepareStatement("delete from ability_upgrades " +
                    "where player_in_match_id in (select id from players_in_matches " +
                    "where match_id = ? " +
                    "and account_id = ? " +
                    "and player_slot = ? " +
                    "and hero_id = ? " +
                    ");");
            prep.setInt(1, playerInMatch.getMatchId());
            prep.setInt(2, playerInMatch.getAccountId());
            prep.setInt(3, playerInMatch.getPlayerSlot());
            prep.setInt(4, playerInMatch.getHeroId());
            execute(prep);
            log("delete abilities: " + prep.getUpdateCount());
            if (playerInMatch.getAbilityUpgrades() != null) {
                for (Map<String, Object> abilityMap : playerInMatch.getAbilityUpgrades()) {
                    prep = connection.prepareStatement("insert into ability_upgrades (player_in_match_id, " +
                            "ability_id, " +
                            "time, " +
                            "level) " +
                            "select (select id from players_in_matches " +
                            "where match_id = ? " +
                            "and account_id = ? " +
                            "and player_slot = ? " +
                            "and hero_id = ? " +
                            ")," +
                            "?," +        //5
                            "?," +
                            "?" +
                            ";");
                    prep.setInt(1, playerInMatch.getMatchId());
                    prep.setInt(2, playerInMatch.getAccountId());
                    prep.setInt(3, playerInMatch.getPlayerSlot());
                    prep.setInt(4, playerInMatch.getHeroId());
                    prep.setInt(5, nvlInt(abilityMap.get(JsonResponseFormat.ABILITY), -1));
                    prep.setInt(6, nvlInt(abilityMap.get(JsonResponseFormat.TIME), -1));
                    prep.setInt(7, nvlInt(abilityMap.get(JsonResponseFormat.LEVEL), -1));
                    execute(prep);
                    log("insert ability: " + prep.getUpdateCount());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute(PreparedStatement prep) throws SQLException {
        prep.execute();
        if (Math.random() < 0.05) {
            //connection.commit();
        }
    }

    @Override
    public void updateMatchDetails(Map<String, Object> matchDetailsRoot) {
        int matchId = anInt(matchDetailsRoot.get(MATCH_ID));
        int radiantWin = ((Boolean) (matchDetailsRoot.get(RADIANT_WIN)) ? 1 : 0);
        int duration = anInt(matchDetailsRoot.get(DURATION));
        int startTime = anInt(matchDetailsRoot.get(DURATION));
        int gameMode = anInt(matchDetailsRoot.get(GAME_MODE));
        int lobbyType = anInt(matchDetailsRoot.get(LOBBY_TYPE));
        int towerStatusRadiant = anInt(matchDetailsRoot.get(TOWER_STATUS_RADIANT));
        int towerStatusDire = anInt(matchDetailsRoot.get(TOWER_STATUS_DIRE));
        int cluster = anInt(matchDetailsRoot.get(CLUSTER));
        int firstBloodTime = anInt(matchDetailsRoot.get(FIRST_BLOOD_TIME));
        int humanPlayers = anInt(matchDetailsRoot.get(JsonResponseFormat.HUMAN_PLAYERS));
        int leagueId = anInt(matchDetailsRoot.get(JsonResponseFormat.LEAGUE_ID));
        int positiveVotes = anInt(matchDetailsRoot.get(JsonResponseFormat.POSITIVE_VOTES));
        int negativeVotes = anInt(matchDetailsRoot.get(JsonResponseFormat.NEGATIVE_VOTES));
        int radiantCaptain = anInt(matchDetailsRoot.get(JsonResponseFormat.RADIANT_CAPTAIN));
        int direCaptain = anInt(matchDetailsRoot.get(JsonResponseFormat.DIRE_CAPTAIN));
        //TODO impl
        throw new NotImplementedException();


    }

    private void saveMatchPlayers(int matchId, ArrayList<Map<String, Object>> players) throws SQLException {
        for (Map<String, Object> playerInMatch : players) {
            PreparedStatement prep = connection.prepareStatement("insert into players_in_matches (match_id, account_id, player_slot, hero_id) select ?, ?, ?, ? " +
                    " where not exists (select 1 from players_in_matches " +
                    "where match_id = ? and account_id = ? and player_slot = ?);");

            int accountId = nvlInt(playerInMatch.get(ACCOUNT_ID), -1);
            savePlayer(accountId);
            prep.setInt(1, matchId);
            prep.setInt(2, accountId);
            prep.setInt(3, anInt(playerInMatch.get(PLAYER_SLOT)));
            prep.setInt(4, anInt(playerInMatch.get(HERO_ID)));
            prep.setInt(5, matchId);
            prep.setInt(6, accountId);
            prep.setInt(7, anInt(playerInMatch.get(PLAYER_SLOT)));
            execute(prep);
        }
    }

    private void savePlayer(int accountId) throws SQLException {
        PreparedStatement prep = connection.prepareStatement("insert into players (account_id) select ? where not exists " +
                " (select 1 from players where account_id = ?);");
        prep.setInt(1, accountId);
        prep.setInt(2, accountId);
        execute(prep);
    }

    private void log(String s) {
        System.out.println(">SQLiteModel: " + s);
    }

    private long anLong(Object value) {
        return ((Double) value).longValue();
    }

    private int anInt(Object value) {
        return ((Double) value).intValue();
    }
}
