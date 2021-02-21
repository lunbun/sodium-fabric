package me.jellysquid.mods.sodium.client.gl.func;

import org.lwjgl.opengl.*;

public class GlFunctions {
    private static final GLCapabilities capabilities = GL.getCapabilities();

    public static final GlVertexArrayFunctions VERTEX_ARRAY = GlVertexArrayFunctions.load(capabilities);
    public static final GlBufferCopyFunctions BUFFER_COPY = GlBufferCopyFunctions.load(capabilities);
    public static final GlIndirectMultiDrawFunctions INDIRECT_DRAW = GlIndirectMultiDrawFunctions.load(capabilities);
    public static final GlInstancedArrayFunctions INSTANCED_ARRAY = GlInstancedArrayFunctions.load(capabilities);

    public static boolean isVertexArraySupported() {
        return VERTEX_ARRAY != GlVertexArrayFunctions.UNSUPPORTED;
    }

    public static boolean isBufferCopySupported() {
        return BUFFER_COPY != GlBufferCopyFunctions.UNSUPPORTED;
    }

    public static boolean isIndirectMultiDrawSupported() {
        return INDIRECT_DRAW != GlIndirectMultiDrawFunctions.UNSUPPORTED;
    }

    public static boolean isInstancedArraySupported() {
        return INSTANCED_ARRAY != GlInstancedArrayFunctions.UNSUPPORTED;
    }

    public static void clearErrors() {
        while (GL43.glGetError() != GL30.GL_NO_ERROR);
    }

    public static void printErrors() {
        int err;
        while ((err = GL43.glGetError()) != GL30.GL_NO_ERROR)
            System.out.println("GL Error: " + err);
    }
}
