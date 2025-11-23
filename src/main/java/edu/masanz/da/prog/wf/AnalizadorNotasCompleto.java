package edu.masanz.da.prog.wf;
import javax.sound.sampled.*;
import java.io.*;

public class AnalizadorNotasCompleto {

    public static void main(String[] args) throws Exception {

        String inputFileName = "notas.wav";

        File input = new File(inputFileName);

        // ---- 1. Leer WAV ----
        AudioInputStream ais = AudioSystem.getAudioInputStream(input);
        AudioFormat format = ais.getFormat();

        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED ||
                format.getSampleSizeInBits() != 16 ||
                format.getChannels() != 1) {
            throw new UnsupportedAudioFileException("Solo WAV PCM 16-bit mono");
        }

        byte[] raw = ais.readAllBytes();
        ais.close();

        int numSamples = raw.length / 2;
        double[] samples = new double[numSamples];

        // Convertir bytes → -1..1
        for (int i = 0; i < numSamples; i++) {
            int low = raw[2*i] & 0xFF;
            int high = raw[2*i+1] << 8;
            short s = (short)(low | high);
            samples[i] = s / 32768.0;
        }

        // ---- 2. Parámetros del pitch tracking ----
        int windowSize = 32768;                      // tamaño FFT
        int hopSize = windowSize / 2;               // solapamiento 50%
        double sampleRate = format.getSampleRate();

        double[] window = hanning(windowSize);

        // ---- 3. Recorrer audio por frames ----
        double freqAnt = 0.0;
        for (int start = 0; start + windowSize < samples.length; start += hopSize) {
            double[] real = new double[windowSize];
            double[] imag = new double[windowSize];

            // Aplicar ventana
            for (int i = 0; i < windowSize; i++) {
                real[i] = samples[start + i] * window[i];
                imag[i] = 0.0;
            }

            // FFT
            fft(real, imag);

            // Magnitudes
            double maxMag = 0;
            int maxBin = 0;
            for (int i = 1; i < windowSize/2; i++) {
                double mag = Math.sqrt(real[i]*real[i] + imag[i]*imag[i]);
                if (mag > maxMag) {
                    maxMag = mag;
                    maxBin = i;
                }
            }

            // Frecuencia dominante
            double freq = maxBin * sampleRate / windowSize;

            // Convertir frecuencia → nota
            String nota = freqToNote(freq);

            // Tiempo en segundos donde ocurre este frame
            double timeSec = (double)start / sampleRate;

            if (freqAnt != freq) {
                freqAnt = freq;
                System.out.printf("t = %6.03f s  --> %7.2f Hz  -->  %s\n", timeSec, freq, nota);
            }
        }
    }

    // --------------------- Ventana Hanning ---------------------
    public static double[] hanning(int n) {
        double[] w = new double[n];
        for (int i = 0; i < n; i++) {
            w[i] = 0.5 - 0.5 * Math.cos(2 * Math.PI * i / (n - 1));
        }
        return w;
    }

    // --------------------- Frecuencia → Nota ---------------------
    public static String freqToNote(double freq) {
        if (freq < 1) return "—";

        String[] notas = {"Do", "Do#", "Re", "Re#", "Mi", "Fa",
                "Fa#", "Sol", "Sol#", "La", "La#", "Si"};

        // midi: A4=440 Hz → midi 69
        double midi = 69 + 12 * Math.log(freq / 440.0) / Math.log(2);
        int midiInt = (int)Math.round(midi);

        int nota = midiInt % 12;
        int octava = (midiInt / 12) - 1;
        double cents = (midi - midiInt) * 100;

        return notas[nota] + octava;
//        return notas[nota] + octava +
//                String.format(" (%+.1f cents)", cents);
    }

    // --------------------- FFT ---------------------
    public static void fft(double[] real, double[] imag) {
        int n = real.length;
        int bits = (int)(Math.log(n) / Math.log(2));

        // bit reversal
        for (int i = 0; i < n; i++) {
            int j = reverseBits(i, bits);
            if (j > i) {
                double tr = real[j]; real[j] = real[i]; real[i] = tr;
                double ti = imag[j]; imag[j] = imag[i]; imag[i] = ti;
            }
        }

        for (int size = 2; size <= n; size <<= 1) {
            double ang = -2 * Math.PI / size;
            double wMulR = Math.cos(ang);
            double wMulI = Math.sin(ang);

            for (int start = 0; start < n; start += size) {
                double wR = 1, wI = 0;

                for (int k = 0; k < size/2; k++) {
                    int even = start + k;
                    int odd = even + size/2;

                    double rTemp = wR * real[odd] - wI * imag[odd];
                    double iTemp = wR * imag[odd] + wI * real[odd];

                    real[odd] = real[even] - rTemp;
                    imag[odd] = imag[even] - iTemp;

                    real[even] += rTemp;
                    imag[even] += iTemp;

                    double wR2 = wR * wMulR - wI * wMulI;
                    double wI2 = wR * wMulI + wI * wMulR;
                    wR = wR2;
                    wI = wI2;
                }
            }
        }
    }

    private static int reverseBits(int x, int nbits) {
        int y = 0;
        for (int i = 0; i < nbits; i++) {
            y = (y << 1) | (x & 1);
            x >>= 1;
        }
        return y;
    }
}
