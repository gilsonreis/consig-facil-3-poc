# ESTÁGIO 1: Compilação (Builder)
# Usa uma imagem do Maven com Java 21 para baixar dependências e compilar o código
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar o cache do Docker (evita baixar a internet toda de novo)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e compila (pulando os testes para ser mais rápido)
COPY src ./src
RUN mvn clean package -DskipTests

# ESTÁGIO 2: Execução (Runtime)
# Usa uma imagem menor apenas com o JRE (Java Runtime Environment) para rodar o app
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Pega o arquivo .jar gerado no Estágio 1 e joga para esta imagem limpa
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]