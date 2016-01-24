package org.rockjs.model;

import java.util.ArrayList;
import java.util.List;

public class Mesh {

    private List<Vertex> vertices = new ArrayList<Vertex>();

    private List<Normal> normals = new ArrayList<Normal>();

    private List<TextureCoordinate> textureCoordinates = new ArrayList<TextureCoordinate>();

    private List<Integer> indexes = new ArrayList<Integer>();
    
    private double[] BBOX = new double[6];
    
    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public void addNormal(Normal normal) {
        normals.add(normal);
    }

    public void addTextureCoordinate(TextureCoordinate textureCoordinate) {
        textureCoordinates.add(textureCoordinate);
    }

    public void addIndex(Integer index) {
        indexes.add(index);
    }
    
    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Normal> getNormals() {
        return normals;
    }

    public List<TextureCoordinate> getTextureCoordinates() {
        return textureCoordinates;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }
    
    public void computeBBOX() {
        Vertex vertex = vertices.get(indexes.get(0));
        double minX = vertex.getX();
        double maxX = vertex.getX();
        double minY = vertex.getY();
        double maxY = vertex.getY();
        double minZ = vertex.getZ();
        double maxZ = vertex.getZ();

        for (int i = 1; i < indexes.size(); i++) {
            vertex = vertices.get(indexes.get(i));
            double vertexX = vertex.getX();
            double vertexY = vertex.getY();
            double vertexZ = vertex.getZ();

            if (vertexX < minX) {
                minX = vertexX;
            } else if (vertexX > maxX) {
                maxX = vertexX;
            }

            if (vertexY < minY) {
                minY = vertexY;
            } else if (vertexY > maxY) {
                maxY = vertexY;
            }

            if (vertexZ < minZ) {
                minZ = vertexZ;
            } else if (vertexZ > maxZ) {
                maxZ = vertexZ;
            }
        }

        BBOX[0] = minX;
        BBOX[1] = maxX;
        BBOX[2] = minY;
        BBOX[3] = maxY;
        BBOX[4] = minZ;
        BBOX[5] = maxZ;
    }
    
    public double[] getBBOX() {
        return BBOX;
    }
}
