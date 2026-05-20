---
AIGC:
    ContentProducer: Minimax Agent AI
    ContentPropagator: Minimax Agent AI
    Label: AIGC
    ProduceID: "00000000000000000000000000000000"
    PropagateID: "00000000000000000000000000000000"
    ReservedCode1: 304602210094f6fcf36a5770d63d79aa489fc0d52ec51c164c63f0b7563dee18be94f4aec8022100b7afa93b281bfb869a7d3a66a32543b58ab35e3021a987ac57b22755483a23b1
    ReservedCode2: 304502203d407925c10c1dbc3d558a8b0ce0125a6707551eb5af81b029cfa4280a933f60022100b383f71be18e4bd580cfda65fc23a461f4fd98b0f451a775ddda22e89d246c1e
---

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

## 二、环境准备

### 2.1 Windows 环境要求

在开始之前，请确保你的 Windows 系统满足以下要求：

| 组件 | 版本要求 | 说明 |
|------|---------|------|
| 操作系统 | Windows 10/11 或 Windows Server | 需要支持 Docker Desktop |
| 内存 | 至少 8GB | Docker 和 IDE 运行需要 |
| 磁盘空间 | 至少 20GB 可用 | 项目文件和容器镜像 |
| Docker Desktop | 最新版本 | 用于构建和运行容器 |

### 2.2 安装 Docker Desktop

访问 Docker 官网下载 Docker Desktop for Windows 安装包。安装过程中需要启用 WSL 2 或 Hyper-V 功能。安装完成后重启计算机。

验证安装是否成功，打开命令提示符执行：

```cmd
docker --version
docker ps
```

## 三、本地运行项目

### 3.1 后端运行步骤

#### 第一步：进入后端目录

```cmd
cd C:\memo-system\backend
```

#### 第二步：确保已安装 Maven

如果没有安装 Maven，可以下载并配置，或者使用 IDE 自带的 Maven。

#### 第三步：编译并运行

```cmd
mvn clean package -DskipTests
java -jar target\memo-system-1.0.0.jar
```

后端服务将在 http://localhost:8080 启动。

### 3.2 前端运行步骤

#### 第一步：进入前端目录

```cmd
cd C:\memo-system\frontend
```

#### 第二步：安装依赖

```cmd
npm install
```

#### 第三步：启动开发服务器

```cmd
npm run dev
```

前端将在 http://localhost:3000 启动。

## 四、Docker 容器化部署

### 4.1 构建 Docker 镜像

#### 第一步：进入项目根目录

```cmd
cd C:\memo-system
```

#### 第二步：执行构建

```cmd
docker build -t memo-system .
```

构建过程可能需要 5-10 分钟，取决于网络速度。

### 4.2 启动容器

```cmd
docker run -d --name memo-system-container -p 8080:8080 memo-system
```

### 4.3 验证容器运行

```cmd
docker ps
```

### 4.4 进入容器查看

```cmd
docker exec -it memo-system-container cmd
```

在容器内可以执行以下命令验证环境：

```cmd
cd C:\app
dir
java -version
git --version
```

## 五、标注任务执行流程

### 5.1 创建仓库级记录

在飞书多维表中创建父记录，填写以下字段：

| 字段名 | 填写内容 | 示例 |
|--------|---------|------|
| repo_url | 项目地址 | https://github.com/yourname/memo-system |
| repo_type | 仓库类型 | 私有仓库 |
| language | 主要语言 | Java, Vue, SQLite |
| dockerfile | Dockerfile 附件 | 上传本项目的 Dockerfile |
| repo | 项目压缩包 | memo-system.zip |
| environment_notes | 环境说明 | Spring Boot 3.2 + Vue 3 + SQLite |
| task_count | Prompt 数量 | 7 |

### 5.2 设计 7 条 Prompt

围绕备忘录系统的业务逻辑，设计 7 条不同类型的任务 Prompt：

## 六、7 条 Prompt 详细设计

### 6.1 Prompt 1：Bug 修复类

**任务描述**：备忘录按时间排序显示异常

**Prompt 内容**：

```
用户反馈：备忘录列表页按创建时间排序时，第三页的数据有重复显示第一页的内容。
请检查 MemoRepository 中的查询方法，找出问题并修复。

相关代码文件：
- backend/src/main/java/com/example/memo/repository/MemoRepository.java
- backend/src/main/java/com/example/memo/controller/MemoController.java
```

**任务类型**：Bug 修复 / 调试

