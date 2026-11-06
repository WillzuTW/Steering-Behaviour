package agents.student;

import agents.engine.Agent;
import agents.engine.Vector2;
import agents.engine.World;

import java.awt.*;

// COMBINAÇÃO 2: Path Following + Wander (patrulha) ou Seek (perseguição) + Obstacle Avoidance
// A aranha patrulha o caminho e, se a formiga chegar perto, vai atrás dela.
public class Aranha extends SteeringAgent {
    private static final double patrol_speed = 150;
    private static final double seek_speed = 220;   // mais devagar que a fuga da formiga (300)
    private static final double raio_deteccao = 170;

    private static final double peso_path = 2.0;
    private static final double peso_wander = 0.2;
    private static final double peso_seek = 1.0;
    private static final double peso_desvio = 3.0;

    private final Agent formiga;

    public Aranha(Agent formiga) {
        super(settings ->
          settings
            .color(Color.GREEN)
            .position(-380, -280)
            .orientationDegrees(0)
            .maxSpeed(250)
            .initialSpeed(0)
        );
        this.formiga = formiga;
    }

    @Override
    public Vector2 calculateSteering(final World world) {
        Vector2 totalForce = new Vector2();

        // Aqui é a troca de estado: ou persegue, ou patrulha (nunca os dois juntos)
        if (Vector2.distance(getPosition(), formiga.getPosition()) < raio_deteccao) {
            totalForce.add(seek(formiga.getPosition(), seek_speed).multiply(peso_seek));
        } else {
            totalForce.add(followPath(Mapa.caminho, patrol_speed, Mapa.raio_caminho).multiply(peso_path));
            totalForce.add(wander(60, 30, 0.5).multiply(peso_wander));
        }

        // Aqui o desvio entra sempre
        totalForce.add(avoidObstacles(Mapa.obstaculos, 200).multiply(peso_desvio));

        return totalForce;
    }
}
