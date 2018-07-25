#version 120
#include "FORWARD/h_lighting"

varying vec2 texCoord0;
varying vec3 normal0;
varying vec3 worldPos0;

uniform sampler2D R_diffuse;
uniform SpotLight R_spotLight;

void main() {
	if(R_spotLight.pointLight.base.intensity > 0)
    	gl_FragColor = texture2D(R_diffuse, texCoord0.xy) * CalcSpotLight(R_spotLight, normalize(normal0), worldPos0);
}
