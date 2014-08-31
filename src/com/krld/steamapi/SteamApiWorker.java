package com.krld.steamapi;

import org.apache.http.client.utils.URIBuilder;

/**
 * Created by Andrey on 8/31/2014.
 */
public class SteamApiWorker implements SteamApiWorkerInterface {
    private static final String HOST = "api.steampowered.com";
    public static final String GET_MATCH_HISTORY = "/IDOTA2Match_570/GetMatchHistory/V001/";
    public static final String KEY = "key";
    public static final String ACCOUND_ID = "accound_id";
    private Model model;
    private String apiKey;
    private String domainName;
    private int steamId32;

    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Override
    public void saveMatches() {
        log("SAVE MATHES");
        try {
            URIBuilder builder = new URIBuilder();
            builder.setScheme("https")
                    .setHost(HOST)
                    .setPath(GET_MATCH_HISTORY)
                    .setParameter(KEY, apiKey)
                    .setParameter(ACCOUND_ID, steamId32+"");
            //TODO ...
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void setMainSteamId(int steamId32) {
        this.steamId32 = steamId32;
    }

    private void log(String s) {
        System.out.println(">Worker: " + s);

    }
}
