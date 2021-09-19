package dev.felnull.fnssu.music;

import red.felnull.ikenainbs.Layer;
import red.felnull.ikenainbs.NBS;

public class MusicSource {
    private final NBS nbs;
    private final int tickCont;

    public MusicSource(NBS nbs) {
        this.nbs = nbs;
        int tick = 0;
        for (Layer value : nbs.getLayers().values()) {
            for (Integer integer : value.getNoteData().keySet()) {
                tick = Math.max(tick, integer);
            }
        }
        this.tickCont = tick;
    }

    public int getTickCont() {
        return tickCont;
    }

    public NBS getNBS() {
        return nbs;
    }
}
