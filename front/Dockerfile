FROM azul/zulu-openjdk-alpine:11
VOLUME /tmp
WORKDIR /source
COPY keystore.pkcs12 /source/keystore.pkcs12
COPY video-key.json /source/video-key.json
COPY storage-key.json /source/storage-key.json
ENV GOOGLE_APPLICATION_CREDENTIALS=storage-key.json
RUN chmod 777 -R /source
ARG JAR_FILE=target/video-dispatcher-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} video-dispatcher.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/source/video-dispatcher.jar"]
