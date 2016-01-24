package org.rockjs.converter.loader.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rockjs.ConverterException;
import org.rockjs.Logger;
import org.rockjs.geom.Point3;
import org.rockjs.geom.Vector3;
import org.rockjs.model.Normal;
import org.rockjs.model.Vertex;

public class NormalHelper {
    
    public static Normal computeNormal(Vertex vertex1, Vertex vertex2, Vertex vertex3) {
        Vector3 vector1 = new Vector3(vertex2.getX() - vertex1.getX(),
                vertex2.getY() - vertex1.getY(),
                vertex2.getZ() - vertex1.getZ());
        Vector3 vector2 = new Vector3(vertex3.getX() - vertex1.getX(),
                vertex3.getY() - vertex1.getY(),
                vertex3.getZ() - vertex1.getZ());
        
        Vector3 crossProduct = vector1.crossProduct(vector2);
        Normal normal = new Normal(crossProduct.getX(), crossProduct.getY(), crossProduct.getZ()); 
        normal.normalize();
        return normal;
    }
    
    public static void computeNormalsOBJMeshes(List<OBJMesh> meshes, List<Vertex> vertices, 
            List<Normal> normals) {
        // Remove all current normals
        normals.clear();

        for (int i = 0; i < meshes.size(); i++) {
            OBJMesh mesh = meshes.get(i);
            computeNormalsOBJMesh(mesh, vertices, normals);
        }
    }
    
    private static void computeNormalsOBJMesh(OBJMesh mesh, List<Vertex> vertices,
            List<Normal> normals) {
        List<OBJFace> faces = mesh.getFaces();
        OBJFace face;
        int[] normalIndices;
        for (int i = 0; i < faces.size(); i++) {
            face = faces.get(i);
            // reset current normal indices
            normalIndices = new int[]{OBJFace.NO_INDEX, OBJFace.NO_INDEX , OBJFace.NO_INDEX};
            face.setNormalIndices(normalIndices);
            // compute expected normal for face
            computeExpectedNormalOBJFace(face, vertices);
        }

        Map<Normal, Integer> existingNormals = new HashMap<Normal, Integer>();
        String smoothGroup;
        for (int i = 0; i < faces.size(); i++) {
            face = faces.get(i);
            smoothGroup = face.getSmoothGroup();
            if (smoothGroup == null) {
                updateFaceNormal(face, normals, existingNormals);
            } else {
                updateFaceNormalFromSmoothGroup(face, mesh, vertices, normals, existingNormals);
            }
        }
    }
    
    private static void updateFaceNormal(OBJFace face, List<Normal> normals, Map<Normal, Integer> existingNormals) {
        int index = addNormal(face.getComputedNormal(), normals, existingNormals);
        int[] normalIndices = new int[]{index, index, index};
        face.setNormalIndices(normalIndices);
    }
    
    private static void updateFaceNormalFromSmoothGroup(OBJFace face, OBJMesh mesh,
            List<Vertex> vertices, List<Normal> normals, Map<Normal, Integer> existingNormals) {  
        int[] normalIndices = face.getNormalIndices();
        int[] vertexIndices = face.getVertexIndices();
        int normalIndex;
        int vertexIndex;
        for (int i = 0; i < 3; i++) {
            normalIndex = normalIndices[i];
            if (normalIndex == OBJFace.NO_INDEX) {
                vertexIndex = vertexIndices[i];
                List<OBJFace> facesSameSmoothGroup = getFacesSameSmoothGroup(face, mesh, vertexIndex, vertices);
                Normal computedNormal = computeNormalFromFaceList(facesSameSmoothGroup);
                if (computedNormal.isValid()) {
                    // Why do not set the computedNormal to all faces used to compute the normal?
                    // Because you can find examples that this not work. For example, imagine two
                    // inverted pyramids that share the 'origin' point (the 'tallest' point of the
                    // pyramid). At least it will not work with this implementation :P
                    updateFaceSameSmoothGroup(face, computedNormal, normals, vertexIndex, existingNormals);
                } else {
                    // This should never happen...
                    updateFaceNormalVertex(face, normals, i, existingNormals);
                    // We could do also...
                    //updateFaceNormal(face, normals);
                    //break;
                }
            }
        }
    }
    
    private static void updateFaceNormalVertex(OBJFace face, List<Normal> normals, int position, Map<Normal, Integer> existingNormals) {
        int index = addNormal(face.getComputedNormal(), normals, existingNormals);
        int[] normalIndices = face.getNormalIndices();
        normalIndices[position] = index;
    }
    
