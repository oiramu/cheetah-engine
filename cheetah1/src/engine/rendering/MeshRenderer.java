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
package engine.rendering;

import engine.core.Transform;

/**
 *
 * @author Carlos Rodriguez.
 * @version 1.0
 * @since 2018
 */
public class MeshRenderer {
	
	private Mesh mesh;
	private Transform transform;
	private Material material;
	private Shader shader = PhongShader.getInstance();
	
	/**
	 * Renderer of a mesh with single material to render.
	 * @param mesh to render.
	 * @param transform of the mesh.
	 */
	public MeshRenderer(Mesh mesh, Transform transform) {
		this.mesh = mesh;
		this.transform = transform;
	}
	
	/**
	 * Renderer of a mesh with more than one possible material
	 * To render.
	 * @param mesh to render.
	 * @param transform of the mesh.
	 * @param material of the mesh.
	 */
	public MeshRenderer(Mesh mesh, Transform transform, Material material) {
		this.mesh = mesh;
		this.transform = transform;
		this.material = material;		
	}
	
	/**
	 * Render method of the mesh.
	 */
	public void render() {
		render(material);
	}
	
	/**
	 * Render method of the mesh.
	 * @param material to render.
	 */
	public void render(Material material) {
		
		shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getPerspectiveTransformation(), material);
        mesh.draw();
	}

}
