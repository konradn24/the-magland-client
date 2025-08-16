#version 330 core

in vec2 fragTex;
out vec4 fragColor;

uniform sampler2D tex;

void main() {
    fragColor = vec4(1.0, 1.0, 1.0, 1.0);
}