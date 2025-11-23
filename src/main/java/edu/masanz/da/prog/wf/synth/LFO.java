package edu.masanz.da.prog.wf.synth;

/**
 * Low-frequency oscillator para vibrato/tremolo.
 */
public class LFO {
    private double freq;
    private double depth;

    public LFO(double freq, double depth) {
        this.freq = freq;
        this.depth = depth;
    }

    public double value(double t) {
        return depth * Math.sin(2 * Math.PI * freq * t);
    }
}


