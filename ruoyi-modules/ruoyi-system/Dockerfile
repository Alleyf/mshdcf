FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER csFan

RUN mkdir -p /ruoyi/system/logs \
    /ruoyi/system/temp \
    /ruoyi/skywalking/agent

WORKDIR /ruoyi/system

ENV SERVER_PORT=9201

EXPOSE ${SERVER_PORT}

ADD ./target/ruoyi-system.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
#            "-Dskywalking.agent.service_name=ruoyi-system", \
#            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
