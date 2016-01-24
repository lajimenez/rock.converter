package org.rockjs.converter.loader.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rockjs.model.Color;
import org.rockjs.model.Material;
import org.rockjs.model.Mesh;
import org.rockjs.model.Model;
import org.rockjs.model.Normal;
import org.rockjs.model.PhongColor;
import org.rockjs.model.PhongTexture;
import org.rockjs.model.TextureCoordinate;
import org.rockjs.model.Vertex;

/**
 * This class converts OBJ elements into a Model
 * 
 * @author Luis Alberto Jiménez
 */
public class ModelLoader {

    public static List<Model> loadFromOBJMeshes(List<OBJMesh> meshes,
            List<OBJMaterial> materials, List<Vertex> vertices,
            List<Normal> normals, List<TextureCoordinate> textureCoordinates) {
        List<Model> models = new ArrayList<Model>();
        
        OBJMesh mesh;
        Model model;
        for (int i = 0; i < meshes.size(); i++) {
            mesh = meshes.get(i);
            model = createModel(mesh, materials, vertices, normals, textureCoordinates);
            if (model != null) {
                models.add(model);
            }
        }

        return models;
    }

    private static Model createModel(OBJMesh objMesh,
            List<OBJMaterial> materials, List<Vertex> vertices,
            List<Normal> normals, List<TextureCoordinate> textureCoordinates) {
        // If there are no faces we can ignore the OBJ mesh
        if (objMesh.getFaces().size() == 0) {
            return null;
        }

        OBJMaterial objMaterial = getMaterialFromList(objMesh.getMaterialId(), materials);
        
        Model model = new Model();
        model.setModelId(objMesh.getMeshId());       
        model.setMesh(convertMesh(objMesh, vertices, normals, textureCoordinates));
        model.setMaterial(convertMaterial(objMaterial));
        //model.setMaterialTexture(convertTexture(objMaterial));
        
        return model;
    }
    
    private static Mesh convertMesh(OBJMesh objMesh, List<Vertex> vertices,
            List<Normal> normals, List<TextureCoordinate> textureCoordinates) {
        Mesh mesh = new Mesh();
        
        HashMap<String, Integer> hash = new HashMap<String, Integer>();
        List<OBJFace> objFaces = objMesh.getFaces();

        int nextIndex = 0;

        for (int i = 0; i < objFaces.size(); i++) {
            OBJFace objFace = objFaces.get(i);
            int[] vertexIndices = objFace.getVertexIndices();
            int[] normalIndices = objFace.getNormalIndices();
            int[] textureCoordinatesIndices = objFace
                    .getTextureCoordinatesIndices();

            int vertexIndex = -1;
            int normalIndex = -1;
            int textureCoordinatesIndex = -1;
            for (int j = 0; j < 3; j++) {
                vertexIndex = vertexIndices[j] - 1;
                normalIndex = normalIndices[j] - 1;
                textureCoordinatesIndex = textureCoordinatesIndices[j] - 1;
                
                // Unlike OBJ, in WebGL we need to share index position for vertices, normals and texture 
                // coordinates. That's why we have to change the used indexes.
                // We convert the tuple of all current indexes to one index (and yes, this method could add
                // a lot repeated elements :P)
                String key = vertexIndex + "-" + normalIndex + "-" + textureCoordinatesIndex;
                if (hash.containsKey(key)) {
                    Integer index = hash.get(key);
                    mesh.addIndex(index);
                } else {
                    mesh.addVertex(vertices.get(vertexIndex));
                    if (normalIndex >= 0) {
                        mesh.addNormal(normals.get(normalIndex));    
                    }
                    if (textureCoordinatesIndex >= 0) {
                        mesh.addTextureCoordinate(textureCoordinates.get(textureCoordinatesIndex));    
                    }
                    Integer index = new Integer(nextIndex);
                    mesh.addIndex(index);
                    hash.put(key, index);
                    nextIndex++;
                }
            }
        }
        
        mesh.computeBBOX();
        return mesh;
    }
    
    private static Material convertMaterial(OBJMaterial objMaterial) {
        if (objMaterial == null){
            return createDefaultMaterial();
        }

        PhongColor phongColor = convertPhongColor(objMaterial);        
        PhongTexture phongTexture = convertPhongTexture(objMaterial);

        Material material = new Material();
        material.setPhongColor(phongColor);
        material.setPhongTexture(phongTexture);
        return material;
    }
    
    private static Material createDefaultMaterial () {
        PhongColor phongColor = new PhongColor();
        phongColor.setAmbient(new Color(0.3F, 0.3F, 0.3F));
        phongColor.setDiffuse(new Color(0.7F, 0.7F, 0.7F));
        phongColor.setSpecular(new Color(0.F, 0.F, 0.F));
        phongColor.setShininess(1);
        phongColor.setAlpha(1);
        
        Material material = new Material();
        material.setPhongColor(phongColor);
        return material;
    }
    
    private static PhongColor convertPhongColor(OBJMaterial objMaterial) {
        if (objMaterial == null){
            return null;
        }
        
        PhongColor phongColor = new PhongColor();
        phongColor.setAmbient(objMaterial.getAmbient());
        phongColor.setDiffuse(objMaterial.getDiffuse());
        phongColor.setSpecular(objMaterial.getSpecular());
        phongColor.setShininess(objMaterial.getShininess());
        phongColor.setAlpha(objMaterial.getAlpha());
        
        return phongColor;
    }
    
    private static PhongTexture convertPhongTexture(OBJMaterial objMaterial) {
        if (objMaterial == null){
            return null;
        }
        
        // If there is no texture info, return null
        if (objMaterial.getMapKa() == null && objMaterial.getMapKd() == null && objMaterial.getMapKs() == null) {
            return null;
        }
        
        PhongTexture materialTexture = new PhongTexture();
        materialTexture.setMapKa(objMaterial.getMapKa());
        materialTexture.setMapKd(objMaterial.getMapKd());
        materialTexture.setMapKs(objMaterial.getMapKs());
        return materialTexture;
    }
    
    private static OBJMaterial getMaterialFromList(String materialId, List<OBJMaterial> materials) {
        for (int i = 0; i < materials.size(); i++) {
            OBJMaterial objMaterial = materials.get(i);
            if (objMaterial != null && materialId.equalsIgnoreCase(objMaterial.getMaterialId())) {
                return objMaterial;
            }
        }
        return null;
    }
}
