#version 330 core

uniform sampler2D uTexture;

in vec2 texCoords;
in vec4 color;
flat in float useTex;

out vec4 fragColor;

void main() {
	if(useTex == 1.0) {
    	fragColor = texture(uTexture, texCoords);
    } else {
    	fragColor = color;
    }
}