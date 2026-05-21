# Task Manager Application Dockerfile
# Base image with Maven and JDK for Spring Boot
FROM maven:3.9-eclipse-temurin-17

# Install SSH, git and other utilities for development environment
RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    curl \
    wget \
    procps \
    tmux \
    vim \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy project files
COPY repo/ .

# Build the Spring Boot application
RUN mvn clean package -DskipTests 2>/dev/null || echo "Build will complete on first run"

# Create directories for logs and data
RUN mkdir -p /app/logs /installed-agent

# Default command to run the application
CMD ["sh", "-c", "java -jar target/task-manager-1.0.0.jar || (mvn spring-boot:run -DskipTests)"]

# ---------- SSH plugin (pluggable, for Cursor/Trae vibe coding) ----------
USER root
COPY ssh_plugin/ /opt/ssh_plugin/
RUN chmod +x /opt/ssh_plugin/*.sh && /opt/ssh_plugin/install_ssh.sh
EXPOSE 22
ENTRYPOINT ["/opt/ssh_plugin/entrypoint.sh"]
# ---------- end SSH plugin ----------