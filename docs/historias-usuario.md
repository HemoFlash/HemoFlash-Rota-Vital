okay# Rota Vital — Histórias de Usuário e Critérios de Aceitação

## Projeto

**Nome:** Rota Vital — Gestão e Distribuição de Hemocomponentes na Rede de Sangue

**Descrição:** Aplicação web em Java/Spring Boot que apoia a rede de sangue na gestão e distribuição de hemocomponentes. O sistema gerencia o estoque por tipo e componente, recebe requisições dos hospitais, aloca bolsas compatíveis priorizando a validade (FEFO), calcula rotas entre hospitais considerando a modalidade de transporte e a validade, e monitora a temperatura dos veículos em transporte.

> O projeto utiliza exclusivamente **dados sintéticos**. A telemetria de temperatura/GPS é simulada, a compatibilidade ABO/Rh possui finalidade didática e não substitui protocolos clínicos, e não há integração com sistemas oficiais da Hemorrede.

---

## HU01 — Consultar estoque de hemocomponentes

**História de usuário**
Como funcionário do hemocentro, eu gostaria de consultar o estoque de hemocomponentes por tipo, componente e quantidade, para que eu possa acompanhar a disponibilidade das bolsas.

**Cenário 1 — Consultar estoque**
- **Dado** que o funcionário está logado no sistema
- **E** existem hemocomponentes cadastrados no estoque
- **Quando** acessar a opção de consulta de estoque
- **Então** o sistema deve apresentar os hemocomponentes disponíveis, informando tipo, componente e quantidade.

**Cenário 2 — Filtrar por tipo sanguíneo**
- **Dado** que existem hemocomponentes de diferentes tipos sanguíneos no estoque
- **Quando** o funcionário selecionar um tipo sanguíneo
- **Então** o sistema deve apresentar somente os hemocomponentes correspondentes ao tipo selecionado.

**Cenário 3 — Consultar componente indisponível**
- **Dado** que não existem unidades disponíveis de determinado componente
- **Quando** o funcionário realizar uma consulta desse componente
- **Então** o sistema deve informar que não existem unidades disponíveis.

---

## HU02 — Registrar requisição hospitalar

**História de usuário**
Como funcionário do hemocentro, eu gostaria de registrar requisições de hemocomponentes realizadas pelos hospitais, para que eu possa organizar o atendimento das solicitações.

**Cenário 1 — Registrar requisição válida**
- **Dado** que o funcionário está logado no sistema
- **E** possui os dados necessários da requisição
- **Quando** registrar uma solicitação de hemocomponentes
- **Então** o sistema deve cadastrar a requisição
- **E** atribuir um identificador à solicitação
- **E** disponibilizá-la para atendimento.

**Cenário 2 — Requisição com informação obrigatória ausente**
- **Dado** que o funcionário está preenchendo uma requisição
- **Quando** tentar registrar a solicitação sem informar um dado obrigatório
- **Então** o sistema não deve registrar a requisição
- **E** deve informar qual dado precisa ser preenchido.

**Cenário 3 — Consultar requisições registradas**
- **Dado** que existem requisições cadastradas
- **Quando** o funcionário acessar a lista de requisições
- **Então** o sistema deve apresentar as solicitações cadastradas e seus respectivos status.

---

## HU03 — Alocar hemocomponentes priorizando a validade

**História de usuário**
Como funcionário do hemocentro, eu gostaria de alocar bolsas compatíveis com a requisição hospitalar priorizando aquelas com menor prazo de validade, para que eu possa reduzir o descarte de hemocomponentes por vencimento.

**Cenário 1 — Alocar bolsas compatíveis**
- **Dado** que existe uma requisição hospitalar
- **E** existem bolsas compatíveis disponíveis no estoque
- **Quando** o funcionário solicitar a alocação
- **Então** o sistema deve selecionar bolsas compatíveis com a requisição.

**Cenário 2 — Priorizar menor prazo de validade**
- **Dado** que existem duas ou mais bolsas compatíveis disponíveis
- **E** as bolsas possuem diferentes datas de validade
- **Quando** o sistema realizar a alocação
- **Então** deve priorizar a bolsa com menor prazo de validade.

**Cenário 3 — Não existem bolsas compatíveis**
- **Dado** que existe uma requisição hospitalar
- **E** não existem bolsas compatíveis disponíveis
- **Quando** o funcionário tentar realizar a alocação
- **Então** o sistema não deve realizar a alocação
- **E** deve informar que não existem hemocomponentes compatíveis disponíveis.

---

