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
 * @version 1.0
 * @since 2017
 */
public class Time {

    private static final long OLD_LONG_SECOND = 1000000000L;
    public static final double SECOND = 1.0;

    private static double delta;

    /**
     * Gets the time of compiling.
     * @return Program's time.
     */
    public static double getTime() {
        return (double)System.nanoTime()/(double)OLD_LONG_SECOND;
    }

    /**
     * Returns the time's delta.
     * @return delta
     */
    public static double getDelta() { return delta; }

    /**
     * Sets a delta for the time.
     * @param delta For time.
     */
    public static void setDelta(double delta) { Time.delta = delta; }
}
