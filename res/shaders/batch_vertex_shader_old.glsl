#version 330 core

layout(location=0) in vec3 aPos;
layout(location=1) in float aZIndex;
layout(location=2) in vec4 aColor;
layout(location=3) in vec2 aTexCoords;
layout(location=4) in float aTexId;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
flat out float fTexId;

void main() {
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexId = aTexId;

    gl_Position = uProjection * uView * vec4(aPos.xy, 1.0, 1.0);
}