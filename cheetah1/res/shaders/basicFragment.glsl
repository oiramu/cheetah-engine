#version 330

in vec2 texCoord0;

out vec4 fragColor;

uniform vec3 color;
uniform sampler2D sampler;

void main() {

    vec4 textureColor = texture(sampler, texCoord0.xy);
    float alpha = texture(sampler, texCoord0.xy).a;
    
    if(alpha < 0.5) {
		discard;
	}

    if(textureSize(sampler, 0).x <= 0) {
        fragColor = vec4(color, 1);
    } else {
        fragColor = textureColor * vec4(color, 1);
    }
}
