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
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.*;
import static org.lwjgl.opengl.GLContext.*;

import static engine.components.Constants.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import engine.core.Debug;
import engine.core.crash.CrashReport;
import engine.rendering.resourceManagement.TextureResource;
import engine.core.utils.Log;
import engine.core.utils.Util;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Texture {

	private static HashMap<String, TextureResource> loadedTextures = new HashMap<String, TextureResource>();
	private String 									fileName;
    private TextureResource 						resource;
    private int										height;
    private int										width;
    private int										FBOID;
    private int 									FBO;
    private static int 								lastWriteBind = 0;
    
    /**
     * Texture's constructor with a file.
     * @param fileName of the texture.
     */
    public Texture(String fileName) {
    	this.fileName = fileName;
    	TextureResource oldResource = loadedTextures.get(fileName);
    	if(oldResource != null) {
    		resource = oldResource;
    		resource.addReferece();
    	} else {
    		resource = loadTexture(fileName);
    		loadedTextures.put(fileName, resource);
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
    	this.fileName = "";
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

		this.FBOID = textureID;
		this.FBO = 0;
		this.width = width;
		this.height = height;
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
    	this.fileName = "";
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
        
        this.width = width;
		this.height = height;
        this.FBOID = texture;
        this.FBO = 0;
	}
    
    /**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {
    	if(resource.removeReference() && !fileName.isEmpty())
    		loadedTextures.remove(fileName);
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
        				float ammount = Util.clamp(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, (int) ANISOTROPIC_LEVEL);
        				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT,
        						ammount);
        			} else {
        				Log.fatal("Anisotropic filtering is not supported by the driver.\nTry with trilinear filtering.");
        			}
            		break;
            }
            
            return resource;
        } catch (Exception e) {
            Debug.crash(new CrashReport(e));
        }

        return null;
    }
	
	/**
	 * Generates a render target in the FBO.
	 * @param attachment of the FBO texture.
	 * @param bind if it should.
	 */
	public void initRenderTarget(int attachment, boolean bind) {
		FBO = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, FBO);
		glFramebufferTexture(GL_FRAMEBUFFER, attachment, FBOID, 0);
		
		if(attachment == GL_DEPTH_ATTACHMENT)
			glDrawBuffer(GL_NONE);
		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Shadow framebuffer creation has failed");
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		if(bind) {
			lastWriteBind = FBO;
			glViewport(0,0,width,height);
		}
		else
			glBindFramebuffer(GL_FRAMEBUFFER, lastWriteBind);
	}
	
    /**
     * Binds the texture for openGL.
     */
    public void bind(int sampler) {
    	glActiveTexture(GL_TEXTURE0 + sampler);
        glBindTexture(GL_TEXTURE_2D, resource.getId());
    }

    /**
     * Returns the texture index.
     * @return Index.
     */
    public int getID() {return resource.getId();}
    
    /**
	 * Binds a render target in the FBO.
	 */
    public void bindAsRenderTarget() {
		if(lastWriteBind != FBO) {
			glBindFramebuffer(GL_FRAMEBUFFER, FBO);
			lastWriteBind = FBO;
			glViewport(0,0,width,height);
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