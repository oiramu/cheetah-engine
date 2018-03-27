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
package engine.rendering;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class Bitmap {

    private final int width;
    private final int height;
    private int[] pixels;

    /**
     * Constructor of the bitmap object.
     * @param width Of the bitmap.
     * @param height Of the bitmap.
     */
    public Bitmap(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
    }

    /**
     * Draws the bitmap's and takes the information of it.
     * @param render The bitmap.
     * @param xOffset X searcher.
     * @param yOffset Y searcher.
     */
    public void draw(Bitmap render, int xOffset, int yOffset) {
        for (int y = 0; y < render.height; y++) {
            int yPix = y + yOffset;

            if (yPix < 0 || yPix >= height) {
                continue;
            }

            for (int x = 0; x < render.width; x++) {
                int xPix = x + xOffset;

                if (xPix < 0 || xPix >= width) {
                    continue;
                }

                int alpha = render.pixels[x + y * render.width];

                pixels[xPix + yPix * width] = alpha;
            }
        }
    }

    /**
     * Flips the X axis data.
     * @return The flip bitmap.
     */
    public Bitmap flipX() {
        int[] temp = new int[pixels.length];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                temp[i + j * width] = pixels[(width - i - 1) + j * width];
                //temp.setPixel(i, j, level.getPixel(level.getWidth() - i - 1, j));
            }
        }

        pixels = temp;

        return this;
    }

    /**
     * Returns the width of the bitmap.
     * @return Width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the bitmap.
     * @return Height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns all the pixel color data to an integer array.
     * @return Pixel.
     */
    public int[] getPixels() {
        return pixels;
    }
    
    /**
     * Return the specific pixels in the bitmap.
     * @param x Pixel coordinates.
     * @param y Pixel coordinates.
     * @return Pixel.
     */
    public int getPixel(int x, int y) {
        return pixels[x + y * width];
    }

    /**
     * Sets the pixel color in a specific space.
     * @param x Pixel coordinates.
     * @param y Pixel coordinates.
     * @param value of the color.
     */
    public void setPixel(int x, int y, int value) {
        pixels[x + y * width] = value;
    }
}
