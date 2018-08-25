package engine.rendering;

import java.util.ArrayList;

import engine.core.Matrix4f;
import engine.core.Util;
import engine.core.Vector2f;
import engine.core.Vector3f;

public class TextureFont
{
    private static Mesh meshFont;
    private static Mesh meshCrossHair;
    private String m_text;
    private Material material;
    private Shader shader;
    private Matrix4f fontMatrix;
    private Matrix4f crossHairMatrix;

    public TextureFont(String text)
    {
    	if(m_text == null)m_text = text;
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(); // ArrayList is a variable length Collection class
        ArrayList<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < m_text.length(); i++)
        {
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

        meshFont = new Mesh(vertArray, Util.toIntArray(intArray), false);

        // Crosshair
        vertices = new ArrayList<Vertex>(); // ArrayList is a variable length Collection class
        indices = new ArrayList<Integer>();
        m_text = "+";
        int c = m_text.charAt(0);
        float u = (c % 16) / 16.0f;
        float v = (c / 16) / 16.0f;
        float w = (1 / 16.0f);

        indices.add(vertices.size() + 0); // Starting at vertices.size()
        indices.add(vertices.size() + 1);
        indices.add(vertices.size() + 2);
        indices.add(vertices.size() + 0);
        indices.add(vertices.size() + 2);
        indices.add(vertices.size() + 3);

        vertices.add(new Vertex(new Vector3f( 0      * w, 0, 0), new Vector2f(u    , v + w)));
        vertices.add(new Vertex(new Vector3f( 0      * w, w, 0), new Vector2f(u    , v)));
        vertices.add(new Vertex(new Vector3f((0 + 1) * w, w, 0), new Vector2f(u + w, v)));
        vertices.add(new Vertex(new Vector3f((0 + 1) * w, 0, 0), new Vector2f(u + w, v + w)));

        vertArray = new Vertex[vertices.size()];
        intArray = new Integer[indices.size()];
        vertices.toArray(vertArray);
        indices.toArray(intArray);

        meshCrossHair = new Mesh(vertArray, Util.toIntArray(intArray), false);

        material = new Material(new Texture("font"));
        
        shader = new Shader("text");
        Matrix4f matrixScaleFont = new Matrix4f();
        Matrix4f matrixTranslationFont = new Matrix4f();
        Matrix4f matrixScaleCrossHair = new Matrix4f();
        Matrix4f matrixTranslationCrossHair = new Matrix4f();
        matrixScaleFont.initScale(1.5f, 1.5f, 1.5f);
        matrixScaleCrossHair.initScale(1,1,1);
        matrixTranslationFont.initTranslation((float) (-2.0/3.0f), (float) (-2.0/3.0f), 0);
        matrixTranslationCrossHair.initTranslation(0, 0, 0);
        fontMatrix = matrixScaleFont.mul(matrixTranslationFont);
        crossHairMatrix = matrixScaleCrossHair.mul(matrixTranslationCrossHair);
        shader = new Shader("text");
    }

    public void update()
    {
    }

    public void render()
    {
    	shader.bind();
        shader.updateUniforms(fontMatrix, material);
        meshFont.draw();
        shader.updateUniforms(crossHairMatrix, material);
        meshCrossHair.draw();
    }


}
