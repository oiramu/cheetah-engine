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

	private int program;
    private int refCount;
    private HashMap<String, Integer>		uniforms;
    private ArrayList<String> 				uniformNames;
    private ArrayList<String> 				uniformTypes;
    
    /**
     * Constructor for the texture manager.
     */
    public ShaderResource() {
        this.program = glCreateProgram();
        this.refCount = 1;
        uniforms = new HashMap<String, Integer>();
        uniformNames = new ArrayList<String>();
        uniformTypes = new ArrayList<String>();
        if (program == 0) {
            System.err.println("Shader creation failed: Could not find valid memory location in constructor");
            System.exit(1);
        }
    }
    
    /**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {glDeleteBuffers(program);}
    
    /**
     * Add a point in the reference counter.
     */
    public void addReferece() {refCount++;}
    
    /**
     * Removes a point in the reference counter.
     */
    public boolean removeReference() {refCount--; return refCount == 0;}

	/**
	 * Gets the program of the texture in object.
	 * @return returns the program.
	 */
	public int getProgram() {return program;}

	/**
	 * Returns the uniform's Hash-Map.
	 * @return the uniforms Hash-Map.
	 */
	public HashMap<String, Integer> getUniforms() {return uniforms;}

	/**
	 * Returns the uniform names' Array-List.
	 * @return the uniformNames Array-List.
	 */
	public ArrayList<String> getUniformNames() {return uniformNames;}

	/**
	 * Returns the uniform types' Array-List.
	 * @return the uniformTypes Array-List.
	 */
	public ArrayList<String> getUniformTypes() {return uniformTypes;}

}
