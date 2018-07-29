#include "FORWARD/h_sampling"

void main() {
	vec3 directionToEye = normalize(R_eyePos - worldPos0);
	vec2 texCoords = CalcParallaxTexCoords(R_dispMap, tbnMatrix, directionToEye, texCoord0, M_dispMapScale, M_dispMapBias);
	
	vec3 normal = normalize(tbnMatrix * (255.0/128.0 * texture2D(R_normalMap, texCoords).xyz - 1));
    gl_FragColor = texture2D(R_diffuse, texCoords) * 
    	CalcLightingEffect(normal, worldPos0);
}