# ============================================================
# 备忘录系统 Dockerfile
# 技术栈：Spring Boot 3.2 (JDK 17) + SQLite + 原生 HTML/JS
# 
# 【重要说明】
# 前端使用纯 HTML + JavaScript，无需 Node.js 构建
# 基础镜像 eclipse-temurin:17-jdk-bookworm 已包含所有必需环境
# ============================================================

# ------------------------------------------------------------
# 构建 Java 后端并打包为可执行 JAR
# ------------------------------------------------------------
FROM eclipse-temurin:17-jdk-bookworm AS builder

WORKDIR /app

# 复制后端代码
COPY backend/ ./

# 【关键】配置 Maven 国内镜像（阿里云镜像）
RUN sed -i 's|https://repo.maven.apache.org/maven2|https://maven.aliyun.com/repository/public|g' /usr/share/maven/conf/settings.xml || true

# 安装 Maven 构建工具（基础镜像已包含，此处保险起见）
RUN apt-get update && \
    apt-get install -y --no-install-recommends maven && \
    rm -rf /var/lib/apt/lists/*

# 构建后端项目
RUN mvn clean package -DskipTests

# ------------------------------------------------------------
# 运行阶段
# ------------------------------------------------------------
FROM eclipse-temurin:17-jre-bookworm

WORKDIR /app

# 复制前端文件到 Spring Boot 静态资源目录
# Spring Boot 会自动从 classpath:/static/ 或 /public serve 静态文件
COPY frontend/index.html /app/public/
COPY frontend/app.js /app/public/

# 复制后端 JAR 文件
COPY --from=builder /app/target/memo-system-1.0.0.jar /app/memo-system.jar

# 暴露端口
EXPOSE 8080

# ============================================================
# SSH 插件配置（用于 Trae 连接容器）
# 【重要】这是 Trae 能够连接容器的关键配置
# ============================================================
USER root

# 复制 SSH 插件脚本到容器内
COPY ssh_plugin/ /opt/ssh_plugin/

# 安装并配置 SSH 服务
RUN chmod +x /opt/ssh_plugin/*.sh && \
    /opt/ssh_plugin/install_ssh.sh

# 暴露 SSH 端口（用于 Trae 连接）
EXPOSE 22

# 设置入口点（启动 SSH 服务）
ENTRYPOINT ["/opt/ssh_plugin/entrypoint.sh"]

# ============================================================
# 启动命令（后端应用）
# CMD 会在 SSH 服务启动后执行
# ============================================================
CMD ["java", "-jar", "memo-system.jar"]

# ============================================================
# 健康检查配置
# ============================================================
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/memos/stats?userId=1 || exit 1