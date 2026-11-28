# Observabilidade — Rota Vital (rascunho para completar)

## Pré-requisitos
- Docker Desktop em execução (Windows com backend Linux/WSL2).
- Docker Compose v2 ou compatível.
- Git.
- Repositório: https://github.com/Vitoria-Gabrielly-DEV/Tuguy-Rota-Vital

## Subir o ambiente
Na raiz do repositório:
```powershell
docker compose up -d --build
docker compose ps
```
Para consultar logs:
```powershell
docker compose logs --tail=100
```
Não execute `docker compose down -v`: isso pode apagar volumes persistentes.

## Serviços e portas
| Serviço | URL/porta | Observação |
|---|---|---|
| Aplicação Rota Vital | http://localhost:8080 | Interface web, se publicada pelo Compose |
| Actuator Prometheus | http://app:8081/actuator/prometheus | Endereço interno entre contêineres; não publicar na internet |
| Prometheus | http://localhost:9090 | Interface de consulta e Status → Targets |
| Grafana Compose | http://localhost:3000 | Configuração original apresentou falha de provisionamento; validar antes de usar |
| Grafana diagnóstico | http://localhost:3001 | Container alternativo `rota-vital-grafana-limpo`; acesso local confirmado, não é URL remota |
| Loki | http://loki:3100 | Interno à rede Docker |
| Tempo | http://tempo:3200 | Interno; OTLP gRPC 4317 / HTTP 4318 |
| Node Exporter | http://node-exporter:9100/metrics | Interno |
| cAdvisor | http://cadvisor:8080/metrics | Interno |

## Métricas
- Métricas padrão Spring/Micrometer: confirmar séries disponíveis em `/actuator/prometheus`.
- Métricas de negócio: preencher somente com nomes realmente implementados e validados no Prometheus.
  - `rotavital_requisicoes_criadas_total`
  - `rotavital_bolsas_alocadas_total`
  - `rotavital_rota_planejamento_seconds_*` 
  - `rotavital_estoque_bolsas_disponiveis` 
  - `rotavital_transporte_veiculos_ativos`

## Logs e traces
- Alloy coleta logs dos contêineres Docker e encaminha ao Loki.
- Consulta inicial no Grafana Explore/Loki: `{application="tuguy"}`.
- Traces no Tempo e correlação por `traceId`: [validar e documentar após teste].

## Acesso remoto
Ainda não configurado. `localhost:3001` é somente acesso local. Antes da entrega, configurar um túnel HTTPS ou VM, criar usuário Grafana Viewer e preencher a URL pública. Não expor Prometheus, Loki, Tempo ou Actuator.

## Segurança
- Manter `.env` fora do Git e incluir `.env.example` sem credenciais reais.
- Não publicar senhas no README nem em commits.
- Criar usuário Viewer para o professor; não compartilhar o admin.