    private static void updateFaceSameSmoothGroup(OBJFace face, Normal normal, List<Normal> normals, int vertexIndex, Map<Normal, Integer> existingNormals) {
        int index = addNormal(normal, normals, existingNormals);

        int[] vertexIndices = face.getVertexIndices();
        int[] normalIndices = face.getNormalIndices();
        for (int j = 0; j < 3; j++) {
            if (vertexIndices[j] == vertexIndex) {
                normalIndices[j] = index;
            }
        }
    }

    private static int addNormal(Normal normal, List<Normal> normals, Map<Normal, Integer> existingNormals) {
        if (existingNormals.containsKey(normal)) {
            return existingNormals.get(normal);
        } else {
            normals.add(normal);
            int index = normals.size();
            existingNormals.put(normal, index);
            return index;
        }
    }

    private static Normal computeNormalFromFaceList(List<OBJFace> faces) {        
        Normal computedNormal = null;
        OBJFace face;
        Normal normal;
        
        double x = 0;
        double y = 0;
        double z = 0;
        int size = faces.size();
        for (int i = 0; i < size; i++) {
            face = faces.get(i);
            normal = face.getComputedNormal();
            x += normal.getX();
            y += normal.getY();
            z += normal.getZ();
        }

        computedNormal = new Normal(x / size, y / size, z / size);
        computedNormal.normalize();
        
        return computedNormal;
    }
    
    private static List<OBJFace> getFacesSameSmoothGroup(OBJFace face, OBJMesh mesh, int vertexIndex, List<Vertex> vertices) {
        List<OBJFace> facesSameSmoothGroup = new ArrayList<OBJFace>();
        String smoothGroup = face.getSmoothGroup();
        List<OBJFace> faces = mesh.getFaces();
        
        OBJFace currentFace;
        for (int i = 0; i < faces.size(); i++) {
            currentFace = faces.get(i);
            if (smoothGroup.equals(currentFace.getSmoothGroup()) && faceUsesVertexIndex(currentFace, vertexIndex)) {
                facesSameSmoothGroup.add(currentFace);
            }
        }
        
        return facesSameSmoothGroup;
    }
    
    private static boolean faceUsesVertexIndex(OBJFace face, int vertexIndex) {
        int[] vertexIndices = face.getVertexIndices();
        for (int i = 0; i < 3; i++) {
            if (vertexIndices[i] == vertexIndex) {
                return true;
            }
        }
        return false;
    }

    // The algorithm implemented here only work if faces are adjacent (or are the same face, or are
    // in the same plane :)
    @SuppressWarnings("unused")
    private static boolean facesShareMeshOrientation(OBJFace face1, OBJFace face2, List<Vertex> vertices) {
        Normal n1 = face1.getComputedNormal();
        Normal n2 = face2.getComputedNormal();
        
        Point3 center1 = getFaceCenter(face1, vertices);
        Point3 center2 = getFaceCenter(face2, vertices);
        
        Point3 middle = new Point3(
                (center1.getX() + center2.getX()) / 2,
                (center1.getY() + center2.getY()) / 2,
                (center1.getZ() + center2.getZ()) / 2);
        
        Vector3 centerToMiddle1 = new Vector3(
                middle.getX() - center1.getX(),
                middle.getY() - center1.getY(),
                middle.getZ() - center1.getZ());
        Vector3 centerToMiddle2 = new Vector3(
                middle.getX() - center2.getX(),
                middle.getY() - center2.getY(),
                middle.getZ() - center2.getZ());
        
        if (!isZero(centerToMiddle1.module())) {
            double dotProduct1 = centerToMiddle1.dotProduct(n1);
            if (!isZero(dotProduct1)) {
                double dotProduct2 = centerToMiddle2.dotProduct(n2);
                
                int dotProduct1Sign = getSign(dotProduct1);
                int dotProduct2Sign = getSign(dotProduct2);
                
                return dotProduct1Sign == dotProduct2Sign;    
            }
            else {
                // The line joining 2 centers is perpendicular to normal in face 1
                return isValueGreaterThanZero(n1.dotProduct(n2));
            }
            
        } else {
            // Share same center
            return isValueGreaterThanZero(n1.dotProduct(n2));
        }
    }
    
    private static double DELTA = 0.05;
    
    private static boolean isZero(double value) {
        double positiveValue = Math.abs(value);
        return positiveValue <= DELTA;
    }
    
