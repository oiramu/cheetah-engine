/*
 * Copyright 2017 Julio Vergara.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package engine.audio;

import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import engine.core.Debug;
import engine.core.crash.CrashReport;

/**
 *
 * @author Julio Vergara
 * @version 1.2
 * @since 2017
 */
public class AudioUtil {

    private static Sequencer 	sequencer;

    /**
     * Plays a sequence of a MIDI theme in game.
     * @param midi to play.
     */
    public static void playMidi(Sequence midi) {
        try {
            if (sequencer == null) {
                sequencer = MidiSystem.getSequencer();
            }
            if (sequencer.isOpen()) {
                sequencer.stop();
                sequencer.setTickPosition(0);
            }

            if (midi == null) {
                sequencer.stop();
                sequencer.close();
                return;
            }

            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            //sequencer.setLoopCount(0);
            sequencer.setSequence(midi);
            sequencer.open();

            sequencer.start();
        } catch (Exception e) {
        	Debug.crash(new CrashReport(e));
        }
    }
    
    /**
	 * Loads a MIDI sequence file named like that.
	 * @param fileName Name of the sequence file.
	 * @return Sequence.
	 */
    public static Sequence loadMidi(String fileName) {
        Sequence sequence = null;

        try {
            sequence = MidiSystem.getSequence(new File("./res/midi/" + fileName + ".mid"));
        } catch (Exception e) {
        	Debug.crash(new CrashReport(e));
        }

        return sequence;
    }

    /**
     * Checks if the program is playing a MIDI sequence.
     * @return Sequencer state.
     */
    public static boolean isPlayingMidi() {return sequencer.isRunning();}
    
    /**
     * Run the MIDI system.
     */
    public static void runMidi() {sequencer.start();}
    
    /**
     * Stop the MIDI system.
     */
    public static void stopMidi() {sequencer.stop();}
    
}
