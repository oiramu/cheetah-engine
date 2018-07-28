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
package engine.rendering.resourceManagement;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glCreateProgram;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class ShaderResource {

	private int 							m_program;
    private int 							m_refCount;
    private HashMap<String, Integer>		m_uniforms;
    private ArrayList<String> 				m_uniformNames;
    private ArrayList<String> 				m_uniformTypes;
    
    /**
     * Constructor for the texture manager.
     */
    public ShaderResource() {
        this.m_program = glCreateProgram();
        this.m_refCount = 1;
        this.m_uniforms = new HashMap<String, Integer>();
        this.m_uniformNames = new ArrayList<String>();
        this.m_uniformTypes = new ArrayList<String>();
        if (m_program == 0) {
            System.err.println("Shader creation failed: Could not find valid memory location in constructor");
            System.exit(1);
        }
    }
    
    /**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {glDeleteBuffers(m_program);}
    
    /**
     * Add a point in the reference counter.
     */
    public void addReferece() {m_refCount++;}
    
    /**
     * Removes a point in the reference counter.
     */
    public boolean removeReference() {m_refCount--; return m_refCount == 0;}

	/**
	 * Gets the program of the texture in object.
	 * @return returns the program.
	 */
	public int getProgram() {return m_program;}

	/**
	 * Returns the uniform's Hash-Map.
	 * @return the uniforms Hash-Map.
	 */
	public HashMap<String, Integer> getUniforms() {return m_uniforms;}

	/**
	 * Returns the uniform names' Array-List.
	 * @return the uniformNames Array-List.
	 */
	public ArrayList<String> getUniformNames() {return m_uniformNames;}

	/**
	 * Returns the uniform types' Array-List.
	 * @return the uniformTypes Array-List.
	 */
	public ArrayList<String> getUniformTypes() {return m_uniformTypes;}

}
