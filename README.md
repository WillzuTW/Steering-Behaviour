# Steering Behaviors: Formiga e Aranha

> Simulação de agentes autônomos usando Steering Behaviors. Uma formiga busca açúcar pelo mapa enquanto foge de uma aranha, que patrulha um caminho e persegue a formiga quando ela chega perto. Os dois desviam das pedras do mapa.

## Como compilar e executar

Você pode rodar este projeto de duas maneiras:

### Opção 1: Via Editor/IDE
1. Baixe ou clone o projeto para o seu computador.
2. Abra a pasta do projeto no seu editor de código ou IDE.
3. Execute o projeto diretamente pelo botão de "Run" do seu editor na classe `Window` (pasta `src/agents/engine`).

### Opção 2: Via Terminal
1. Baixe ou clone o projeto para o seu computador.
2. Navegue até a pasta `src` do projeto.
3. No Windows, você pode clicar na barra de endereços do explorador de arquivos, digitar `cmd` e apertar Enter para abrir o terminal direto nessa pasta.
4. Compile os arquivos digitando o comando abaixo:
   ```
   javac agents/engine/*.java agents/student/*.java
   ```
5. Em seguida, rode o programa compilado com o comando:
   ```
   java agents.engine.Window
   ```

## Agentes

### Formiga
Esse agente é responsável por buscar o açúcar pelo mapa; Fugir da aranha quando ela chega perto; E desviar das pedras no caminho. Quando a formiga pega o açúcar, ele some e aparece em outro lugar aleatório do mapa.
#### Comportamentos:
1. Arrive: Vai até o açúcar e vai freando quando chega perto, para parar em cima dele.
2. Flee: Foge da aranha quando ela chega a menos de 150 px, e só para de fugir quando está a 260 px.
3. Obstacle Avoidance: Desvia das pedras que estão na frente.

### Aranha
Esse agente é responsável por patrulhar o caminho do mapa; Perseguir a formiga quando ela chega perto; E desviar das pedras no caminho.
#### Estados:
1. Patrulha: Segue o caminho retangular do mapa (Path Following) com uma leve variação aleatória (Wander).
2. Perseguição: Quando a formiga está a menos de 170 px, vai atrás dela (Seek).

Nos dois estados ela também usa o Obstacle Avoidance para desviar das pedras.

## Comportamentos
Todos os comportamentos estão no arquivo `SteeringAgent.java`. Cada um devolve uma força, e a engine transforma essa força em movimento. Quase todos seguem a mesma ideia: **força = velocidade desejada − velocidade atual**, ou seja, a força é o que falta para o agente ir para onde ele quer.

### Seek (buscar)
A velocidade desejada aponta direto para o alvo. Ele nunca desacelera, por isso passa do alvo e volta. A aranha usa ele para perseguir a formiga a 220 px/s.

### Flee (fugir)
É o Seek ao contrário: a velocidade desejada aponta para longe da ameaça. A formiga usa ele para fugir da aranha a 300 px/s, mais rápido que a aranha, para conseguir escapar.

### Arrive (chegar)
É o Seek, mas dentro de um raio de frenagem de 120 px a velocidade vai diminuindo conforme chega perto (na metade do raio, metade da velocidade). Assim o agente para em cima do alvo em vez de passar direto. A formiga usa ele para ir até o açúcar.

### Wander (vagar)
Cria um círculo na frente do agente (60 px à frente, raio 30) e escolhe um ponto na borda dele. A cada quadro esse ponto muda só um pouquinho, por isso o movimento fica aleatório mas suave. A aranha usa ele com peso baixo para a patrulha não parecer robótica.

### Obstacle Avoidance (desviar de obstáculos)
O agente estica um "bigode" para frente, maior quanto mais rápido ele anda (até 200 px), e testa três pontos: a ponta do bigode, o meio e a própria posição. Se algum ponto encostar numa pedra (raio da pedra + 12 px do tamanho do agente), ele pega a pedra mais próxima e empurra o agente para fora dela. É a versão simplificada do Fernando Bevilacqua, baseada na ideia do Reynolds.

