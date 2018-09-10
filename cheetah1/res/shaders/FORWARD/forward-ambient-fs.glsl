#version 120
varying vec3 worldPos0;
varying vec2 texCoord0;
varying mat3 tbnMatrix;

uniform sampler2D diffuse;
uniform sampler2D dispMap;

uniform vec3 R_ambient;
uniform vec3 R_fogColor;
uniform vec3 C_eyePos;

uniform float R_fogDensity;
uniform float R_fogGradient;
uniform float dispMapScale;
uniform float dispMapBias;

#include "FORWARD/h_sampling"

void main() {

	float distance = length(C_eyePos - worldPos0);
    float visibility = exp(-pow((distance * R_fogDensity), R_fogGradient));
    visibility = clamp(visibility, 0.0, 1.0);

	vec4 textureColor = texture2D(diffuse, texCoord0.xy); if(textureColor.a < 0.625) {discard;}
	vec3 directionToEye = normalize(C_eyePos - worldPos0);
	vec2 texCoords = CalcParallaxTexCoords(dispMap, tbnMatrix, directionToEye, texCoord0, dispMapScale, dispMapBias);
    
    gl_FragColor = textureColor * vec4(R_ambient, 1);
	gl_FragColor = mix(vec4(R_fogColor,1.0), gl_FragColor, visibility);
}
