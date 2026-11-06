package agents.student;

import agents.engine.Agent;

import java.util.ArrayList;
import java.util.List;

public class Setup {
    /**
     * Retorne uma lista com todos os agentes que serão desenhados.
     */
    public List<Agent> createAgents() {
        Formiga formiga = new Formiga(Mapa.acucar);
        Aranha aranha = new Aranha(formiga);
        formiga.setAranha(aranha);

        List<Agent> agentes = new ArrayList<>();
        agentes.add(formiga);
        agentes.add(aranha);
        return agentes;
    }
}