## HU04 — Consultar compatibilidade ABO/Rh

**História de usuário**
Como funcionário do hemocentro, eu gostaria de consultar a compatibilidade ABO/Rh entre a requisição e as bolsas disponíveis, para que eu possa selecionar hemocomponentes compatíveis de forma adequada.

**Cenário 1 — Identificar bolsa compatível**
- **Dado** que existe uma requisição com determinado tipo ABO/Rh
- **E** existe uma bolsa compatível disponível no estoque
- **Quando** o funcionário consultar a compatibilidade
- **Então** o sistema deve indicar que a bolsa é compatível.

**Cenário 2 — Identificar bolsa incompatível**
- **Dado** que existe uma requisição com determinado tipo ABO/Rh
- **E** existe uma bolsa incompatível disponível no estoque
- **Quando** o funcionário consultar a compatibilidade
- **Então** o sistema deve indicar que a bolsa não é compatível.

**Cenário 3 — Consultar várias bolsas**
- **Dado** que existem várias bolsas disponíveis no estoque
- **Quando** o funcionário realizar uma consulta de compatibilidade
- **Então** o sistema deve identificar quais bolsas são compatíveis e quais não são.

> **Observação:** A compatibilidade ABO/Rh utilizada pelo Rota Vital possui finalidade didática e não substitui protocolos clínicos.

---

## HU05 — Planejar rota de distribuição

**História de usuário**
Como responsável pela distribuição, eu gostaria de calcular uma rota para transferir hemocomponentes entre hospitais da rede, considerando o hospital onde a bolsa está em estoque, a modalidade de transporte e a data de validade, para que eu possa redistribuir excedentes antes do vencimento e atender hospitais com necessidade.

**Cenário 1 — Calcular rota**
- **Dado** que existe um hemocomponente alocado a uma requisição de um hospital cadastrado
- **E** a bolsa informa em qual hospital cadastrado está em estoque
- **E** existe um caminho entre os hospitais na rede
- **Quando** o responsável solicitar o cálculo da rota
- **Então** o sistema deve apresentar uma rota de distribuição.

**Cenário 2 — Considerar modalidade de transporte e validade**
- **Dado** que uma bolsa possui componente e data de validade cadastrados
- **E** o responsável seleciona transporte refrigerado ou não refrigerado
- **Quando** o sistema calcular a rota
- **Então** deve estimar distância e duração entre os hospitais
- **E** deve comparar a duração com o limite demonstrativo do componente para a modalidade
- **E** a chegada estimada não pode ultrapassar a data de validade.

**Cenário 3 — Transporte incompatível com o limite ou a validade**
- **Dado** que a duração da rota excede o limite de transporte do componente
- **Ou** a chegada estimada ocorre depois da validade do hemocomponente
- **Quando** o responsável solicitar o planejamento da distribuição
- **Então** o sistema deve informar que a transferência não é viável nas condições selecionadas.

**Cenário 4 — Não existe rota disponível**
- **Dado** que não existe um trecho/caminho cadastrado entre o hospital de estoque e o hospital de destino
- **Quando** o responsável solicitar o cálculo da rota
- **Então** o sistema deve informar que não foi encontrada uma rota válida.

> **Observação:** distâncias, velocidades e limites por componente/modalidade são sintéticos e demonstrativos; não representam protocolos clínicos nem validação da cadeia fria.

---

## HU06 — Monitorar temperatura dos veículos de transporte

**História de usuário**
Como responsável pelo monitoramento, eu gostaria de acompanhar a temperatura de cada veículo em transporte em um painel, para que eu possa identificar situações que possam comprometer a cadeia fria.

**Cenário 1 — Temperatura dentro da faixa esperada**
- **Dado** que existe um veículo registrado em transporte
- **E** a temperatura registrada está dentro da faixa estabelecida
- **Quando** o responsável acessar o painel de monitoramento
- **Então** o sistema deve apresentar o veículo como dentro das condições esperadas.

**Cenário 2 — Temperatura fora da faixa esperada**
- **Dado** que existe um veículo registrado em transporte
- **E** a temperatura registrada está fora da faixa estabelecida
- **Quando** o sistema receber a informação de temperatura
- **Então** deve sinalizar uma situação de alerta no painel.

**Cenário 3 — Monitorar vários veículos**
- **Dado** que existem vários veículos registrados em transporte
- **Quando** o responsável acessar o painel de monitoramento
- **Então** o sistema deve apresentar uma leitura individual para cada veículo monitorado.

