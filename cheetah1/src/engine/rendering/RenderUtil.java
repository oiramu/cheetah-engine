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
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

import engine.core.Vector3f;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class RenderUtil {

	/**
	 * Cleans everything on the openGL screen.
	 */
    public static void clearScreen() {
        //TODO: Stencil Buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    /**
     * Sets textures to openGL.
     * @param enabled Able switch.
     */
    public static void setTextures(boolean enabled) {
        if (enabled) {
            glEnable(GL_TEXTURE_2D);
        } else {
            glDisable(GL_TEXTURE_2D);
        }
    }

    /**
     * Bind the textures to openGL.
     */
    public static void unbindTextures() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Cleans all the colors in the environment.
     * @param color to clean.
     */
    public static void setClearColor(Vector3f color) {
        glClearColor(color.getX(), color.getY(), color.getZ(), 1.0f);
    }

    /**
     * Sets all of the rendering systems to work as it should.
     */
    public static void initGraphics() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glFrontFace(GL_CW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_TEXTURE_2D);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glEnable(GL_DEPTH_CLAMP);
        
        System.out.println("==============================");
        System.out.println("||CHEETAH ENGINE; BUILD v1.0||");
        System.out.println("==============================");
        System.out.println("Compiliation specs: ");
        System.out.println("-OS name: " + System.getProperty("os.name"));
        System.out.println("-OS version: " + System.getProperty("os.version"));
        System.out.println("-LWJGL version: " + org.lwjgl.Sys.getVersion());
        System.out.println("-OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("\n");

    }

}
