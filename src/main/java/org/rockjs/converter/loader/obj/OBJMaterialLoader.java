package org.rockjs.converter.loader.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rockjs.ConverterException;
import org.rockjs.utils.FileUtils;

/**
 * This class is responsible to load a material file
 * 
 * @author Luis Alberto Jiménez
 */
public class OBJMaterialLoader {

    private static final String NEWMTL = "newmtl";
    
    private static final String KA = "Ka";
    
    private static final String KD = "Kd";
    
    private static final String KS = "Ks";
    
    private static final String NS = "Ns";
    
    private static final String D = "d";
    
    private static final String MAP_KA = "map_Ka";
    
    private static final String MAP_KD = "map_Kd";
    
    private static final String MAP_KS = "map_Ks";    
    
    private List<OBJMaterial> materials = null;
    
    private OBJMaterial currentMaterial = null;

    public List<OBJMaterial> loadMaterials(String materialFileName, File modelFile) throws ConverterException {
        materials = new ArrayList<OBJMaterial>();

        File materialFile = getMaterialFile(materialFileName, modelFile);
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader(materialFile));
            line = br.readLine();

            while (line != null) {
                parseLine(line);
                line = br.readLine();
            }
            
            materials.add(currentMaterial);
            return materials;

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

    private File getMaterialFile(String materialFileName, File modelFile) {
        File modelFolder = modelFile.getParentFile();
        String parentFolder = "";
        if (modelFolder != null) {
            parentFolder = modelFolder.getAbsolutePath() + File.separator;
        }

        return new File(parentFolder + FileUtils.getFileName(materialFileName));
    }

    private void parseLine(String line) {
        String[] parts = line.trim().split("\\s+");
        String type = parts[0];

        if (NEWMTL.equalsIgnoreCase(type)) {
            materials.add(currentMaterial);
            currentMaterial = new OBJMaterial();
            currentMaterial.setMaterialId(parts[1]);
        } else if (KA.equalsIgnoreCase(type)) {
            currentMaterial.setAmbient(Parser.parseColor(parts));
        } else if (KD.equalsIgnoreCase(type)) {
            currentMaterial.setDiffuse(Parser.parseColor(parts));
        } else if (KS.equalsIgnoreCase(type)) {
            currentMaterial.setSpecular(Parser.parseColor(parts));
        } else if (NS.equalsIgnoreCase(type)) {
            currentMaterial.setShininess(Float.parseFloat(parts[1]));
        } else if (D.equalsIgnoreCase(type)) {
            currentMaterial.setAlpha(Float.parseFloat(parts[1]));
        } else if (MAP_KA.equalsIgnoreCase(type)) {
            currentMaterial.setMapKa(Parser.parseTexture(parts));
        } else if (MAP_KD.equalsIgnoreCase(type)) {
            currentMaterial.setMapKd(Parser.parseTexture(parts));
        } else if (MAP_KS.equalsIgnoreCase(type)) {
            currentMaterial.setMapKs(Parser.parseTexture(parts));
        } 
    }    
}
