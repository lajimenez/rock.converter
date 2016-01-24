package org.rockjs.converter.loader.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class OBJMesh {
    private List<OBJFace> faces = null;
    
    private String meshId = getRandomId();
    
    private String materialId = null;

    public OBJMesh() {
        faces = new ArrayList<OBJFace>();
    }
    
    public void addFace(OBJFace face) {
        faces.add(face);
    }
    
    public void addFaces(List<OBJFace> faces) {
        this.faces.addAll(faces);
    }

    public List<OBJFace> getFaces() {
        return faces;
    }
    
    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }
    
    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }
    public void updateFromObjectMesh(OBJMesh objectMesh) {
        if (materialId == null) {
            materialId = objectMesh.getMaterialId();
        }
    }
    
    public static String getRandomId() {
        return UUID.randomUUID().toString();
    }

}
