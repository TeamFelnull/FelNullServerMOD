package red.felnull.fnssu.music;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import red.felnull.fnssu.util.ServerUtils;

public class MusicInstance {
    private final RegistryKey<World> dimension;
    private final Vector3d pos;
    private final MusicData musicData;
    private final long startTime;
    private boolean running;
    private int lastNote = -1;
    private long lastRing;

    public MusicInstance(RegistryKey<World> dimension, Vector3d pos, MusicData musicData) {
        this.dimension = dimension;
        this.pos = pos;
        this.startTime = System.currentTimeMillis();
        this.musicData = musicData;
        this.running = true;
    }

    public void tick() {
        long time = System.currentTimeMillis() - startTime;
        if (musicData.allTime() < time) {
            running = false;
            return;
        }
        float pich = musicData.getPich(time);
        if (pich >= 0) {
            if (lastNote != musicData.getNote(time)) {
                getWorld().playSound(null, pos.x, pos.y, pos.z, musicData.getSoundEvent(time), SoundCategory.RECORDS, 3, pich);
                lastNote = musicData.getNote(time);
                lastRing = System.currentTimeMillis();
            } else {
                if (System.currentTimeMillis() - lastRing >= 0.5f) {
                    lastNote = -3;
                }
            }
        } else {
            lastNote = -1;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public World getWorld() {
        return ServerUtils.getMinecraftServer().getLevel(dimension);
    }

}
