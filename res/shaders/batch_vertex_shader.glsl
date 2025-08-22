#version 330 core

layout(location=0) in vec2 aPosition;
layout(location=1) in vec2 aTexCoords;
layout(location=2) in vec4 aColor;
layout(location=3) in float aUseTex;

uniform mat4 projection;
uniform mat4 view;

out vec2 texCoords;
out vec4 color;
flat out float useTex;

void main() {
    gl_Position = projection * view * vec4(aPosition, 0.0, 1.0);
    
    texCoords = aTexCoords;
    color = aColor;
    useTex = aUseTex;
}