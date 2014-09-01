package com.krld.steamapi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    private void log(String s) {
        System.out.println(">SQLiteModel: " + s);
    }
}
