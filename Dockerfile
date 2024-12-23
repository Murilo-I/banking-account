# Usar a imagem base do Java 17
FROM openjdk:17-jdk-slim

# Definir o diretório de trabalho no container
WORKDIR /app

# Copiar o arquivo JAR da aplicação para o container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Copiar o arquivo de chave JKS para o container
COPY target/bbdctoken.jks /app/bbdctoken.jks

# Expor a porta padrão do Spring Boot (8080)
EXPOSE 8080

# Comando para executar a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
