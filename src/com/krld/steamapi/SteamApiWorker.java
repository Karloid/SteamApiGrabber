package com.krld.steamapi;

import com.krld.steamapi.model.Model;

/**
 * Created by Andrey on 8/31/2014.
 */
public interface SteamApiWorker {
    void setModel(Model model);

    void setApiKey(String property);

    void setDomainName(String property);

    void saveAllMatchesByHero();

    void setMainSteamId(int steamId32);

    void saveAllHeroes();

    void updatePlayersInfo();

    void saveAllMatchesByHero(int id);

    void updateAllMatchesDetails();
}
