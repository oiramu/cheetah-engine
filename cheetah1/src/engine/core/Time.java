/*
 * Copyright 2017 Carlos Rodriguez.
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
package engine.core;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Time {

    private static final long 	SECOND = 1000000000L;
    private static double 		fps;
    private static double		frametime;

    /**
     * Gets the time of compiling.
     * @return Program's time.
     */
    public static double getTime() { return (double) System.nanoTime() / (double) SECOND; }
    
    /**
     * Returns the frames per second.
     * @return FPS
     */
    public static double getFPS() { return fps; }

    /**
     * Sets the frames per second.
     * @param fps.
     */
    public static void setFPS(double fps) { Time.fps = fps; }
    
    /**
     * Returns the time between frames.
     * @return frame time
     */
    public static double getFrametime() { return frametime; }

    /**
     * Sets the time between frames.
     * @param frametime
     */
    public static void setFrametime(double frametime) { Time.frametime = frametime; }

}
