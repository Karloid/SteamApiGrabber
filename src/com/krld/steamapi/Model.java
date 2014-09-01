package com.krld.steamapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrey on 8/31/2014.
 */
public interface Model {
    void saveHero(String name, int id, String localizedName);

    void saveHero(Hero hero);

    List<Hero> getAllHeroes();

    void saveMatches(ArrayList<Map<String,Object>> matches);
}
