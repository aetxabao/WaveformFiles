package edu.masanz.da.prog.wf.synth;

import java.io.*;

/**
 * Clase para guardar archivos WAV.
 */
public class WavWriter {

    public static void save(String filename, double[] samples, int sampleRate) throws Exception {
        byte[] data = new byte[samples.length * 2];

        for (int i = 0; i < samples.length; i++) {
            short val = (short)(samples[i] * Short.MAX_VALUE);
            data[2*i] = (byte)(val & 0xFF);
            data[2*i+1] = (byte)((val >> 8) & 0xFF);
        }

        FileOutputStream fos = new FileOutputStream(filename);
        writeWavHeader(fos, samples.length, sampleRate);
        fos.write(data);
        fos.close();
    }

    private static void writeWavHeader(OutputStream out, int numSamples, int sampleRate) throws IOException {
        int byteRate = sampleRate * 2;
        int subchunk2Size = numSamples * 2;
        int chunkSize = 36 + subchunk2Size;

        out.write("RIFF".getBytes());
        out.write(intLE(chunkSize));
        out.write("WAVE".getBytes());

        out.write("fmt ".getBytes());
        out.write(intLE(16));
        out.write(shortLE((short)1));
        out.write(shortLE((short)1));
        out.write(intLE(sampleRate));
        out.write(intLE(byteRate));
        out.write(shortLE((short)2));
        out.write(shortLE((short)16));

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

