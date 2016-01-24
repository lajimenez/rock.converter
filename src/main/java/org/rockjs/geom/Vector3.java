package org.rockjs.geom;

public class Vector3 extends Vector2{
    protected double z;
    
    public Vector3(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    public double getZ() {
        return z;
    }
    
    public double module() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public double dotProduct(Vector3 v2) {
        return x * v2.getX() + y * v2.getY() + z * v2.getZ(); 
    }
    
    public Vector3 crossProduct(Vector3 v2) {
        double x = this.y * v2.getZ() - v2.getY() * this.z;
        double y = this.x * v2.getZ() - v2.getX() * this.z;
        double z = this.x * v2.getY() - v2.getX() * this.y;
        
        return new Vector3(x, -y, z);
    }
    
    public void invertDirection() {
        x = -x;
        y = -y;
        z = -z;
    }
    
    public void normalize() {
        double module = this.module();
        x = x / module;
        y = y / module;
        z = z / module;
    }
    
    public void add(Vector3 v2) {
        x = (x + v2.getX()) / 2.;
        y = (y + v2.getY()) / 2.;
        z = (z + v2.getZ()) / 2.;
    }
}
