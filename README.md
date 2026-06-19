# Parking System

Sistema de gerenciamento de estacionamento desenvolvido em **Java + Spring Boot** para processamento de eventos de entrada, estacionamento e saída de veículos com cálculo dinâmico de preços e consulta de faturamento.

---

## Tecnologias utilizadas

* Java 21
* Spring Boot
* Spring Data JPA
* MySQL
* Maven
* Docker
* JUnit 5
* Mockito
* Lombok

---

## Funcionalidades

### Inicialização da garagem

Ao iniciar a aplicação, o sistema:

* Consome configuração externa da garagem
* Carrega setores disponíveis
* Carrega vagas disponíveis
* Persiste os dados no banco

---

### Processamento de eventos

Endpoint:

```http
POST /webhook
```

Eventos suportados:

#### ENTRY

* Registra entrada do veículo
* Seleciona vaga disponível
* Calcula preço dinâmico por ocupação
* Marca vaga como ocupada

#### PARKED

* Atualiza posição do veículo na vaga

#### EXIT

* Registra saída
* Calcula valor final
* Aplica regra de gratuidade até 30 minutos
* Libera vaga

---

## Regras de preço

O preço por hora é definido na entrada conforme ocupação do setor:

| Ocupação | Ajuste       |
| -------- | ------------ |
| < 25%    | -10%         |
| ≤ 50%    | preço normal |
| ≤ 75%    | +10%         |
| ≤ 100%   | +25%         |

Tempo de permanência:

* Até 30 minutos → gratuito
* Acima de 30 minutos → cobrança por hora (arredondamento para cima)

---

## Receita

Consultar faturamento:

```http
GET /revenue
```

Exemplo:

```http
GET /revenue?date=2025-01-01&sector=A
```

Resposta:

```json
{
  "amount": 72.9,
  "currency": "BRL"
}
```

---

## Como executar

### 1. Clonar projeto

```bash
git clone https://github.com/ecampos14/parking-system.git
```

---

### 2. Configurar banco

Criar banco:

```sql
CREATE DATABASE parking_system;
```

Configurar:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/parking_system
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
```

---

### 3. Executar simulador

```bash
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0
```

---

### 4. Subir aplicação

```bash
./mvnw spring-boot:run
```

ou

```bash
mvn spring-boot:run
```

---

## Executando testes

```bash
mvn test
```

Cobertura:

* Entrada de veículo
* Saída de veículo
* Cálculo de preço
* Garagem lotada
* Consulta de faturamento

---

## Estrutura

```plaintext
controller/
service/
service/impl/
repository/
entity/
dto/
config/
exception/
test/
```

---

