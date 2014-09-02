package com.krld.steamapi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrey on 8/31/2014.
 */
public class SQLiteModel implements Model {
    private Connection connection;

    public SQLiteModel(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
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
            prep.execute();
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
        log("SAVE MATHES");
        for (Map<String, Object> match : matches) {
            try {
                PreparedStatement prep = connection.prepareStatement("insert into matches (id, match_seq_num, start_time, lobby_type, radiant_team_id, dire_team_id) " +
                        " select ?, ?, ?, ?, ?, ? where not exists (select 1 from matches where id = ?);");
                prep.setInt(1, anInt(match.get(JsonResponseFormat.MATCH_ID))); // insert into ID field
                prep.setInt(2, anInt(match.get(JsonResponseFormat.MATCH_SEQ_NUM)));
                prep.setInt(3, anInt(match.get(JsonResponseFormat.START_TIME)));
                prep.setInt(4, anInt(match.get(JsonResponseFormat.LOBBY_TYPE)));
                prep.setInt(5, anInt(match.get(JsonResponseFormat.RADIANT_TEAM_ID)));
                prep.setInt(6, anInt(match.get(JsonResponseFormat.DIRE_TEAM_ID)));
                prep.setInt(7, anInt(match.get(JsonResponseFormat.MATCH_ID)));
                prep.execute();
                saveMatchPlayers(anInt(match.get(JsonResponseFormat.MATCH_ID)), (ArrayList<Map<String, Object>>) match.get(JsonResponseFormat.PLAYERS));
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public List<Player> getAllPlayers() {
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

            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveMatchPlayers(int matchId, ArrayList<Map<String, Object>> players) throws SQLException {
        for (Map<String, Object> playerInMatch : players) {
            PreparedStatement prep = connection.prepareStatement("insert into players_in_matches (match_id, account_id, player_slot, hero_id) select ?, ?, ?, ? " +
                                                                                                          " where not exists (select 1 from players_in_matches " +
                                                                                                                                "where match_id = ? and account_id = ? and player_slot = ?);");

            savePlayer(anInt(playerInMatch.get(JsonResponseFormat.ACCOUNT_ID)));
            prep.setInt(1, matchId);
            prep.setInt(2, anInt(playerInMatch.get(JsonResponseFormat.ACCOUNT_ID)));
            prep.setInt(3, anInt(playerInMatch.get(JsonResponseFormat.PLAYER_SLOT)));
            prep.setInt(4, anInt(playerInMatch.get(JsonResponseFormat.HERO_ID)));
            prep.setInt(5, matchId);
            prep.setInt(6, anInt(playerInMatch.get(JsonResponseFormat.ACCOUNT_ID)));
            prep.setInt(7, anInt(playerInMatch.get(JsonResponseFormat.PLAYER_SLOT)));
            prep.execute();
        }
    }

    private void savePlayer(int accountId) throws SQLException {
        PreparedStatement prep = connection.prepareStatement("insert into players (account_id) select ? where not exists " +
                                                                                    " (select 1 from players where account_id = ?);");
        prep.setInt(1, accountId);
        prep.setInt(2, accountId);
        prep.execute();
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
