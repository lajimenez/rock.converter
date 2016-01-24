package org.rockjs.model;

public class PhongColor {

    private Color ambient = null;

    private Color diffuse = null;
    
    private Color specular = null;
    
    private float shininess = -1;
    
    private float alpha = -1;

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
}
