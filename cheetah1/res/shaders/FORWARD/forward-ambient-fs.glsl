#version 120

varying vec2 texCoord0;
varying float visibility;

uniform vec3 R_ambient;
uniform sampler2D R_diffuse;
uniform vec3 R_fogColor;

void main() {
	vec4 textureColor = texture2D(R_diffuse, texCoord0.xy);
	if(textureColor.a < 1.0) {discard;}
	gl_FragColor = textureColor * vec4(R_ambient, 1);
	gl_FragColor = mix(vec4(R_fogColor,1.0), gl_FragColor, visibility);
}
