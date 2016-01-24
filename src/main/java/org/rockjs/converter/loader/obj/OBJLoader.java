package org.rockjs.converter.loader.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rockjs.ConverterException;
import org.rockjs.converter.ConverterConfig;
import org.rockjs.converter.loader.ILoader;
import org.rockjs.model.Model;
import org.rockjs.model.Normal;
import org.rockjs.model.TextureCoordinate;
import org.rockjs.model.Vertex;

/**
 * This class is responsible to load a model from an OBJ file
 * 
 * @author Luis Alberto Jiménez
 */
public class OBJLoader implements ILoader {

    public static final String FORMAT = "OBJ";

    private static final String VERTEX = "v";

    private static final String NORMAL = "vn";

    private static final String TEXTURE_COORDINTE = "vt";

    private static final String FACE = "f";

    private static final String OBJECT = "o";

    private static final String GROUP = "g";

    private static final String USEMTL = "usemtl";

    private static final String MTLLIB = "mtllib";
    
    private static final String SMOOTH_GROUP = "s";
    
    private static final String SMOOTH_GROUP_OFF = "off";

    private File modelFile = null;

    private List<Vertex> vertices = null;
    private List<Normal> normals = null;
    private List<TextureCoordinate> textureCoordinates = null;

    private List<OBJMesh> meshes = null;
    private List<OBJMaterial> materials = null;
    private OBJMesh currentObjectMesh = null;
    private OBJMesh currentMesh = null;

    private String currentSmoothGroup = null;

    private MeshType currentMeshType = null;

    /*
     * (non-Javadoc)
     * @see org.rockjs.converter.loader.ILoader#loadModels(org.rockjs.converter.ConverterConfig)
     */
    public List<Model> loadModels(ConverterConfig config) throws ConverterException {
        this.modelFile = config.getModelFile();
        loadOBJ();

        // we have to update the last mesh
        currentMesh.updateFromObjectMesh(currentObjectMesh);
        meshes.add(currentMesh);        
        
        clearEmptyMeshes();
        
        // ADVICE: Compute normals not necessary generate correct normals.
        // If the normal groups (s XX) are not set correctly, the normals
        // may not be correct (specially in vertices used for 'different' parts
        // of the object (intersection vertices))
        if (config.isComputeNormals()){
            NormalHelper.computeNormalsOBJMeshes(meshes, vertices, normals);    
        }
        
        if (config.isRepairNormals()) {
            NormalHelper.repairNormalsOBJMeshes(meshes, vertices, normals);
        }
        
        return ModelLoader.loadFromOBJMeshes(meshes, materials, vertices, normals, textureCoordinates);
    }

    private void clearEmptyMeshes() {
        List<Integer> indexToRemove = new ArrayList<Integer>();
        
        for (int i = 0; i < meshes.size(); i++) {
            OBJMesh mesh = meshes.get(i);
            if (mesh.getFaces().size() == 0) {
                indexToRemove.add(i);
            }
        }
        
        for (int i = indexToRemove.size() - 1; i >= 0; i--) {
            meshes.remove(indexToRemove.get(i).intValue());
        }
    }
    
    private void loadOBJ() throws ConverterException {
        // init properties
        vertices = new ArrayList<Vertex>();
        normals = new ArrayList<Normal>();
        textureCoordinates = new ArrayList<TextureCoordinate>();
        meshes = new ArrayList<OBJMesh>();
        materials = new ArrayList<OBJMaterial>();

        currentObjectMesh = new OBJMesh();
        currentMesh = new OBJMesh();

        currentSmoothGroup = null;

        currentMeshType = MeshType.o;

        parseFile();
    }
    
    private void parseFile() throws ConverterException {
        BufferedReader br = null;

        try {
            String line;
            br = new BufferedReader(new FileReader(modelFile));
            line = br.readLine();

            while (line != null) {
                parseLine(line);
                line = br.readLine();
            }

        } catch (IOException e) {
            throw new ConverterException(e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void parseLine(String line) throws ConverterException {
        String[] parts = line.trim().split("\\s+");
        String type = parts[0];

        if (VERTEX.equalsIgnoreCase(type)) {
            vertices.add(Parser.parseVertex(parts));
        } else if (NORMAL.equalsIgnoreCase(type)) {
            normals.add(Parser.parseNormal(parts));
        } else if (TEXTURE_COORDINTE.equalsIgnoreCase(type)) {
            textureCoordinates.add(Parser.parseTextureCoordinate(parts));
        } else if (FACE.equalsIgnoreCase(type)) {
            List<OBJFace> faces = Parser.parseFaces(parts);
            for (int i = 0; i < faces.size(); i++) {
                OBJFace face = faces.get(i);
                face.setSmoothGroup(currentSmoothGroup);
            }
            currentMesh.addFaces(faces);
        } else if (OBJECT.equalsIgnoreCase(type)) {
            currentMeshType = MeshType.o;
            createNewGroupMesh(parts);
            currentObjectMesh = new OBJMesh();
        } else if (GROUP.equalsIgnoreCase(type)) {
            currentMeshType = MeshType.g;
            createNewGroupMesh(parts);
        }  else if (SMOOTH_GROUP.equalsIgnoreCase(type)) {
            String smoothGroup = parts[1];
            if (SMOOTH_GROUP_OFF.equalsIgnoreCase(smoothGroup)) {
                currentSmoothGroup = null;
            } else {
                currentSmoothGroup = smoothGroup;
            }
        } else if (USEMTL.equalsIgnoreCase(type)) {
            String materialId = parts[1];
            if (currentMeshType == MeshType.o) {
                currentObjectMesh.setMaterialId(materialId);
            } else {
                currentMesh.setMaterialId(materialId);
            }
        } else if (MTLLIB.equalsIgnoreCase(type)) {
            String materialFileName = parts[1];
            OBJMaterialLoader materialLoader = new OBJMaterialLoader();
            materials.addAll(materialLoader.loadMaterials(materialFileName,
                    modelFile));
        }
    }

    private void createNewGroupMesh(String[] parts) {
        // the material can be set at object mesh level
        currentMesh.updateFromObjectMesh(currentObjectMesh);
        meshes.add(currentMesh);
        currentMesh = new OBJMesh();
        if (parts.length > 1) {
            currentMesh.setMeshId(parts[1]);
        }
        currentSmoothGroup = null;
    }

    private enum MeshType {
        o, g
    };
}
