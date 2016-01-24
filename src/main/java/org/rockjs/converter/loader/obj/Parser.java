package org.rockjs.converter.loader.obj;

import java.util.ArrayList;
import java.util.List;

import org.rockjs.model.Color;
import org.rockjs.model.Normal;
import org.rockjs.model.TextureCoordinate;
import org.rockjs.model.Vertex;
import org.rockjs.utils.FileUtils;

/**
 * This class process OBJ lines and convert to model's element
 * 
 * @author Luis Alberto Jiménez
 */
public class Parser {

    public static Vertex parseVertex(String[] parts) {
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        return new Vertex(x, y, z);
    }
    
    public static Normal parseNormal(String[] parts) {
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        return new Normal(x, y, z);
    }
    
    public static TextureCoordinate parseTextureCoordinate(String[] parts) {
        double u = Double.parseDouble(parts[1]);
        double v = Double.parseDouble(parts[2]);
        return new TextureCoordinate(u, v);
    }
    
    public static List<OBJFace> parseFaces(String[] parts) {
        List<OBJFace> faces = new ArrayList<OBJFace>();
        
        // We extract 3 because: 
        // in TRIANGLE_FAN, the number of triangles are (#vertices - 2) (so we have to remove 2)
        // and we have to remove another because the first element value is the type 'f'
        for (int i = 0; i < parts.length - 3; i++) {
            // we have to start at position 2 because we skip the type 'f' and
            // the first value (it will be part of all vertices)
            faces.add(parseFace(parts, i + 2));
        }
        return faces;
    }
    
    private static OBJFace parseFace(String[] parts, int index) {
        OBJFace face = new OBJFace();
        String verticesInfo[] = new String[3];
        verticesInfo[0] = parts[1];
        verticesInfo[1] = parts[index];
        verticesInfo[2] = parts[index + 1];
        
        int vertexIndices[] = new int[3];
        int normalIndices[] = new int[3];    
        int textureCoordinatesIndices[] = new int[3];
        String vertexInfo[];
        
        for (int i = 0; i < 3; i++) {
            vertexInfo = verticesInfo[i].split("/");
            
            // first part must always exists and is the vertex position
            String strVertexInfo = vertexInfo[0];
            String strTextureCoordinateInfo = null;
            String strNormalInfo = null;
            
            // If there are more than 2 parts, second is texture coodinate
            if (vertexInfo.length > 1) {
                strTextureCoordinateInfo = vertexInfo[1].trim();                
            }
            
            // If there are 3 parts, last is normal
            if (vertexInfo.length == 3) {
                strNormalInfo = vertexInfo[2].trim();
            }
            
            vertexIndices[i] = parseInt(strVertexInfo);
            normalIndices[i] = parseInt(strNormalInfo);
            textureCoordinatesIndices[i] = parseInt(strTextureCoordinateInfo);
        }
        
        face.setVertexIndices(vertexIndices);
        face.setNormalIndices(normalIndices);
        face.setTextureCoordinatesIndices(textureCoordinatesIndices);
        
        return face;
    }
    
    private static int parseInt(String value) {
        if (value != null && value.length() > 0) {
            return Integer.parseInt(value);
        } else {
            return OBJFace.NO_INDEX;
        }
    }
    
    public static Color parseColor(String[] parts) {        
        float red = Float.parseFloat(parts[1]);
        float green = Float.parseFloat(parts[2]);
        float blue = Float.parseFloat(parts[3]);
        return new Color(red, green, blue);
    }
    
    public static String parseTexture(String[] parts) {
        return FileUtils.getFileName(parts[1]);
    }

}
