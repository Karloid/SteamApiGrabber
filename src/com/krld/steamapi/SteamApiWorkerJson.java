package com.krld.steamapi;

import com.google.gson.Gson;
import com.krld.steamapi.jsonkeys.JsonResponseFormat;
import com.krld.steamapi.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.krld.steamapi.Utils.*;
import static com.krld.steamapi.jsonkeys.JsonResponseFormat.*;

/**
 * Created by Andrey on 8/31/2014.
 */
public class SteamApiWorkerJson implements SteamApiWorker {
    private static final String HOST = "api.steampowered.com";
    public static final String GET_MATCH_HISTORY = "/IDOTA2Match_570/GetMatchHistory/V001/";
    private static final String GET_HEROES = "/IEconDOTA2_570/GetHeroes/v0001/";
    public static final String KEY = "key";
    public static final String ACCOUNT_ID = "account_id";
    public static final int INTERVAL_BETWEEN_REQUESTS = 900;
    public static final String START_AT_MATCH_ID = "start_at_match_id";
    public static final String DATE_MAX = "date_max";
    public static final String EN_US = "en_us";
    public static final String LANGUAGE = "language";
    private static final int OK_STATUS_HEROES = 200;
    public static final String HERO_ID = "hero_id";
    public static final int MAX_PLAYERS_IN_DETAIL_REQUEST = 99;
    private static final String GET_PLAYERS_SUMMARIES = "/ISteamUser/GetPlayerSummaries/v0002/";
    public static final String STEAMIDS = "steamids";
    public static final long MAGIC_STEAM_ID64_LONG = 76561197960265728l;
    public static final String RESPONSE = "response";
    public static final String STEAMID = "steamid";
    public static final String COMMUNITYVISIBILITYSTATE = "communityvisibilitystate";
    public static final String PROFILE_STATE = "profilestate";
    public static final String PERSONANAME = "personaname";
    public static final String LASTLOGOFF = "lastlogoff";
    public static final String PROFILEURL = "profileurl";
    public static final String AVATAR = "avatar";
    public static final String AVATARMEDIUM = "avatarmedium";
    public static final String AVATARFULL = "avatarfull";
    public static final String PERSONASTATE = "personastate";
    public static final String PRIMARYCLANID = "primaryclanid";
    public static final String TIME_CREATED = "timecreated";
    public static final String PERSONA_STATE_FLAGS = "personastateflags";
    public static final String NOTHING = "nothing";
    public static final String GET_MATCH_DETAILS = "/IDOTA2Match_570/GetMatchDetails/V001/";
    private Model model;
    private String apiKey;
    private String domainName;
    private int steamId32;
    private int DAY_UNIX_TIME_STAMP = 24 * 60 * 60;
    private boolean verboseLogging = true;

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
    public void saveAllMatchesByHero(int id) {
        log("SAVE MATHES");
        List<Hero> heroes = model.getAllHeroes();
        for (Hero hero : heroes) {

            int heroId = hero.getId();
            int resultsRemaining = 1;
            int startAtMatchId = -1;
            while (resultsRemaining > 0) {
                try {
                    log("make request with " + START_AT_MATCH_ID + "=" + startAtMatchId + "&" + ACCOUNT_ID + "=" + id + "&" + HERO_ID + "=" + heroId);
                    URI uri = getMatchHistoryUri(startAtMatchId, heroId, id);
                    HttpGet httpGet = new HttpGet(uri);
                    String result = makeRequest(httpGet);
                    // log("resultRequest: " + result);
                    if (result.equals(NOTHING)) {
                        throw new Exception("Result is nothing");
                    }
                    Map root = (Map<String, Object>) new Gson().fromJson(result, Map.class).get(RESULT);
                    if (anInt(root.get(STATUS)) != OK_STATUS) {
                        throw new Exception("Incorrect response status: " + anInt(root.get(STATUS)));
                    }
                    parseAndSaveMatches((ArrayList<Map<String, Object>>) root.get(JsonResponseFormat.MATCHES));
                    resultsRemaining = anInt(root.get(RESULTS_REMAINING));

                    ArrayList<Map<String, Object>> matches = (ArrayList<Map<String, Object>>) root.get(MATCHES);
                    if (matches.size() > 0) {
                        startAtMatchId = anInt(matches.get(matches.size() - 1).get(JsonResponseFormat.MATCH_ID));
                    }
                    sleep(INTERVAL_BETWEEN_REQUESTS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void updateAllMatchesDetails() {
        List<Match> allMatches = model.getMatchesWithoutDetails();
        for (Match match : allMatches) {
            log("All players count: " + allMatches.size() + "; current match index: " + allMatches.indexOf(match));
            updateMatchDetails(match.getId());
            sleep(INTERVAL_BETWEEN_REQUESTS);
        }
    }

    private void updateMatchDetails(int id) {
        URI uri = null;
        try {
            uri = getMatchDetailsUri(id);
            HttpGet httpGet = new HttpGet(uri);
            String result = makeRequest(httpGet);
            //logV("resultRequest: " + result);
            if (result.equals(NOTHING)) {
                throw new Exception("Result is nothing");
            }
            Map<String, Object> matchDetailsRoot = (Map<String, Object>) new Gson().fromJson(result, Map.class).get(RESULT);
            parseAndSaveMatchDetails(matchDetailsRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logV(String s) {
        if (verboseLogging) {
            log(s);
        }
    }

    private void parseAndSaveMatchDetails(Map<String, Object> matchDetailsRoot) {
        int matchId = anInt(matchDetailsRoot.get(MATCH_ID));
        int radiantWin = ((Boolean) (matchDetailsRoot.get(RADIANT_WIN)) ? 1 : 0);
        int duration = anInt(matchDetailsRoot.get(DURATION));
        int startTime = anInt(matchDetailsRoot.get(DURATION));
        int gameMode = anInt(matchDetailsRoot.get(GAME_MODE));
        int lobbyType = anInt(matchDetailsRoot.get(LOBBY_TYPE));
        int towerStatusRadiant = anInt(matchDetailsRoot.get(TOWER_STATUS_RADIANT));
        int towerStatusDire = anInt(matchDetailsRoot.get(TOWER_STATUS_DIRE));
        int barracksStatusRadiant = anInt(matchDetailsRoot.get(BARRACKS_STATUS_RADIANT));
        int barracksStatusDire = anInt(matchDetailsRoot.get(BARRACKS_STATUS_DIRE));
        int cluster = anInt(matchDetailsRoot.get(CLUSTER));
        int firstBloodTime = anInt(matchDetailsRoot.get(FIRST_BLOOD_TIME));
        int humanPlayers = anInt(matchDetailsRoot.get(HUMAN_PLAYERS));
        int leagueId = anInt(matchDetailsRoot.get(LEAGUE_ID));
        int positiveVotes = anInt(matchDetailsRoot.get(POSITIVE_VOTES));
        int negativeVotes = anInt(matchDetailsRoot.get(NEGATIVE_VOTES));
        int radiantCaptain = nvlInt(matchDetailsRoot.get(RADIANT_CAPTAIN), -1);
        int direCaptain = nvlInt(matchDetailsRoot.get(DIRE_CAPTAIN), -1);

        Match match = new Match(matchId, radiantWin, duration, startTime, gameMode, lobbyType,
                towerStatusRadiant, towerStatusDire, cluster, firstBloodTime, humanPlayers, leagueId,
                positiveVotes, negativeVotes, radiantCaptain, direCaptain, barracksStatusRadiant, barracksStatusDire);

        ArrayList<Map<String, Object>> playersMaps = (ArrayList<Map<String, Object>>) matchDetailsRoot.get(PLAYERS);
        for (Map<String, Object> playerMap : playersMaps ) {
            int accountId = nvlInt(playerMap.get(ACCOUNT_ID), -1);
            int playerSlot = nvlInt(playerMap.get(PLAYER_SLOT), -1);
            int heroId = nvlInt(playerMap.get(HERO_ID), -1);
            int item0 = nvlInt(playerMap.get(ITEM_0), -1);
            int item1 = nvlInt(playerMap.get(ITEM_1), -1);
            int item2 = nvlInt(playerMap.get(ITEM_2), -1);
            int item3 = nvlInt(playerMap.get(ITEM_3), -1);
            int item4 = nvlInt(playerMap.get(ITEM_4), -1);
            int item5 = nvlInt(playerMap.get(ITEM_5), -1);
            int kills = nvlInt(playerMap.get(KILLS), -1);
            int deaths = nvlInt(playerMap.get(DEATHS), -1);
            int assists = nvlInt(playerMap.get(ASSISTS), -1);
            int leaverStatus = nvlInt(playerMap.get(LEAVER_STATUS), -1);
            int gold = nvlInt(playerMap.get(GOLD), -1);
            int lastHits = nvlInt(playerMap.get(LAST_HITS), -1);
            int denies = nvlInt(playerMap.get(DENIES), -1);
            int goldSpent = nvlInt(playerMap.get(GOLD_SPENT), -1);
            int heroDamage = nvlInt(playerMap.get(HERO_DAMAGE), -1);
            int towerDamage = nvlInt(playerMap.get(TOWER_DAMAGE), -1);
            int heroHealing = nvlInt(playerMap.get(HERO_HEALING), -1);
            int level = nvlInt(playerMap.get(LEVEL), -1);
            int goldPerMin = nvlInt(playerMap.get(GOLD_PER_MIN), -1);
            int xpPerMin = nvlInt(playerMap.get(XP_PER_MIN), -1);
            PlayerInMatch player = new PlayerInMatch(
                    accountId,
                    playerSlot,
                    heroId,
                    item0,
                    item1,
                    item2,
                    item3,
                    item4,
                    item5,
                    kills,
                    deaths,
                    assists,
                    leaverStatus,
                    gold,
                    lastHits,
                    denies,
                    goldSpent,
                    heroDamage,
                    towerDamage,
                    heroHealing,
                    level,
                    matchId,
                    goldPerMin,
                    xpPerMin
            );
            player.setAbilityUpgrades((List<Map<String, Object>>) playerMap.get(JsonResponseFormat.ABILITY_UPGRADES));
            match.addPlayer(player);
        }

        model.updateMatchDetails(match);
    }

    private URI getMatchDetailsUri(int id) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https")
                .setHost(HOST)
                .setPath(GET_MATCH_DETAILS)
                .setParameter(KEY, apiKey)
                .setParameter(MATCH_ID, id + "");
        return builder.build();
    }

    @Override
    public void saveAllMatchesByHero() {
        saveAllMatchesByHero(steamId32);

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

    private URI getMatchHistoryUri(int startAtMatchId, int heroId, int id32) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https")
                .setHost(HOST)
                .setPath(GET_MATCH_HISTORY)
                .setParameter(KEY, apiKey)
                .setParameter(ACCOUNT_ID, id32 + "");
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
                String responseAsString = null;
                responseAsString = IOUtils.toString(inputStream);
                return responseAsString;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return NOTHING;
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
            //log("resultRequest: " + result);
            if (result.equals(NOTHING)) {
                throw new Exception("Result is nothing!");
            }
            Map root = (Map<String, Object>) new Gson().fromJson(result, Map.class).get(RESULT);
            if (anInt(root.get(STATUS)) != OK_STATUS_HEROES) {
                throw new Exception("Incorrect response status: " + anInt(root.get(STATUS)));
            }
            parseAndSaveHeroes((ArrayList<Map<String, Object>>) root.get(JsonResponseFormat.HEROES));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void updatePlayersInfo() {
        List<Player> allPlayers = model.getAllPlayers();
        List<Player> playersToRequest = new ArrayList<Player>();
        for (Player player : allPlayers) {
            if (player.getPersonaname() == null) {
                playersToRequest.add(player);
                if (playersToRequest.size() > MAX_PLAYERS_IN_DETAIL_REQUEST) {
                    updatePlayersNames(playersToRequest);
                    playersToRequest.clear();
                    log("All players count: " + allPlayers.size() + "; current player index: " + allPlayers.indexOf(player));
                    sleep(INTERVAL_BETWEEN_REQUESTS);
                }
            }
        }
        if (!playersToRequest.isEmpty()) {
            updatePlayersNames(playersToRequest);
        }
    }

    private void updatePlayersNames(List<Player> playersToRequest) {
        String steamIds = "";
        for (Player player : playersToRequest) {
            long id64 = player.getAccountId() + MAGIC_STEAM_ID64_LONG;
            steamIds += "," + id64;
        }
        steamIds = steamIds.substring(1);
        URI uri = null;
        try {
            uri = getPlayerDetailUri(steamIds);

            HttpGet httpGet = new HttpGet(uri);
            String result = makeRequest(httpGet);
            //log("resultRequest: " + result);
            parseAndSavePlayersSummaries(result);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void parseAndSavePlayersSummaries(String result) {
        if (result.equals(NOTHING)) {
            return;
        }
        Map root = (Map<String, Object>) new Gson().fromJson(result, Map.class).get(RESPONSE);
        ArrayList<Map<String, Object>> players = (ArrayList<Map<String, Object>>) root.get(PLAYERS);
        for (Map<String, Object> player : players) {
            int id32 = calcSteamId32(Long.valueOf((String) player.get(STEAMID)));
            int profileState = nvlInt(player.get(PROFILE_STATE), -1);
            int timeCreated = nvlInt(player.get(TIME_CREATED), -1);
            int personaStateFlags = nvlInt(player.get(PERSONA_STATE_FLAGS), -1);
            int lastLogoff = nvlInt(player.get(LASTLOGOFF), -1);
            model.updatePlayer(id32,
                    anInt(player.get(COMMUNITYVISIBILITYSTATE)),
                    profileState,
                    (String) player.get(PERSONANAME),
                    lastLogoff,
                    (String) player.get(PROFILEURL),
                    (String) player.get(AVATAR),
                    (String) player.get(AVATARMEDIUM),
                    (String) player.get(AVATARFULL),
                    anInt(player.get(PERSONASTATE)),
                    (String) (player.get(PRIMARYCLANID)),
                    timeCreated,
                    personaStateFlags
            );
        }
    }


    private int calcSteamId32(long l) {
        return (int) (l - MAGIC_STEAM_ID64_LONG);
    }

    private URI getPlayerDetailUri(String steamIds) throws URISyntaxException {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("https")
                .setHost(HOST)
                .setPath(GET_PLAYERS_SUMMARIES)
                .setParameter(KEY, apiKey)
                .setParameter(STEAMIDS, steamIds);
        return builder.build();
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
