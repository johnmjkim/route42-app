package com.comp6442.route42.data.model;

public class MapPoint< T extends Number > {
    private final double x;
    private final double y;
    private final double z;

    public MapPoint(T[] coordinate) {
        this.x = coordinate[0].doubleValue();
        this.y = coordinate[1].doubleValue();
        this.z = coordinate[2].doubleValue();
    }
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public double getDistance(MapPoint<T> p2) {
        return Math.sqrt( Math.pow( this.x - p2.x,2) +
                Math.pow( this.y - p2.y,2) +
                Math.pow( this.z - p2.z,2) );
    }
}
