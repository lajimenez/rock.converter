package org.rockjs.converter.loader.obj;

import org.rockjs.model.Color;

public class OBJMaterial {
   
    private String materialId = null;
    
    private Color ambient = null;

    private Color diffuse = null;
    
    private Color specular = null;
    
    private float shininess = -1;
    
    private float alpha = 1;

    private String mapKa = null;
    
    private String mapKd = null;
    
    private String mapKs = null;

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }
    
    public Color getAmbient() {
        return ambient;
    }

    public void setAmbient(Color ambient) {
        this.ambient = ambient;
    }

    public Color getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Color diffuse) {
        this.diffuse = diffuse;
    }

    public Color getSpecular() {
        return specular;
    }

    public void setSpecular(Color specular) {
        this.specular = specular;
    }
    
    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
    
    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
    
    public String getMapKa() {
        return mapKa;
    }

    public void setMapKa(String mapKa) {
        this.mapKa = mapKa;
    }

    public String getMapKd() {
        return mapKd;
    }

    public void setMapKd(String mapKd) {
        this.mapKd = mapKd;
    }
    
    public String getMapKs() {
        return mapKs;
    }

    public void setMapKs(String mapKs) {
        this.mapKs = mapKs;
    }
}
