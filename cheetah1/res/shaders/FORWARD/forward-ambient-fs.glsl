#version 120

varying vec2 texCoord0;
varying float visibility;

uniform vec3 ambientIntensity;
uniform sampler2D sampler;
uniform vec3 fogColor;

void main()
{
	if(texture2D(sampler, texCoord0.xy).a < 1.0) {discard;}
	gl_FragColor = texture2D(sampler, texCoord0.xy) * vec4(ambientIntensity, 1);
	gl_FragColor = mix(vec4(fogColor,1.0), gl_FragColor, visibility);
}
