package edu.masanz.da.prog.wf;
import javax.sound.sampled.*;
import java.io.*;

public class DetectorFrecuenciaFundamental {

    public static void main(String[] args) throws Exception {

        String inputFileName = "notas.wav";

        File input = new File(inputFileName);

        // ---- 1. Leer WAV ----
        AudioInputStream ais = AudioSystem.getAudioInputStream(input);
        AudioFormat format = ais.getFormat();

        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED ||
                format.getSampleSizeInBits() != 16 ||
                format.getChannels() != 1) {
            throw new UnsupportedAudioFileException(
                    "Solo se admite WAV PCM 16-bit MONO.");
        }

        byte[] raw = ais.readAllBytes();
        ais.close();

        int numSamples = raw.length / 2;
        double[] samples = new double[numSamples];

        // Convertir a doubles en -1..1
        for (int i = 0; i < numSamples; i++) {
            int low = raw[2*i] & 0xFF;
            int high = raw[2*i + 1] << 8;
            short val = (short)(low | high);
            samples[i] = val / 32768.0;
        }

        // ---- 2. Preparar FFT (siguiente potencia de 2) ----
        int N = 1;
        while (N < samples.length) N *= 2;
        double[] real = new double[N];
        double[] imag = new double[N];

        System.arraycopy(samples, 0, real, 0, samples.length);

        // ---- 3. FFT ----
        fft(real, imag);

        // ---- 4. Buscar frecuencia fundamental ----
        // TODO 61: Buscar frecuencia fundamental
        double sampleRate = format.getSampleRate();
        int peak = 0;           // Índice del pico
        double peakMag = 0;     // Magnitud del pico

        // Se busca en la mitad del espectro
        for (int i = 1; i < N/2; i++) {
            // Se calcula la magnitud de cada muestra que es la hipotenusa de real[i] e imag[i]
            double mag = 1.0;       // Calcular magnitud
            // Si la magnitud es mayor que la máxima encontrada, se actualiza el pico



        }


        double fundamental = (peak * sampleRate) / N;
        System.out.printf("Frecuencia detectada: %.2f Hz%n", fundamental);

        // ---- 5. Convertir frecuencia → nota ----
        String nota = freqToNote(fundamental);
        System.out.println("Nota detectada: " + nota);
    }

    // -------------------------------
    //    Conversión frecuencia→nota
    // -------------------------------
    public static String freqToNote(double freq) {

        String[] notas = {"Do", "Do#", "Re", "Re#", "Mi", "Fa",
                "Fa#", "Sol", "Sol#", "La", "La#", "Si"};

        // referencia: La4 = 440 Hz, midi 69
        double midi = 69 + 12 * Math.log(freq / 440.0) / Math.log(2);

        int midiInt = (int)Math.round(midi);
        int nota = midiInt % 12;
        int octava = (midiInt / 12) - 1;

        return notas[nota] + octava;
    }

    // -------------------------------
    //             FFT
    // -------------------------------
    public static void fft(double[] real, double[] imag) {
        int n = real.length;
        int logN = (int)(Math.log(n) / Math.log(2));

        // bit reversal
        for (int i = 0; i < n; i++) {
            int j = reverseBits(i, logN);
            if (j > i) {
                double tR = real[j];
                double tI = imag[j];
                real[j] = real[i];
                imag[j] = imag[i];
                real[i] = tR;
                imag[i] = tI;
            }
        }

        // Cooley–Tukey
        for (int size = 2; size <= n; size <<= 1) {
            double ang = -2 * Math.PI / size;
            double wMulR = Math.cos(ang);
            double wMulI = Math.sin(ang);

            for (int start = 0; start < n; start += size) {
                double wR = 1;
                double wI = 0;

                for (int j = 0; j < size/2; j++) {

                    int even = start + j;
                    int odd  = even + size/2;

                    double rTemp = wR * real[odd] - wI * imag[odd];
                    double iTemp = wR * imag[odd] + wI * real[odd];

                    real[odd] = real[even] - rTemp;
                    imag[odd] = imag[even] - iTemp;

                    real[even] += rTemp;
                    imag[even] += iTemp;

                    double nwR = wR * wMulR - wI * wMulI;
                    double nwI = wR * wMulI + wI * wMulR;

                    wR = nwR;
                    wI = nwI;
                }
            }
        }
    }

    private static int reverseBits(int x, int bits) {
        int y = 0;
        for (int i = 0; i < bits; i++) {
            y = (y << 1) | (x & 1);
            x >>= 1;
        }
        return y;
    }
}
