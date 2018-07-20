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

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class ShaderResource {

	private int program;
    private int refCount;
    
    /**
     * Constructor for the texture manager.
     */
    public ShaderResource() {
        this.program = glCreateProgram();
        this.refCount = 1;
        if (program == 0) {
            System.err.println("Shader creation failed: Could not find valid memory location in constructor");
            System.exit(1);
        }
    }
    
    /**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {
    	glDeleteBuffers(program);
    }
    
    /**
     * Add a point in the reference counter.
     */
    public void addReferece() {refCount++;}
    
    /**
     * Removes a point in the reference counter.
     */
    public boolean removeReference() {refCount--; return refCount == 0;}

	/**
	 * Gets the id of the texture in object.
	 * @return returns the id.
	 */
	public int getId() {return program;}

}
