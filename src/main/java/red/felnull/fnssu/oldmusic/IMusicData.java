package red.felnull.fnssu.oldmusic;

import net.minecraft.util.SoundEvent;

import java.util.List;

public interface IMusicData {
    List<MusicSource> getMusicSource(long time);

    long getAllTime();

    public static class MusicSource {
        public final SoundEvent event;
        public final float volume;
        public final float pitch;

        public MusicSource(SoundEvent event, float volume, float pitch) {
            this.event = event;
            this.volume = volume;
            this.pitch = pitch;
        }
    }
}