    private static boolean isValueGreaterThanZero(double value) {
        return value >= DELTA;
    }
    
    private static int getSign(double value) {
        if (value == 0) {
            return 0;
        } else if (value > 0) {
            return 1;
        } else {
            return -1;
        }
    }
    
    private static Point3 getFaceCenter(OBJFace face, List<Vertex> vertices) {
        int[] vertexIndices = face.getVertexIndices();
        
        Vertex vertex1 = vertices.get(vertexIndices[0] - 1);
        Vertex vertex2 = vertices.get(vertexIndices[1] - 1);
        Vertex vertex3 = vertices.get(vertexIndices[2] - 1);
        double x = (vertex1.getX() + vertex2.getX() + vertex3.getX()) / 3;
        double y = (vertex1.getY() + vertex2.getY() + vertex3.getY()) / 3;
        double z = (vertex1.getZ() + vertex2.getZ() + vertex3.getZ()) / 3;

        return new Point3(x, y, z);
    }
    
    private static void computeExpectedNormalOBJFace(OBJFace face, List<Vertex> vertices) {
        int[] vertexIndices = face.getVertexIndices();
        Vertex vertex1 = vertices.get(vertexIndices[0] - 1);
        Vertex vertex2 = vertices.get(vertexIndices[1] - 1);
        Vertex vertex3 = vertices.get(vertexIndices[2] - 1);

        face.setComputedNormal(computeNormal(vertex1, vertex2, vertex3));
    }
    
    /**
     * This function will check if there are incorrect normals.
     * In this case, normals will be repaired.
     * 
     * @return if the meshes were correct
     */    
    public static boolean repairNormalsOBJMeshes(List<OBJMesh> meshes, List<Vertex> vertices, 
            List<Normal> normals) throws ConverterException {        
        boolean correctMeshes = true;
        for (int i = 0; i < meshes.size(); i++) {
            OBJMesh mesh = meshes.get(i);
            boolean correctMesh = repairNormalsOBJMesh(mesh, vertices, normals);
            String correctness = (correctMesh) ? "correct" : "no correct";
            Logger.log("Mesh with id '" + mesh.getMeshId() + "' is " + correctness);
            correctMeshes &= correctMesh;
        }
        
        return correctMeshes;
    }

    private static boolean repairNormalsOBJMesh(OBJMesh mesh, List<Vertex> vertices,
            List<Normal> normals) throws ConverterException {
        boolean correctMesh = true;
        List<OBJFace> faces = mesh.getFaces();
        OBJFace face;
        for (int i = 0; i < faces.size(); i++) {
            face = faces.get(i);
            correctMesh &= repairNormalsOBJFace(face, vertices, normals);
        }
        
        return correctMesh;
    }
    
    private static boolean repairNormalsOBJFace(OBJFace face, List<Vertex> vertices,
            List<Normal> normals) throws ConverterException {
        Normal expectedNormal = face.getComputedNormal();
        if (expectedNormal == null) {
            computeExpectedNormalOBJFace(face, vertices);
            expectedNormal = face.getComputedNormal();
        }

        Normal normal1, normal2, normal3;
        boolean correctNormal1, correctNormal2, correctNormal3;
        int[] normalIndices = face.getNormalIndices();

        normal1 = normals.get(normalIndices[0] - 1);
        normal2 = normals.get(normalIndices[1] - 1);
        normal3 = normals.get(normalIndices[2] - 1);

        correctNormal1 = correctNormalOrientation(normal1, expectedNormal);
        correctNormal2 = correctNormalOrientation(normal2, expectedNormal);
        correctNormal3 = correctNormalOrientation(normal3, expectedNormal);
        
        boolean correctFace = correctNormal1 && correctNormal2 && correctNormal3;
        int newNormalIndex = -1;
        if (!correctFace) {
            normals.add(expectedNormal);
            newNormalIndex = normals.size();
        }
        
        if (!correctNormal1) {
            normalIndices[0] = newNormalIndex;
        }
        
        if (!correctNormal2) {
            normalIndices[1] = newNormalIndex;
        }
        
        if (!correctNormal3) {
            normalIndices[2] = newNormalIndex;
        }
        
        return correctFace;
    }

    private static boolean correctNormalOrientation(Normal normal, Normal normalExpectedVector) {
        double dotProduct = normal.dotProduct(normalExpectedVector);
        return dotProduct >= 0;
    }
}
