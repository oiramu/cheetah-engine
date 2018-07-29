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
import java.util.ArrayList;
import java.util.HashMap;

import engine.components.BaseLight;
import engine.components.DirectionalLight;
import engine.components.PointLight;
import engine.components.SpotLight;
import engine.core.Matrix4f;
import engine.core.Transform;
import engine.core.Util;
import engine.core.Vector3f;
import engine.rendering.resourceManagement.ShaderResource;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Shader {

	private HashMap<String, ShaderResource> m_loadedShaders = new HashMap<String, ShaderResource>();
    private RenderingEngine 				m_renderingEngine;
    private ShaderResource					m_resource;
    private String							m_fileName;
    public final String 					BASIC = "BASIC/";
    public final String 					PHONG = "PHONG/";
    public final String 					FORWARD = "FORWARD/";

    /**
     * Constructor of the shader structure with the program
     * and all of he's uniforms.
     */
    public Shader(String fileName) {
    	this.m_fileName = fileName;
    	ShaderResource oldResource = m_loadedShaders.get(fileName);
    	if(oldResource != null) {
    		m_resource = oldResource;
    		m_resource.addReferece();
    	} else {
    		m_resource = new ShaderResource();
            
            String vertexShaderText = loadShader(FORWARD + fileName + "-vs");
    		String fragmentShaderText = loadShader(FORWARD + fileName + "-fs");
    		
    		addVertexShader(vertexShaderText);
            addFragmentShader(fragmentShaderText);

    		addAllAttributes(vertexShaderText);

    		compileShader();
    		
    		addAllUniforms(vertexShaderText);
    		addAllUniforms(fragmentShaderText);
    	}
    }
    
    /**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {
    	if(m_resource.removeReference() && !m_fileName.isEmpty())
    		m_loadedShaders.remove(m_fileName);
    }

    /**
     * Binds the GLSL program(s) to compile.
     */
    public void bind() {
        glUseProgram(m_resource.getProgram());
    }

    /**
     * Updates all the uniforms of the shading program.
     * @param transform of the object.
     * @param material Material of the object.
     */
    @SuppressWarnings("static-access")
	public void updateUniforms(Transform transform, Material material) {
    	Matrix4f worldMatrix = transform.getTransformation();
		Matrix4f MVPMatrix = transform.getPerspectiveTransformation();
    	for(int i = 0; i < m_resource.getUniformNames().size(); i++) {
    		String uniformName = m_resource.getUniformNames().get(i);
    		String uniformType = m_resource.getUniformTypes().get(i);
    		
    		if(uniformName.startsWith("T_")) {
    			if(uniformName.equals("T_MVP")) 
    				setUniform(uniformName, MVPMatrix);
    			else if(uniformName.equals("T_model"))
    				setUniform(uniformName, worldMatrix);
    			else
    				throw new IllegalArgumentException(uniformName + " is not a valid component of Transform.");
    		} else if(uniformName.startsWith("R_")) {
    			if(uniformType.equals("sampler2D")) {
    				if(uniformName.equals("R_diffuse")) {
	    				material.getDiffuse().bind(0);
	    				setUniformi(uniformName, 0);
    				}else if(uniformName.equals("R_normalMap")) {
	    				material.getNormalMap().bind(1);
	    				setUniformi(uniformName, 1);
    				}else if(uniformName.equals("R_dispMap")) {
	    				material.getDispMap().bind(2);
	    				setUniformi(uniformName, 2);
    				}
    			}else if(uniformType.equals("vec3")) {
	    			if(uniformName.equals("R_eyePos")) 
	    				setUniform(uniformName, getRenderingEngine().getMainCamera().getPos());
	    			else if(uniformName.equals("R_ambient"))
	    				setUniform(uniformName, getRenderingEngine().getAmbientLight());
	    			else if(uniformName.equals("R_fogColor"))
	    				setUniform(uniformName, getRenderingEngine().getFogColor());
    			}else if(uniformType.equals("float")) {
	    			if(uniformName.equals("R_fogDensity"))
	    				setUniformf(uniformName, getRenderingEngine().getFogDensity());
	    			else if(uniformName.equals("R_fogGradient"))
	    				setUniformf(uniformName, getRenderingEngine().getFogGradient());
    			}else if(uniformType.equals("DirectionalLight")) {
	    			if(uniformName.equals("R_directionalLight"))
	    				setUniformDirectionalLight(uniformName, (DirectionalLight)getRenderingEngine().getActiveLight());
    			}else if(uniformType.equals("PointLight")) {
    				if(uniformName.equals("R_pointLight"))
    					setUniformPointLight(uniformName, (PointLight)getRenderingEngine().getActiveLight());
    			}else if(uniformType.equals("SpotLight")){
    				if(uniformName.equals("R_spotLight"))
    					setUniformSpotLight(uniformName, (SpotLight)getRenderingEngine().getActiveLight());
    			}else
    				throw new IllegalArgumentException(uniformName + " is not a valid component of Rendering Engine.");
    		}else if(uniformName.startsWith("M_")) {
    			if(uniformName.equals("M_specularIntensity")) 
    				setUniformf(uniformName, material.getSpecularIntensity());
    			else if(uniformName.equals("M_specularPower"))
    				setUniformf(uniformName, material.getSpecularPower());
    			else if(uniformName.equals("M_dispMapScale"))
    				setUniformf(uniformName, material.getDispMapScale());
    			else if(uniformName.equals("M_dispMapBias"))
    				setUniformf(uniformName, material.getDispMapBias());
    			else
    				throw new IllegalArgumentException(uniformName + " is not a valid component of Material.");
    		}
    	}
    }
    
    /**
     * Add all of the attributes in the GLSL class.
     * into the Java shader class.
     * @param shaderText Shader name.
     */
    private void addAllAttributes(String shaderText) {
    	final String ATTRIBUTE_KEYWORD = "attribute";
    	int attributeStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD);
    	int attribNumber = 0;
    	while(attributeStartLocation != -1) {
			if(!(attributeStartLocation != 0 && (Character.isWhitespace(shaderText.charAt(attributeStartLocation - 1)) 
					|| shaderText.charAt(attributeStartLocation - 1) == ';') 
					&& Character.isWhitespace(shaderText.charAt(attributeStartLocation + ATTRIBUTE_KEYWORD.length()))))
				continue;
    		int begin = attributeStartLocation + ATTRIBUTE_KEYWORD.length() + 1;
    		int end = shaderText.indexOf(";", begin);
    		
    		String attributeLine = shaderText.substring(begin, end).trim();
    		String attributeName = attributeLine.substring(attributeLine.indexOf(' ') + 1, attributeLine.length()).trim();
    		
    		setAttribLocation(attributeName, attribNumber);
    		attribNumber++;
    		
    		attributeStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD, attributeStartLocation + ATTRIBUTE_KEYWORD.length());
    	}
    }
    
    /**
     * Emulation of a GLSL structure in Java.
     * @author Carlos Rodriguez
     * @version 1.0
     * @since 2018
     */
    private class GLSLStruct {
		public String name;
		public String type;
	}
    
    /**
     * Finds the structure uniforms in the GLSL code
     * and send the data into a more Java like code.
     * @param shaderText Shader name
     * @return Result of the structure.
     */
    private HashMap<String, ArrayList<GLSLStruct>> findUniformStructs(String shaderText) {
    	
		HashMap<String, ArrayList<GLSLStruct>> result = new HashMap<String, ArrayList<GLSLStruct>>();

		final String STRUCT_KEYWORD = "struct";
		int structStartLocation = shaderText.indexOf(STRUCT_KEYWORD);
		while(structStartLocation != -1) {
			if(!(structStartLocation != 0 && (Character.isWhitespace(shaderText.charAt(structStartLocation - 1)) 
					|| shaderText.charAt(structStartLocation - 1) == ';') 
					&& Character.isWhitespace(shaderText.charAt(structStartLocation + STRUCT_KEYWORD.length())))) 
				continue;
			int nameBegin = structStartLocation + STRUCT_KEYWORD.length() + 1;
			int braceBegin = shaderText.indexOf("{", nameBegin);
			int braceEnd = shaderText.indexOf("}", braceBegin);

			String structName = shaderText.substring(nameBegin, braceBegin).trim();
			ArrayList<GLSLStruct> glslStructs = new ArrayList<GLSLStruct>();

			int componentSemicolonPos = shaderText.indexOf(";", braceBegin);
			while(componentSemicolonPos != -1 && componentSemicolonPos < braceEnd) {
				int componentNameStart = componentSemicolonPos;

				while(!Character.isWhitespace(shaderText.charAt(componentNameStart - 1)))
					componentNameStart--;

				int componentTypeEnd = componentNameStart - 1;
				int componentTypeStart = componentTypeEnd;

				while(!Character.isWhitespace(shaderText.charAt(componentTypeStart - 1)))
					componentTypeStart--;

				String componentName = shaderText.substring(componentNameStart, componentSemicolonPos);
				String componentType = shaderText.substring(componentTypeStart, componentTypeEnd);

				GLSLStruct glslStruct = new GLSLStruct();
				glslStruct.name = componentName;
				glslStruct.type = componentType;

				glslStructs.add(glslStruct);

				componentSemicolonPos = shaderText.indexOf(";", componentSemicolonPos + 1);
			}

			result.put(structName, glslStructs);

			structStartLocation = shaderText.indexOf(STRUCT_KEYWORD, structStartLocation + STRUCT_KEYWORD.length());
		}

		return result;
	}
    
    /**
     * Add all of the uniforms in the GLSL class.
     * into the Java shader class.
     * @param shaderText Shader name.
     */
    private void addAllUniforms(String shaderText) {
    	HashMap<String, ArrayList<GLSLStruct>> structs = findUniformStructs(shaderText);

		final String UNIFORM_KEYWORD = "uniform";
		int uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD);
		while(uniformStartLocation != -1) {
			if(!(uniformStartLocation != 0 && (Character.isWhitespace(shaderText.charAt(uniformStartLocation - 1)) 
					|| shaderText.charAt(uniformStartLocation - 1) == ';') 
					&& Character.isWhitespace(shaderText.charAt(uniformStartLocation + UNIFORM_KEYWORD.length()))))
				continue;
			int begin = uniformStartLocation + UNIFORM_KEYWORD.length() + 1;
			int end = shaderText.indexOf(";", begin);

			String uniformLine = shaderText.substring(begin, end).trim();

			int whiteSpacePos = uniformLine.indexOf(' ');
			String uniformName = uniformLine.substring(whiteSpacePos + 1, uniformLine.length()).trim();
			String uniformType = uniformLine.substring(0, whiteSpacePos).trim();
			
			m_resource.getUniformNames().add(uniformName);
			m_resource.getUniformTypes().add(uniformType);
			addUniform(uniformName, uniformType, structs);

			uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation + UNIFORM_KEYWORD.length());
		}
    }
    
    /**
     * Add uniforms of the GLSL class that uses structures.
     * into the Java shader class.
     * @param uniformName
     * @param uniformType
     * @param structs
     */
    private void addUniform(String uniformName, String uniformType, HashMap<String, ArrayList<GLSLStruct>> structs) {
		boolean addThis = true;
		ArrayList<GLSLStruct> structComponents = structs.get(uniformType);

		if(structComponents != null) {
			addThis = false;
			for(GLSLStruct struct : structComponents) {
				addUniform(uniformName + "." + struct.name, struct.type, structs);
			}
		}

		if(!addThis)
			return;
		int uniformLocation = glGetUniformLocation(m_resource.getProgram(), uniformName);

        if (uniformLocation == 0xFFFFFFFF) {
            System.err.println("Error: Could not find uniform: " + uniformName);
            new Exception().printStackTrace();
            System.exit(1);
        }

        m_resource.getUniforms().put(uniformName, uniformLocation);
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
    @SuppressWarnings("unused")
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
    private void setAttribLocation(String attributeName, int location) {
    	glBindAttribLocation(m_resource.getProgram(), location, attributeName);
    }

    /**
     * Method that sets all the shading programs and then
     * compile everything to work as it should.
     */
    @SuppressWarnings("deprecation")
	private void compileShader() {
        glLinkProgram(m_resource.getProgram());

        if (glGetProgram(m_resource.getProgram(), GL_LINK_STATUS) == 0) {
            System.err.println(glGetProgramInfoLog(m_resource.getProgram(), 1024));
            System.exit(1);
        }

        glValidateProgram(m_resource.getProgram());

        if (glGetProgram(m_resource.getProgram(), GL_VALIDATE_STATUS) == 0) {
            System.err.println(glGetProgramInfoLog(m_resource.getProgram(), 1024));
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

        glAttachShader(m_resource.getProgram(), shader);
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
        glUniform1i(m_resource.getUniforms().get(uniformName), value);
    }

    /**
     * Sets a new float uniform from the GLSL code for java and then
     * OpenGL know what to do.
     * @param uniformName The uniform name in GLSL.
     * @param value Float value of the uniform.
     */
    public void setUniformf(String uniformName, float value) {
        glUniform1f(m_resource.getUniforms().get(uniformName), value);
    }

    /**
     * Sets a new uniform to vector from the GLSL code for java and then
     * OpenGL know what to do.
     * @param uniformName The uniform name in GLSL.
     * @param value Vector values of the uniform.
     */
    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(m_resource.getUniforms().get(uniformName), value.getX(), value.getY(), value.getZ());
    }

    /**
     * Sets a new uniform to matrix from the GLSL code for java and then
     * OpenGL know what to do.
     * @param uniformName The uniform name in GLSL.
     * @param value Matrix values of the uniform.
     */
    public void setUniform(String uniformName, Matrix4f value) {
        glUniformMatrix4(m_resource.getUniforms().get(uniformName), true, Util.createFlippedBuffer(value));
    }
    
    /**
	 * Sets a new uniform of color by name and the intensity by name.
	 * @param uniformName Name in baseLight.
	 * @param baseLight of the uniformName.
	 */
	public void setUniformBaseLight(String uniformName, BaseLight baseLight) {
		setUniform(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());
	}

	/**
	 * Sets a new uniform of base by name and the intensity by direction.
	 * @param uniformName Name in directionalLight.
	 * @param directionalLight of the uniformName.
	 */
	public void setUniformDirectionalLight(String uniformName, DirectionalLight directionalLight) {
		setUniformBaseLight(uniformName + ".base", directionalLight);
		setUniform(uniformName + ".direction", directionalLight.getDirection());
	}
	
	/**
	 * Sets a new uniform of base by name and the pointLight constructor.
	 * @param uniformName Name in pointLight.
	 * @param pointLight's constructor.
	 */
	public void setUniformPointLight(String uniformName, PointLight pointLight) {
		setUniformBaseLight(uniformName + ".base", pointLight);
		setUniformf(uniformName + ".atten.constant", pointLight.getAtten().getConstant());
		setUniformf(uniformName + ".atten.linear", pointLight.getAtten().getLinear());
		setUniformf(uniformName + ".atten.exponent", pointLight.getAtten().getExponent());
		setUniform(uniformName + ".position", pointLight.getPosition());
		setUniformf(uniformName + ".range", pointLight.getRange());
	}
	
	/**
	 * Sets a new uniform of base by name and the spotLight constructor.
	 * @param uniformName Name in pointLight.
	 * @param spotLight's constructor.
	 */
	public void setUniformSpotLight(String uniformName, SpotLight spotLight) {
		setUniformPointLight(uniformName + ".pointLight", spotLight);
		setUniform(uniformName + ".direction", spotLight.getDirection());
		setUniformf(uniformName + ".cutoff", spotLight.getCutoff());
	}
    
    /**
     * Sets the rendering engine of the shader.
     * @param renderingEngine for shader.
     */
    public void setRenderingEngine(RenderingEngine renderingEngine) {this.m_renderingEngine = renderingEngine;}

    /**
     * Returns the rendering engine of the shader.
     * @return rendering engine.
     */
	public RenderingEngine getRenderingEngine() {return m_renderingEngine;}

}
