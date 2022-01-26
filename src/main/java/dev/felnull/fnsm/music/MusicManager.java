package dev.felnull.fnsm.music;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import red.felnull.ikenainbs.NBS;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class MusicManager {
    private static final MusicManager INSTANCE = new MusicManager();
    private final Map<UUID, MusicPlayer> musicPlayers = new HashMap<>();

    public static MusicManager getInstance() {
        return INSTANCE;
    }

    public UUID start(NBS nbs, Supplier<Vector3d> playPos, Supplier<ResourceLocation> playDim) {
        UUID id = UUID.randomUUID();
        MusicPlayer mp = new MusicPlayer(id, nbs, playPos, playDim);
        synchronized (musicPlayers) {
            musicPlayers.put(id, mp);
        }
        mp.start();
        return id;
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
}
