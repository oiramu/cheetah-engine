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
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import engine.core.Util;
import engine.core.Vector3f;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class Mesh {

    private int vbo;
    private int ibo;
    private int size;

    /**
     * Constructor of the game obect's mesh.
     */
    public Mesh() {
        vbo = glGenBuffers();
        ibo = glGenBuffers();
        size = 0;
    }
    
    /**
	 * Adds to the mesh his vertices and his respective indices.
	 * @param vertices Mesh vertices.
	 * @param indices Vertices indices.
	 */
    public void addVertices(Vertex[] vertices, int[] indices) {
    	addVertices(vertices, indices, false);
    }

	/**
	 * Adds to the mesh his vertices and his respective indices
	 * and also sets if do the normals' calculations.
	 * @param vertices Mesh vertices.
	 * @param indices Vertices indices.
	 * @param calcNormals If the mesh calculates normals or not
	 */
    public void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals) {
        if(calcNormals) {
        	calcNormals(vertices, indices);
        }
    	size = indices.length;

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);
    }

    /**
     * Draws the mesh.
     */
    public void draw() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glDrawElements(GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
    }
    
    /**
     * Calculates the normals on the mesh.
     * @param vertices of the object.
     * @param indices of vertices.
     */
    private void calcNormals(Vertex[] vertices, int[] indices) {
    	for (int i = 0; i < vertices.length; i+=3) {
			int i0 = indices[i];
			int i1 = indices[i+1];
			int i2 = indices[i+2];
			
			Vector3f v1 = vertices[i1].getPos().sub(vertices[i0].getPos());
			Vector3f v2 = vertices[i2].getPos().sub(vertices[i0].getPos());
			
			Vector3f normal = v1.cross(v2).normalized();
			
			vertices[i0].setNormal(vertices[i0].getNormal().add(normal));
			vertices[i1].setNormal(vertices[i1].getNormal().add(normal));
			vertices[i2].setNormal(vertices[i2].getNormal().add(normal));
		}
    	
    	for (int i = 0; i < vertices.length; i++) {
			vertices[i].setNormal(vertices[i].getNormal().normalized());
		}
    }

}