### Path Following (seguir caminho)
Feito como no artigo do Reynolds. O caminho é o retângulo do mapa com uma faixa de 20 px para cada lado. A cada quadro o agente:
1. Prevê onde vai estar 50 px à frente.
2. Acha o ponto do caminho mais perto dessa previsão (a "sombra" dela no caminho).
3. Se a previsão estiver dentro da faixa, não faz nada. Se estiver fora, faz um Seek num ponto 30 px à frente da sombra, para voltar ao caminho e continuar andando.

## Combinações e pesos

### Combinação 1: Formiga (soma de forças)
A formiga soma todas as forças, cada uma multiplicada por um peso:

| Comportamento | Peso | Por quê |
| --- | --- | --- |
| Arrive | 1 | Comer é o objetivo normal |
| Flee | 2 | Fugir é mais importante que comer, então a fuga domina quando a aranha chega perto |
| Obstacle Avoidance | 3 | Atravessar uma pedra é impossível, então o desvio tem que ganhar de tudo |

A fuga usa dois limites (histerese): começa a fugir a 150 px e só para a 260 px. Sem isso a formiga ficava indo e voltando na borda dos 150 px e a aranha ficava girando atrás dela.

### Combinação 2: Aranha (troca de estado)
A aranha não soma a patrulha com a perseguição, ela faz uma coisa ou outra. Se somasse, ficaria dividida entre o caminho e a formiga e não faria nenhum dos dois direito.

| Comportamento | Peso | Quando |
| --- | --- | --- |
| Path Following | 2 | Patrulhando |
| Wander | 0,2 | Patrulhando |
| Seek | 1 | Formiga a menos de 170 px |
| Obstacle Avoidance | 3 | Sempre |

O Path Following tem peso 2 para segurar a aranha no caminho mesmo com o Wander e os desvios empurrando para os lados. Nos testes, com Wander 0,4 e Path Following 1 a aranha ficava perto do caminho só uns 50% do tempo; com os valores finais ficou em uns 75%.

## Como analisar na tela as alterações?
Quando você iniciar você irá se deparar com essa tela aqui:

- O triângulo **azul** é a formiga e o **verde** é a aranha.
- O círculo **vermelho** é o açúcar.
- Os círculos **marrons** são as pedras.
- A faixa **cinza** com a linha tracejada é o caminho que a aranha patrulha.

Em cima de cada agente aparecem duas setas que ajudam a entender o que está acontecendo:
- Seta **azul**: a velocidade do agente (para onde e quão rápido ele está indo).
- Seta **laranja**: a força que está sendo aplicada naquele momento (para onde ele está sendo puxado).

Aperte a tecla **D** para ligar ou desligar essas setas.

## Estrutura dos arquivos
| Arquivo | O que faz |
| --- | --- |
| `SteeringAgent.java` | Todos os comportamentos (Seek, Flee, Arrive, Wander, Obstacle Avoidance e Path Following) |
| `Formiga.java` | Combinação 1 |
| `Aranha.java` | Combinação 2 |
| `Mapa.java` | Pedras, caminho, açúcar e o sorteio de posições livres |
| `Obstaculo.java` | Uma pedra do mapa (centro e raio) |
| `Sugar.java` | O açúcar, que some e reaparece quando a formiga pega |
| `Setup.java` | Cria a formiga e a aranha |

A pasta `engine` é a engine da professora. A única alteração nela foi uma linha no `Window.java` para desenhar o mapa.

## Referências
- Reynolds, C. *Steering Behaviors For Autonomous Characters* (GDC 1999): https://www.red3d.com/cwr/steer/gdc99/
- Bevilacqua, F. *Understanding Steering Behaviors* (Envato Tuts+).
- Buckland, M. *Programming Game AI by Example*, capítulo 3 (2005).

Neste projeto foi usado IA para auxiliar na implementação dos comportamentos, nos testes e no estudo do código.
