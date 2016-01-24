package org.rockjs.model;

import org.rockjs.geom.Vector3;

public class Normal extends Vector3{
    public Normal(double x, double y, double z) {
        super(x, y, z);
    }
    
    public boolean isValid() {
        return !Double.isInfinite(x) && !Double.isNaN(x) && 
                !Double.isInfinite(y) && !Double.isNaN(y) &&
                !Double.isInfinite(z) && !Double.isNaN(z);
    }
    
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof Normal)) {
            return false;
        }
        
        Normal normal = (Normal) o;
        return x == normal.getX() && y == normal.getY() && z == normal.getZ();
    }
    
    public int hashCode() {
        if (!isValid()) {
            return Integer.MIN_VALUE;
        } else {
            Double sum = new Double((x + y + z) * 1000000);
            return sum.intValue();
        }
        
    }
}
