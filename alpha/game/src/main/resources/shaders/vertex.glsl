
#version 460 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec3 aNormal;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out vec2 TexCoord;
out vec3 Normal;
out vec3 FragPos;

void main() {
    gl_Position = projectionMatrix * viewMatrix * vec4(aPos, 1.0);
    FragPos = aPos;
    TexCoord = aTexCoord;
    Normal = aNormal;
}
