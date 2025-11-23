package edu.masanz.da.prog.wf.synth;

import java.util.ArrayList;
import java.util.List;

/**
 * Este código genera un archivo WAV reproducido por tu sintetizador:
 * - Múltiples voces
 * - ADSR realista
 * - Vibrato
 * - Timbre configurable
 * - Acordes si llamas varias veces a noteOn al mismo tiempo
 */public class Main {

    public static void main(String[] args) throws Exception {

        int SR = 44100;
        SynthEngine synth = new SynthEngine(SR);

        double[] melody = { 261.63, 293.66, 329.63, 392.00, 440.00 };

        int samplesPerNote = (int)(0.8 * SR);
        int samplesSilence  = (int)(0.2 * SR);

        List<Double> buffer = new ArrayList<>();

        for (double freq : melody) {

            // Note ON
            synth.noteOn(freq);
            for (int i = 0; i < samplesPerNote; i++)
                buffer.add(synth.nextSample());

            // Note OFF
            synth.noteOff();
            for (int i = 0; i < samplesSilence; i++)
                buffer.add(synth.nextSample());
        }

        // Convert to array
        // TODO 81: Convierte la lista de valores buffer a un array out de double[]
        // Para cada elemento de buffer, asignalo a out en la misma posición
        double[] out = null;




        String outputFileName = "synth.wav";

        WavWriter.save(outputFileName, out, SR);

        System.out.println("✔ Generado " + outputFileName);
    }
}
