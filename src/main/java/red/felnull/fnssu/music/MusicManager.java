package red.felnull.fnssu.music;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import red.felnull.fnssu.FelNullServerSideUtil;
import red.felnull.fnssu.util.PlayerUtils;
import red.felnull.fnssu.util.URLUtils;
import red.felnull.ikenainbs.NBS;

import java.net.URL;
import java.util.*;

public class MusicManager {
    private static final MusicManager INSTANCE = new MusicManager();
    private final Map<String, MusicPlayer> musics = new HashMap<>();
    private final Map<String, MusicSource> SOUND_CHACH = new HashMap<>();
    private final Map<String, String> URL_CACH = new HashMap<>();
    private long last = 0;

    public static MusicManager getInstance() {
        return INSTANCE;
    }

    public void stop(String name) {
        getPlayer(name).stoped();
    }

    public MusicPlayer getPlayer(String name) {
        return musics.get(name);
    }

    public void start(String dname, RegistryKey<World> dim, Vector3d pos, String url, String name, PlayerEntity playerd) throws Exception {

        if (name.isEmpty())
            name = UUID.randomUUID().toString();

        if (URL_CACH.containsKey(url))
            url = URL_CACH.get(url);

        if (!name.isEmpty() && musics.containsKey(name))
            return;

        if (System.currentTimeMillis() - last >= 1000 * 60 * 10) {
            SOUND_CHACH.clear();
            last = System.currentTimeMillis();
        }

        MusicSource musicData;
        if (SOUND_CHACH.containsKey(url)) {
            musicData = SOUND_CHACH.get(url);
        } else {
            musicData = getMusicSource(url);
            SOUND_CHACH.put(url, musicData);
        }
        UUID uuid = playerd == null ? null : playerd.getGameProfile().getId();
        MusicPlayer player = playerd == null ? new MusicPlayer(musicData, () -> dim, () -> pos) : new MusicPlayer(musicData, () -> {
            if (PlayerUtils.isOnline(uuid))
                return PlayerUtils.getPlayer(uuid).getLevel().dimension();
            return dim;
        }, () -> {
            if (PlayerUtils.isOnline(uuid))
                return PlayerUtils.getPlayer(uuid).position();
            return pos;
        });
        player.setName(name);
        player.start();
        musics.put(name, player);

        FelNullServerSideUtil.LOGGER.info("Music Play Start: " + dname);
    }

    public void tick() {
        List<String> stopps = new ArrayList<>();
        musics.entrySet().stream().filter(n -> !n.getValue().isAlive()).forEach(n -> stopps.add(n.getKey()));
        stopps.forEach(musics::remove);
    }

    public Set<String> getPlayingNames() {
        return musics.keySet();
    }

    public void stopAll() {
        musics.values().forEach(MusicPlayer::stoped);
        musics.clear();
    }

    private MusicSource getMusicSource(String url) throws Exception {
        URL musicURL = new URL(url);
        return new MusicSource(new NBS(URLUtils.getStream(musicURL)));
    }

    private int convertNotFr(float francy) {
        return 0;
    }

    public void addMusicChach(String url, String name) {
        URL_CACH.put(name, url);
    }

    public Map<String, String> getUrlCach() {
        return URL_CACH;
    }
}
