FROM openjdk:11.0.4-jdk-slim

EXPOSE 8080 1099
ADD build/libs/github-top-contributors-app-1.0.0.jar /opt/service/service.jar
WORKDIR /opt/service

ENV CONFIG_PATH /opt/configuration
ENV TZ CET

ENTRYPOINT exec java --illegal-access=warn \
            -jar \
            ${JAVA_OPTS} \
            -Djava.rmi.server.hostname=${JMX_HOSTNAME:-localhost} \
            -Dcom.sun.management.jmxremote.ssl=false \
            -Dcom.sun.management.jmxremote.authenticate=false \
            -Dcom.sun.management.jmxremote.port=${JMX_PORT:-1099} \
            -Dcom.sun.management.jmxremote.rmi.port=${JMX_PORT:-1099} \
            -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/service/log \
            service.jar
