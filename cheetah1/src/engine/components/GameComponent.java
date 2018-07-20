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
package engine.components;

import engine.rendering.Shader;

/**
 *
 * @author Carlos Rodriguez.
 * @version 1.0
 * @since 2018
 */
public abstract class GameComponent {
	
	public void input() {}
	public void render(Shader shader) {}
	public void update() {}
}
