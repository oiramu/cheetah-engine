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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import engine.rendering.Vertex;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class Util {

	/**
	 * Creates a buffer for floating point data.
	 * @param size Index size.
	 * @return A float buffer.
	 */
    public static FloatBuffer createFloatBuffer(int size) {
        return BufferUtils.createFloatBuffer(size);
    }

    /**
	 * Creates a buffer for integer data.
	 * @param size Index size.
	 * @return An integer buffer.
	 */
    public static IntBuffer createIntBuffer(int size) {
        return BufferUtils.createIntBuffer(size);
    }

    /**
     * Creates a flip buffer with all the possible integer data.
     * @param values All the data to the buffer.
     * @return The buffer.
     */
    public static IntBuffer createFlippedBuffer(int... values) {
        IntBuffer buffer = createIntBuffer(values.length);
        buffer.put(values);
        buffer.flip();

        return buffer;
    }

    /**
     * Creates a flip buffer with all the vertices when need it.
     * @param vertices All the vertices to store.
     * @return The buffer.
     */
    public static FloatBuffer createFlippedBuffer(Vertex[] vertices) {
        FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.SIZE);

        for (int i = 0; i < vertices.length; i++) {
            buffer.put(vertices[i].getPos().getX());
            buffer.put(vertices[i].getPos().getY());
            buffer.put(vertices[i].getPos().getZ());
            buffer.put(vertices[i].getTexCoord().getX());
            buffer.put(vertices[i].getTexCoord().getY());
            buffer.put(vertices[i].getNormal().getX());
            buffer.put(vertices[i].getNormal().getY());
            buffer.put(vertices[i].getNormal().getZ());
        }

        buffer.flip();

        return buffer;
    }

    /**
	 * Creates a flip buffer for all the data in a matrix 4 by 4.
	 * @param value Data in matrix.
	 * @return The buffer.
	 */
    public static FloatBuffer createFlippedBuffer(Matrix4f value) {
        FloatBuffer buffer = createFloatBuffer(4 * 4);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buffer.put(value.get(i, j));
            }
        }

        buffer.flip();

        return buffer;
    }

    /**
     * Deletes all the empty data on a string vector.
     * @param data Data in vector.
     * @return A new string empty.
     */
    public static String[] removeEmptyStrings(String[] data) {
        ArrayList<String> result = new ArrayList<String>();

        for (int i = 0; i < data.length; i++) {
            if (!data[i].equals("")) {
                result.add(data[i]);
            }
        }

        String[] res = new String[result.size()];
        result.toArray(res);

        return res;
    }

    /**
     * Converts all the integer data to an array of integer.
     * @param data Integer data to array.
     * @return Integer array.
     */
    public static int[] toIntArray(Integer[] data) {
        int[] result = new int[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = data[i].intValue();
        }

        return result;
    }
    
}
