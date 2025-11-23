package edu.masanz.da.prog.wf.synth;

/**
 * Genera una muestra según la forma de onda.
 */
public class Oscillator {

    private Waveform type;
    private double baseFreq;

    public Oscillator(Waveform type, double freq) {
        this.type = type;
        this.baseFreq = freq;
    }

    public double getSample(double t, double vibrato) {
        double f = baseFreq * (1 + vibrato);  // vibrato aplicado correctamente
        double x = 2 * Math.PI * f * t;

        switch (type) {
            case SINE:     return Math.sin(x);
            case SQUARE:   return Math.signum(Math.sin(x));
            case SAW:      return 2 * (f*t - Math.floor(0.5 + f*t));
            case TRIANGLE: return 2 * Math.abs(2 * (f*t - Math.floor(f*t + 0.5))) - 1;
            default:       return 0;
        }
    }
}

