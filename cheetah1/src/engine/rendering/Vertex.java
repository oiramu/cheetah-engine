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

import engine.core.Vector2f;
import engine.core.Vector3f;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Vertex {

    public static final int SIZE = 11;

    private Vector3f m_pos;
    private Vector2f m_texCoord;
    private Vector3f m_normal;
    private Vector3f m_tangent;
    
    /**
     * Constructor of the vertex object (stores his data into a
     * 3 by 3 float vector).
     * @param pos Vertices' position.
     */
    public Vertex(Vector3f pos) {
		this(pos, new Vector2f(0,0));
	}
	
    /**
     * Constructor of the vertex with his positions vector data and
     * his texture vector coordinates.
     * @param pos Vertices' position.
     * @param texCoord Texture's coordinates.
     */
	public Vertex(Vector3f pos, Vector2f texCoord) {
		this(pos, texCoord, new Vector3f(0,0,0));
	}
	
	/**
     * Constructor of the vertex with his positions vector data and
     * his texture vector coordinates and with he's normals.
     * @param pos Vertices' position.
     * @param texCoord Texture's coordinates.
     * @param normal of the vertex mesh.
     */
	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal) {
		this(pos, texCoord, normal, new Vector3f(0,0,0));
	}

	/**
     * Constructor of the vertex with his positions vector data and
     * his texture vector coordinates, with he's normals and
     * tangent space.
     * @param pos Vertices' position.
     * @param texCoord Texture's coordinates.
     * @param normal of the vertex mesh.
     * @param tangent space of the mesh.
     */
	public Vertex(Vector3f pos, Vector2f texCoord, Vector3f normal, Vector3f tangent) {
		this.m_pos = pos;
		this.m_texCoord = texCoord;
		this.m_normal = normal;
		this.m_tangent = tangent;
	}

	/**
	 * Returns the tangent space of the vertex.
	 * @return Tangent space.
	 */
	public Vector3f getTangent() { return m_tangent; }

	/**
	 * Sets a new tangent space for the vertex.
	 * @param tangent to set.
	 */
	public void setTangent(Vector3f tangent) { this.m_tangent = tangent; }

    /**
     * Returns the vertex position.
     * @return Position.
     */
    public Vector3f getPos() { return m_pos; }
    
    /**
     * Sets a new position for the vertex.
     * @param pos New position.
     */
    public void setPos(Vector3f pos) { this.m_pos = pos; }

    /**
     * Returns the texture' coordinates.
     * @return Coordinates.
     */
    public Vector2f getTexCoord() { return m_texCoord; }

    /**
     * Sets a new coordinates for the texture.
     * @param texCoord New coordinates.
     */
    public void setTexCoord(Vector2f texCoord) { this.m_texCoord = texCoord; }

    /**
     * Returns the normals' coordinates.
     * @return Normals.
     */
	public Vector3f getNormal() { return m_normal;}

    /**
     * Sets a new normals for the vertex.
     * @param normal New normals.
     */
	public void setNormal(Vector3f normal) { this.m_normal = normal; }
	
}