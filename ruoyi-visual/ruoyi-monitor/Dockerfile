FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER csFan

RUN mkdir -p /ruoyi/monitor/logs

WORKDIR /ruoyi/monitor

EXPOSE 9100

ADD ./target/ruoyi-monitor.jar ./app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