**难度等级**：中等

**评分标准**：

- 2分：找到问题根因并正确修复排序逻辑
- 1分：修复了但引入了新问题或修复不完整
- 0分：未定位到问题或修复错误

---

### 6.2 Prompt 2：功能迭代类

**任务描述**：新增备忘录置顶功能

**Prompt 内容**：

```
用户希望能够将重要的备忘录置顶显示。
请在 Memo 实体中添加 isPinned 字段，在 MemoRepository 中添加 findPinnedMemos 方法，
在 MemoController 中添加切换置顶状态的接口 /api/memos/{id}/toggle-pin，
并在前端 App.vue 中添加置顶按钮和相关交互逻辑。

相关文件：
- backend/src/main/java/com/example/memo/entity/Memo.java
- backend/src/main/java/com/example/memo/repository/MemoRepository.java
- backend/src/main/java/com/example/memo/controller/MemoController.java
- frontend/src/App.vue
```

**任务类型**：功能迭代

**难度等级**：中等

**评分标准**：

- 2分：前后端完整实现，用户体验流畅
- 1分：功能实现但有细节问题（如样式、边界情况）
- 0分：功能无法使用或严重破坏现有功能

---

### 6.3 Prompt 3：功能迭代类

**任务描述**：添加标签筛选功能

**Prompt 内容**：

```
用户希望能够通过标签筛选备忘录。
请实现以下功能：
1. 允许用户创建、编辑、删除标签（Tag 实体已有）
2. 允许备忘录关联多个标签（需要修改 MemoTag 关联逻辑）
3. 在前端添加标签筛选下拉框
4. 后端添加按标签查询备忘录的接口

相关文件：
- backend/src/main/java/com/example/memo/entity/MemoTag.java
- backend/src/main/java/com/example/memo/entity/Tag.java
- backend/src/main/java/com/example/memo/repository/MemoTagRepository.java
- backend/src/main/java/com/example/memo/controller/TagController.java
- frontend/src/App.vue
```

**任务类型**：功能迭代

**难度等级**：困难

**评分标准**：

- 2分：完整实现多对多关联和筛选功能
- 1分：基本功能可用但有缺陷
- 0分：无法完成或破坏现有功能

---

### 6.4 Prompt 4：代码重构类

**任务描述**：重构 MemoService 消除长方法

**Prompt 内容**：

```
MemoService 中的 saveMemo 和 updateMemo 方法过长，包含了过多的业务逻辑。
请按照单一职责原则，将这两个方法拆分为更小的私有方法：
- validateMemo 方法：验证备忘录数据
- updateBasicFields 方法：更新基本字段
- handleTags 方法：处理标签关联
- notifyReminder 方法：处理提醒逻辑（如果需要）

确保拆分后功能保持不变，方法调用逻辑清晰。

相关文件：
- backend/src/main/java/com/example/memo/service/MemoService.java
```

**任务类型**：代码重构

**难度等级**：简单

**评分标准**：

- 2分：方法拆分合理，职责单一，原功能保持
- 1分：拆分但引入了新问题或拆分不合理
- 0分：破坏原有功能或未完成重构

---

### 6.5 Prompt 5：测试编写类

**任务描述**：为 MemoController 编写单元测试

**Prompt 内容**：

```
请为 MemoController 编写完整的单元测试，覆盖以下场景：
1. 创建备忘录成功
2. 创建备忘录失败（标题为空）
3. 查询单个备忘录
4. 更新备忘录
5. 删除备忘录
6. 切换完成状态
7. 切换置顶状态
8. 获取统计信息

使用 JUnit 5 和 Mockito 进行测试编写。

相关文件：
- backend/src/main/java/com/example/memo/controller/MemoController.java
- backend/src/test/java/com/example/memo/（测试目录）
```

**任务类型**：测试

**难度等级**：中等

**评分标准**：

- 2分：测试覆盖全面，断言准确，可执行
- 1分：测试用例不足但核心场景覆盖
- 0分：测试无法运行或严重偏离需求

---

### 6.6 Prompt 6：代码理解类

**任务描述**：分析 JWT 认证流程并提出优化建议

**Prompt 内容**：

```
项目目前没有实现完整的用户认证机制。
请分析以下问题并给出优化方案：
1. 当前 UserController 的登录接口存在哪些安全隐患？
2. 如何实现基于 Token 的认证机制（可以添加 JWT 支持）？
3. 如何保护 API 接口防止未授权访问？
4. 如何实现 Token 刷新机制？

请在 analysis.md 文件中详细说明分析结果和改进方案。
```

