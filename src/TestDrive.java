import com.krld.steamapi.SQLiteModel;
import com.krld.steamapi.Model;
import com.krld.steamapi.SteamApiWorker;
import com.krld.steamapi.SteamApiWorkerInterface;

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
    private static Properties prop;

    public static void main(String[] args) {
        testGrabber();
    }

    private static void testGrabber() {
        loadProperties();
        SteamApiWorkerInterface worker = new SteamApiWorker();
        Model model = new SQLiteModel();
        worker.setModel(model);
        worker.setApiKey(prop.getProperty(KEY));
        worker.setDomainName(prop.getProperty(DOMAIN));
        worker.setMainSteamId(Integer.valueOf(prop.getProperty(STEAMID_32)));
        worker.saveMatches();
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
