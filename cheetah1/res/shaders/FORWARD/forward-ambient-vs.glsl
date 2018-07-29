#version 120

attribute vec3 position;
attribute vec2 texCoord;
attribute vec3 normal;
attribute vec3 tangent;

varying float visibility;
varying vec2 texCoord0;
varying vec3 worldPos0;
varying mat3 tbnMatrix;

uniform mat4 T_model;
uniform mat4 T_MVP;
uniform float R_fogDensity;
uniform float R_fogGradient;
uniform vec3 R_eyePos;

void main() {
    gl_Position = T_MVP * vec4(position, 1.0);
    texCoord0 = texCoord;
    worldPos0 = (T_model * vec4(position, 1.0)).xyz;
    
    float distance = length(R_eyePos - worldPos0);
    visibility = exp(-pow((distance * R_fogDensity), R_fogGradient));
    visibility = clamp(visibility, 0.0, 1.0);
    
    vec3 n = normalize((T_model * vec4(normal, 0.0)).xyz);
    vec3 t = normalize((T_model * vec4(tangent, 0.0)).xyz);
    t = normalize(t - dot(t, n) * n);
    
    vec3 biTangent = cross(t, n);
    tbnMatrix = mat3(t, biTangent, n);
}
