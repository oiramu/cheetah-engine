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

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class Texture {

    private int id;

    /**
     * Texture's constructor.
     * @param id texture index.
     */
    public Texture(int id) {
        this.id = id;
    }

    /**
     * Binds the texture for openGL.
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /**
     * Returns the texture index.
     * @return Index.
     */
    public int getID() {
        return id;
    }
}
