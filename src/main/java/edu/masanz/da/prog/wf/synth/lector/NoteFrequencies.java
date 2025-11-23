package edu.masanz.da.prog.wf.synth.lector;

public class NoteFrequencies {

    private static final double A4 = 440.0;

    // abc: C=do, D=re...
    public static double noteToFreq(String note, int octave) {
        int semitone;

        switch (note.toUpperCase()) {
            case "C": semitone = -9; break;
            case "C#": case "^C": semitone = -8; break;
            case "Db": case "_D": semitone = -8; break;
            case "D": semitone = -7; break;
            case "D#": case "^D": semitone = -6; break;
            case "Eb": case "_E": semitone = -6; break;
            case "E": semitone = -5; break;
            case "F": semitone = -4; break;
            case "F#": case "^F": semitone = -3; break;
            case "Gb": case "_G": semitone = -3; break;
            case "G": semitone = -2; break;
            case "G#": case "^G": semitone = -1; break;
            case "Ab": case "_A": semitone = -1; break;
            case "A": semitone = 0; break;
            case "A#": case "^A": semitone = 1; break;
            case "Bb": case "_B": semitone = 1; break;
            case "B": semitone = 2; break;
            default: return 0;
        }

        // Octava ABC: C = octava 4 normalmente
        int semitoneFromA4 = semitone + (octave - 4) * 12;
        return A4 * Math.pow(2, semitoneFromA4 / 12.0);
    }
}
