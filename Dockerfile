# 1) JDK 이미지 선택 (버전 맞게)
FROM eclipse-temurin:21-jre

# 빌드 결과물 jar 복사
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} app.jar

COPY build/libs/beeve-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