**Regras da simulação**
- Cada leitura de temperatura pertence a um veículo em transporte, nunca a uma bolsa individual.
- O responsável informa a identificação e a descrição do veículo ao iniciar seu monitoramento; ao concluir o transporte, o veículo deixa de receber novas leituras e sai do painel de ativos.
- As leituras são sintéticas, atualizadas a cada 10 segundos enquanto a aplicação está ativa; a primeira leitura ocorre após 1 segundo.
- A faixa esperada desta simulação é de 1 °C a 7 °C, inclusive. Leituras abaixo ou acima dela são sinalizadas como alerta.
- O painel apresenta a leitura mais recente e seu horário, e marca como desatualizada uma amostra com mais de 20 segundos; a simulação não representa sensores reais nem mantém histórico de leituras.

---

## HU07 — Acompanhar indicadores da rede

**História de usuário**
Como gestor da rede de sangue, eu gostaria de visualizar indicadores de estoque e demanda de hemocomponentes, para que eu possa acompanhar a situação da rede e apoiar a tomada de decisões.

**Cenário 1 — Visualizar indicadores de estoque**
- **Dado** que existem dados de estoque registrados no sistema
- **Quando** o gestor acessar o painel de indicadores
- **Então** o sistema deve apresentar informações sobre a quantidade de hemocomponentes disponíveis.

**Cenário 2 — Visualizar indicadores de demanda**
- **Dado** que existem requisições hospitalares registradas
- **Quando** o gestor acessar os indicadores de demanda
- **Então** o sistema deve apresentar informações relacionadas às solicitações realizadas.

**Cenário 3 — Identificar baixa disponibilidade**
- **Dado** que determinado hemocomponente possui quantidade reduzida no estoque
- **Quando** o gestor acessar o painel
- **Então** o sistema deve destacar a situação de baixa disponibilidade.

**Regras dos indicadores**
- O estoque considera somente bolsas disponíveis, não vencidas e sem requisição associada.
- O painel alerta quando o total de um grupo ABO (A, B, AB ou O) fica abaixo de 4 bolsas.
- O painel sinaliza como reserva baixa um tipo ABO/Rh com apenas 1 bolsa e como indisponível um tipo sem bolsas.
- O estoque também é apresentado por tipo ABO/Rh e componente, sinalizando pares conhecidos sem bolsas como indisponíveis; a demanda é a contagem de requisições registradas por tipo sanguíneo, não o número de unidades solicitadas.

---

## HU08 — Cadastrar e acessar o aplicativo

**História de usuário**
Como usuário, eu gostaria de realizar meu cadastro utilizando meu e-mail de trabalho e acessar minha conta por meio de login, para que eu possa utilizar o aplicativo.

**Cenário 1 — Realizar cadastro**
- **Dado** que o usuário possui um e-mail de trabalho autorizado
- **Quando** preencher todos os campos obrigatórios e confirmar o cadastro
- **Então** o sistema deve realizar o cadastro
- **E** redirecionar o usuário para a tela de login.

**Cenário 2 — Realizar login**
- **Dado** que o usuário possui uma conta cadastrada
- **Quando** inserir suas credenciais corretamente
- **Então** o sistema deve autenticar o usuário
- **E** direcioná-lo para a tela inicial do aplicativo.

**Cenário 3 — Campos obrigatórios incompletos**
- **Dado** que o usuário está na tela de cadastro ou login
- **E** existe pelo menos um campo obrigatório não preenchido
- **Quando** tentar confirmar a operação
- **Então** o sistema deve exibir um alerta informando que existem dados obrigatórios não preenchidos
- **E** não deve concluir a operação.

---

## Resumo das histórias

| ID | História | Principal valor |
|---|---|---|
| HU01 | Consultar estoque de hemocomponentes | Conhecer a disponibilidade |
| HU02 | Registrar requisição hospitalar | Organizar as solicitações hospitalares |
| HU03 | Alocar hemocomponentes priorizando a validade | Reduzir perdas por vencimento |
| HU04 | Consultar compatibilidade ABO/Rh | Identificar bolsas compatíveis |
| HU05 | Planejar rota de distribuição | Realizar entregas dentro das condições necessárias |
| HU06 | Monitorar temperatura dos veículos de transporte | Preservar a cadeia fria |
| HU07 | Acompanhar indicadores da rede | Apoiar a tomada de decisões |
| HU08 | Cadastrar e acessar o aplicativo | Permitir acesso ao sistema |

**Quantitativo final:** 8 histórias de usuário · 25 cenários de aceitação
(HU01–04, 06–08: 3 cenários cada · HU05: 4 cenários)
