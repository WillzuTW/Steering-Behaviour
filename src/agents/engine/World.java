package agents.engine;

import java.util.List;

import static agents.engine.Agent.distance;

/**
 * "Fotografia" do mundo entregue a cada agente em cada quadro:
 * tempo do quadro, mouse, clique, vizinhos e tamanho da tela.
 */
public final class World {
    private final Agent current;
    private final List<Agent> agents;
    private final Vector2 mousePos;   // relativo ao centro da tela; pode ser null
    private final Vector2 clickPos;   // relativo ao centro da tela; pode ser null
    private final double secs;

    private final double width;
    private final double height;

    public World(double secs,
                 Agent current,
                 List<Agent> agents,
                 Vector2 mousePos,
                 Vector2 clickPos,
                 double width,
                 double height) {
        this.current = current;
        this.agents = agents;
        this.mousePos = mousePos;
        this.clickPos = clickPos;
        this.secs = secs;
        this.width = width;
        this.height = height;
    }

    public Vector2 getMousePos() {
        return mousePos == null ? null : mousePos.clone();
    }

    public Vector2 getClickPos() {
        return clickPos == null ? null : clickPos.clone();
    }

    public List<Agent> getNeighbors() {
        return this.agents.stream()
            .filter(a -> a != current)
            .toList();
    }

    public List<Agent> getNeighbors(int radius) {
        return this.agents.stream()
            .filter(a -> a != current)
            .filter(a -> distance(current, a) <= radius)
            .toList();
    }

    /** Segundos que se passaram desde o quadro anterior (o "dt"). */
    public double getSecs() {
        return secs;
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
