/*
 * Copyright 2018 Carlos Rodriguez.
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
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

import java.util.ArrayList;

import engine.components.BaseLight;
import engine.components.Camera;
import engine.components.GameComponent;
import engine.core.Transform;
import engine.core.Vector3f;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.2
 * @since 2018
 */
public class RenderingEngine {
	
	private Camera mainCamera;
	private BaseLight activeLight;
	public static Vector3f ambientLight;
	
	private static ArrayList<BaseLight> lights;
	
	public static float fogDensity;
	public static float fogGradient;
    private static Vector3f fogColor;
	
	/**
	 * Constructor for the rendering engine.
	 */
	public RenderingEngine() {
        lights = new ArrayList<BaseLight>();
        
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glFrontFace(GL_CW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glEnable(GL_DEPTH_CLAMP);
        
        Transform.setProjection(70, Window.getWidth(), Window.getHeight(), 0.01f, 1000f);
		
        printCompilationStuff();
	}
	
	/**
     * Renders everything every on screen.
     */
    public void render(GameComponent component) {
    	try {
	    	clearScreen();
	    	Shader forwardAmbient = ForwardAmbient.getInstance();
			forwardAmbient.setRenderingEngine(this);;
	        component.render(forwardAmbient);
	        glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glDepthMask(false);
			glDepthFunc(GL_EQUAL);
			for(int i = 0; i < lights.size(); i++) {
				activeLight = lights.get(i);
				component.render(activeLight.getShader(this));
			}
			glDepthFunc(GL_LESS);
			glDepthMask(true);
			glDisable(GL_BLEND);
    	} catch(RuntimeException e) {
    		e.printStackTrace();
    		System.exit(0);
    	}
    }
    
    /**
	 * Cleans everything light related.
	 */
    public void clearLights() {
        lights.clear();
    }

	/**
	 * Cleans everything on the openGL screen.
	 */
    private void clearScreen() {
        //TODO: Stencil Buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    /**
     * Sets textures to openGL.
     * @param enabled Able switch.
     */
    public static void setTextures(boolean enabled) {
        if (enabled) {
            glEnable(GL_TEXTURE_2D);
        } else {
            glDisable(GL_TEXTURE_2D);
        }
    }
    
    /**
	 * Prints the compilation configuration.
	 */
	private void printCompilationStuff() {
		System.out.println("==============================");
        System.out.println("||CHEETAH ENGINE; BUILD v1.0||");
        System.out.println("==============================");
        System.out.println("Compiliation specs: ");
        System.out.println("-OS name: " + System.getProperty("os.name"));
        System.out.println("-OS version: " + System.getProperty("os.version"));
        System.out.println("-LWJGL version: " + org.lwjgl.Sys.getVersion());
        System.out.println("-OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("\n");
	}
	
	/**
	 * Adds a new directional light to the rendering engine.
	 * @param light to add.
	 */
	public static void addLight(BaseLight light) {lights.add(light);}
	
	/**
	 * Removes a new directional light to the rendering engine.
	 * @param light to remove.
	 */
	public static void removeLight(BaseLight light) {lights.remove(light);}
	
	/**
	 * Returns the light that is been used.
	 * @return Active light.
	 */
	public BaseLight getActiveLight() {return activeLight;}
	
	/**
	 * Returns the color of the fog.
	 * @return fogColor.
	 */
	public static Vector3f getFogColor() {return fogColor;}

	/**
	 * Sets a new color for the fog.
	 * @param color of the fog.
	 */
	public static void setFogColor(Vector3f color) {fogColor = color; setClearColor(color);}

    /**
     * Bind the textures to openGL.
     */
    @SuppressWarnings("unused")
	private static void unbindTextures() {glBindTexture(GL_TEXTURE_2D, 0);}

    /**
     * Cleans all the colors in the environment.
     * @param color to clean.
     */
    public static void setClearColor(Vector3f color) {glClearColor(color.getX(), color.getY(), color.getZ(), 1.0f);}
	
	/**
	 * Returns the actual ambient light.
	 * @return Ambient light.
	 */
	public static Vector3f getAmbientLight() {return ambientLight;}
    
    /**
     * Returns the main camera in game.
     * @return camera.
     */
    public Camera getMainCamera() {return mainCamera;}

    /**
     * Sets the main camera of all the game to the rendering
     * engine.
     * @param mainCamera of the game.
     */
	public void setMainCamera(Camera mainCamera) {this.mainCamera = mainCamera;}

}
