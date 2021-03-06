import com.krld.steamapi.model.SQLiteModel;
import com.krld.steamapi.model.Model;
import com.krld.steamapi.SteamApiWorker;
import com.krld.steamapi.SteamApiWorkerJson;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

/**
 * Created by Andrey on 8/31/2014.
 */
public class TestDrive {
    private static final String PROP_NAME = "config.prop";
    public static final String KEY = "KEY";
    public static final String DOMAIN = "DOMAIN";
    public static final String STEAMID_32 = "STEAMID32";
    public static final String DB_PATH = "dota2.db";
    private static Properties prop;

    public static void main(String[] args) {

        testUpdatePlayersDetails();
        testGrabberListIds();
        testGrabber();

        testGrabberMatchDetails();
    }

    private static void testUpdatePlayersDetails() {
        SteamApiWorker worker = getSteamApiWorker();
        worker.updatePlayersInfo();
    }

    private static void testGrabberMatchDetails() {
        SteamApiWorker worker = getSteamApiWorker();
        worker.updateAllMatchesDetails();
    }

    private static void testGrabberListIds() {
        int[] ids = new int[]{194097398, 68617303, 86716093, 88460580};
        SteamApiWorker worker = getSteamApiWorker();
        for (int id : ids) {
            worker.saveAllMatchesByHero(id);
        }
        worker.updatePlayersInfo();
    }

    private static void testGrabber() {
        SteamApiWorker worker = getSteamApiWorker();

        worker.saveAllHeroes();
        worker.saveAllMatchesByHero();
        worker.updatePlayersInfo();

    }

    private static SteamApiWorker getSteamApiWorker() {
        loadProperties();
        SteamApiWorker worker = new SteamApiWorkerJson();
        Model model = new SQLiteModel(DB_PATH);
        worker.setModel(model);
        worker.setApiKey(prop.getProperty(KEY));
        worker.setDomainName(prop.getProperty(DOMAIN));
        worker.setMainSteamId(Integer.valueOf(prop.getProperty(STEAMID_32)));
        return worker;
    }

    private static void loadProperties() {
        log("LOAD PROPERTIES");
        if (!new File(PROP_NAME).exists()) {
            log(PROP_NAME + " not existed!");
            return;
        }
        prop = new Properties();
        try {
            prop.load(new FileReader(PROP_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void log(String s) {
        System.out.println("*TestDrive: " + s);
    }

}
