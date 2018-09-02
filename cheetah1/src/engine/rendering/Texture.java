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

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GLContext;

import engine.core.Util;
import engine.rendering.resourceManagement.TextureResource;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Texture {

	private static HashMap<String, TextureResource> m_loadedTextures = new HashMap<String, TextureResource>();
	private String 									m_fileName;
    private TextureResource 						m_resource;
    
    /**
     * Texture's constructor with a file.
     * @param fileName of the texture.
     */
    public Texture(String fileName) {
    	this.m_fileName = fileName;
    	TextureResource oldResource = m_loadedTextures.get(fileName);
    	if(oldResource != null) {
    		m_resource = oldResource;
    		m_resource.addReferece();
    	} else {
    		m_resource = loadTexture(fileName);
    		m_loadedTextures.put(fileName, m_resource);
    	}
    }
    
    /**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {
    	if(m_resource.removeReference() && !m_fileName.isEmpty())
    		m_loadedTextures.remove(m_fileName);
    }
    
    /**
	 * Loads a PNG image file named like that.
	 * @param fileName Name of the image file.
	 * @return Image.
	 */
	private static TextureResource loadTexture(String fileName) {
        String[] splitArray = fileName.split("\\.");
        @SuppressWarnings("unused")
		String ext = splitArray[splitArray.length - 1];
        
        try {            
            BufferedImage image = ImageIO.read(new File("./res/textures/" + fileName + ".png"));
            int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            
            ByteBuffer buffer = Util.createByteBuffer(image.getHeight() * image.getWidth() * 4);
            boolean hasAlpha = image.getColorModel().hasAlpha();
            for(int y = 0; y < image.getHeight(); y++) {
            	for(int x = 0; x <image.getWidth(); x++) {
            		int pixel = pixels[y * image.getWidth() + x];
            		
            		buffer.put((byte) ((pixel >> 16) & 0xFF));
            		buffer.put((byte) ((pixel >> 8) & 0xFF));
            		buffer.put((byte) ((pixel) & 0xFF));
            		if(hasAlpha)
            			buffer.put((byte) ((pixel >> 24) & 0xFF));
            		else
            			buffer.put((byte) (0xFF));
            	}
            }
            
            buffer.flip();
            
            TextureResource resource = new TextureResource();
            glBindTexture(GL_TEXTURE_2D, resource.getId());
            
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);    
            
            switch("2filter") {
            	case "0filter":
            		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            		break;
            	case "1filter":
            		glGenerateMipmap(GL_TEXTURE_2D);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -1.0f);
            		break;
            	case "2filter":
            		glGenerateMipmap(GL_TEXTURE_2D);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0.0f);
        			if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
        				float ammount = Math.min(4,
        						glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
        				glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
        						ammount);
        			}
            		break;
            }
            
            return resource;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }
	
    /**
     * Binds the texture for openGL.
     */
    public void bind(int sampler) {
    	glActiveTexture(GL_TEXTURE0 + sampler);
        glBindTexture(GL_TEXTURE_2D, m_resource.getId());
    }

    /**
     * Returns the texture index.
     * @return Index.
     */
    public int getID() {return m_resource.getId();}
    
}