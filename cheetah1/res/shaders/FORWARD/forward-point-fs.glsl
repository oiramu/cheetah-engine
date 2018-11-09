#version 120

#include "FORWARD/h_lighting-fs"

uniform PointLight R_pointLight;

vec4 CalcLightingEffect(vec3 normal, vec3 worldPos) {
	return CalcPointLight(R_pointLight, normal, worldPos);
}

#include "FORWARD/h_lightingMain-fs"