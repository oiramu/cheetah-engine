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
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static engine.components.Constants.*;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GLContext.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import engine.core.Debug;
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
    private int										m_height;
    private int										m_width;
    private int										m_FBOId;
    private int 									m_FBO;
    private static int 								m_lastWriteBind = 0;
    
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
     * Texture's constructor for a raw or FBO texture.
     * @param width of the texture.
     * @param height of the texture.
     * @param data of the texture.
     * @param filter of the texture.
     * @param wrapMode if it should repeat or not.
     */
    public Texture(int width, int height, FloatBuffer data, int filter, int wrapMode) {
    	this.m_fileName = "";
		int textureID = glGenTextures(); // Generate texture ID
		glBindTexture(GL_TEXTURE_2D, textureID); // Bind texture ID
		
		// Setup wrap mode (GL_CLAMP | GL_REPEAT)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapMode);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapMode);

		// Setup texture scaling filtering (GL_LINEAR | GL_NEAREST)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);

		// Send texture to graphics card
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT16, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, data);

		this.m_FBOId = textureID;
		this.m_FBO = 0;
		this.m_width = width;
		this.m_height = height;
	}
    
    /**
     * Texture's constructor for a raw or FBO texture.
     * @param width of the texture.
     * @param height of the texture.
     * @param data of the texture.
     * @param linearFiltering if it uses it.
     * @param repeatTexture what method of texturing.
     */
	public Texture(int width, int height, int internalFormat, int format, int type, ByteBuffer data, boolean linearFiltering, boolean repeatTexture) {
    	this.m_fileName = "";
		float filter;
        int wrapMode;

        if(linearFiltering)
            filter = GL_LINEAR;
        else
            filter = GL_NEAREST;

        if(repeatTexture)
            wrapMode = GL_REPEAT;
        else
            wrapMode = GL_NEAREST;

        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapMode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapMode);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
        //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, type, data);
        
        this.m_width = width;
		this.m_height = height;
        this.m_FBOId = texture;
        this.m_FBO = 0;
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
            	for(int x = 0; x < image.getWidth(); x++) {
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
            
            switch(TEXTURE_FILTER) {
            	case "Nearest":
            		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            		break;
            	case "Linear":
            		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            		break;
            	case "Bilinear":
            		glGenerateMipmap(GL_TEXTURE_2D);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
        			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -MIPMAP_LEVEL);
            		break;
            	case "Trilinear":
            		glGenerateMipmap(GL_TEXTURE_2D);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -MIPMAP_LEVEL);
            		break;
            	case "Anisotropic":
            		glGenerateMipmap(GL_TEXTURE_2D);
                    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0.0f);
        			if(getCapabilities().GL_EXT_texture_filter_anisotropic) {
        				float ammount = Math.min(ANISOTROPIC_LEVEL,
        						glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
        				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT,
        						ammount);
        			} else {
        				Debug.printErrorMessage("Anisotropic filtering is not supported by the driver.\n"
        						+ "Try with trilinear filtering.", "Driver error!");
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
	 * Generates a render target in the FBO.
	 * @param attachment of the FBO texture.
	 * @param bind if it should.
	 */
	public void initRenderTarget(int attachment, boolean bind) {
		m_FBO = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, m_FBO);
		glFramebufferTexture(GL_FRAMEBUFFER, attachment, m_FBOId, 0);
		
		if(attachment == GL_DEPTH_ATTACHMENT)
			glDrawBuffer(GL_NONE);
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Shadow framebuffer creation has failed");
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		if(bind) {
			m_lastWriteBind = m_FBO;
			glViewport(0,0,m_width,m_height);
		}
		else
			glBindFramebuffer(GL_FRAMEBUFFER, m_lastWriteBind);
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
    
    /**
	 * Binds a render target in the FBO.
	 */
    public void bindAsRenderTarget() {
		if(m_lastWriteBind != m_FBO) {
			glBindFramebuffer(GL_FRAMEBUFFER, m_FBO);
			m_lastWriteBind = m_FBO;
			glViewport(0,0,m_width,m_height);
		}
	}
	
    /**
	 * Stop binding of the render target in the FBO.
	 */
	public void unbindRenderTarget() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0,0,Window.getWidth(),Window.getHeight());
	}
    
}