# 🎷 SaxTracker — API REST de Registro de Prática do Saxofone

API REST para organização e acompanhamento de sessões diárias de estudo do saxofone, construída com Java 21 e Spring Boot.

## Sobre o Projeto

SaxTracker nasceu de uma rotina real de prática. A ideia é dar ao músico uma estrutura para cadastrar exercícios, montar rotinas e registrar sessões diárias — com ciclo de vida automático de sessão e validações de negócio por tipo de exercício.

## Stack

| Tecnologia | Detalhe |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring Data JPA | ORM + queries JPQL |
| Spring Web MVC | Camada REST |
| Bean Validation | Validação de requisições |
| SpringDoc OpenAPI | Documentação Swagger UI |
| PostgreSQL | Banco de produção |
| H2 | Banco em memória para testes |
| Maven | Build |

## Modelo de Domínio

**Exercise** — Exercício cadastrado na biblioteca, classificado por `ExerciseType` enum: `ORAL_CAVITY`, `FLEXIBILITY`, `BREATH_SPEED`, `SCALE`. O tipo determina qual métrica é obrigatória na execução (BPM para `SCALE`, nota 1–10 para os demais).

**Routine** — Sequência nomeada de exercícios. Suporta ativação e desativação. Somente rotinas ativas podem ser associadas a novas sessões.

**Session** — Representa um dia de prática. Vincula data (única) e rotina. Status gerenciado automaticamente: `OPEN` → `CLOSED` quando todas as execuções e avaliações estiverem completas.

**ExerciseExecution** — Registra a execução de cada exercício numa sessão. Aplica validação condicional de métrica conforme o tipo e bloqueia duplicatas.

**SkillAssessment** — Avaliação por sessão das habilidades: `TIMBRE`, `TUNING`, `ARTICULATION`, `BREATHING`, `READING`. Toda sessão exige avaliação completa de todas as habilidades.

## Funcionalidades

- **Auto-fechamento de sessão** — `checkAndCloseSessionIfComplete` usa COUNT queries nos repositórios de execução e avaliação para fechar a sessão automaticamente quando completa.
- **Avaliação atômica** — `evaluateSession` valida a lista inteira de avaliações antes de qualquer escrita, prevenindo persistência parcial.
- **Soft delete com reativação** — Exercícios e rotinas suportam desativação e reativação independentes.
- **Tratamento global de erros** — `@RestControllerAdvice` com `GlobalExceptionHandler` mapeia exceções de domínio para status HTTP corretos.
- **Testes** — Testes unitários de service com Mockito e testes de integração com MockMvc + H2.

## Endpoints

### Exercícios (`/exercicios`)
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/exercicios` | Listar todos |
| `GET` | `/exercicios/{id}` | Buscar por ID |
| `POST` | `/exercicios` | Cadastrar exercício |
| `PATCH` | `/exercicios/{id}` | Atualizar exercício |
| `PATCH` | `/exercicios/{id}/reativar` | Reativar exercício |
| `DELETE` | `/exercicios/{id}/desativar` | Desativar (soft delete) |
| `DELETE` | `/exercicios/{id}` | Remover permanentemente |

### Rotinas (`/rotinas`)
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/rotinas` | Listar todas |
| `GET` | `/rotinas/{id}` | Buscar por ID |
| `POST` | `/rotinas` | Criar rotina |
| `PATCH` | `/rotinas/{id}` | Atualizar rotina |
| `PATCH` | `/rotinas/{id}/reativar` | Reativar rotina |
| `DELETE` | `/rotinas/{id}/desativar` | Desativar rotina |

### Sessões (`/sessoes`)
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/sessoes` | Listar sessões |
| `GET` | `/sessoes/{id}` | Detalhar sessão |
| `GET` | `/sessoes/data/{date}` | Buscar sessão por data |
| `GET` | `/sessoes/execucoes` | Listar todas as execuções |
| `POST` | `/sessoes` | Iniciar sessão |
| `POST` | `/sessoes/{id}/execucoes` | Registrar execução de exercício |
| `POST` | `/sessoes/{id}/avaliacoes` | Registrar avaliações de habilidades |

### Progresso (`/progresso`)
| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/progresso/habilidades?semana=2026-W15` | Média de todas as habilidades na semana |
| `GET` | `/progresso/habilidades/{skill}?semana=2026-W15` | Comparação de uma habilidade com semana anterior |
| `GET` | `/progresso/exercicios/{id}` | Evolução do exercício na semana atual |
| `GET` | `/progresso/exercicios/{id}/historico?semanas=4` | Histórico de evolução em N semanas |
| `GET` | `/progresso/resumo?semana=2026-W15` | Resumo semanal completo |

## Documentação Interativa

Com a aplicação rodando, acesse o Swagger UI em:

```
http://localhost:8080/swagger-ui/index.html
```

## Rodando Localmente

**Pré-requisitos:** Java 21, PostgreSQL

1. Clone o repositório:
```bash
git clone https://github.com/marcosv-dev-java/Saxophone-Study-Register.git
cd Saxophone-Study-Register
```

2. Configure as variáveis de ambiente:
```bash
export DB_USERNAME=seu_usuario
export DB_PASSWORD=sua_senha
```

3. Configure o banco em `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/saxtracker
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

4. Suba a aplicação:
```bash
./mvnw spring-boot:run
```

## Rodando os Testes

Os testes usam H2 em memória e não exigem configuração externa:

```bash
./mvnw test
```

## Fluxo de Uso

```
POST /exercicios              → Cadastrar exercícios
POST /rotinas                 → Criar rotina com os exercícios
POST /sessoes                 → Abrir sessão do dia com a rotina
POST /sessoes/{id}/execucoes  → Registrar execução de cada exercício
POST /sessoes/{id}/avaliacoes → Avaliar habilidades (TIMBRE, TUNING, etc.)
                               Sessão fecha automaticamente quando completa
```

## Autor

Marcos V. — Desenvolvedor Backend Java  
[GitHub](https://github.com/marcosv-dev-java)
