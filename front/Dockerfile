FROM openjdk:8-jdk-alpine
VOLUME /tmp
WORKDIR /source
COPY keystore.pkcs12 /source/keystore.pkcs12
RUN chmod 777 -R /source
ARG JAR_FILE=target/video-dispatcher-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} video-dispatcher.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/source/video-dispatcher.jar"]
