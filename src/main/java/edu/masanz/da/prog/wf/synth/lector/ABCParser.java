package edu.masanz.da.prog.wf.synth.lector;

import java.util.*;
import java.util.regex.*;

public class ABCParser {

    private double baseNoteLength = 0.25;   // negra = 1/4
    private double tempo = 120;             // BPM
    private int octave = 4;                 // octava base ABC
    private int transpose = 0;              // para K:

    // Convierte duración ABC a segundos
    private double abcDurationToSeconds(String dur) {
        double unit = baseNoteLength;
        if (dur.isEmpty()) return unit * 60.0 / tempo;

        if (dur.contains("/")) {
            String[] p = dur.split("/");
            double num = p[0].isEmpty() ? 1 : Double.parseDouble(p[0]);
            double den = Double.parseDouble(p[1]);
            unit = num / den;
        } else {
            unit = Double.parseDouble(dur) * baseNoteLength;
        }

        return unit * 60.0 / tempo;
    }

    public List<NoteEvent> parse(String abc) {

        List<NoteEvent> list = new ArrayList<>();
        String[] lines = abc.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("L:")) {
                String v = line.substring(2).trim();
                if (v.contains("/")) {
                    String[] p = v.split("/");
                    baseNoteLength = Double.parseDouble(p[0]) /
                            Double.parseDouble(p[1]);
                }
            }
            if (line.startsWith("Q:")) {
                tempo = Double.parseDouble(line.substring(2).trim());
            }
            if (line.startsWith("K:")) {
                // aquí podríamos aplicar transposición
                // pero por simplicidad no lo usamos
            }
        }

        // regex para notas ABC
        Pattern pNote = Pattern.compile(
                "([_=^]?)([A-Ga-gz])([',]*)(\\d*/?\\d*)?"
        );

        for (String line : lines) {
            if (line.startsWith("%") || line.contains(":")) continue;

            Matcher m = pNote.matcher(line);
            while (m.find()) {

                String accidental = m.group(1);
                String noteChar   = m.group(2);
                String octMod     = m.group(3);
                String durStr     = m.group(4) != null ? m.group(4) : "";

                boolean isRest = noteChar.equalsIgnoreCase("z");

                // procesar octava
                int localOct = octave;
                for (char c : octMod.toCharArray()) {
                    if (c == '\'') localOct++;
                    if (c == ',') localOct--;
                }

                // alteraciones
                String fullNote = noteChar.toUpperCase();
                if (accidental.equals("^")) fullNote = fullNote + "#";
                if (accidental.equals("_")) fullNote = (char)(fullNote.charAt(0)+1) + "b";

                double duration = abcDurationToSeconds(durStr);

                if (isRest) {
                    list.add(new NoteEvent(0, duration, true));
                } else {
                    double freq = NoteFrequencies.noteToFreq(fullNote, localOct);
                    list.add(new NoteEvent(freq, duration, false));
                }
            }
        }

        return list;
    }
}
