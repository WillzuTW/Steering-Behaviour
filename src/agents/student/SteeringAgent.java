package agents.student;

import agents.engine.Agent;
import agents.engine.Settings;
import agents.engine.Vector2;

import java.util.List;
import java.util.function.Consumer;

public abstract class SteeringAgent extends Agent {
    private static final double raio_agente = 12;     
    private static final double previsao = 50;         
    private static final double avanco_no_caminho = 30; 

    private double wanderAngle = 0;

    public SteeringAgent(Consumer<Settings> settings) {
        super(settings);
    }
    public Vector2 seek(Vector2 alvo, double velocidade) {
        Vector2 paraAlvo = Vector2.subtract(alvo, getPosition());
        Vector2 desejada = paraAlvo.resize(velocidade);
        return Vector2.subtract(desejada, getVelocity());
    }

    public Vector2 flee(Vector2 ameaca, double velocidade) {
        Vector2 longeDaAmeaca = Vector2.subtract(getPosition(), ameaca);
        Vector2 desejada = longeDaAmeaca.resize(velocidade);
        return Vector2.subtract(desejada, getVelocity());
    }

    public Vector2 arrive(Vector2 alvo, double velocidade, double raioFrenagem) {
        Vector2 paraAlvo = Vector2.subtract(alvo, getPosition());
        double distancia = paraAlvo.size();

        if (distancia < 3) return Vector2.negate(getVelocity());

        double v;
        if (distancia < raioFrenagem) {
            v = velocidade * (distancia / raioFrenagem);
        } else {
            v = velocidade;                              
        }
        Vector2 desejada = paraAlvo.resize(v);
        return Vector2.subtract(desejada, getVelocity());
    }

    public Vector2 wander(double distanciaCirculo, double raioCirculo, double variacao) {
        wanderAngle += (Math.random() * 2 - 1) * variacao; 
        Vector2 centro = Vector2.multiply(getDirection(), distanciaCirculo);
        Vector2 deslocamento = Vector2.byAngleSize(wanderAngle, raioCirculo);
        return Vector2.add(centro, deslocamento);
    }

    public Vector2 avoidObstacles(List<Obstaculo> obstaculos, double visao) {
        double alcance = visao * getSpeed() / getMaxSpeed(); 
        Vector2 frente = Vector2.add(getPosition(), Vector2.multiply(getDirection(), alcance));
        Vector2 meio = Vector2.add(getPosition(), Vector2.multiply(getDirection(), alcance * 0.5));

        Obstaculo maisProximo = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Obstaculo o : obstaculos) {
            double raio = o.getRaio() + raio_agente; 
            boolean colide = false;
            if (Vector2.distance(frente, o.getCentro()) <= raio) colide = true;
            if (Vector2.distance(meio, o.getCentro()) <= raio) colide = true;
            if (Vector2.distance(getPosition(), o.getCentro()) <= raio) colide = true;

            double d = Vector2.distance(getPosition(), o.getCentro());
            if (colide == true && d < menorDistancia) {
                menorDistancia = d;
                maisProximo = o;
            }
        }

        if (maisProximo == null) return new Vector2(); 
        Vector2 empurrao = Vector2.subtract(frente, maisProximo.getCentro());
        return empurrao.resize(getMaxForce());
    }

    public Vector2 followPath(List<Vector2> caminho, double velocidade, double raioCaminho) {
        Vector2 futuro = Vector2.add(getPosition(), Vector2.multiply(getDirection(), previsao));

        Vector2 alvo = null;
        double menorDistancia = Double.MAX_VALUE;

        for (int i = 0; i < caminho.size(); i++) {
            Vector2 a = caminho.get(i);
            Vector2 b = caminho.get((i + 1) % caminho.size()); 
            Vector2 normal = pontoNormal(futuro, a, b);
            double d = Vector2.distance(futuro, normal);

            if (d < menorDistancia) {
                menorDistancia = d;
                Vector2 sentido = Vector2.normalize(Vector2.subtract(b, a));
                alvo = Vector2.add(normal, Vector2.multiply(sentido, avanco_no_caminho));
            }
        }

        if (menorDistancia <= raioCaminho && getSpeed() > velocidade * 0.5) return new Vector2();
        return seek(alvo, velocidade);
    }

    public Vector2 pontoNormal(Vector2 p, Vector2 a, Vector2 b) {
        Vector2 ab = Vector2.subtract(b, a);
        double t = Vector2.subtract(p, a).dot(ab) / ab.sizeSqr(); 
        if (t < 0) t = 0;
        if (t > 1) t = 1;
        return Vector2.add(a, Vector2.multiply(ab, t));
    }
}
