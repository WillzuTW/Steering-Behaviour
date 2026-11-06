package agents.engine;

import java.awt.*;
import java.awt.geom.*;
import java.util.function.Consumer;

import static agents.engine.Vector2.*;
import static java.lang.Math.toRadians;

/**
 * Agente genérico: um "corpo" com posição, velocidade, massa e limites.
 * NÃO contém nenhum comportamento. Quem decide a força é a subclasse,
 * através do método calculateSteering().
 */
public abstract class Agent implements Cloneable {
    private final Color color;
    private final double mass;
    private final double maxForce;
    private final double maxSpeed;
    private Vector2 position;
    private Vector2 velocity;

    // Última força aplicada (já limitada), guardada só para desenhar a seta de debug
    private Vector2 lastSteering = vec2();

    public Agent(Consumer<Settings> settings) {
        var s = new Settings();
        settings.accept(s);
        this.color = s.color;
        this.position = s.position;
        this.velocity = Vector2.byAngleSize(s.orientation, s.initialSpeed);
        this.mass = s.mass;
        this.maxForce = s.maxForce;
        this.maxSpeed = s.maxSpeed;
    }

    // ---- Leitura do estado do agente ----
    public Vector2 getPosition() { return position.clone(); }
    public Vector2 getVelocity() { return velocity.clone(); }
    public double getSpeed() { return velocity.size(); }

    public Vector2 getDirection() { return velocity.isZero() ? byAngle(0) : normalize(velocity); }
    public double getMass() { return mass; }
    public double getMaxForce() { return maxForce; }
    public double getMaxSpeed() { return maxSpeed; }
    private Vector2 getLastSteering() { return lastSteering == null ? null : lastSteering.clone(); }

    /**
     * Deve devolver a força total (soma de todas as forças) que age
     * sobre o agente neste quadro. Implementado pela subclasse.
     */
    public abstract Vector2 calculateSteering(World world);

    /**
     * Física básica, executada uma vez por quadro:
     * força -> aceleração -> velocidade -> posição.
     */
    void update(World world) {
        // 1. Pergunta à subclasse qual a força total deste quadro
        final var steeringForce = calculateSteering(world);
        if (steeringForce == null) {
            lastSteering = vec2();
            return;
        }

        // 2. Limita a força ao máximo permitido
        lastSteering = truncate(steeringForce, maxForce);

        // 3. Força -> variação de velocidade:  dv = (F / m) * dt
        final var dt = world.getSecs();
        final var impulse = multiply(lastSteering, dt);   // F * dt
        final var deltaVelocity = divide(impulse, mass);  // (F * dt) / m

        // 4. Soma na velocidade e limita à velocidade máxima
        velocity = truncate(add(velocity, deltaVelocity), maxSpeed);

        // 5. Velocidade -> posição:  p = p + v * dt
        position = add(position, multiply(velocity, dt));

        // 6. Se sair da tela, reaparece do lado oposto ("mundo dá a volta")
        final var w = world.getWidth() / 2.0;
        final var h = world.getHeight() / 2.0;
        if (position.x < -(w + 20)) position.x =  w;
        if (position.x >  (w + 20)) position.x = -w;
        if (position.y < -(h + 20)) position.y =  h;
        if (position.y >  (h + 20)) position.y = -h;
    }

    // -----------------------------------------------------
    // Desenho (Java2D)
    // -----------------------------------------------------
    void draw(Graphics2D g, boolean debug) {
        // Desenha o agente girado na direção em que está andando
        final var g2 = (Graphics2D) g.create();
        g2.translate(position.x, position.y);
        g2.rotate(velocity.getAngle());   // radianos

        drawBody(g2, color);

        g2.dispose();

        // Setas de debug no espaço do MUNDO (sem rotação)
        if (debug) {
            drawDebugArrows(g);
        }
    }

    // Forma genérica: um triângulo apontando para +X (a "frente" do agente)
    private static void drawBody(Graphics2D g, Color bodyColor) {
        final var oldPaint = g.getPaint();
        final var oldStroke = g.getStroke();

        final var body = new Path2D.Double();
        body.moveTo( 16,  0);   // ponta (frente)
        body.lineTo(-10, -8);
        body.lineTo(-10,  8);
        body.closePath();

        g.setPaint(bodyColor);
        g.fill(body);
        g.setPaint(bodyColor.darker());
        g.setStroke(new BasicStroke(1.5f));
        g.draw(body);

        g.setPaint(oldPaint);
        g.setStroke(oldStroke);
    }

    // Azul = velocidade | Laranja = força aplicada neste quadro
    private void drawDebugArrows(Graphics2D g) {
        final var origin = add(position, multiply(getDirection(), 12));
        drawArrow(g, origin, velocity, 0.20, new Color(0, 0, 255));           // AZUL
        drawArrow(g, origin, getLastSteering(), 0.10, new Color(255, 69, 0)); // LARANJA
    }

    private static void drawArrow(Graphics2D g, Vector2 origin, Vector2 vector, double scale, Color color) {
        if (Vector2.isZero(vector)) return;

        final var tip = add(origin, multiply(vector, scale));
        final var dir = subtract(tip, origin);

        final var oldComp = g.getComposite();
        final var oldStroke = g.getStroke();
        final var oldColor = g.getColor();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        g.setColor(color);
        g.setStroke(new BasicStroke(3f));

        g.draw(new Line2D.Double(origin.x, origin.y, tip.x, tip.y));

        final var angle = dir.getAngle();
        final var a = toRadians(30);
        final var head = 8.0;
        final var p1 = subtract(tip, byAngleSize(angle - a, head));
        final var p2 = subtract(tip, byAngleSize(angle + a, head));
        g.draw(new Line2D.Double(tip.x, tip.y, p1.x, p1.y));
        g.draw(new Line2D.Double(tip.x, tip.y, p2.x, p2.y));

        g.setComposite(oldComp);
        g.setStroke(oldStroke);
        g.setColor(oldColor);
    }

    @Override
    public Agent clone() {
        try {
            final var other = (Agent) super.clone();
            other.position = position.clone();
            other.velocity = velocity.clone();
            return other;
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }

    public static double distance(Agent a1, Agent a2) {
        return Vector2.distance(a1.position, a2.position);
    }
}
