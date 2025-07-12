# 🏨 Hotel API

API RESTful para gerenciamento de hóspedes e check-ins em um hotel. Este projeto foi desenvolvido como parte de um desafio backend e oferece funcionalidades como registro de hóspedes, controle de hospedagens, cálculo de valores e exclusão lógica.

---

## 📚 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Data JPA**
- **Flyway**
- **PostgreSQL**
- **Lombok**
- **Swagger (springdoc-openapi 2.6.0)**
- **JUnit 5 + Mockito**

---

## 🚀 Como Rodar o Projeto

### 1. Pré-requisitos

- Java 21
- Maven 3.8+
- PostgreSQL

### 2. Clonar o repositório

```bash
git clone https://github.com/CarvalhoDeLucas/teste_senior_hotel.git
cd teste_senior_hotel
```

### 3. Configurar o banco de dados

Crie um banco PostgreSQL local chamado `hotel` (ou modifique o nome no `application.yml`):

```sql
CREATE DATABASE hotel;
```

### 4. Configuração do `application.yml`

Em `src/main/resources/application.yml`:

```properties
spring:
    datasource:
        url: jdbc:postgresql://localhost:5432/hotel
        username: seu_username
        password: sua_password
    jpa:
        hibernate:
            ddl-auto: validate
        properties:
            hibernate.format_sql: true
        open-in-view: false
    flyway:
        enabled: true
        locations: classpath:db/migration
```

### 5. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em:  
📍 **http://localhost:8080**

---

## 🔄 Migrations com Flyway

As migrations estão na pasta `src/main/resources/db/migration`.

Ao subir o projeto, o Flyway executará automaticamente os arquivos `V1__init.sql` e `V2__add_unique_constraints_to_guest.sql` para criar ou atualizar as tabelas no banco.

---

## 📘 Documentação Swagger

A documentação interativa da API está disponível em:

🔗 **http://localhost:8080/swagger-ui.html**  
Ou  
🔗 **http://localhost:8080/swagger-ui/index.html**

Essa documentação é gerada automaticamente com base nos controllers e modelos da aplicação.

---

## 🧪 Testes

Testes unitários estão implementados com **JUnit 5** e **Mockito**. Para executá-los:

```bash
./mvnw test
```

Eles cobrem os serviços principais: `GuestService` e `CheckinService`, com verificação de regras de negócio, integrações e exceções.

---
