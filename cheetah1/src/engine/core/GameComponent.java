package engine.core;

import engine.rendering.Shader;

public interface GameComponent {
	
	void input();
	void render(Shader shader);
	void update();

}
