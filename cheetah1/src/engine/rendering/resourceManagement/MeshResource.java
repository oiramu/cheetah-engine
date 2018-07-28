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

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class MeshResource {

	private int m_vbo;
    private int m_ibo;
    private int m_size;
    private int m_refCount;
    
    /**
     * Constructor for the mesh manager.
     */
    public MeshResource(int size) {
    	m_vbo = glGenBuffers();
        m_ibo = glGenBuffers();
        this.m_size = size;
        this.m_refCount = 1;
    }
    
    /**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {
    	glDeleteBuffers(m_vbo);
    	glDeleteBuffers(m_ibo);
    }
    
    /**
     * Add a point in the reference counter.
     */
    public void addReferece() {m_refCount++;}
    
    /**
     * Removes a point in the reference counter.
     */
    public boolean removeReference() {m_refCount--; return m_refCount == 0;}

	/**
	 * Gets the vertex buffer object.
	 * @return returns the vbo.
	 */
	public int getVbo() {return m_vbo;}

	/**
	 * Gets the index buffer object.
	 * @return returns the ibo.
	 */
	public int getIbo() {return m_ibo;}
	
	/**
	 * Gets the size of the mesh.
	 * @return returns the size.
	 */
	public int getSize() {return m_size;}

}
