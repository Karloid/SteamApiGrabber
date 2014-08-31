package com.krld.steamapi;

/**
 * Created by Andrey on 8/31/2014.
 */
public interface SteamApiWorkerInterface {
    void setModel(Model model);

    void setApiKey(String property);

    void setDomainName(String property);

    void saveMatches();

}
