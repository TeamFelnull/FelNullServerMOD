package red.felnull.fnssu.music;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class MusicData implements IMusicData {
    private final List<MusicEntry> musics;
    private final long allTime;

    public MusicData(List<MusicEntry> musics, long allTime) {
        this.musics = musics;
        this.allTime = allTime;
    }

    public long allTime() {
        return allTime;
    }

    public MusicEntry getEntry(long time) {
        MusicEntry entry = null;
        for (int i = 0; i < musics.size(); i++) {
            MusicEntry ch = musics.get(i);
            if (ch.startPos == time) {
                entry = ch;
                break;
            } else if (ch.startPos > time) {
                entry = musics.get(i - 1);
                break;
            }
        }
        return entry;
    }

    public int getNote(long time) {
        if (getPich(time) < 0)
            return -1;

        return getEntry(time).note;
    }

    public float getPich(long time) {

        if (getEntry(time) != null && time - getEntry(time).startPos <= getEntry(time).beepDuration) {
            int i = getEntry(time).note % 24;
            return (float) Math.pow(2.0D, (double) (i - 12) / 12.0D);
        }
        return -1;
    }

    public SoundEvent getSoundEvent(long time) {
        if (getEntry(time) != null) {
            int note = getEntry(time).note;
            if (0 < note && 24 >= note) {
                return SoundEvents.NOTE_BLOCK_BASS;
            } else if (24 < note && 48 >= note) {
                return SoundEvents.NOTE_BLOCK_HARP;
            } else if (48 < note && 70 >= note) {
                return SoundEvents.NOTE_BLOCK_BELL;
            }
        }
        return SoundEvents.NOTE_BLOCK_BASS;
    }

    @Override
    public long getAllTime() {
        return allTime();
    }

    @Override
    public List<MusicSource> getMusicSource(long time) {
        List<MusicSource> ss = new ArrayList<>();
        ss.add(new MusicSource(getSoundEvent(time), 1, getPich(time)));
        return ss;
    }

    public static class MusicEntry {
        private final long startPos;
        private final int note;
        private final long beepDuration;

        public MusicEntry(long startPos, int note, long beepDuration) {
            this.startPos = startPos;
            this.note = note;
            this.beepDuration = beepDuration;
        }
    }
}
