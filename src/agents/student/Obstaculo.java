package agents.student;

import agents.engine.Vector2;

public class Obstaculo {
    private Vector2 centro;
    private double raio;

    public Obstaculo(double x, double y, double raio) {
        this.centro = new Vector2(x, y);
        this.raio = raio;
    }

    public Vector2 getCentro() {
        return this.centro.clone();
    }

    public double getRaio() {
        return this.raio;
    }
}
