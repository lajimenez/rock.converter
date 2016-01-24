package org.rockjs.model;

public class Material {

    private PhongColor phongColor;

    private PhongTexture phongTexture;

    public PhongColor getPhongColor() {
        return phongColor;
    }

    public void setPhongColor(PhongColor phongColor) {
        this.phongColor = phongColor;
    }

    public PhongTexture getPhongTexture() {
        return phongTexture;
    }

    public void setPhongTexture(PhongTexture phongTexture) {
        this.phongTexture = phongTexture;
    }

}
