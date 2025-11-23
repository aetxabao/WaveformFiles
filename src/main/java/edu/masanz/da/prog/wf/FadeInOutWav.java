package edu.masanz.da.prog.wf;

import javax.sound.sampled.*;
import java.io.*;

public class FadeInOutWav {

    public static void main(String[] args) throws Exception {

        File input = new File("notas.wav");
        File output = new File("notas_fade.wav");

        // ---- 1. Leer archivo WAV ----
        AudioInputStream ais = AudioSystem.getAudioInputStream(input);
        AudioFormat format = ais.getFormat();

        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED ||
                format.getSampleSizeInBits() != 16 ||
                format.getChannels() != 1) {
            throw new UnsupportedAudioFileException(
                    "Solo se admite WAV PCM 16-bit mono.");
        }

        byte[] buffer = ais.readAllBytes();
        ais.close();

        int numSamples = buffer.length / 2;
        short[] samples = new short[numSamples];

        // Convertir bytes → samples
        for (int i = 0; i < numSamples; i++) {
            int low  = buffer[2*i] & 0xFF;
            int high = buffer[2*i+1] << 8;
            samples[i] = (short)(low | high);
        }

        // ---- 2. Aplicar fade in / fade out ----

        float fadeTimeSec = 8.0f;  // duración del fade-in y del fade-out
        // 8 s * 44100 Hz = 352800 samples de fade
        int fadeSamples = (int)(fadeTimeSec * format.getSampleRate());

        // No pasarse del tamaño
        fadeSamples = Math.min(fadeSamples, numSamples / 2);

//        for (int i = 0; i < numSamples; i++) {
//            float gain = 1.0f;
//
//            // Fade-in
//            if (i < fadeSamples) {
//                gain = (float)i / fadeSamples;
//            }
//
//            // Fade-out
//            if (i > numSamples - fadeSamples) {
//                int pos = numSamples - i;
//                float fadeOutGain = (float)pos / fadeSamples;
//                gain = Math.min(gain, fadeOutGain);
//            }
//
//            samples[i] = (short)(samples[i] * gain);
//        }

        // http://www.labbookpages.co.uk/audio/wavGenFunc.html
        for (int s = 0 ; s < fadeSamples ; s++) {
            // Calculate weight based on Hann 'raised cosine' window
            double weight = 0.5 * (1 - Math.cos(Math.PI * s / (fadeSamples - 1)));

            // Apply weighting
            samples[s] *= weight;                      // Fade In
            samples[numSamples - (s+1)] *= weight;       // Fade Out
        }

        // ---- 3. Convertir samples → bytes ----
        byte[] outBytes = new byte[samples.length * 2];

        for (int i = 0; i < samples.length; i++) {
            outBytes[2*i]     = (byte)(samples[i] & 0xFF);
            outBytes[2*i + 1] = (byte)((samples[i] >> 8) & 0xFF);
        }

        // ---- 4. Guardar WAV ----
        ByteArrayInputStream bais = new ByteArrayInputStream(outBytes);
        AudioInputStream outAis =
                new AudioInputStream(bais, format, samples.length);

        AudioSystem.write(outAis, AudioFileFormat.Type.WAVE, output);

        System.out.println("Archivo generado con fade-in/out: notas_fade.wav");
    }
}

