

# 备忘录系统 - 标注任务完整指南

## 一、项目概述

### 1.1 项目简介

本项目是一个基于 Spring Boot + Vue + SQLite 的备忘录系统，专门为标注任务设计，具备足够的业务复杂度，能够支撑 7 轮不同类型的 Prompt 标注任务。

### 1.2 技术架构

| 层级 | 技术选型 | 说明 |
|------|---------|------|
| 后端框架 | Spring Boot 3.2 | Java 17 运行环境 |
| ORM 框架 | Spring Data JPA | 数据库持久化 |
| 数据库 | SQLite | 内置数据库，无需安装 |
| 前端框架 | Vue 3 | 响应式用户界面 |
| 构建工具 | Vite | 前端开发服务器 |
| 构建工具 | Maven | 后端项目构建 |

### 1.3 项目结构

```
memo-system/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/com/example/memo/
│   │   ├── MemoApplication.java      # 应用入口
│   │   ├── entity/                   # 实体类
│   │   │   ├── User.java             # 用户实体
│   │   │   ├── Category.java        # 分类实体
│   │   │   ├── Memo.java             # 备忘录实体
│   │   │   ├── Tag.java              # 标签实体
│   │   │   └── MemoTag.java          # 备忘录标签关联
│   │   ├── repository/               # 数据访问层
│   │   ├── service/                  # 业务逻辑层
│   │   ├── controller/               # REST API 控制器
│   │   └── dto/                      # 数据传输对象
│   ├── src/main/resources/
│   │   └── application.properties   # 应用配置
│   └── pom.xml                       # Maven 依赖配置
├── frontend/                         # Vue 前端
│   ├── src/
│   │   ├── main.js                   # 前端入口
│   │   ├── App.vue                   # 根组件
│   │   └── style.css                 # 全局样式
│   ├── index.html                    # HTML 模板
│   ├── package.json                  # 前端依赖
│   └── vite.config.js                # Vite 配置
├── Dockerfile                        # Docker 构建文件
└── README.md                         # 项目文档
```