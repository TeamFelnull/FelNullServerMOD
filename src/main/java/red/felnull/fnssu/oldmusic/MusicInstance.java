package red.felnull.fnssu.oldmusic;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import red.felnull.fnssu.util.ServerUtils;

import java.util.List;

public class MusicInstance {
    private final RegistryKey<World> dimension;
    private final Vector3d pos;
    private final IMusicData musicData;
    private final long startTime;
    private boolean running;
    private int lastNote = -1;
    public int currentTick = 0;
    private long lastRing;

    public MusicInstance(RegistryKey<World> dimension, Vector3d pos, IMusicData musicData) {
        this.dimension = dimension;
        this.pos = pos;
        this.startTime = System.currentTimeMillis();
        this.musicData = musicData;
        this.running = true;
    }

    public void tick() {
        currentTick++;
        long time = System.currentTimeMillis() - startTime;


        if (musicData instanceof MusicData && musicData.getAllTime() < time) {
            running = false;
            return;
        } else if (musicData instanceof NBSMusicData && musicData.getAllTime() < currentTick) {
            running = false;
            return;
        }

        if (musicData instanceof MusicData) {
            if (musicData.getMusicSource(time).size() != 0) {
                IMusicData.MusicSource source = musicData.getMusicSource(time).get(0);
                float pich = source.pitch;
                if (pich >= 0) {
                    if (lastNote != (int) (source.event.getLocation().toString().hashCode() + pich * 100)) {
                        getWorld().playSound(null, pos.x, pos.y, pos.z, source.event, SoundCategory.RECORDS, 3, pich);
                        lastNote = (int) (source.event.getLocation().toString().hashCode() + pich * 100);
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
        } else if (musicData instanceof NBSMusicData) {
            if (currentTick % NBSMusicData.speed == 0) {
                List<IMusicData.MusicSource> musicSources = musicData.getMusicSource(currentTick / 2);
                if (musicSources.size() > 0) for (IMusicData.MusicSource n : musicSources) {
                    getWorld().playSound(null, pos.x, pos.y, pos.z, n.event, SoundCategory.RECORDS, NBSMusicData.speed, n.pitch);
                }
            }
        }

    }


    public boolean isRunning() {
        return running;
    }

    public World getWorld() {
        return ServerUtils.getMinecraftServer().getLevel(dimension);
    }

}
