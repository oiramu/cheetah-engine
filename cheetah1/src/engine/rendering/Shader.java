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

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import engine.core.Matrix4f;
import engine.core.Util;
import engine.core.Vector3f;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class Shader {

    private int program;
    private RenderingEngine renderingEngine;
    private HashMap<String, Integer> uniforms;
    public final String BASIC = "BASIC/";
    public final String PHONG = "PHONG/";
    public final String FORWARD = "FORWARD/";

    /**
     * Constructor of the shader structure with the program
     * and all of he's uniforms.
     */
    public Shader() {
        program = glCreateProgram();
        uniforms = new HashMap<String, Integer>();

        if (program == 0) {
            System.err.println("Shader creation failed: Could not find valid memory location in constructor");
            System.exit(1);
        }
    }

    /**
     * Binds the GLSL program(s) to compile.
     */
    public void bind() {
        glUseProgram(program);
    }

    /**
     * Updates all the uniforms of the shading program.
     * @param worldMatrix World matrix data.
     * @param projectedMatrix Projection matrix data.
     * @param material Material of the object.
     */
    public void updateUniforms(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material material) {

    }

    /**
     * Adds a new uniform from the GLSL code for java and then OpenGL know
     * What to do.
     * @param uniform The uniform in the GLSL code.
     */
    public void addUniform(String uniform) {
        int uniformLocation = glGetUniformLocation(program, uniform);

        if (uniformLocation == 0xFFFFFFFF) {
            System.err.println("Error: Could not find uniform: " + uniform);
            new Exception().printStackTrace();
            System.exit(1);
        }

        uniforms.put(uniform, uniformLocation);
    }
    
    /**
     * Adds a new vertex shader to the shading program
     * from a file.
     * @param text Vertex shader's resource path.
     */
    public void addVertexShaderFromFile(String text) {
    	addVertexShader(loadShader(text));
    }

    /**
     * Adds a new geometry shader to the shading program
     * from a file.
     * @param text Geometry shader's resource path.
     */
    public void addGeometryShaderFromFile(String text) {
    	addGeometryShader(loadShader(text));
    }

    /**
     * Adds a new fragment shader to the shading program
     * from a file.
     * @param text Fragment shader's resource path.
     */
    public void addFragmentShaderFromFile(String text) {
    	addFragmentShader(loadShader(text));
    }

    /**
     * Adds a new vertex shader to the shading program.
     * @param text Vertex shader's resource path.
     */
    private void addVertexShader(String text) {
        addProgram(text, GL_VERTEX_SHADER);
    }

    /**
     * Adds a new geometry shader to the shading program.
     * @param text Geometry shader's resource path.
     */
    private void addGeometryShader(String text) {
        addProgram(text, GL_GEOMETRY_SHADER);
    }

    /**
     * Adds a new fragment shader to the shading program.
     * @param text Fragment shader's resource path.
     */
    private void addFragmentShader(String text) {
        addProgram(text, GL_FRAGMENT_SHADER);
    }
    
    /**
     * If the system ain't compatible with OpenGL 3 and above
     * Use this attribute binding attribute with a fixed
     * location.
     * @param attributeName of the uniform in GLSL code.
     * @param location of the uniform.
     */
    public void setAttribLocation(String attributeName, int location) {
    	glBindAttribLocation(program, location, attributeName);
    }

    /**
     * Method that sets all the shading programs and then
     * compile everything to work as it should.
     */
    @SuppressWarnings("deprecation")
	public void compileShader() {
        glLinkProgram(program);

        if (glGetProgram(program, GL_LINK_STATUS) == 0) {
            System.err.println(glGetProgramInfoLog(program, 1024));
            System.exit(1);
        }

        glValidateProgram(program);

        if (glGetProgram(program, GL_VALIDATE_STATUS) == 0) {
            System.err.println(glGetProgramInfoLog(program, 1024));
            System.exit(1);
        }
    }

    /**
     * Adds a new shader program for the OpenGL programmable pipeline
     * (GLSL).
     * @param text Shader program's resource path.
     * @param type of shader to set.
     */
    @SuppressWarnings("deprecation")
	private void addProgram(String text, int type) {
        int shader = glCreateShader(type);

        if (shader == 0) {
            System.err.println("Shader creation failed: Could not find valid memory location when adding shader");
            System.exit(1);
        }

        glShaderSource(shader, text);
        glCompileShader(shader);

        if (glGetShader(shader, GL_COMPILE_STATUS) == 0) {
            System.err.println(glGetShaderInfoLog(shader, 1024));
            System.exit(1);
        }

        glAttachShader(program, shader);
    }
    
    /**
	 * Loads a GLSL shader file named like that.
	 * @param fileName Name of the shader file.
	 * @return Shader.
	 */
    private static String loadShader(String fileName) {
        StringBuilder shaderSource = new StringBuilder();
        BufferedReader shaderReader = null;

        try {
            shaderReader = new BufferedReader(new FileReader("./res/shaders/" + fileName + ".glsl"));
            String line;
            final String INCLUDE_DIRECTIVE = "#include";

            while ((line = shaderReader.readLine()) != null) {
            	if(line.startsWith(INCLUDE_DIRECTIVE)) {
            		shaderSource.append(loadShader(line.substring(INCLUDE_DIRECTIVE.length()+2, line.length()-1)));
            	} else
            		shaderSource.append(line).append("\n");
            }

            shaderReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return shaderSource.toString();
    }

    /**
     * Sets a new integer uniform from the GLSL code for java and then
     * OpenGL know what to do.
     * @param uniformName The uniform name in GLSL.
     * @param value Integer value of the uniform.
     */
    public void setUniformi(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    /**
     * Sets a new float uniform from the GLSL code for java and then
     * OpenGL know what to do.
     * @param uniformName The uniform name in GLSL.
     * @param value Float value of the uniform.
     */
    public void setUniformf(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    /**
     * Sets a new uniform to vector from the GLSL code for java and then
     * OpenGL know what to do.
     * @param uniformName The uniform name in GLSL.
     * @param value Vector values of the uniform.
     */
    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.getX(), value.getY(), value.getZ());
    }

    /**
     * Sets a new uniform to matrix from the GLSL code for java and then
     * OpenGL know what to do.
     * @param uniformName The uniform name in GLSL.
     * @param value Matrix values of the uniform.
     */
    public void setUniform(String uniformName, Matrix4f value) {
        glUniformMatrix4(uniforms.get(uniformName), true, Util.createFlippedBuffer(value));
    }
    
    /**
     * Sets the rendering engine of the shader.
     * @param renderingEngine for shader.
     */
    public void setRenderingEngine(RenderingEngine renderingEngine) {
		this.renderingEngine = renderingEngine;
	}

    /**
     * Returns the rendering engine of the shader.
     * @return rendering engine.
     */
	public RenderingEngine getRenderingEngine() {
		return renderingEngine;
	}

}
