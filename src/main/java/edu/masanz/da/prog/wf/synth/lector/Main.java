package edu.masanz.da.prog.wf.synth.lector;

import edu.masanz.da.prog.wf.synth.SynthEngine;
import edu.masanz.da.prog.wf.synth.WavWriter;

import java.nio.file.*;
import java.util.*;
import javax.sound.sampled.*;

public class Main {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.printf("Introduce el nombre del archivo ABC (ej. def. 'retrogame.abc'): ");
        String txtFileName = scanner.nextLine().trim();
        if (txtFileName.isEmpty()) {
            txtFileName = "retrogame.abc";
        }
        String wavFileName = txtFileName.replace(".abc", ".wav");

        // Leer contenido del fichero abc.txt
        String abc = Files.readString(Paths.get(txtFileName));

        System.out.println("Archivo ABC cargado correctamente:");
        System.out.println("----------------------------------");
        System.out.println(abc);
        System.out.println("----------------------------------");

        ABCParser parser = new ABCParser();
        List<NoteEvent> song = parser.parse(abc);

        int SR = 44100;
        SynthEngine synth = new SynthEngine(SR);
        List<Double> buffer = new ArrayList<>();

        for (NoteEvent ne : song) {

            if (!ne.isRest)
                synth.noteOn(ne.freq);

            int samples = (int)(ne.duration * SR);

            for (int i = 0; i < samples; i++)
                buffer.add(synth.nextSample());

            synth.noteOff();
        }

        // Convertir a array
        double[] out = new double[buffer.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = buffer.get(i);
        }

        WavWriter.save(wavFileName, out, SR);

        System.out.println("✔ Generado " + wavFileName);

        // Reproducir directamente ---
        playAudio(out, SR);

    }

    // Función para reproducir audio directamente desde un array de doubles
    private static void playAudio(double[] audio, int sampleRate) throws Exception {

        // Convertir de double [-1,1] a byte PCM 16-bit little endian
        byte[] byteBuf = new byte[audio.length * 2];
        for (int i = 0; i < audio.length; i++) {
            short val = (short) (Math.max(-1.0, Math.min(1.0, audio[i])) * Short.MAX_VALUE);
            byteBuf[2 * i] = (byte) (val & 0xFF);
            byteBuf[2 * i + 1] = (byte) ((val >> 8) & 0xFF);
        }

        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
            line.open(format);
            line.start();
            line.write(byteBuf, 0, byteBuf.length);
            line.drain();
        }

        System.out.println("✔ Reproducción finalizada");
    }

}

