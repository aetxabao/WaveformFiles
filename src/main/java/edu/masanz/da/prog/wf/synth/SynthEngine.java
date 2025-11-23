package edu.masanz.da.prog.wf.synth;

import java.util.*;

/**
 * Motor del sintetizador
 * Genera el audio final.
 */public class SynthEngine {

    private final int sr;
    private List<Voice> voices = new ArrayList<>();
    private double currentTime = 0;

    public SynthEngine(int sampleRate) {
        this.sr = sampleRate;
    }

    public void noteOn(double freq) {
        Oscillator osc = new Oscillator(Waveform.SAW, freq);
        ADSR adsr = new ADSR(0.01, 0.20, 0.6, 0.25);
        LFO lfo = new LFO(5, 0.003);
        voices.add(new Voice(osc, adsr, lfo, currentTime));
    }

    public void noteOff() {
        for (Voice v : voices)
            v.noteOff(currentTime);
    }

    public double nextSample() {

        double mix = 0;

        Iterator<Voice> it = voices.iterator();
        while (it.hasNext()) {
            Voice v = it.next();
            mix += v.getSample(currentTime);
            if (v.isFinished(currentTime))
                it.remove();
        }

        currentTime += 1.0 / sr;

        return Math.tanh(mix * 0.7);
    }
}
