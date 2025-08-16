#version 330 core

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 texCoords;

layout(location = 2) in vec4 modelRow0;
layout(location = 3) in vec4 modelRow1;
layout(location = 4) in vec4 modelRow2;
layout(location = 5) in vec4 modelRow3;

layout(location = 6) in vec2 texOffset;
layout(location = 7) in vec2 texScale;

uniform mat4 projection;
uniform mat4 view;

out vec2 fragTex;

void main() {
    mat4 model = mat4(modelRow0, modelRow1, modelRow2, modelRow3);
    gl_Position = projection * view * model * vec4(position, 0.0, 1.0);
    fragTex = texCoords * texScale + texOffset;
}