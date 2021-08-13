package red.felnull.fnssu.oldmusic;

import cf.leduyquang753.nbsapi.Song;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import red.felnull.fnssu.FelNullServerSideUtil;
import red.felnull.fnssu.util.URLUtils;

import java.net.URL;
import java.util.*;

public class MusicManager {
    private static final MusicManager INSTANCE = new MusicManager();
    private final Map<String, MusicInstance> musics = new HashMap<>();
    private final List<Float> FRE_NOTE;
    private final Map<String, IMusicData> SOUND_CHACH = new HashMap<>();
    private final Map<String, String> URL_CACH = new HashMap<>();
    private long last = 0;

    public static MusicManager getInstance() {
        return INSTANCE;
    }

    public MusicManager() {
        FRE_NOTE = new ArrayList<>();
        FRE_NOTE.add(43.65f);
        FRE_NOTE.add(46.25f);
        FRE_NOTE.add(49.00f);
        FRE_NOTE.add(51.91f);
        FRE_NOTE.add(55.00f);
        FRE_NOTE.add(58.27f);
        FRE_NOTE.add(61.74f);
        FRE_NOTE.add(65.41f);
        FRE_NOTE.add(69.30f);
        FRE_NOTE.add(73.42f);
        FRE_NOTE.add(77.78f);
        FRE_NOTE.add(87.31f);
        FRE_NOTE.add(92.50f);
        FRE_NOTE.add(98.00f);
        FRE_NOTE.add(103.83f);
        FRE_NOTE.add(110.00f);
        FRE_NOTE.add(116.54f);
        FRE_NOTE.add(123.47f);
        FRE_NOTE.add(130.81f);
        FRE_NOTE.add(138.59f);
        FRE_NOTE.add(146.83f);
        FRE_NOTE.add(155.56f);
        FRE_NOTE.add(164.81f);
        FRE_NOTE.add(174.61f);
        FRE_NOTE.add(185.00f);
        FRE_NOTE.add(196.00f);
        FRE_NOTE.add(207.65f);
        FRE_NOTE.add(220.00f);
        FRE_NOTE.add(233.08f);
        FRE_NOTE.add(246.94f);
        FRE_NOTE.add(261.63f);
        FRE_NOTE.add(277.18f);
        FRE_NOTE.add(293.66f);
        FRE_NOTE.add(311.13f);
        FRE_NOTE.add(329.63f);
        FRE_NOTE.add(349.23f);
        FRE_NOTE.add(369.99f);
        FRE_NOTE.add(392.00f);
        FRE_NOTE.add(415.30f);
        FRE_NOTE.add(440.00f);
        FRE_NOTE.add(466.16f);
        FRE_NOTE.add(493.88f);
        FRE_NOTE.add(523.25f);
        FRE_NOTE.add(554.37f);
        FRE_NOTE.add(587.33f);
        FRE_NOTE.add(622.25f);
        FRE_NOTE.add(659.25f);
        FRE_NOTE.add(698.46f);
        FRE_NOTE.add(739.99f);
        FRE_NOTE.add(783.99f);
        FRE_NOTE.add(830.61f);
        FRE_NOTE.add(880.00f);
        FRE_NOTE.add(932.33f);
        FRE_NOTE.add(987.77f);
        FRE_NOTE.add(1046.50f);
        FRE_NOTE.add(1108.73f);
        FRE_NOTE.add(1174.66f);
        FRE_NOTE.add(1244.51f);
        FRE_NOTE.add(1318.51f);
        FRE_NOTE.add(1396.91f);
        FRE_NOTE.add(1479.98f);
        FRE_NOTE.add(1567.98f);
        FRE_NOTE.add(1661.22f);
        FRE_NOTE.add(1760.00f);
        FRE_NOTE.add(1864.66f);
        FRE_NOTE.add(2093.00f);
        FRE_NOTE.add(2217.46f);
        FRE_NOTE.add(2349.32f);
        FRE_NOTE.add(2489.02f);
        FRE_NOTE.add(2637.02f);
        FRE_NOTE.add(2793.83f);
    }

    public void start(String dname, RegistryKey<World> dim, Vector3d pos, String url, String name) throws Exception {

        if (URL_CACH.containsKey(url))
            url = URL_CACH.get(url);

        if (!name.isEmpty() && musics.containsKey(name))
            return;

        if (System.currentTimeMillis() - last >= 1000 * 60 * 10) {
            SOUND_CHACH.clear();
            last = System.currentTimeMillis();
        }

        IMusicData musicData;
        if (SOUND_CHACH.containsKey(url)) {
            musicData = SOUND_CHACH.get(url);
        } else {
            musicData = getMusicData(url);
            SOUND_CHACH.put(url, musicData);
        }

        musics.put(name.isEmpty() ? UUID.randomUUID().toString() : name, new MusicInstance(dim, pos, musicData));

        FelNullServerSideUtil.LOGGER.info("Music Play Start: " + dname);
    }

    public void tick() {
        List<String> stopps = new ArrayList<>();
        musics.entrySet().stream().filter(n -> !n.getValue().isRunning()).forEach(n -> stopps.add(n.getKey()));
        stopps.forEach(musics::remove);
        musics.values().forEach(n -> {
            try {
                n.tick();
            } catch (Throwable ignored) {
            }
        });
    }

    public void stopAll() {
        musics.clear();
    }

    private IMusicData getMusicData(String url) throws Exception {
        URL musicURL = new URL(url);
        try {
            List<MusicData.MusicEntry> musics = new ArrayList<>();
            JsonObject jo = URLUtils.getJsonResponse(musicURL);
            JsonArray ja = jo.getAsJsonArray("Beeps");
            long startTime = 0;
            for (JsonElement jsonElement : ja) {
                JsonArray array = jsonElement.getAsJsonArray();
                for (int i = 0; i < array.get(3).getAsInt(); i++) {
                    long duration = array.get(1).getAsLong();
                    MusicData.MusicEntry entry = new MusicData.MusicEntry(startTime, convertNotFr(array.get(0).getAsFloat()), duration);
                    musics.add(entry);
                    startTime += duration + Math.max(array.get(2).getAsLong(), 1);
                }
            }
            return new MusicData(musics, startTime);
        } catch (Exception ignored) {
        }

        try {
            Song song = new Song(URLUtils.getStream(musicURL));
            return new NBSMusicData(song);
        } catch (Exception ignored) {

        }


        throw new IllegalStateException("No Music Src");
    }

    private int convertNotFr(float francy) {
        float hose = MathHelper.clamp(francy, 43.65f, 2793.83f);
        float hoseiFr = 43.65f;
        for (int i = 0; i < FRE_NOTE.size(); i++) {
            float gr = FRE_NOTE.get(i);
            if (gr == hose) {
                hoseiFr = gr;
                break;
            } else if (gr > francy) {
                hoseiFr = FRE_NOTE.get(MathHelper.clamp(i - 1, 0, FRE_NOTE.size() - 1));
                break;
            }
        }

        return FRE_NOTE.indexOf(hoseiFr);
    }

    public void addMusicChach(String url, String name) {
        URL_CACH.put(name, url);
    }

    public Map<String, String> getUrlCach() {
        return URL_CACH;
    }
}
