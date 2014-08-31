package com.krld.steamapi;

/**
 * Created by Andrey on 8/31/2014.
 */
public class SteamApiWorker implements SteamApiWorkerInterface {
    private Model model;
    private String apiKey;
    private String domainName;

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

    }

    private void log(String s) {
        System.out.println(">Worker: " + s);
    }
}
