#version 120

attribute vec3 position;
attribute vec2 texCoord;

varying float visibility;
varying vec2 texCoord0;
varying vec3 worldPos0;

uniform mat4 T_world;
uniform float R_fogDensity;
uniform float R_fogGradient;
uniform vec3 R_eyePos;
uniform mat4 T_MVP;

void main()
{
    gl_Position = T_MVP * vec4(position, 1.0);
    texCoord0 = texCoord;
    worldPos0 = (T_world * vec4(position, 1.0)).xyz;
    float distance = length((R_eyePos - worldPos0).xyz);
    visibility = exp(-pow((distance * R_fogDensity), R_fogGradient));
    visibility = clamp(visibility, 0.0, 1.0);
}
