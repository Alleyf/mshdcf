FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER csFan

RUN mkdir -p /ruoyi/job/logs \
    /ruoyi/job/temp \
    /ruoyi/skywalking/agent

WORKDIR /ruoyi/job

ENV SERVER_PORT=9203

EXPOSE ${SERVER_PORT}

ADD ./target/ruoyi-job.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
#            "-Dskywalking.agent.service_name=ruoyi-job", \
#            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
