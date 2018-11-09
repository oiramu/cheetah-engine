#version 120

#include "FORWARD/h_lighting-fs"

uniform DirectionalLight R_directionalLight;

vec4 CalcLightingEffect(vec3 normal, vec3 worldPos) {
	if(R_directionalLight.base.intensity > 0)
		return CalcDirectionalLight(R_directionalLight, normal, worldPos);
}

#include "FORWARD/h_lightingMain-fs"