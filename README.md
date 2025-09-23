# API de Gerenciamento de Eventos de Festivais

API RESTful para gerenciamento de eventos, artistas e ingressos de festivais, desenvolvida com Quarkus.

## Tecnologias Utilizadas

- Quarkus 3.0+
- Java 17
- Hibernate ORM with Panache
- H2 Database
- OpenAPI/Swagger
- Bean Validation

## Estrutura do Projeto

- **Artista**: Entidade que representa os artistas que participam dos eventos
- **Evento**: Entidade que representa os eventos do festival
- **Ingresso**: Entidade que representa os ingressos vendidos para os eventos

## Como Executar

1. Clone o repositório
2. Execute `mvn quarkus:dev`
3. Acesse a API em `http://localhost:8080`
4. Acesse a documentação Swagger em `http://localhost:8080/q/swagger-ui`

## Endpoints Principais

### Artistas
- `GET /artistas` - Lista todos os artistas
- `GET /artistas/{id}` - Busca artista por ID
- `POST /artistas` - Cria um novo artista
- `PUT /artistas/{id}` - Atualiza um artista
- `DELETE /artistas/{id}` - Exclui um artista
- `GET /artistas/{id}/eventos` - Lista eventos do artista

### Eventos
- `GET /eventos` - Lista todos os eventos
- `GET /eventos/{id}` - Busca evento por ID
- `POST /eventos` - Cria um novo evento
- `PUT /eventos/{id}` - Atualiza um evento
- `DELETE /eventos/{id}` - Exclui um evento
- `PUT /eventos/{id}/artistas` - Adiciona artistas ao evento
- `GET /eventos/{id}/artistas` - Lista artistas do evento
- `GET /eventos/{id}/ingressos` - Lista ingressos do evento

### Ingressos
- `GET /ingressos` - Lista todos os ingressos
- `GET /ingressos/{id}` - Busca ingresso por ID
- `POST /ingressos` - Cria um novo ingresso
- `PUT /ingressos/{id}` - Atualiza um ingresso
- `DELETE /ingressos/{id}` - Exclui um ingresso
- `PUT /ingressos/{id}/status` - Atualiza status do ingresso

## Exemplos de Uso

### Criar um artista
```bash
curl -X POST "http://localhost:8080/artistas" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Novo Artista","generoMusical":"Rock","biografia":"Banda de rock nova"}'

### Criar um evento
curl -X POST "http://localhost:8080/eventos" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Novo Festival","dataEvento":"2024-12-01","local":"Parque de Exposições","capacidadeMaxima":10000,"precoIngresso":150.0}'

### Criar um ingresso
curl -X POST "http://localhost:8080/ingressos" \
  -H "Content-Type: application/json" \
  -d '{"nomeComprador":"João Silva","emailComprador":"joao@email.com","quantidade":2,"evento":{"id":1}}'