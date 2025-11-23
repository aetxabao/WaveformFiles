package edu.masanz.da.prog.wf.synth;

/**
 * Voz del sintetizador
 * Una voz = oscilador + ADSR + (opcional) LFO.
 */
public class Voice {

    private Oscillator osc;
    private ADSR adsr;
    private LFO lfo;
    private double startTime;
    private boolean released = false;

    public Voice(Oscillator osc, ADSR adsr, LFO lfo, double t) {
        this.osc = osc;
        this.adsr = adsr;
        this.lfo = lfo;
        this.startTime = t;
        adsr.noteOn(t);
    }

    public void noteOff(double t) {
        adsr.noteOff(t);
        released = true;
    }

    public boolean isFinished(double t) {
        return released && adsr.get(t) <= 0.0001;
    }

    public double getSample(double t) {
        double amp = adsr.get(t);
        if (amp <= 0) return 0;

        double vibrato = (lfo != null ? lfo.value(t) : 0);
        double sample = osc.getSample(t, vibrato);

        return amp * sample;
    }
}


