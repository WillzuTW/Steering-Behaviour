package agents.student;

import agents.engine.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Mapa {
    public static final double raio_caminho = 20; 

    public static final List<Obstaculo> obstaculos = new ArrayList<>();
    public static final List<Vector2> caminho = new ArrayList<>();

    static {
        obstaculos.add(new Obstaculo(0, -280, 45));  
        obstaculos.add(new Obstaculo(-180, 40, 55));
        obstaculos.add(new Obstaculo(180, 80, 60));
        obstaculos.add(new Obstaculo(380, 0, 40));   

        caminho.add(new Vector2(-380, -280));
        caminho.add(new Vector2(380, -280));
        caminho.add(new Vector2(380, 280));
        caminho.add(new Vector2(-380, 280));
    }

    public static final Sugar acucar = new Sugar();

    public static Vector2 posicaoAleatoria() {
        while (true) {
            double x = Math.random() * 600 - 300;   
            double y = Math.random() * 400 - 200;   
            Vector2 ponto = new Vector2(x, y);

            boolean livre = true;
            for (Obstaculo o : obstaculos) {
                if (Vector2.distance(ponto, o.getCentro()) <= o.getRaio() + 30) {
                    livre = false;
                }
            }
            if (livre == true) return ponto;
        }
    }

    public static void draw(Graphics2D g) {
        g.setColor(new Color(200, 200, 200));
        g.setStroke(new BasicStroke((float) (2 * raio_caminho), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < caminho.size(); i++) {
            Vector2 a = caminho.get(i);
            Vector2 b = caminho.get((i + 1) % caminho.size());
            g.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
        }

        g.setColor(new Color(150, 150, 150));
        g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{6f, 6f}, 0f));
        for (int i = 0; i < caminho.size(); i++) {
            Vector2 a = caminho.get(i);
            Vector2 b = caminho.get((i + 1) % caminho.size());
            g.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
            g.fillOval((int) a.x - 4, (int) a.y - 4, 8, 8);
        }

        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(110, 90, 70));
        for (Obstaculo o : obstaculos) {
            int r = (int) o.getRaio();
            g.fillOval((int) o.getCentro().x - r, (int) o.getCentro().y - r, 2 * r, 2 * r);
        }

        acucar.draw(g);
    }
}
