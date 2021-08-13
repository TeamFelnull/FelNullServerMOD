package red.felnull.fnssu.oldmusic;

import cf.leduyquang753.nbsapi.Instrument;
import cf.leduyquang753.nbsapi.Layer;
import cf.leduyquang753.nbsapi.Note;
import cf.leduyquang753.nbsapi.Song;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class NBSMusicData implements IMusicData {
    public static int speed = 3;
    private final Song song;
    private final long allTime;

    public NBSMusicData(Song song) {
        this.song = song;
        this.allTime = crateAllTime();
    }

    @Override
    public List<MusicSource> getMusicSource(long time) {
        List<MusicSource> ss = new ArrayList<>();
        for (Layer l : song.getSongBoard()) {
            Note n = l.getNoteList().get((int) time);
            if (n != null)
                ss.add(new MusicSource(getByInstrument(n.getInstrument()), l.getVolume() / 100f, (float) Math.pow(2.0D, (double) (n.getPitch() - 45) / 12.0D)));
        }
        return ss;
    }

    @Override
    public long getAllTime() {
        return allTime;
    }

    private long crateAllTime() {
        int res = -1;
        for (Layer l : song.getSongBoard()) {
            int max = -1;
            for (int i : l.getNoteList().keySet()) max = Math.max(max, i);
            res = Math.max(res, max);
        }
        return (long) res * speed + 120;
    }

    private SoundEvent getByInstrument(Instrument instrument) {
        switch (instrument) {
            case HARP:
                return SoundEvents.NOTE_BLOCK_HARP;
            case BASS:
                return SoundEvents.NOTE_BLOCK_BASS;
            case DRUM:
                return SoundEvents.NOTE_BLOCK_BASEDRUM;
            case SNARE:
                return SoundEvents.NOTE_BLOCK_SNARE;
            case CLICK:
                return SoundEvents.NOTE_BLOCK_HAT;
            case GUITAR:
                return SoundEvents.NOTE_BLOCK_GUITAR;
            case FLUTE:
                return SoundEvents.NOTE_BLOCK_FLUTE;
            case BELL:
                return SoundEvents.NOTE_BLOCK_BELL;
            case CHIME:
                return SoundEvents.NOTE_BLOCK_CHIME;
            case XYLOPHONE:
                return SoundEvents.NOTE_BLOCK_XYLOPHONE;
        }
        return SoundEvents.NOTE_BLOCK_HARP;
    }
}
