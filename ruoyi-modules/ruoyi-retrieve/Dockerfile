# 使用官方的OpenJDK 8镜像作为基础镜像，以获得更好的维护和支持
FROM anapsix/alpine-java:8_server-jre_unlimited

# 设置维护者信息
MAINTAINER csFan

# 设置环境变量
ENV SERVER_PORT=9208
ENV WORKDIR=/alleyf/ruoyi-retrieve


# 设置工作目录
WORKDIR ${WORKDIR}

# 创建工作目录
RUN mkdir -p ${WORKDIR}/logs

# 暴露端口
EXPOSE ${SERVER_PORT}

# 使用多阶段构建优化镜像大小
# 第一阶段：构建阶段
FROM anapsix/alpine-java:8_server-jre_unlimited AS build

# 复制应用程序的源代码到容器中
COPY ./target/ruoyi-retrieve.jar ./app.jar

# 第二阶段：运行阶段
FROM anapsix/alpine-java:8_server-jre_unlimited

# 复制在构建阶段生成的JAR包
COPY --from=build ${WORKDIR}/app.jar ${WORKDIR}/app.jar


# 设置环境变量，以允许在容器外部通过环境变量调整
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -Dserver.port=${SERVER_PORT}"

# 启动容器时执行的命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

