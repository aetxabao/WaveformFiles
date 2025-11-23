package edu.masanz.da.prog.wf.synth;

/**
 * Envolvente ADSR (Attack, Decay, Sustain, Release)
 */
public class ADSR {
    private double attack, decay, sustain, release;
    private double noteOnTime = -1;
    private double noteOffTime = -1;
    private double releaseStartAmp = 0;

    public ADSR(double a, double d, double s, double r) {
        attack = a; decay = d; sustain = s; release = r;
    }

    public void noteOn(double t) {
        noteOnTime = t;
        noteOffTime = -1;
    }

    public void noteOff(double t) {
        noteOffTime = t;
        releaseStartAmp = amplitudeAt(t);
    }

    public double get(double t) {
        return amplitudeAt(t);
    }

    private double amplitudeAt(double t) {

        if (noteOnTime < 0) return 0;

        // Nota ON
        if (noteOffTime < 0) {
            double dt = t - noteOnTime;

            if (dt < attack)                       // Attack
                return dt / attack;

            if (dt < attack + decay)               // Decay
                return sustain + (1 - sustain) *
                        (1 - (dt - attack) / decay);

            return sustain;                        // Sustain
        }

        // Nota en Release:
        double dr = t - noteOffTime;
        if (dr >= release) return 0;

        return releaseStartAmp * (1 - dr / release);
    }
}


