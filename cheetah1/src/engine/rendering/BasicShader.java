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

import engine.core.Matrix4f;
import engine.core.ResourceLoader;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class BasicShader extends Shader {

    private static final BasicShader instance = new BasicShader();

    /**
     * Instances the shader to be used.
     * @return Shader.
     */
    public static BasicShader getInstance() {
        return instance;
    }
    
    /**
     * Constructor of the basic shader with all his uniforms.
     */
    private BasicShader() {
        super();

        addVertexShader(ResourceLoader.loadShader("basicVertex"));
        addFragmentShader(ResourceLoader.loadShader("basicFragment"));
        compileShader();

        addUniform("transform");
        addUniform("color");
    }

    /**
     * Updates all the uniforms of the shading program.
     * @param worldMatrix World matrix data.
     * @param projectedMatrix Projection matrix data.
     * @param material Material of the object.
     */
    public void updateUniforms(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material material) {
        if (material.getTexture() != null) {
            material.getTexture().bind();
        } else {
            RenderUtil.unbindTextures();
        }

        setUniform("transform", projectedMatrix);
        setUniform("color", material.getColor());
    }
}
