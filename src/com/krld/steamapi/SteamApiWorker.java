package com.krld.steamapi;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.krld.steamapi.JsonResponseFormat.*;

/**
 * Created by Andrey on 8/31/2014.
 */
public class SteamApiWorker implements SteamApiWorkerInterface {
    private static final String HOST = "api.steampowered.com";
    public static final String GET_MATCH_HISTORY = "/IDOTA2Match_570/GetMatchHistory/V001/";
    private static final String GET_HEROES = "/IEconDOTA2_570/GetHeroes/v0001/";
    public static final String KEY = "key";
    public static final String ACCOUNT_ID = "account_id";
    public static final int INTERVAL_BETWEEN_REQUESTS = 1000;
    public static final String START_AT_MATCH_ID = "start_at_match_id";
    public static final String DATE_MAX = "date_max";
    public static final String EN_US = "en_us";
    public static final String LANGUAGE = "language";
    private static final int OK_STATUS_HEROES = 200;
    public static final String HERO_ID = "hero_id";
    private Model model;
    private String apiKey;
    private String domainName;
    private int steamId32;
    private int DAY_UNIX_TIME_STAMP = 24 * 60 * 60;

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
    public void saveAllMatchesByHero() {
        log("SAVE MATHES");
        List<Hero> heroes = model.getAllHeroes();
        for (Hero hero : heroes) {
            try {
                int heroId = hero.getId();
                int resultsRemaining = 1;
                int startAtMatchId = -1;
                while (resultsRemaining > 0) {
                    log("make request with " + START_AT_MATCH_ID + "=" + startAtMatchId + "&" + HERO_ID + "=" + heroId);
                    URI uri = getMatchHistoryUri(startAtMatchId, heroId);
                    HttpGet httpGet = new HttpGet(uri);
                    String result = makeRequest(httpGet);
                    log("resultRequest: " + result);
                    Map root = (Map<String, Object>) new Gson().fromJson(result, Map.class).get(RESULT);
                    if (anInt(root.get(STATUS)) != OK_STATUS) {
                        throw new Exception("Incorrect response status: " + anInt(root.get(STATUS)));
                    }
                    parseAndSaveMatches((ArrayList<Map<String, Object>>) root.get(JsonResponseFormat.MATCHES));
                    resultsRemaining = anInt(root.get(RESULTS_REMAINING));

                    ArrayList<Map<String, Object>> matches = (ArrayList<Map<String, Object>>) root.get(MATCHES);
                    startAtMatchId = anInt(matches.get(matches.size() - 1).get(JsonResponseFormat.MATCH_ID));
                    sleep(INTERVAL_BETWEEN_REQUESTS);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void sleep(int interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void parseAndSaveMatches(ArrayList<Map<String, Object>> matches) {
        model.saveMatches(matches);
    }

    private long anLong(Object value) {
        return ((Double) value).longValue();
    }

    private int anInt(Object value) {
        return ((Double) value).intValue();
    }

    private URI getMatchHistoryUri(int startAtMatchId, int heroId) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https")
                .setHost(HOST)
                .setPath(GET_MATCH_HISTORY)
                .setParameter(KEY, apiKey)
                .setParameter(ACCOUNT_ID, steamId32 + "");
        if (startAtMatchId != -1) {
            builder.setParameter(START_AT_MATCH_ID, startAtMatchId + "");
        }
        if (heroId > 0) {
            builder.setParameter(HERO_ID, heroId + "");
        }
        return builder.build();
    }

    private String makeRequest(HttpGet httpGet) {
        InputStream inputStream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();
                String resposneAsString = null;
                resposneAsString = IOUtils.toString(inputStream);
                return resposneAsString;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "nothing";
    }

    @Override
    public void setMainSteamId(int steamId32) {
        this.steamId32 = steamId32;
    }

    @Override
    public void saveAllHeroes() {
        log("SAVE ALL HEROES");
        try {
            URI uri = getHeroesUri();
            HttpGet httpGet = new HttpGet(uri);
            String result = makeRequest(httpGet);
            log("resultRequest: " + result);
            Map root = (Map<String, Object>) new Gson().fromJson(result, Map.class).get(RESULT);
            if (anInt(root.get(STATUS)) != OK_STATUS_HEROES) {
                throw new Exception("Incorrect response status: " + anInt(root.get(STATUS)));
            }
            parseAndSaveHeroes((ArrayList<Map<String, Object>>) root.get(JsonResponseFormat.HEROES));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void parseAndSaveHeroes(ArrayList<Map<String, Object>> heroesMaps) {
        for (Map<String, Object> heroMap : heroesMaps) {
            model.saveHero(new Hero((String) heroMap.get("name"), anInt(heroMap.get("id")), (String) heroMap.get("localized_name")));
        }

    }

    private URI getHeroesUri() throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https")
                .setHost(HOST)
                .setPath(GET_HEROES)
                .setParameter(KEY, apiKey)
                .setParameter(LANGUAGE, EN_US);
        return builder.build();
    }

    private void log(String s) {
        System.out.println(">Worker: " + s);

    }
}
