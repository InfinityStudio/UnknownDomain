#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec4 color;
layout (location = 2) in vec2 texcoord;
layout (location = 3) in vec3 normal;

out vec4 vertexColor;
out vec3 vertexNormal;
out vec3 vertexMv;
out vec2 textureCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;


layout(std140) uniform VertexStatus{
    bool usepos;
    bool usecolor;
    bool usetex;
    bool usenormal;
};

void main() {
    vertexColor = color;
    textureCoord = texcoord;
    mat4 modelView = view * model;
    mat4 mvp = projection * modelView;
    vertexNormal = normalize(modelView * vec4(normal, 0.0)).xyz;
    vertexMv = (modelView * vec4(position, 1.0)).xyz;
    gl_Position = mvp * vec4(position, 1.0);
}
