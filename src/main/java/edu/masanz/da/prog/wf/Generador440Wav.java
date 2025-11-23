package edu.masanz.da.prog.wf;
import java.io.*;

public class Generador440Wav {

    public static void main(String[] args) throws IOException {

        final int SAMPLE_RATE = 44100;     // Frecuencia de muestreo (44.1 kHz)
        final int DURATION = 2;            // Duración en segundos
        final double FREQUENCY = 440.0;    // Frecuencia "La"

        int numSamples = SAMPLE_RATE * DURATION;
        byte[] data = new byte[numSamples * 2]; // 16-bit PCM → 2 bytes por muestra

        // --- Generar señal senoidal ---
        for (int i = 0; i < numSamples; i++) {
            // TODO 11: Generar la muestra i-ésima de la señal senoidal de 440 Hz
            // sample = sin(2π⋅f⋅t), siendo f = 440 Hz y t = i / SAMPLE_RATE
            double t = 1.0;                 // reemplazar valor correctamente
            double sample = 0.0;            // reemplazar valor correctamente, utiliza FREQUENCY y t

            // Convertir a 16-bit PCM
            short val = (short) (sample * Short.MAX_VALUE);
            data[2*i] = (byte) (val & 0xFF);         // byte bajo
            data[2*i + 1] = (byte) ((val >> 8) & 0xFF); // byte alto
        }

        // --- Escribir archivo WAV ---
        String outputFileName = "la440.wav";
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
