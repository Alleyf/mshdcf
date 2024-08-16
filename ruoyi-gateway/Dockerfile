## 使用官方的 AdoptOpenJDK 的 alpine 镜像，替代 anapsix/alpine-java，以获得长期支持和安全更新
#FROM anapsix/alpine-java:8_server-jre_unlimited
#
## 更新维护者信息
#MAINTAINER csFan
#
## 设置环境变量，虽然这里不再直接使用APP_NAME，但保留以示例完整性
#ENV APP_PORT=8080
#ENV WORKDIR=/alleyf/ruoyi-gateway
#
## 创建必要的目录
#RUN mkdir -p ${WORKDIR}/logs ${WORKDIR}/temp ${WORKDIR}/skywalking/agent
#
## 设置工作目录
#WORKDIR ${WORKDIR}
#
## 暴露环境变量定义的服务端口
#EXPOSE ${APP_PORT}
#
## 使用多阶段构建来减小最终镜像的大小
## 第一阶段：构建阶段
#FROM anapsix/alpine-java:8_server-jre_unlimited AS build
#COPY ./target/ruoyi-gateway.jar app.jar
#
## 第二阶段：运行阶段
#FROM anapsix/alpine-java:8_server-jre_unlimited
#RUN apk --no-cache add curl procps
#
## 将jar包从构建阶段拷贝到运行阶段
#COPY --from=build ${WORKDIR}/app.jar ${WORKDIR}/app.jar
#
## 设置Java运行参数，以优化性能和安全性
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dserver.port=${APP_PORT} -jar ${WORKDIR}/app.jar"]

#优化后
# 使用更小的基础镜像
FROM openjdk:8-jre-alpine

# 使用 ARG 来设置构建时的变量
ARG APP_PORT=8080
ARG WORKDIR=/alleyf/ruoyi-gateway

# 创建必要的目录
RUN mkdir -p ${WORKDIR}/logs ${WORKDIR}/temp

# 设置工作目录
WORKDIR ${WORKDIR}

# 暴露服务端口
EXPOSE ${APP_PORT}

# 使用多阶段构建来减小最终镜像的大小
# 第一阶段：构建阶段
FROM openjdk:8-jre-alpine as build
COPY ./target/ruoyi-gateway.jar app.jar

# 第二阶段：运行阶段
FROM openjdk:8-jre-alpine

# 将jar包从构建阶段拷贝到运行阶段
COPY --from=build /app.jar ${WORKDIR}/app.jar

# 设置Java运行参数，优化性能和安全性
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${APP_PORT} -jar ${WORKDIR}/app.jar"]
