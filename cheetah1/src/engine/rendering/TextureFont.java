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

import java.util.ArrayList;

import engine.core.Matrix4f;
import engine.core.Util;
import engine.core.Vector2f;
import engine.core.Vector3f;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class TextureFont {
	
    private Mesh		m_mesh;
    private Shader		m_shader;
    private String		m_text;
    private Material	m_material;
    private Matrix4f 	m_fontMatrix;
    private Vector2f 	m_position;
    private Vector2f 	m_scale;
    
    /**
     * Constructor for a HUD based GUI texture.
     * @param material of the text.
     * @param position of the text.
     * @param scale of the text.
     */
    public TextureFont(Material material, Vector2f position, Vector2f scale) {
    	if(m_position == null) m_position = position;
    	if(m_scale == null) m_scale = scale;
        final float sizeY = 1.0f;
        final float sizeX = (float) ((double) sizeY / (sizeY * 2.0));

        final float offsetX = 0.05f;
        final float offsetY = 0.01f;

        final float texMinX = -offsetX;
        final float texMaxX = -1 - offsetX;
        final float texMinY = -offsetY;
        final float texMaxY = 1 - offsetY;

        Vertex[] verts = new Vertex[]{new Vertex(new Vector3f(-sizeX, 0, 0), new Vector2f(texMaxX, texMaxY)),
            new Vertex(new Vector3f(-sizeX, sizeY, 0), new Vector2f(texMaxX, texMinY)),
            new Vertex(new Vector3f(sizeX, sizeY, 0), new Vector2f(texMinX, texMinY)),
            new Vertex(new Vector3f(sizeX, 0, 0), new Vector2f(texMinX, texMaxY))};

        int[] indices = new int[]{0, 1, 2,
                                    0, 2, 3};

        if(m_mesh == null)
        	m_mesh = new Mesh(verts, indices);
        if(m_shader == null)
        	m_shader = new Shader("hud");
        
        Matrix4f matrixScaleFont = new Matrix4f();
        Matrix4f matrixTranslationFont = new Matrix4f();
        matrixScaleFont.initScale(scale.getX(), scale.getY(), 1.5f);
        matrixTranslationFont.initTranslation(position.getX(), position.getY(), 0);
        m_fontMatrix = matrixScaleFont.mul(matrixTranslationFont);
    }
    
    /**
     * Constructor for a font based GUI texture.
     * @param text to add.
     * @param position of the text.
     * @param scale of the text.
     */
    public TextureFont(String text, Vector2f position, Vector2f scale) {  	
    	this(text, new Material(new Texture("font")), position, scale);
    }

    /**
     * Constructor for a font based GUI texture.
     * @param text to add.
     * @param material of the text.
     * @param position of the text.
     * @param scale of the text.
     */
    public TextureFont(String text, Material material, Vector2f position, Vector2f scale) {
    	
    	if(m_text == null) m_text = text;
    	if(m_position == null) m_position = position;
    	if(m_scale == null) m_scale = scale;
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(); // ArrayList is a variable length Collection class
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < m_text.length(); i++) {
            int c = m_text.charAt(i);
            float u = (c % 16) / 16.0f;
            float v = (c / 16) / 16.0f;
            float w = (1 / 16.0f);

            indices.add(vertices.size() + 0); // Starting at vertices.size()
            indices.add(vertices.size() + 1);
            indices.add(vertices.size() + 2);
            indices.add(vertices.size() + 0);
            indices.add(vertices.size() + 2);
            indices.add(vertices.size() + 3);

            vertices.add(new Vertex(new Vector3f( i      * w, 0, 0), new Vector2f(u    , v + w)));
            vertices.add(new Vertex(new Vector3f( i      * w, w, 0), new Vector2f(u    , v)));
            vertices.add(new Vertex(new Vector3f((i + 1) * w, w, 0), new Vector2f(u + w, v)));
            vertices.add(new Vertex(new Vector3f((i + 1) * w, 0, 0), new Vector2f(u + w, v + w)));
        }

        Vertex[] vertArray = new Vertex[vertices.size()];
        Integer[] intArray = new Integer[indices.size()];
        vertices.toArray(vertArray);
        indices.toArray(intArray);

        m_material = material;
        
        if(m_mesh == null)
        	m_mesh = new Mesh(vertArray, Util.toIntArray(intArray));
        if(m_shader == null)
        	m_shader = new Shader("hud");
        
        Matrix4f matrixScaleFont = new Matrix4f();
        Matrix4f matrixTranslationFont = new Matrix4f();
        matrixScaleFont.initScale(scale.getX(), scale.getY(), 1.5f);
        matrixTranslationFont.initTranslation(position.getX(), position.getY(), 0);
        m_fontMatrix = matrixScaleFont.mul(matrixTranslationFont);
    }
    
    /**
     * Method that renders the text.
     * @param renderingEngine to use
     */
    public void render(RenderingEngine renderingEngine) {
    	m_shader.bind();
    	m_shader.updateUniforms(m_fontMatrix, m_material);
    	m_mesh.draw();
    }
    
    /**
     * Method that updates the text.
     * @param text to change.
     */
    public void setText(String text) {   	
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(); // ArrayList is a variable length Collection class
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i);
            float u = (c % 16) / 16.0f;
            float v = (c / 16) / 16.0f;
            float w = (1 / 16.0f);

            indices.add(vertices.size() + 0); // Starting at vertices.size()
            indices.add(vertices.size() + 1);
            indices.add(vertices.size() + 2);
            indices.add(vertices.size() + 0);
            indices.add(vertices.size() + 2);
            indices.add(vertices.size() + 3);

            vertices.add(new Vertex(new Vector3f( i      * w, 0, 0), new Vector2f(u    , v + w)));
            vertices.add(new Vertex(new Vector3f( i      * w, w, 0), new Vector2f(u    , v)));
            vertices.add(new Vertex(new Vector3f((i + 1) * w, w, 0), new Vector2f(u + w, v)));
            vertices.add(new Vertex(new Vector3f((i + 1) * w, 0, 0), new Vector2f(u + w, v + w)));
        }
        
        Vertex[] vertArray = new Vertex[vertices.size()];
        Integer[] intArray = new Integer[indices.size()];
        vertices.toArray(vertArray);
        indices.toArray(intArray);
        
        m_mesh = new Mesh(vertArray, Util.toIntArray(intArray));

    }

	/**
	 * Method that updates the mesh' font.
	 * @param meshFont to set
	 */
	public void setMeshFont(Mesh meshFont) {this.m_mesh = meshFont;}

	/**
	 * Method that updates the material.
	 * @param material to set
	 */
	public void setMaterial(Material material) {this.m_material = material;}

	/**
	 * Method that updates the position.
	 * @param position to set
	 */
	public void setPosition(Vector2f position) {this.m_position = position;}

	/**
	 * Method that updates the scale.
	 * @param scale to set
	 */
	public void setScale(Vector2f scale) {this.m_scale = scale;}

}
