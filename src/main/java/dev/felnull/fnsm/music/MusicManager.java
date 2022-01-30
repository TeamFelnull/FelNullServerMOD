package dev.felnull.fnsm.music;

import dev.felnull.fnjl.util.FNURLUtil;
import dev.felnull.fnnbs.FelNullNBSLibrary;
import dev.felnull.fnnbs.NBS;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MusicManager {
    private static final MusicManager INSTANCE = new MusicManager();
    private final Map<UUID, MusicPlayer> musicPlayers = new HashMap<>();
    private final Map<String, String> musicURLS = new HashMap<>();
    private final Map<String, NBS> musicCashes = new HashMap<>();
    private final Map<String, Long> musicLastCashes = new HashMap<>();
    private final Map<String, UUID> ids = new HashMap<>();

    public static MusicManager getInstance() {
        return INSTANCE;
    }

    public void start(String name, String id, Supplier<Vector3d> playPos, Supplier<ResourceLocation> playDim, int loop) {
        UUID uuid = ids.get(id);
        if (uuid == null) {
            uuid = UUID.randomUUID();
            ids.put(id, uuid);
        }
        if (musicPlayers.containsKey(uuid))
            throw new IllegalStateException("すでに再生中の曲IDです");

        if (musicURLS.containsKey(name))
            name = musicURLS.get(name);

        NBS nbs = musicCashes.get(name);
        long last = musicLastCashes.getOrDefault(name, 0L);
        if (!musicCashes.containsKey(name) || System.currentTimeMillis() - last > 1000 * 60) {
            musicLastCashes.put(name, System.currentTimeMillis());
            try {
                nbs = FelNullNBSLibrary.loadNBS(FNURLUtil.getStream(new URL(name)));
                musicCashes.put(name, nbs);
            } catch (Exception ex) {
                musicCashes.put(name, null);
                throw new IllegalStateException("曲の取得に失敗しました: " + ex.getMessage());
            }
        }
        if (nbs == null) {
            throw new IllegalStateException("読み込み失敗したため、しばらくお待ちください");
        } else {
            start(nbs, playPos, playDim, uuid, loop != 0, Math.min(10, loop));
        }
    }

    public void start(NBS nbs, Supplier<Vector3d> playPos, Supplier<ResourceLocation> playDim, UUID id, boolean loop, int loopCount) {
        MusicPlayer mp = new MusicPlayer(id, nbs, playPos, playDim, loopCount);
        synchronized (musicPlayers) {
            musicPlayers.put(id, mp);
        }
        mp.start(loop);
    }

    public void stop(UUID id) {
        MusicPlayer mp = musicPlayers.get(id);
        if (mp != null) {
            mp.stop();
            synchronized (musicPlayers) {
                musicPlayers.remove(id);
            }
        }
    }

    public void tick() {
        synchronized (ids) {
            synchronized (musicPlayers) {
                List<UUID> rids = musicPlayers.entrySet().stream().filter(n -> !n.getValue().isPlaying()).map(Map.Entry::getKey).collect(Collectors.toList());
                for (UUID rid : rids) {
                    musicPlayers.remove(rid);
                    List<String> irks = ids.entrySet().stream().filter(n -> n.getValue().equals(rid)).map(Map.Entry::getKey).collect(Collectors.toList());
                    for (String irk : irks) {
                        ids.remove(irk);
                    }
                }
            }
        }
    }

    public void musicURLRegister(String name, String url) {
        musicURLS.put(name, url);
    }

    public Map<String, String> getMusicURLs() {
        return musicURLS;
    }
}
