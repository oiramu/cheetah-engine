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
package engine;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.opengl.TextureLoader;

import engine.core.Util;
import engine.core.Vector3f;
import engine.rendering.Bitmap;
import engine.rendering.Mesh;
import engine.rendering.Texture;
import engine.rendering.Vertex;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class ResourceLoader {
	
	private static final int MIPMAP_SAMPLES = 4; 

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
            e.printStackTrace();
            System.exit(1);
        }

        return sequence;
    }

    /**
	 * Loads a WAV sequence file named like that.
	 * @param fileName Name of the clip file.
	 * @return Clip.
	 */
    public static Clip loadAudio(String fileName) {
        Clip clip = null;

        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File("./res/audio/" + fileName + ".wav"));
            clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, stream.getFormat()));
            clip.open(stream);

            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return clip;
    }

    /**
     * Loads a PNG image file named like that an uses it as the level
     * bitmap. 
     * @param fileName Name of the bitmap file. 
     * @return Bitmap. 
     * @throws Exception if the bitmap can't load something. 
     */ 
    public static Bitmap loadBitmap(String fileName) throws RuntimeException {
        try {
            //BufferedImage image = ImageIO.read(ResourceLoader.class.getResource("/res/bitmaps/" + fileName));
            BufferedImage image = ImageIO.read(new File("./res/bitmaps/" + fileName + ".png"));
            int width = image.getWidth();
            int height = image.getHeight();

            Bitmap result = new Bitmap(width, height);

            image.getRGB(0, 0, width, height, result.getPixels(), 0, width);

            return result;
        } catch (Exception ex) {
            //ex.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    
    /**
	 * Loads a PNG image file named like that and uses it as the window's icon.
	 * @param fileName Name of the icon file.
	 * @return Icon.
	 */
    public static ByteBuffer loadIcon(String fileName) {
        FileInputStream in;
        ByteBuffer buffer = null;
        try {
        	in = new FileInputStream(new File(("./res/" + fileName + ".png")));
            PNGDecoder decoder = new PNGDecoder(in);
            buffer = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
            decoder.decode(buffer, decoder.getWidth()*4, PNGDecoder.RGBA);
            buffer.flip();
        }catch (Exception e) {
        	System.err.println("Error loading " + fileName);
            e.printStackTrace();
        }
        return buffer;
    }

    /**
	 * Loads a PNG image file named like that.
	 * @param fileName Name of the image file.
	 * @return Image.
	 */
    public static Texture loadTexture(String fileName) {
        String[] splitArray = fileName.split("\\.");
        String ext = splitArray[splitArray.length - 1];

        try {
            int id = TextureLoader.getTexture(ext, new FileInputStream(new File("./res/textures/" + fileName + ".png")), GL_NEAREST).getTextureID();
            
            glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
					GL_LINEAR_MIPMAP_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -MIPMAP_SAMPLES);
			
            return new Texture(id);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    /**
	 * Loads a GLSL shader file named like that.
	 * @param fileName Name of the shader file.
	 * @return Shader.
	 */
    public static String loadShader(String fileName) {
        StringBuilder shaderSource = new StringBuilder();
        BufferedReader shaderReader = null;

        try {
            shaderReader = new BufferedReader(new FileReader("./res/shaders/" + fileName + ".glsl"));
            String line;

            while ((line = shaderReader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }

            shaderReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return shaderSource.toString();
    }

    /**
	 * Loads a OBJ 3D model file named like that.
	 * @param fileName Name of the 3D model file.
	 * @return 3D model.
	 */
    public static Mesh loadMesh(String fileName) {
        String[] splitArray = fileName.split("\\.");
        String ext = splitArray[splitArray.length - 1];

        if (!ext.equals("obj")) {
            System.err.println("Error: File format not supported for mesh data: " + ext);
            new Exception().printStackTrace();
            System.exit(1);
        }

        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Integer> indices = new ArrayList<Integer>();

        BufferedReader meshReader = null;

        try {
            meshReader = new BufferedReader(new FileReader("./res/models/" + fileName + ".obj"));
            String line;

            while ((line = meshReader.readLine()) != null) {
                String[] tokens = line.split(" ");
                tokens = Util.removeEmptyStrings(tokens);

                if (tokens.length == 0 || tokens[0].equals("#")) {
                    continue;
                } else if (tokens[0].equals("v")) {
                    vertices.add(new Vertex(new Vector3f(Float.valueOf(tokens[1]),
                            Float.valueOf(tokens[2]),
                            Float.valueOf(tokens[3]))));
                } else if (tokens[0].equals("f")) {
                    indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
                    indices.add(Integer.parseInt(tokens[2].split("/")[0]) - 1);
                    indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);

                    if (tokens.length > 4) {
                        indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
                        indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
                        indices.add(Integer.parseInt(tokens[4].split("/")[0]) - 1);
                    }
                }
            }

            meshReader.close();

            Mesh res = new Mesh();
            Vertex[] vertexData = new Vertex[vertices.size()];
            vertices.toArray(vertexData);

            Integer[] indexData = new Integer[indices.size()];
            indices.toArray(indexData);

            res.addVertices(vertexData, Util.toIntArray(indexData));

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }
}
