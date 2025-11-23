package edu.masanz.da.prog.wf;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NotasConSilencio {

    public static void main(String[] args) throws IOException {

        final int SAMPLE_RATE = 44100;
        final double NOTE_DURATION = 1.0;       // 1 segundo por nota
        final double SILENCE_DURATION = 0.5;    // 0.5 segundos de silencio entre notas

        double[] fs = {261.63, 293.66, 329.63, 349.23, 392, 440, 493.88, 523.25}; // Do, Re, Mi...

        int samplesPerNote = (int)(SAMPLE_RATE * NOTE_DURATION);
        int samplesSilence = (int)(SAMPLE_RATE * SILENCE_DURATION);

        int totalSamples = fs.length * (samplesPerNote + samplesSilence);

        byte[] data = new byte[totalSamples * 2];  // 16-bit PCM → 2 bytes por sample
        int idx = 0;

        for (double freq : fs) {

            // --------------------------
            // 1. Generar la nota
            // --------------------------
            for (int n = 0; n < samplesPerNote; n++) {
                double t = n / (double) SAMPLE_RATE;
                double sample = Math.sin(2 * Math.PI * freq * t);

                short val = (short)(sample * Short.MAX_VALUE);

                data[2*idx]     = (byte)(val & 0xFF);
                data[2*idx + 1] = (byte)((val >> 8) & 0xFF);
                idx++;
            }

            // --------------------------
            // 2. Añadir silencio
            // --------------------------
            for (int n = 0; n < samplesSilence; n++) {
                data[2*idx] = 0;
                data[2*idx + 1] = 0;
                idx++;
            }
        }

        // Guardar WAV
        String outputFileName = "notas2.wav";
        FileOutputStream fos = new FileOutputStream(outputFileName);
        writeWavHeader(fos, totalSamples, SAMPLE_RATE);
        fos.write(data);
        fos.close();

        System.out.println("Archivo generado: " + outputFileName);
    }

    // Header WAV estándar PCM 16-bit mono
    private static void writeWavHeader(OutputStream out, int numSamples, int sampleRate) throws IOException {
        int byteRate = sampleRate * 2;
        int subchunk2Size = numSamples * 2;
        int chunkSize = 36 + subchunk2Size;

        out.write("RIFF".getBytes());
        out.write(intLE(chunkSize));
        out.write("WAVE".getBytes());

        out.write("fmt ".getBytes());
        out.write(intLE(16));             // PCM
        out.write(shortLE((short)1));     // audio format = PCM
        out.write(shortLE((short)1));     // mono
        out.write(intLE(sampleRate));
        out.write(intLE(byteRate));
        out.write(shortLE((short)2));     // block align
        out.write(shortLE((short)16));    // bits per sample

        out.write("data".getBytes());
        out.write(intLE(subchunk2Size));
    }

    private static byte[] intLE(int v) {
        return new byte[]{(byte)v, (byte)(v>>8), (byte)(v>>16), (byte)(v>>24)};
    }

    private static byte[] shortLE(short v) {
        return new byte[]{(byte)v, (byte)(v>>8)};
    }
}
