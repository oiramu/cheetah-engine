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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import engine.core.Util;
import engine.core.Vector3f;
import engine.rendering.resourceManagement.MeshResource;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Mesh {

	private static HashMap<String, MeshResource> loadedModels = new HashMap<String, MeshResource>();
	private String fileName;
	private MeshResource resource;

    /**
     * Constructor of a mesh loaded from file.
     * @param fileName of the mesh.
     */
    public Mesh(String fileName) {
    	this.fileName = fileName;
    	MeshResource oldResource = loadedModels.get(fileName);
    	if(oldResource != null) {
    		resource = oldResource;
    		resource.addReferece();
    	} else {
	    	loadMesh(fileName);
	    	loadedModels.put(fileName, resource);
    	}
    }
    
    /**
     * Constructor of the game obect's mesh.
     * @param vertices of the mesh.
     * @param indices of the mesh.
     */
    public Mesh(Vertex[] vertices, int[] indices) {
        this(vertices, indices, false);
    }
    
    /**
     * Constructor of the game obect's mesh.
     * @param vertices of the mesh.
     * @param indices of the mesh.
     * @param calcNormals if the mesh uses normals
     * or not.
     */
    public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals) {
    	fileName = "";
        addVertices(vertices, indices, calcNormals);
    }
    
    /**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {
    	if(resource.removeReference() && !fileName.isEmpty())
    		loadedModels.remove(fileName);
    }

	/**
	 * Adds to the mesh his vertices and his respective indices
	 * and also sets if do the normals' calculations.
	 * @param vertices Mesh vertices.
	 * @param indices Vertices indices.
	 * @param calcNormals If the mesh calculates normals or not
	 */
    private void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals) {
        if(calcNormals) {
        	calcNormals(vertices, indices);
        }
        resource = new MeshResource(indices.length);

        glBindBuffer(GL_ARRAY_BUFFER, resource.getVbo());
        glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIbo());
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);
    }

    /**
     * Draws the mesh.
     */
    public void draw() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, resource.getVbo());
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.getIbo());
        glDrawElements(GL_TRIANGLES, resource.getSize(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
    }
    
    /**
     * Calculates the normals of the mesh.
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
    
    /**
	 * Loads a OBJ 3D model file named like that.
	 * @param fileName Name of the 3D model file.
	 * @return 3D model.
	 */
    private Mesh loadMesh(String fileName) {
        String[] splitArray = fileName.split("\\.");
        String ext = splitArray[splitArray.length - 1];

        if (!ext.equals("obj")) {
            System.err.println("Error: File format not supported for mesh data: " + ext);
            new Exception().printStackTrace();
            System.exit(1);
        }

        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Integer> indices = new ArrayList<Integer>();

        BufferedReader meshReader = null;

        try {
            meshReader = new BufferedReader(new FileReader("./res/models/" + fileName + ".obj"));
            String line;

            while ((line = meshReader.readLine()) != null) {
                String[] tokens = line.split(" ");
                tokens = Util.removeEmptyStrings(tokens);

                if (tokens.length == 0 || tokens[0].equals("#")) {
                    continue;
                } else if (tokens[0].equals("v")) {
                    vertices.add(new Vertex(new Vector3f(Float.valueOf(tokens[1]),
                            Float.valueOf(tokens[2]),
                            Float.valueOf(tokens[3]))));
                } else if (tokens[0].equals("f")) {
                    indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
                    indices.add(Integer.parseInt(tokens[2].split("/")[0]) - 1);
                    indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);

                    if (tokens.length > 4) {
                        indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
                        indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
                        indices.add(Integer.parseInt(tokens[4].split("/")[0]) - 1);
                    }
                }
            }

            meshReader.close();

            Vertex[] vertexData = new Vertex[vertices.size()];
            vertices.toArray(vertexData);

            Integer[] indexData = new Integer[indices.size()];
            indices.toArray(indexData);

            addVertices(vertexData, Util.toIntArray(indexData), true);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

}
