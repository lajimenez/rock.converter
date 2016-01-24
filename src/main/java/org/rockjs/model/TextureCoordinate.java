package org.rockjs.model;

public class TextureCoordinate {
    private double u;
    private double v;
    
    public TextureCoordinate(double u, double v) {
        this.u = u;
        this.v = v;
    }
 
    public double getU() {
        return u;
    }
    
    public double getV() {
        return v;
    }

    public double getNormalizedU() {
        double u = this.u;
        while (u > 1) {
            u = u - 1;
        }
        return u;
    }

    public double getNormalizedV() {
        double v = this.v;
        while (v > 1) {
            v = v -1;
        }
        return v;
    }
}
