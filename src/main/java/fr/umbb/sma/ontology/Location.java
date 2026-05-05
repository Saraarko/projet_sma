package fr.umbb.sma.ontology;

import jade.content.Concept;
import java.io.Serializable;

public class Location implements Concept, Serializable {
    private static final long serialVersionUID = 1L;
    private int x;
    private int y;

    public Location() {}

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public double distanceTo(Location other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
