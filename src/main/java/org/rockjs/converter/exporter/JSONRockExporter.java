package org.rockjs.converter.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import org.rockjs.ConverterException;
import org.rockjs.Logger;
import org.rockjs.converter.ConverterConfig;
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
 * This class is responsible to export a model to JSON rock format.
 * 
 * This class is horrible. What a lot time wasted debugging bad JSON. I should have added a nice
 * JSON library with maven and used it...
 * Well, at least my original (and stupid) commitment of writing all the code by myself it's still alive... 
 * 
 * @author Luis Alberto Jiménez
 */
public class JSONRockExporter implements IExporter {

    private static final int MAXIMUN_INDEX_VALUE = 65535;

    private DecimalFormat decimalFormat;

    public JSONRockExporter() {
        decimalFormat = new DecimalFormat("#.####");
        decimalFormat.setGroupingUsed(false);
        DecimalFormatSymbols dfs = decimalFormat.getDecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(dfs);
    }

    public void export(List<Model> models, ConverterConfig config) throws ConverterException {
        Writer writer = null;
        File resultFile = config.getResultFile();
        
        try {
            doValidations(models);
            
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(resultFile), "utf-8"));

            writer.write("[\n");
            for (int i = 0; i < models.size(); i++) {
                if (i > 0) {
                    writer.write(",\n");
                }
                writer.write(getmodelAsJSON(models.get(i)));
            }
            writer.write("]");

        } catch (IOException e) {
            throw new ConverterException(e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void doValidations (List<Model> models) {
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            validateModel(model);
        }
    }
    
    private void validateModel(Model model) {
        validateIndexes(model);
        validateTextureCoordinates(model);
    }
    
    private void validateIndexes(Model model) {
        int maxIndex = model.getMesh().getVertices().size();
        if (maxIndex >= MAXIMUN_INDEX_VALUE) {
            Logger.log("Model '" + model.getModelId() + "' has more than " + MAXIMUN_INDEX_VALUE +
                    " and you might have problems when visualizing it (sure if you use rock :)");
        }
    }
    
    private void validateTextureCoordinates(Model model) {
        List<TextureCoordinate> textureCoordinates = model.getMesh().getTextureCoordinates();
        for (TextureCoordinate textureCoordinate : textureCoordinates) {
            if (textureCoordinate.getU() > 1 || textureCoordinate.getV() > 1) {
                Logger.log("Model '" + model.getModelId() + 
                        "' has texture coordinate greater than 1 that will be normalized");
                return;
            }
        }
    }
    
    private String getmodelAsJSON(Model model) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("{\n");
        
        sb.append(getMeshAsJSON(model.getMesh()));
        sb.append(",\n");
        
        sb.append(getMaterialAsJSON(model.getMaterial()));

        sb.append("\n}\n");
        return sb.toString();
    }
    
    private String getMeshAsJSON(Mesh mesh) {
        StringBuilder sb = new StringBuilder();
        List<Vertex> vertices = mesh.getVertices();
        List<Normal> normals = mesh.getNormals();
        List<TextureCoordinate> textureCoordinates = mesh.getTextureCoordinates();
        List<Integer> indexes = mesh.getIndexes();

        sb.append("\"mesh\": {\n");
        
        sb.append("\t\"vertices\": [");
        Vertex vertex;
        for (int i = 0; i < vertices.size(); i++) {
            vertex = vertices.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append(getDoubleAsJSON(vertex.getX()) + "," + 
                    getDoubleAsJSON(vertex.getY()) + "," + 
                    getDoubleAsJSON(vertex.getZ()));
        }
        sb.append("],\n");
        
        sb.append("\t\"normals\": [");
        Normal normal;
        for (int i = 0; i < normals.size(); i++) {
            normal = normals.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append(getDoubleAsJSON(normal.getX()) + "," + 
                    getDoubleAsJSON(normal.getY()) + "," + 
                    getDoubleAsJSON(normal.getZ()));
        }
        sb.append("],\n");
        
        sb.append("\t\"textureCoordinates\": [");
        TextureCoordinate textureCoordinate;
        for (int i = 0; i < textureCoordinates.size(); i++) {
            textureCoordinate = textureCoordinates.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append(getDoubleAsJSON(textureCoordinate.getNormalizedU()) + "," + 
                    getDoubleAsJSON(invertTextureCoordinateValue(textureCoordinate.getNormalizedV())));
        }
        sb.append("],\n");
        
        sb.append("\t\"indexes\": [");
        Integer index;
        for (int i = 0; i < indexes.size(); i++) {
            index = indexes.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append(index.toString());
        }
        sb.append("],\n");
        
        double[] BBOX = mesh.getBBOX();
        sb.append("\t\"BBOX\": [");
        for (int i = 0; i < BBOX.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(getDoubleAsJSON(BBOX[i]));
        }                
        sb.append("]\n");
        
        sb.append("}");
        
        return sb.toString();
    }
    
    private double invertTextureCoordinateValue(double value) {
        return 1. - value;
    }
    
    private String getMaterialAsJSON(Material material) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("\"material\": {\n");
        sb.append(getPhongColorAsJSON(material.getPhongColor()));
        sb.append("\n,\n");
        sb.append(getModelTextureAsJSON(material.getPhongTexture()));
        sb.append("\n}");
        
        return sb.toString();
    }
    
    private String getPhongColorAsJSON(PhongColor phongColor) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"phongColor\": ");
        if (phongColor != null) {
            sb.append("{\n");
            sb.append("\t\"ambient\": " + getColorAsJSON(phongColor.getAmbient()) + ",\n");
            sb.append("\t\"diffuse\": " + getColorAsJSON(phongColor.getDiffuse()) + ",\n");
            sb.append("\t\"specular\": " + getColorAsJSON(phongColor.getSpecular()) + ",\n");
            sb.append("\t\"shininess\": " + phongColor.getShininess() + ",\n");
            sb.append("\t\"alpha\": " + phongColor.getAlpha() + "\n");
            sb.append("}");
        } else {
            sb.append("null");
        }
        return sb.toString();
    }
    
    private String getModelTextureAsJSON(PhongTexture phongTexture) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"phongTexture\": ");
        if (phongTexture != null) {
            sb.append("{\n");
            sb.append("\t\"ambient\": " + getStringAsJSON(phongTexture.getMapKa()) + ",\n");
            sb.append("\t\"diffuse\": " + getStringAsJSON(phongTexture.getMapKd()) + ",\n");
            sb.append("\t\"specular\": " + getStringAsJSON(phongTexture.getMapKs()) + "\n");
            sb.append("}");
        } else {
            sb.append("null");
        }
        return sb.toString();
    }
    
    private String getColorAsJSON(Color color) {
        return "[" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + "]";
    }   
    
    private String getStringAsJSON(String value) {
        if (value == null) {
            return "null";
        } else {
            return "\"" + value + "\"";
        }
    }

    private String getDoubleAsJSON(double value) {
        return decimalFormat.format(value);
    }
}
