package agents.student;

import agents.engine.Vector2;

import java.awt.*;

public class Sugar {
    private Vector2 posicao;

    public Sugar() {
        respawn();
    }

    public Vector2 getPosition() {
        return this.posicao.clone();
    }

    public void respawn() {
        this.posicao = Mapa.posicaoAleatoria();
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillOval((int) posicao.x - 7, (int) posicao.y - 7, 14, 14);
        g.setColor(Color.RED.darker());
        g.drawOval((int) posicao.x - 7, (int) posicao.y - 7, 14, 14);
    }
}
