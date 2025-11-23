package edu.masanz.da.prog.wf.synth.lector;

public class NoteEvent {
    public double freq;
    public double duration;
    public boolean isRest;

    public NoteEvent(double freq, double dur, boolean rest) {
        this.freq = freq;
        this.duration = dur;
        this.isRest = rest;
    }
}


