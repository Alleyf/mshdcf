FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER csFan

RUN mkdir -p /ruoyi/sentinel-dashboard/logs \
    /ruoyi/skywalking/agent

WORKDIR /ruoyi/sentinel-dashboard

EXPOSE 8718

ADD ./target/ruoyi-sentinel-dashboard.jar ./app.jar

ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
#            "-Dskywalking.agent.service_name=ruoyi-sentinel-dashboard", \
#            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar"]
