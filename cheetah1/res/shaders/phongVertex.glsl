#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 normal;

out vec2 texCoord0;
out vec3 normal0;
out vec3 worldPos0;

out float visibility;

uniform mat4 transform;
uniform mat4 transformProjected;
uniform vec3 eyePos;

const float density = 0.07;
const float gradient = 1.5;

void main() {
    gl_Position = transformProjected * vec4(position, 1.0);
    texCoord0 = texCoord;
    normal0 = (transform * vec4(normal, 0.0)).xyz;
    worldPos0 = (transform * vec4(position, 1.0)).xyz;
    float distance = length((eyePos - worldPos0).xyz);
    visibility = exp(-pow((distance * density), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}
