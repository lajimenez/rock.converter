package org.rockjs.geom;

public class Vector2 {
    protected double x;
    protected double y;
    
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    public double module() {
        return Math.sqrt(x * x + y * y);
    }
    
    public double dotProduct(Vector2 v2) {
        return x * v2.getX() + y * v2.getY(); 
    }
    
    public void invertDirection() {
        x = -x;
        y = -y;
    }
}
