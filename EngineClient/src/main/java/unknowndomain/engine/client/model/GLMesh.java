package unknowndomain.engine.client.model;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class GLMesh {
    private int vao;
    private int[] vbos;
    private int counts;
    private byte[] attributes = new byte[]{0, 1};

    private int mode; // gl draw mode

    GLMesh(int vao, int[] vbos, int counts, int mode) {
        this.vao = vao;
        this.vbos = vbos;
        this.counts = counts;
        this.mode = mode;
    }

    public void dispose() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vbos) {
            glDeleteBuffers(vboId);
        }

        // Delete the texture

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vao);
    }

    public static GLMesh of(Mesh t) {
        if (t == null) return null;
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        IntBuffer indicesBuffer = null;
        int[] indices = t.getIndices();
        float[] positions = t.getVertices();
        float[] normals = t.getNormals();
        float[] textCoords = t.getUv();
        int mode = t.getMode();
        try {
            int vertexCount = indices.length;
            int[] vboIdList = new int[3];

            int vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            int vboId = glGenBuffers();
            vboIdList[0] = (vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            vboId = glGenBuffers();
            vboIdList[1] = (vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

//            vboId = glGenBuffers();
//            vboIdList[2] = (vboId);
//            textCoordsBuffer = MemoryUtil.memAllocFloat(normals.length);
//            textCoordsBuffer.put(normals).flip();
//            glBindBuffer(GL_ARRAY_BUFFER, vboId);
//            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
//            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            vboId = glGenBuffers();
            vboIdList[2] = (vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            return new GLMesh(vaoId, vboIdList, vertexCount, mode);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    @Override
    public String toString() {
        return "GLMesh { vao: " + vao + ", count: " + counts + " }";
    }

    public void render() {
        // Draw the mesh
        glBindVertexArray(vao);
        for (byte attribute : attributes)
            glEnableVertexAttribArray(attribute);

//        glDrawElements(GL_LINES, counts, GL_UNSIGNED_INT, 0);
        glDrawElements(mode, counts, GL_UNSIGNED_INT, 0);

        // Restore state
        for (byte attribute : attributes)
            glDisableVertexAttribArray(attribute);
        glBindVertexArray(0);
    }
}
