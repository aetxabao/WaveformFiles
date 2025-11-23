# WaveformFiles

Este es un proyecto para ver el uso de arrays en un contexto de señales de audio y ficheros de tipo waveform (.wav)

Este proyecto se ha realizado con la ayuda de chatgpt y los ejemplos pueden ser fácilmente adaptables.

## Ejemplos

El orden para hacer una práctica progresiva es el siguiente:

1. Generador440Wav
    - Genera un fichero de audio .wav con una señal senoidal de 440Hz (La4)
2. GeneradorNotasWav
    - Genera un fichero de audio .wav con una serie de notas musicales (La4, Si4, Do5, Re5, Mi5, Fa5, Sol5, La5)
3. NotasConSilencio
    - Genera un fichero de audio .wav con una serie de notas musicales separadas por silencios
4. EcoWav
    - Genera un fichero de audio .wav con un efecto de eco a partir de un fichero de audio de entrada
5. FadeInOutWav
    - Genera un fichero de audio .wav con un efecto de fade-in y fade-out a partir de un fichero de audio de entrada
6. DetectorFrecuenciaFundamental
    - Lee un fichero de audio .wav y detecta la frecuencia fundamental en el mismo
7. AnalizadorNotasCompleto
    - Lee un fichero de audio .wav con una serie de notas musicales y detecta las notas presentes en el mismo

Dentro del paquete synth está el código de un sintetizador:

8. synth/Main
    - Permite múltiples voces, ADSR, vibrato, timbre y acordes

A su vez, dentro del paquete synth se encuentra el paquete lector que permite leer ficheros con notas musicales en formato ABC y reproducirlas:

9. synth/lector/Main
    - Lee ficheros ABC y los transforma a ficheros WAV y los reproduce usando el sintetizador

## Ficheros de entrada y salida

| Tipo | Fichero        | Programa                      | Descripción                                                          | 
|------|----------------|-------------------------------|----------------------------------------------------------------------|
| out  | la440.wav      | Generador440Wav               | Señal sinusoidal de 440 Hz de frecuencia (Nota la)                   |
| out  | notas.wav      | GeneradorNotasWav             | Notas (Hz): 261.63, 293.66, 329.63, 349.23, 392, 440, 493.88, 523.25 |
| out  | notas2.wav     | NotasConSilencio              | Anteriores señales separadas por silencios                           |
| in   | eco4.wav       | EcoWav                        | Voz diciendo "eco" cuatro veces                                      |
| out  | eco4eco.wav    | EcoWav                        | Efecto de "eco" sobre el audio anterior                              |
| in   | notas.wav      | FadeInOutWav                  | Do, Re, Mi, Fa, Sol, La, Si, Do                                      |
| out  | notas_fade.wav | FadeInOutWav                  | Efecto fade-in y fade-out sobre las notas anteriores                 |
| in   | notas.wav      | DetectorFrecuenciaFundamental | Senos (Hz): 261.63, 293.66, 329.63, 349.23, 392, 440, 493.88, 523.25 |
| in   | notas.wav      | AnalizadorNotasCompleto       | Senos (Hz): 261.63, 293.66, 329.63, 349.23, 392, 440, 493.88, 523.25 |
| out  | synth.wav      | synth.Main                    | Audio creado con el sintetizador                                     |
| in   | retrogame.abc  | synth.lector.Main             | Fichero texto ABC estilo juego retro                                 |
| out  | retrogame.wav  | synth.lector.Main             | Fichero audio ABC estilo juego retro                                 |
| in   | odetojoy.abc   | synth.lector.Main             | Fichero texto ABC ode to joy                                         |
| out  | odetojoy.wav   | synth.lector.Main             | Fichero audio ABC ode to joy                                         |
