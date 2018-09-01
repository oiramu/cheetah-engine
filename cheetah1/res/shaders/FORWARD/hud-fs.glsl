#version 120

varying vec2 texCoord0;

uniform sampler2D R_diffuse;

void main() {

    vec4 textureColor = texture2D(R_diffuse, texCoord0.xy) * vec4(0.03, 0.03, 0.03, 1);
    if(textureColor.a < 0.5) {discard;}
    gl_FragColor = textureColor;
}