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
import java.util.HashMap;

import game.enemies.*;
import engine.components.BaseLight;
import engine.components.Camera;
import engine.components.GameComponent;
import engine.core.Transform;
import engine.core.Vector3f;
import engine.rendering.resourceManagement.MappedValues;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.2
 * @since 2018
 */
public class RenderingEngine extends MappedValues {
	
	private Camera 						m_mainCamera;
	private BaseLight 					m_activeLight;
	private Shader 						m_forwardAmbient;
	
	private static ArrayList<BaseLight> m_lights;
	private HashMap<String, Integer> 	m_samplerMap;
	
	/**
	 * Constructor for the rendering engine.
	 */
	public RenderingEngine() {
		super();
        m_lights = new ArrayList<BaseLight>();
        m_samplerMap = new HashMap<String, Integer>();
		m_samplerMap.put("diffuse", 0);
		m_samplerMap.put("normalMap", 1);
		m_samplerMap.put("dispMap", 2);
        
		TextureFont.m_shader.setRenderingEngine(this);
		m_forwardAmbient = new Shader("forward-ambient");
		m_forwardAmbient.setRenderingEngine(this);
		
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glFrontFace(GL_CW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEPTH_CLAMP);

        glEnable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        Transform.setProjection(70, Window.getWidth(), Window.getHeight(), 0.01f, 1000f);
		
        printCompilationStuff();
	}
	
	/**
     * Renders everything every on screen.
     */
    public void render(GameComponent component) {
    	try {
    		Window.bindAsRenderTarget();
    		
    		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			//glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	        component.render(m_forwardAmbient);
	        
	        glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glDepthMask(false);
			glDepthFunc(GL_EQUAL);
			
			for(int i = 0; i < m_lights.size(); i++) {
				m_activeLight = m_lights.get(i);
				component.render(m_activeLight.getShader(this));
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
     * Adds to the render pipeline the lists of objects to render.
     * @param list of objects.
     * @param shader to render.
     */
    public <E> void addListToRenderPipeline(ArrayList<E> list, Shader shader) {
    	for (E component : list) ((GameComponent) component).render(shader);
    }
    
    /**
     * Kills everything on the list of objects.
     * @param list of objects.
     */
    public <E> void updateAndKillToRenderPipeline(ArrayList<E> list) {
    	for (E component : list) {
    		((NaziSoldier) component).update();
    		((NaziSoldier) component).setState(4);
    	}
    }
    
    /**
     * Adds to the render pipeline the lists of objects to update.
     * @param list of objects.
     */
    public <E> void updateListToRenderPipeline(ArrayList<E> list) {
    	for (E component : list) ((GameComponent) component).update();
    }
    
    /**
     * Removes to the render pipeline the lists of objects to render.
     * @param list of objects.
     * @param removeList of objects.
     */
    public <E> void removeListToRenderPipeline(ArrayList<E> list, ArrayList<E> removeList) {
    	for (E component : removeList) list.remove(component);
    }
    
    /**
     * Sorts the number of components added.
     * @param list of objects.
     */
    public <E> void sortNumberComponents(ArrayList<E> list) {
    	if (list.size() > 0) {
    		sortComponents(list, 0, list.size() - 1);
        }
    }
    
    /**
     * Sorts all the objects in the level.
     * @param list of objects.
     * @param low of the array
     * @param high of the array
     */
    private <E> void sortComponents(ArrayList<E> list, int low, int high) {
    	int i = low;
        int j = high;

        E pivot = list.get(low + (high - low) / 2);
        float pivotDistance = ((GameComponent) pivot).getTransform().getPosition().sub(Transform.getCamera().getPos()).length();

        while (i <= j) {
            while (((GameComponent) list.get(i)).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() > pivotDistance) {
                i++;
            }
            while (((GameComponent) list.get(j)).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() < pivotDistance) {
                j--;
            }

            if (i <= j) {
            	E temp = list.get(i);

            	list.set(i, list.get(j));
            	list.set(j, temp);

                i++;
                j--;
            }
        }

        if (low < j) {
        	sortComponents(list, low, j);
        }
        if (i < high) {
        	sortComponents(list, i, high);
        }
    }
    
    /**
	 * Cleans everything light related.
	 */
    public void clearLights() {m_lights.clear();}

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
	}
	
	/**
	 * Adds a new directional light to the rendering engine.
	 * @param light to add.
	 */
	public void addLight(BaseLight light) {m_lights.add(light);}
	
	/**
	 * Removes a new directional light to the rendering engine.
	 * @param light to remove.
	 */
	public void removeLight(BaseLight light) {m_lights.remove(light);}
	
	/**
	 * Returns the light that is been used.
	 * @return Active light.
	 */
	public BaseLight getActiveLight() {return m_activeLight;}

	/**
	 * Sets a new color for the fog.
	 * @param color of the fog.
	 */
	public void setFogColor(Vector3f color) {AddVector3f("fogColor", color); setClearColor(color);}

	/**
	 * Sets a density to the actual fog.
	 * @param fogDensity to set.
	 */
	public void setFogDensity(float fogDensity) {AddFloat("fogDensity", fogDensity);}

	/**
	 * Sets a new gradient for the fog.
	 * @param fogGradient to set.
	 */
	public void setFogGradient(float fogGradient) {AddFloat("fogGradient", fogGradient);}
	
	/**
	 * Returns the sampler's slot of the texture's unit.
	 * @param samplerName of the texture.
	 * @return Texture's slot.
	 */
	public int getSamplerSlot(String samplerName) { return m_samplerMap.get(samplerName); }

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
	 * Sets a new ambient light.
	 * @return ambient to set.
	 */
	public void setAmbientLight(Vector3f ambient) {AddVector3f("ambient", ambient);}
    
    /**
     * Returns the main camera in game.
     * @return camera.
     */
    public Camera getMainCamera() {return m_mainCamera;}

    /**
     * Sets the main camera of all the game to the rendering
     * engine.
     * @param mainCamera of the game.
     */
	public void setMainCamera(Camera mainCamera) {this.m_mainCamera = mainCamera;}

}
