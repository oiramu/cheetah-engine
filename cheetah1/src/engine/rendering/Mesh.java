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

import java.util.ArrayList;
import java.util.HashMap;

import engine.core.Util;
import engine.core.Vector3f;
import engine.rendering.meshLoading.IndexedModel;
import engine.rendering.meshLoading.OBJModel;
import engine.rendering.resourceManagement.MeshResource;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Mesh {

	private HashMap <String, MeshResource>	m_loadedModels = new HashMap<String, MeshResource>();
	private String 							m_fileName;
	private MeshResource 					m_resource;

    /**
     * Constructor of a mesh loaded from file.
     * @param fileName of the mesh.
     */
    public Mesh(String fileName) {
    	this.m_fileName = fileName;
    	MeshResource oldResource = m_loadedModels.get(fileName);
    	if(oldResource != null) {
    		m_resource = oldResource;
    		m_resource.addReferece();
    	} else {
	    	loadMesh(fileName);
	    	m_loadedModels.put(fileName, m_resource);
    	}
    }
    
    /**
     * Constructor of the game obect's mesh.
     * @param vertices of the mesh.
     * @param indices of the mesh.
     */
    public Mesh(Vertex[] vertices, int[] indices) {this(vertices, indices, false);}
    
    /**
     * Constructor of the game obect's mesh.
     * @param vertices of the mesh.
     * @param indices of the mesh.
     * @param calcNormals if the mesh uses normals
     * or not.
     */
    public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals) {this(vertices, indices, calcNormals, false);}
    
    /**
     * Constructor of the game obect's mesh.
     * @param vertices of the mesh.
     * @param indices of the mesh.
     * @param calcNormals if the mesh uses normals
     * or not.
     */
    public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals, boolean calcTangents) {
    	m_fileName = "";
        addVertices(vertices, indices, calcNormals, calcTangents);
    }

	/**
	 * Adds to the mesh his vertices and his respective indices
	 * and also sets if do the normals' calculations.
	 * @param vertices Mesh vertices.
	 * @param indices Vertices indices.
	 * @param calcNormals If the mesh calculates normals or not
	 */
    private void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals, boolean calcTangent) {
        if(calcNormals)
        	calcNormals(vertices, indices);
        if(calcTangent)
        	calcTangents(vertices, indices);
        m_resource = new MeshResource(indices.length);

        glBindBuffer(GL_ARRAY_BUFFER, m_resource.getVbo());
        glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_resource.getIbo());
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);
    }
    
    /**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {
    	if(m_resource.removeReference() && !m_fileName.isEmpty())
    		m_loadedModels.remove(m_fileName);
    }

    /**
     * Draws the mesh.
     */
    public void draw() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        glBindBuffer(GL_ARRAY_BUFFER, m_resource.getVbo());
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.SIZE * 4, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.SIZE * 4, 12);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.SIZE * 4, 20);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, Vertex.SIZE * 4, 32);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_resource.getIbo());
        glDrawElements(GL_TRIANGLES, m_resource.getSize(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
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
	 * Calculates the tangent space of the mesh.
	 * @param vertices of the object.
     * @param indices of vertices.
	 */
	public void calcTangents(Vertex[] vertices, int[] indices) {
		
		for(int i = 0; i < indices.length; i+=3) {
			int i0 = indices[i];
			int i1 = indices[i+1];
			int i2 = indices[i+2];

			Vector3f edge1 = vertices[i1].getPos().sub(vertices[i0].getPos());
			Vector3f edge2 = vertices[i2].getPos().sub(vertices[i0].getPos());

			float deltaU1 = vertices[i1].getTexCoord().getX() - vertices[i0].getTexCoord().getX();
			float deltaV1 = vertices[i1].getTexCoord().getY() - vertices[i0].getTexCoord().getY();
			float deltaU2 = vertices[i2].getTexCoord().getX() - vertices[i0].getTexCoord().getX();
			float deltaV2 = vertices[i2].getTexCoord().getY() - vertices[i0].getTexCoord().getY();

			float dividend = (deltaU1*deltaV2 - deltaU2*deltaV1);
			//TODO: The first 0.0f may need to be changed to 1.0f here.
			float f = dividend == 0 ? 0.0f : 1.0f/dividend;

			Vector3f tangent = new Vector3f(0,0,0);
			tangent.setX(f * (deltaV2 * edge1.getX() - deltaV1 * edge2.getX()));
			tangent.setY(f * (deltaV2 * edge1.getY() - deltaV1 * edge2.getY()));
			tangent.setZ(f * (deltaV2 * edge1.getZ() - deltaV1 * edge2.getZ()));

			vertices[i0].setTangent(vertices[i0].getTangent().add(tangent));
			vertices[i1].setTangent(vertices[i1].getTangent().add(tangent));
			vertices[i2].setTangent(vertices[i2].getTangent().add(tangent));
		}

		for(int i = 0; i < vertices.length; i++)
			vertices[i].setTangent(vertices[i].getTangent().normalized());
	}
    
    /**
	 * Loads a OBJ 3D model file named like that.
	 * @param fileName Name of the 3D model file.
	 * @return 3D model.
	 */
    private Mesh loadMesh(String fileName) {
    	
    	String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1];

		if(!ext.equals("obj")) {
			System.err.println("Error: '" + ext + "' file format not supported for mesh data.");
			new Exception().printStackTrace();
			System.exit(1);
		}

		OBJModel test = new OBJModel("./res/models/" + fileName + ".obj");
		IndexedModel model = test.ToIndexedModel();

		ArrayList<Vertex> vertices = new ArrayList<Vertex>();

		for(int i = 0; i < model.GetPositions().size(); i++) {
			vertices.add(new Vertex(model.GetPositions().get(i),
					model.GetTexCoords().get(i),
					model.GetNormals().get(i),
					model.GetTangents().get(i)));
		}

		Vertex[] vertexData = new Vertex[vertices.size()];
		vertices.toArray(vertexData);

		Integer[] indexData = new Integer[model.GetIndices().size()];
		model.GetIndices().toArray(indexData);

		addVertices(vertexData, Util.toIntArray(indexData), false, false);
		
		return this;
    }

}
