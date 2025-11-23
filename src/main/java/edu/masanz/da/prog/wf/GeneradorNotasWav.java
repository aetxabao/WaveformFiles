package edu.masanz.da.prog.wf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GeneradorNotasWav {

    public static void main(String[] args) throws IOException {

        final int SAMPLE_RATE = 44100;     // Frecuencia de muestreo (44.1 kHz)
        final int DURATION = 16;           // Duración en segundos

        double[] fs = {261.63, 293.66, 329.63, 349.23, 392, 440, 493.88, 523.25}; // Do, Re, Mi, Fa, Sol, La, Si, Do

        int numSamples = SAMPLE_RATE * DURATION;
        byte[] data = new byte[numSamples * 2]; // 16-bit PCM → 2 bytes por muestra

        int idx = 0;

        for (double freq : fs) {
            for (int n = 0; n < SAMPLE_RATE * 2; n++) {
                double t = n / (double) SAMPLE_RATE;
                double sample = Math.sin(2 * Math.PI * freq * t);

                // Convertir a 16-bit PCM
                short val = (short)(sample * Short.MAX_VALUE);
                data[2*idx] = (byte)(val & 0xFF);
                data[2*idx+1] = (byte)((val >> 8) & 0xFF);

                idx++;
            }
        }

        // --- Escribir archivo WAV ---
        String outputFileName = "notas.wav";
        FileOutputStream fos = new FileOutputStream(outputFileName);
        writeWavHeader(fos, numSamples, SAMPLE_RATE);
        fos.write(data);
        fos.close();

        System.out.printf("Archivo %s generado correctamente.\n", outputFileName);
    }

    private static void writeWavHeader(OutputStream out, int numSamples, int sampleRate) throws IOException {
        int byteRate = sampleRate * 2; // mono, 16-bit
        int subchunk2Size = numSamples * 2;
        int chunkSize = 36 + subchunk2Size;

        // RIFF chunk descriptor
        out.write("RIFF".getBytes());
        out.write(intToLittleEndian(chunkSize));
        out.write("WAVE".getBytes());

        // fmt subchunk
        out.write("fmt ".getBytes());
        out.write(intToLittleEndian(16));   // Subchunk1Size = 16 para PCM
        out.write(shortToLittleEndian((short) 1)); // AudioFormat = 1 (PCM)
        out.write(shortToLittleEndian((short) 1)); // NumChannels = 1
        out.write(intToLittleEndian(sampleRate));  // SampleRate
        out.write(intToLittleEndian(byteRate));    // ByteRate
        out.write(shortToLittleEndian((short) 2)); // BlockAlign = 2 bytes
        out.write(shortToLittleEndian((short) 16));// BitsPerSample = 16

        // data subchunk
        out.write("data".getBytes());
        out.write(intToLittleEndian(subchunk2Size));
    }

    private static byte[] intToLittleEndian(int value) {
        return new byte[] {
                (byte)(value & 0xFF),
                (byte)((value >> 8) & 0xFF),
                (byte)((value >> 16) & 0xFF),
                (byte)((value >> 24) & 0xFF)
        };
    }

    private static byte[] shortToLittleEndian(short value) {
        return new byte[] {
                (byte)(value & 0xFF),
                (byte)((value >> 8) & 0xFF)
        };
    }
}
