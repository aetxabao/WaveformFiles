package edu.masanz.da.prog.wf;
import javax.sound.sampled.*;
import java.io.*;

public class EcoWav {

    public static void main(String[] args) throws Exception {

        String inputFileName = "eco4.wav";
        String outputFileName = "eco4eco.wav";

        File input = new File(inputFileName);
        File output = new File(outputFileName);

        // ---- 1. Leer WAV en un array ----
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

        // Convertimos bytes → array de samples short[]
        int numSamples = buffer.length / 2;
        short[] samples = new short[numSamples];

        for (int i = 0; i < numSamples; i++) {
            int low = buffer[2*i] & 0xFF;
            int high = buffer[2*i + 1] << 8;
            samples[i] = (short) (low | high);
        }

        // ---- 2. Aplicar eco ----
        float delay = 0.3f;      // retardo del eco
        float decay = 0.5f;      // atenuación del eco

        // 44100 Hz * 0.3 s = 13230 samples de retardo
        int delaySamples = (int)(format.getSampleRate() * delay);

        short[] outputSamples = new short[numSamples + delaySamples];

        // Copiamos el audio original
        System.arraycopy(samples, 0, outputSamples, 0, numSamples);

        // Añadimos la señal retardada
        // TODO 41: A cada muestra sumale la muestra retrasada por delaySamples multiplicada por decay

            int mixed = (int)(0.0);

            // Limitar por saturación (clipping seguro)
            // Al sumar, puede que se pase del rango de un short, así que se limita
            if (mixed > Short.MAX_VALUE) mixed = Short.MAX_VALUE;
            if (mixed < Short.MIN_VALUE) mixed = Short.MIN_VALUE;


        // ---- 3. Convertir de outputSamples[] a outBytes[] ----
        byte[] outBytes = new byte[outputSamples.length * 2];
        for (int i = 0; i < outputSamples.length; i++) {
            outBytes[2*i] = (byte)(outputSamples[i] & 0xFF);
            outBytes[2*i + 1] = (byte)((outputSamples[i] >> 8) & 0xFF);
        }

        // ---- 4. Guardar WAV ----
        ByteArrayInputStream bais = new ByteArrayInputStream(outBytes);
        AudioInputStream outAis =
                new AudioInputStream(bais, format, outputSamples.length);

        AudioSystem.write(outAis, AudioFileFormat.Type.WAVE, output);
        outAis.close();

        System.out.println("Archivo con eco generado: " + outputFileName);
    }
}
