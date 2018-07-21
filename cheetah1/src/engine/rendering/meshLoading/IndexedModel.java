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
package engine.rendering.meshLoading;

import java.util.ArrayList;

import engine.core.Vector2f;
import engine.core.Vector3f;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class IndexedModel {
	
	private ArrayList<Vector3f> m_positions;
	private ArrayList<Vector2f> m_texCoords;
	private ArrayList<Vector3f> m_normals;
	private ArrayList<Vector3f> m_tangents;
	private ArrayList<Integer>  m_indices;

	/**
	 * Constructor for the indexed model mesh.
	 */
	public IndexedModel() {
		m_positions = new ArrayList<Vector3f>();
		m_texCoords = new ArrayList<Vector2f>();
		m_normals = new ArrayList<Vector3f>();
		m_tangents = new ArrayList<Vector3f>();
		m_indices = new ArrayList<Integer>();
	}

	/**
     * Calculates the normals of the mesh.
     */
	public void CalcNormals() {
		
		for(int i = 0; i < m_indices.size(); i += 3) {
			int i0 = m_indices.get(i);
			int i1 = m_indices.get(i + 1);
			int i2 = m_indices.get(i + 2);

			Vector3f v1 = m_positions.get(i1).sub(m_positions.get(i0));
			Vector3f v2 = m_positions.get(i2).sub(m_positions.get(i0));

			Vector3f normal = v1.cross(v2).normalized();

			m_normals.get(i0).set(m_normals.get(i0).add(normal));
			m_normals.get(i1).set(m_normals.get(i1).add(normal));
			m_normals.get(i2).set(m_normals.get(i2).add(normal));
		}

		for(int i = 0; i < m_normals.size(); i++)
			m_normals.get(i).set(m_normals.get(i).normalized());
	}

	/**
	 * Calculates the tangent space of the mesh.
	 */
	public void CalcTangents() {
		
		for(int i = 0; i < m_indices.size(); i += 3) {
			int i0 = m_indices.get(i);
			int i1 = m_indices.get(i + 1);
			int i2 = m_indices.get(i + 2);

			Vector3f edge1 = m_positions.get(i1).sub(m_positions.get(i0));
			Vector3f edge2 = m_positions.get(i2).sub(m_positions.get(i0));

			float deltaU1 = m_texCoords.get(i1).getX() - m_texCoords.get(i0).getX();
			float deltaV1 = m_texCoords.get(i1).getY() - m_texCoords.get(i0).getY();
			float deltaU2 = m_texCoords.get(i2).getX() - m_texCoords.get(i0).getX();
			float deltaV2 = m_texCoords.get(i2).getY() - m_texCoords.get(i0).getY();

			float dividend = (deltaU1*deltaV2 - deltaU2*deltaV1);
			//TODO: The first 0.0f may need to be changed to 1.0f here.
			float f = dividend == 0 ? 0.0f : 1.0f/dividend;

			Vector3f tangent = new Vector3f(0,0,0);
			tangent.setX(f * (deltaV2 * edge1.getX() - deltaV1 * edge2.getX()));
			tangent.setY(f * (deltaV2 * edge1.getY() - deltaV1 * edge2.getY()));
			tangent.setZ(f * (deltaV2 * edge1.getZ() - deltaV1 * edge2.getZ()));

			m_tangents.get(i0).set(m_tangents.get(i0).add(tangent));
			m_tangents.get(i1).set(m_tangents.get(i1).add(tangent));
			m_tangents.get(i2).set(m_tangents.get(i2).add(tangent));
		}

		for(int i = 0; i < m_tangents.size(); i++)
			m_tangents.get(i).set(m_tangents.get(i).normalized());
	}

	//Getters
	public ArrayList<Vector3f> GetPositions() { return m_positions; }
	public ArrayList<Vector2f> GetTexCoords() { return m_texCoords; }
	public ArrayList<Vector3f> GetNormals() { return m_normals; }
	public ArrayList<Vector3f> GetTangents() { return m_tangents; }
	public ArrayList<Integer>  GetIndices() { return m_indices; }

}
