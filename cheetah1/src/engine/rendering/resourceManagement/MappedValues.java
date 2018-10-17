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

import java.util.HashMap;

import engine.core.Vector3f;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public abstract class MappedValues {
	
	private HashMap<String, Vector3f> m_vector3fHashMap;
	private HashMap<String, Float>    m_floatHashMap;

	/**
	 * Constructor for the values of the mapping
	 * information.
	 */
	public MappedValues() {
		m_vector3fHashMap = new HashMap<String, Vector3f>();
		m_floatHashMap = new HashMap<String, Float>();
	}
	
	/**
	 * Adds some vector data to the hash-map.
	 * @param name of the vector.
	 * @param vector3f to add.
	 */
	public void AddVector3f(String name, Vector3f vector3f) { m_vector3fHashMap.put(name, vector3f); }
	
	/**
	 * Adds some float data to the hash-map.
	 * @param name of the float.
	 * @param vector3f to add.
	 */
	public void AddFloat(String name, float floatValue) { m_floatHashMap.put(name, floatValue); }

	/**
	 * Returns the vector data allocated on the name
	 * to search for.
	 * @param name to search.
	 * @return vector data.
	 */
	public Vector3f GetVector3f(String name) {
		Vector3f result = m_vector3fHashMap.get(name);
		if(result != null)
			return result;

		return new Vector3f(0,0,0);
	}

	/**
	 * Returns the float data allocated on the name
	 * to search for.
	 * @param name to search.
	 * @return float data.
	 */
	public float GetFloat(String name) {
		Float result = m_floatHashMap.get(name);
		if(result != null)
			return result;

		return 0;
	}
}
