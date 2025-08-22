#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
flat in float fTexId;

uniform sampler2D uTextures[8];

out vec4 color;

void main() {
    if (fTexId >= 0) {
        color = texture(uTextures[int(fTexId)], fTexCoords);
    } else {
        color = vec4(0, 1, 0, 1);
    }
}