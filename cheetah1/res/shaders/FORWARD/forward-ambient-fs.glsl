#version 120
varying vec3 worldPos0;
varying vec2 texCoord0;
varying mat3 tbnMatrix;

uniform sampler2D R_diffuse;
uniform sampler2D R_normalMap;
uniform sampler2D R_dispMap;

uniform vec3 R_ambient;
uniform vec3 R_fogColor;
uniform vec3 R_eyePos;

uniform float R_fogDensity;
uniform float R_fogGradient;
uniform float M_dispMapScale;
uniform float M_dispMapBias;

#include "FORWARD/h_sampling"

void main() {

	float distance = length(R_eyePos - worldPos0);
    float visibility = exp(-pow((distance * R_fogDensity), R_fogGradient));
    visibility = clamp(visibility, 0.0, 1.0);

	vec4 textureColor = texture2D(R_diffuse, texCoord0.xy); if(textureColor.a < 0.5) {discard;}
	vec3 directionToEye = normalize(R_eyePos - worldPos0);
	vec2 texCoords = CalcParallaxTexCoords(R_dispMap, tbnMatrix, directionToEye, texCoord0, M_dispMapScale, M_dispMapBias);
	vec3 normal = normalize(tbnMatrix * (255.0/128.0 * texture2D(R_normalMap, texCoords).xyz - 1));
    
    gl_FragColor = textureColor * vec4(R_ambient, 1);
	gl_FragColor = mix(vec4(R_fogColor,1.0), gl_FragColor, visibility);
}
