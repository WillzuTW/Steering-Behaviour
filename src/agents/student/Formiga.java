package agents.student;

import agents.engine.Agent;
import agents.engine.Vector2;
import agents.engine.World;

import java.awt.*;

// COMBINAÇÃO 1: Arrive + Flee + Obstacle Avoidance
// A formiga vai até o açúcar, foge da aranha e desvia das pedras.
public class Formiga extends SteeringAgent {
    private static final double arrive_speed = 200;
    private static final double flee_speed = 300;
    private static final double raio_frenagem = 120;
    private static final double distancia_perigo = 150;  // começa a fugir
    private static final double distancia_segura = 260;  // só para de fugir aqui
    private static final double distancia_pegar = 15;

    private static final double peso_arrive = 1.0;
    private static final double peso_flee = 2.0;
    private static final double peso_desvio = 3.0;

    private final Sugar sugar;
    private Agent aranha;
    private boolean fugindo = false;

    public Formiga(Sugar sugar) {
        super(settings ->
          settings
            .color(Color.BLUE)
            .position(Mapa.posicaoAleatoria())
            .randomOrientation()
            .maxSpeed(300)
            .initialSpeed(0)
        );
        this.sugar = sugar;
    }

    // Aqui é apenas um método para definir a referência da Aranha.
    public void setAranha(Agent aranha) {
        this.aranha = aranha;
    }

    public boolean getFugindo() {
        return this.fugindo;
    }

    @Override
    public Vector2 calculateSteering(final World world) {
        Vector2 totalForce = new Vector2();

        // Aqui se pegou o açúcar, ele some e aparece em outro lugar
        if (Vector2.distance(getPosition(), sugar.getPosition()) < distancia_pegar) {
            sugar.respawn();
        }

        // Aqui é a histerese: começa a fugir a 150, mas só para de fugir a 260.
        // Sem isso a formiga fica indo e voltando na borda dos 150 e a aranha fica girando atrás dela.
        double distanciaAranha = Vector2.distance(getPosition(), aranha.getPosition());
        if (distanciaAranha < distancia_perigo) fugindo = true;
        if (distanciaAranha > distancia_segura) fugindo = false;

        if (fugindo == true) totalForce.add(flee(aranha.getPosition(), flee_speed).multiply(peso_flee));
        totalForce.add(arrive(sugar.getPosition(), arrive_speed, raio_frenagem).multiply(peso_arrive));
        totalForce.add(avoidObstacles(Mapa.obstaculos, 200).multiply(peso_desvio));

        return totalForce;
    }
}
