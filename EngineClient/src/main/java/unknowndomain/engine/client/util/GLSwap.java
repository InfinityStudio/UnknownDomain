package unknowndomain.engine.client.util;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL31.glCopyBufferSubData;


public class GLSwap {

    class Mapping {
        int vbo;

        int srcOffset;
        int destOffset;

        int length;
    }

    public static void remap(Mapping[] mappings) {
        int length = 0;
        for (Mapping m : mappings) {
            length += m.length;
        }

        int dest = glGenBuffers(); // create dest buffer
        glBindBuffer(GL_ARRAY_BUFFER, dest);
        nglBufferData(GL_ARRAY_BUFFER, length, 0, GL_STATIC_DRAW); // init with empty and correct size

        for (Mapping m : mappings) {
            glCopyBufferSubData(m.vbo, dest, m.srcOffset, m.destOffset, m.length);
        }
    }
}