package org.rockjs.converter.loader.obj;

import org.rockjs.model.Normal;

public class OBJFace {
   
    public static final int NO_INDEX = -1;
    
    // Size must be 3
    private int[] vertexIndices;

    private int[] normalIndices;

    private int[] textureCoordinatesIndices;

    private String smoothGroup = null;
    
    private Normal computedNormal = null;

    public int[] getVertexIndices() {
        return vertexIndices;
    }

    public void setVertexIndices(int[] vertexIndices) {
        this.vertexIndices = vertexIndices;
    }

    public int[] getNormalIndices() {
        return normalIndices;
    }

    public void setNormalIndices(int[] normalIndices) {
        this.normalIndices = normalIndices;
    }

    public int[] getTextureCoordinatesIndices() {
        return textureCoordinatesIndices;
    }

    public void setTextureCoordinatesIndices(int[] textureCoordinatesIndices) {
        this.textureCoordinatesIndices = textureCoordinatesIndices;
    }
    
    public String getSmoothGroup() {
        return smoothGroup;
    }

    public void setSmoothGroup(String smoothGroup) {
        this.smoothGroup = smoothGroup;
    }
    
    public Normal getComputedNormal() {
        return computedNormal;
    }

    public void setComputedNormal(Normal computedNormal) {
        this.computedNormal = computedNormal;
    }
}
