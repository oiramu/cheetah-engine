#version 120
#include "FORWARD/h_lighting"

varying vec2 texCoord0;
varying vec3 normal0;
varying vec3 worldPos0;

uniform sampler2D R_diffuse;
uniform PointLight R_pointLight;

void main() {
    gl_FragColor = texture2D(R_diffuse, texCoord0.xy) * CalcPointLight(R_pointLight, normalize(normal0), worldPos0);
}