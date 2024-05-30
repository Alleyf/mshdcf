FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER csFan

RUN mkdir -p /ruoyi/retrieve/logs

WORKDIR /ruoyi/retrieve

ENV SERVER_PORT=9208

EXPOSE ${SERVER_PORT}

ADD ./target/ruoyi-retrieve.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
#            "-Dskywalking.agent.service_name=ruoyi-auth", \
#            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
