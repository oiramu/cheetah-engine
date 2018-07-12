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
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

import java.util.ArrayList;

import engine.core.GameComponent;
import engine.core.Transform;
import engine.core.Vector3f;

/**
*
* @author Carlos Rodriguez
* @version 1.1
* @since 2017
*/
public class RenderingEngine {
	
	private Camera mainCamera;
	private DirectionalLight directionalLight;
	private PointLight pointLight;
	private SpotLight spotLight;

	public static Vector3f ambientLight;
	public static ArrayList<DirectionalLight> directionalLights;
	public static ArrayList<PointLight> pointLights;
	public static ArrayList<SpotLight> spotLights;
	
	public static float fogDensity;
	public static float fogGradient;
    public static Vector3f fogColor;
	
	/**
	 * Constructor for the rendering engine.
	 */
	public RenderingEngine() {
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
       
        if(directionalLights == null) directionalLights = new ArrayList<DirectionalLight>();
        if(pointLights == null) pointLights = new ArrayList<PointLight>();
        if(spotLights == null) spotLights = new ArrayList<SpotLight>();
        
        //ambientLight = new Vector3f(0.5f, 0.5f, 0.5f);
		//directionalLight = new DirectionalLight(new BaseLight(new Vector3f(0,0,1), 0.4f), new Vector3f(1,1,1));
		
        printCompilationStuff();
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
	 * Returns the actual ambient light.
	 * @return Ambient light.
	 */
	public Vector3f getAmbientLight() {return ambientLight;}

	/**
	 * Returns the actual directional light.
	 * @return Directional light.
	 */
	public DirectionalLight getDirectionalLight() {return directionalLight;}

	/**
	 * Returns the actual point of light.
	 * @return Point of light.
	 */
	public PointLight getPointLight() {return pointLight;}

	/**
	 * Returns the actual spot of light.
	 * @return Spot of light.
	 */
	public SpotLight getSpotLight() {return spotLight;}
	
	/**
     * Renders everything every on screen.
     */
    public void render(GameComponent component) {
    	clearScreen();
    	Shader forwardAmbient = ForwardAmbient.getInstance();
		Shader forwardPoint = ForwardPoint.getInstance();
		Shader forwardSpot = ForwardSpot.getInstance();
		Shader forwardDirectional = ForwardDirectional.getInstance();
		forwardAmbient.setRenderingEngine(this);
		forwardDirectional.setRenderingEngine(this);
		forwardPoint.setRenderingEngine(this);
		forwardSpot.setRenderingEngine(this);
        component.render(forwardAmbient);
        glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		glDepthMask(false);
		glDepthFunc(GL_EQUAL);
		for(int i = 0; i < directionalLights.size(); i++) {
			directionalLight = directionalLights.get(i);
			component.render(forwardDirectional);
		}
		for(int i = 0; i < pointLights.size(); i++) {
			pointLight = pointLights.get(i);
			component.render(forwardPoint);
		}
		for(int i = 0; i < spotLights.size(); i++) {
			spotLight = spotLights.get(i);
			component.render(forwardSpot);
		}
		glDepthFunc(GL_LESS);
		glDepthMask(true);
		glDisable(GL_BLEND);
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
     * Bind the textures to openGL.
     */
    @SuppressWarnings("unused")
	private static void unbindTextures() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Cleans all the colors in the environment.
     * @param color to clean.
     */
    public static void setClearColor(Vector3f color) {
        glClearColor(color.getX(), color.getY(), color.getZ(), 1.0f);
    }
    
    /**
     * Returns the main camera in game.
     * @return camera.
     */
    public Camera getMainCamera() {
		return mainCamera;
	}

    /**
     * Sets the main camera of all the game to the rendering
     * engine.
     * @param mainCamera of the game.
     */
	public void setMainCamera(Camera mainCamera) {
		this.mainCamera = mainCamera;
	}

}