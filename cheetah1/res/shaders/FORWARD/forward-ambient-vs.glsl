#version 120

attribute vec3 position;
attribute vec2 texCoord;

varying float visibility;
varying vec2 texCoord0;
varying vec3 worldPos0;

uniform mat4 transform;
uniform float fogDensity;
uniform float fogGradient;
uniform vec3 eyePos;
uniform mat4 MVP;

void main()
{
    gl_Position = MVP * vec4(position, 1.0);
    texCoord0 = texCoord;
    worldPos0 = (transform * vec4(position, 1.0)).xyz;
    float distance = length((eyePos - worldPos0).xyz);
    visibility = exp(-pow((distance * fogDensity), fogGradient));
    visibility = clamp(visibility, 0.0, 1.0);
}