**任务类型**：代码理解与分析

**难度等级**：困难

**评分标准**：

- 2分：分析深入，建议具体可实施，包含代码示例
- 1分：分析了主要问题但建议不够具体
- 0分：分析偏离主题或建议不可行

---

### 6.7 Prompt 7：DevOps 类

**任务描述**：配置 Docker 多阶段构建

**Prompt 内容**：

```
当前 Dockerfile 使用的多阶段构建可以进一步优化。
请完成以下优化：
1. 使用 Maven Wrapper 替代全局 Maven
2. 添加 .dockerignore 文件减少镜像体积
3. 优化层缓存提高构建速度
4. 添加健康检查 HEALTHCHECK
5. 使用非 root 用户运行应用

同时编写 docker-compose.yml 实现一键启动，包含：
- 应用服务
- 必要的环境变量配置
- 端口映射
- 数据卷挂载

相关文件：
- Dockerfile（需要优化）
- docker-compose.yml（需要创建）
- .dockerignore（需要创建）
```

**任务类型**：DevOps / 工程化

**难度等级**：中等

**评分标准**：

- 2分：优化完整，Dockerfile 最佳实践，compose 配置正确
- 1分：部分优化但有改进空间
- 0分：配置错误或无法构建成功

## 七、Rollout 执行指南

### 7.1 执行前准备

1. 确保 Docker Desktop 已安装并运行
2. 确保 Trae 客户端已安装并配置 PPE 环境
3. 确保飞书多维表已创建并配置好字段

### 7.2 单条 Prompt 执行流程

#### 第一步：打开 Trae 并进入 PPE

1. 打开 Trae 客户端
2. 按 F1，选择 "Preferences: Open User Settings (JSON)"
3. 添加 PPE 配置并保存
4. 重新加载窗口

#### 第二步：在 Trae 中打开容器

1. 确保容器已启动：`docker ps`
2. 在 Trae 中连接到容器环境
3. 验证当前工作目录为 `C:\app`

#### 第三步：执行 Prompt

1. 选择对应的模型
2. 输入 Prompt 内容
3. 等待模型执行完成

#### 第四步：记录结果

1. 记录 session_id
2. 记录 model_name
3. 记录运行后的代码状态
4. 执行 git diff 生成 patch

#### 第五步：评分并填写理由

根据运行结果填写：

- 0分：任务未完成或结果明显错误
- 1分：任务完成但有瑕疵
- 2分：任务完美完成

### 7.3 模型使用顺序

每个 Prompt 需要使用 5 个模型各执行一次：

| Rollout 序号 | 模型名称 |
|-------------|---------|
| 1 | Doubao-Seed-2.0-Code |
| 2 | GPT5.4 |
| 3 | Gemini 3.1 pro |
| 4 | DeepSeek-v4 |
| 5 | MinMax-M2.7 / GLM-5.1 / Qwen3.6-Plus（轮流） |

## 八、git diff 操作指南

### 8.1 生成 patch 文件

每次 Rollout 结束后，在容器内执行：

```cmd
cd C:\app
git add -A
git diff --cached > rollout_001.patch
```

### 8.2 复制 patch 到宿主机

```cmd
docker cp memo-system-container:/app/rollout_001.patch C:\Users\你的用户名\Downloads\
```

### 8.3 上传到飞书

将 patch 文件作为 git_diff 附件上传到对应的 Rollout 记录中。

## 九、常见问题排查

| 问题现象 | 可能原因 | 解决方案 |
|---------|---------|---------|
| docker build 失败 | Dockerfile 语法错误 | 检查 COPY 路径是否正确 |
| 容器启动后立即退出 | CMD 命令错误 | 使用 `docker logs` 查看日志 |
| Maven 构建失败 | 依赖下载失败 | 检查网络连接，增加超时时间 |
| 前端无法访问后端 | 端口未映射 | 检查 -p 参数配置 |
| git diff 为空 | 代码无修改 | 确认 Prompt 确实产生了代码变更 |

## 十、注意事项

1. 所有 Rollout 必须在 Trae 中打开容器后执行
2. 每次 Rollout 都要从相同的初始代码现场开始
3. 不得修改真实评分结果
4. 如果容器启动失败，需要在 notes 中说明原因
5. Prompt 内容发生实质修改后，需要重新执行全部 5 次 Rollout