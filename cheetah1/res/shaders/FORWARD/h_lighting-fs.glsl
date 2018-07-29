varying vec2 texCoord0;
varying vec3 worldPos0;
varying mat3 tbnMatrix;

uniform sampler2D R_diffuse;
uniform sampler2D R_normalMap;
uniform sampler2D R_dispMap;

uniform float M_dispMapScale;
uniform float M_dispMapBias;

#include "FORWARD/h_lighting"