#version 120

#include "FORWARD/h_lighting-fs"

uniform SpotLight R_spotLight;

vec4 CalcLightingEffect(vec3 normal, vec3 worldPos) {
	return CalcSpotLight(R_spotLight, normal, worldPos);
}

#include "FORWARD/h_lightingMain-fs"