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

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Bitmap {

    private final int 	m_width;
    private final int 	m_height;
    private int[] 		m_pixels;
    
    /**
     * Constructor of the bitmap object with a file.
     * @param fileName of the bitmap.
     */
    public Bitmap(String fileName) {
		try {
			BufferedImage image = ImageIO.read(new File("./res/bitmaps/" + fileName + ".png"));
			
			m_width = image.getWidth();
			m_height = image.getHeight();
			
			m_pixels = new int[m_width * m_height];
			image.getRGB(0, 0, m_width, m_height, m_pixels, 0, m_width);
		} catch(Exception ex) {
			throw new RuntimeException();
		}
	}

    /**
     * Constructor of the bitmap object.
     * @param width Of the bitmap.
     * @param height Of the bitmap.
     */
    public Bitmap(int width, int height) {
        this.m_width = width;
        this.m_height = height;
        m_pixels = new int[width * height];
    }

    /**
     * Draws the bitmap's and takes the information of it.
     * @param render The bitmap.
     * @param xOffset X searcher.
     * @param yOffset Y searcher.
     */
    public void draw(Bitmap render, int xOffset, int yOffset) {
        for (int y = 0; y < render.m_height; y++) {
            int yPix = y + yOffset;

            if (yPix < 0 || yPix >= m_height) {
                continue;
            }

            for (int x = 0; x < render.m_width; x++) {
                int xPix = x + xOffset;

                if (xPix < 0 || xPix >= m_width) {
                    continue;
                }

                int alpha = render.m_pixels[x + y * render.m_width];

                m_pixels[xPix + yPix * m_width] = alpha;
            }
        }
    }

    /**
     * Flips the X axis data.
     * @return The flip bitmap.
     */
    public Bitmap flipX() {
        int[] temp = new int[m_pixels.length];

        for (int i = 0; i < m_width; i++) {
            for (int j = 0; j < m_height; j++) {
                temp[i + j * m_width] = m_pixels[(m_width - i - 1) + j * m_width];
                //temp.setPixel(i, j, level.getPixel(level.getWidth() - i - 1, j));
            }
        }

        m_pixels = temp;

        return this;
    }

    /**
     * Returns the width of the bitmap.
     * @return Width.
     */
    public int getWidth() {return m_width;}

    /**
     * Returns the height of the bitmap.
     * @return Height.
     */
    public int getHeight() {return m_height;}

    /**
     * Returns all the pixel color data to an integer array.
     * @return Pixel.
     */
    public int[] getPixels() {return m_pixels;}
    
    /**
     * Return the specific pixels in the bitmap.
     * @param x Pixel coordinates.
     * @param y Pixel coordinates.
     * @return Pixel.
     */
    public int getPixel(int x, int y) {return m_pixels[x + y * m_width];}

    /**
     * Sets the pixel color in a specific space.
     * @param x Pixel coordinates.
     * @param y Pixel coordinates.
     * @param value of the color.
     */
    public void setPixel(int x, int y, int value) {m_pixels[x + y * m_width] = value;}
    
}
