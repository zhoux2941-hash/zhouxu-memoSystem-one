---
AIGC:
    ContentProducer: Minimax Agent AI
    ContentPropagator: Minimax Agent AI
    Label: AIGC
    ProduceID: "00000000000000000000000000000000"
    PropagateID: "00000000000000000000000000000000"
    ReservedCode1: 3046022100b750b28fa4bc4d51f5dc46bf79ed6a69ce956e6839499e7a82c2fc14b6900ef0022100b69f21d6200ba2de4845cedf8c73f76b1543acb2cb144195e83893cd784ee444
    ReservedCode2: 304502207d8fb37b72328336d619c8c98765fece206cda31d6657c9d411db8339478367602210082f225151215c581b3bc511e1f7ae8876d3f5d29b8ffa3894872df9e3d83487a
---

# 备忘录系统 - 7套Prompt详细设计

## Prompt 1：修复备忘录分页排序异常

### 基本信息

| 字段 | 值 |
|------|-----|
| prompt_index | 1 |
| difficulty | 中等 |
| category | Bug 修复 / 调试 |
| tech_stack | Java, Spring Boot, Spring Data JPA, SQLite |
| module_tags | MemoRepository, MemoController, 前端列表组件 |

### Prompt 内容

```
用户反馈：备忘录列表页按创建时间排序时，第三页的数据有重复显示第一页的内容。
当用户在列表页面点击"加载更多"或切换到第二页、第三页时，部分备忘录会重复出现，
特别是跨分类筛选后再进行分页操作时，问题更加明显。

请检查以下文件并修复问题：
1. backend/src/main/java/com/example/memo/repository/MemoRepository.java - 检查查询方法是否正确处理分页参数
2. backend/src/main/java/com/example/memo/service/MemoService.java - 检查业务层是否正确传递分页参数
3. backend/src/main/java/com/example/memo/controller/MemoController.java - 检查REST接口的分页参数处理
4. frontend/src/App.vue - 检查前端分页请求是否正确发送page和size参数

请确保：
- 后端分页使用Spring Data的Pageable接口
- 前端正确传递分页参数
- 排序字段和方向在翻页时保持一致
- 修复后验证：切换多页后数据无重复，每页数据量正确
```

### 评分标准

| 分数 | 定义 | 评分理由 |
|------|------|---------|
| 2 | 任务完美执行 | 找到问题根因（可能是Pageable未正确传递或排序字段不一致），修复后多页切换无重复 |
| 1 | 任务完成但有瑕疵 | 修复了主要问题，但边界情况（如筛选+分页组合）仍有问题 |
| 0 | 未完成任务 | 未定位到问题，或修复后引入新问题（如排序混乱、数据丢失） |

---

## Prompt 2：新增备忘录置顶功能

### 基本信息

| 字段 | 值 |
|------|-----|
| prompt_index | 2 |
| difficulty | 中等 |
| category | 功能迭代 |
| tech_stack | Java, Spring Boot, Vue 3, SQLite |
| module_tags | Memo实体, MemoRepository, MemoService, MemoController, 前端UI |

### Prompt 内容

```
用户希望能够将重要的备忘录置顶显示，置顶的备忘录应该在列表最上方，无论创建时间先后。

请实现以下功能：

【后端改造】
1. 在 Memo 实体中添加 isPinned 字段（Boolean类型，默认false）
2. 在 application.properties 中确认数据库会自动创建新字段
3. 在 MemoRepository 中添加 findPinnedMemos 方法，返回按创建时间倒序排列的已置顶备忘录
4. 在 MemoService 中添加 togglePin(Long id) 方法，切换备忘录的置顶状态
5. 在 MemoController 中添加 PATCH /api/memos/{id}/toggle-pin 接口

【前端改造】
1. 在备忘录列表中，每个条目右上角添加"置顶"按钮（📌图标）
2. 已置顶的备忘录左侧显示蓝色边框，并且在列表中排在最上方
3. 点击置顶按钮调用 toggle-pin 接口，切换置顶状态
4. 按钮点击后立即更新UI显示状态

请确保：
- 置顶状态切换流畅，无需刷新页面
- 多条置顶备忘录之间按时间倒序排列
- 非置顶备忘录在置顶备忘录下方，也按时间倒序排列
```

### 评分标准

| 分数 | 定义 | 评分理由 |
|------|------|---------|
| 2 | 任务完美执行 | 前后端完整实现，置顶功能流畅，排序正确，用户体验良好 |
| 1 | 任务完成但有瑕疵 | 基本功能可用，但存在样式问题或边界情况（如同时多条置顶排序） |
| 0 | 未完成任务 | 功能无法使用，排序错误，或破坏现有功能 |

---

## Prompt 3：添加标签管理和筛选功能

### 基本信息

| 字段 | 值 |
|------|-----|
| prompt_index | 3 |
| difficulty | 困难 |
| category | 功能迭代 |
| tech_stack | Java, Spring Boot, Spring Data JPA, Vue 3, SQLite |
| module_tags | Tag实体, MemoTag关联, TagService, TagController, 前端标签组件 |

### Prompt 内容

```
用户希望能够通过标签对备忘录进行分类和筛选。目前系统已有 Tag 实体和 MemoTag 关联表，
但标签功能未完全实现。用户可以创建标签，并为备忘录分配多个标签，然后通过标签筛选备忘录。

【后端改造】
1. 完善 TagController 的增删改查接口：
   - GET /api/tags?userId={id} - 获取用户所有标签
   - POST /api/tags - 创建新标签
   - DELETE /api/tags/{id} - 删除标签（同时删除所有关联）

2. 添加备忘录标签管理接口：
   - POST /api/memos/{memoId}/tags - 为备忘录添加标签
   - DELETE /api/memos/{memoId}/tags/{tagId} - 移除备忘录的标签
   - GET /api/memos/by-tag/{tagId} - 获取指定标签下的所有备忘录

3. 在 MemoRepository 中添加按标签查询的方法

【前端改造】
1. 在侧边栏添加"标签管理"区域，显示用户所有标签（带颜色标识）
2. 在新建/编辑备忘录弹窗中，添加"选择标签"的多选下拉框
3. 添加"按标签筛选"功能：点击侧边栏的标签，列表只显示带有该标签的备忘录
4. 显示标签筛选的标签名（如"当前筛选：工作"），并提供清除筛选的按钮

请确保：
- 一个备忘录可以关联多个标签
- 一个标签可以被多个备忘录使用（多对多关系）
- 删除标签时自动清除所有关联
- 标签筛选支持与其他筛选（分类、时间）组合使用
```

### 评分标准

| 分数 | 定义 | 评分理由 |
|------|------|---------|
| 2 | 任务完美执行 | 完整实现多对多关联管理，前端标签选择和筛选功能正常，边界情况处理完善 |
| 1 | 任务完成但有瑕疵 | 基本功能可用，但组合筛选有问题或标签删除关联处理不完整 |
| 0 | 未完成任务 | 功能无法使用，数据混乱，或破坏现有功能 |

---

## Prompt 4：重构MemoService消除长方法

### 基本信息

| 字段 | 值 |
|------|-----|
| prompt_index | 4 |
| difficulty | 简单 |
| category | 代码重构 |
| tech_stack | Java, Spring Boot |
| module_tags | MemoService, 代码质量 |

### Prompt 内容

```
当前 MemoService 中的 createMemo 和 updateMemo 方法过长，包含了数据验证、业务逻辑处理、
关联更新等多方面的代码，单个方法超过了80行。这导致代码难以阅读、测试和维护。

请按照单一职责原则，对 MemoService 进行重构：

【需要重构的方法】
1. createMemo(Long userId, Memo memo) - 约90行，需要拆分
2. updateMemo(Long id, Memo memoDetails) - 约85行，需要拆分

【拆分方案】
将每个长方法拆分为以下私有辅助方法：
- validateMemo(Memo memo) - 验证备忘录数据（标题不为空、长度合理等）
- setDefaultValues(Memo memo) - 设置默认值（创建时间、初始化状态等）
- updateBasicFields(Memo existing, Memo updates) - 更新基本字段
- handleCategoryUpdate(Memo memo, Long categoryId) - 处理分类关联更新
- handleTagsUpdate(Memo memo, List<Long> tagIds) - 处理标签关联更新（如果标签功能已实现）
- notifyUser(Memo memo, String action) - 发送通知（如到期提醒等）

【具体要求】
1. 重构后的方法调用逻辑清晰：
   createMemo → validateMemo → setDefaultValues → [save] → handleCategoryUpdate → notifyUser
2. 每个私有方法不超过20行，职责单一
3. 保持原有的业务逻辑不变，重构后功能完全一致
4. 添加必要的JavaDoc注释说明每个方法的职责
5. 确保重构后所有现有测试仍然通过

【重构后验证】
运行单元测试（如果有），确认所有功能正常工作
```

### 评分标准

| 分数 | 定义 | 评分理由 |
|------|------|---------|
| 2 | 任务完美执行 | 方法拆分合理，每个方法职责单一，原有功能完全保持，代码可读性显著提升 |
| 1 | 任务完成但有瑕疵 | 完成拆分但引入新问题（如循环依赖、职责划分不合理）或部分功能受损 |
| 0 | 未完成任务 | 破坏原有功能，编译失败，或拆分不合理导致更严重的问题 |

---

## Prompt 5：为MemoController编写完整单元测试

### 基本信息

| 字段 | 值 |
|------|-----|
| prompt_index | 5 |
| difficulty | 中等 |
| category | 测试 |
| tech_stack | Java, JUnit 5, Mockito, Spring Boot Test |
| module_tags | MemoController, 单元测试 |

### Prompt 内容

```
请为 MemoController 编写完整的单元测试，确保所有接口都有充分的测试覆盖。

【需要测试的接口】
1. GET /api/memos?userId={id} - 获取用户所有备忘录
2. GET /api/memos/{id} - 获取单个备忘录详情
3. POST /api/memos - 创建新备忘录
4. PUT /api/memos/{id} - 更新备忘录
5. DELETE /api/memos/{id} - 删除备忘录
6. PATCH /api/memos/{id}/toggle-complete - 切换完成状态
7. PATCH /api/memos/{id}/toggle-pin - 切换置顶状态
8. GET /api/memos/stats?userId={id} - 获取统计信息

【测试用例要求】
每个接口至少包含以下测试场景：

【创建备忘录】
- 成功创建（必填字段完整）
- 创建失败：标题为空（返回400）
- 创建失败：userId不存在（返回处理）

【获取备忘录】
- 成功获取单个备忘录
- 获取不存在的备忘录（返回404）

【更新备忘录】
- 成功更新标题和内容
- 更新不存在的备忘录（返回处理）

【删除备忘录】
- 成功删除
- 删除不存在的备忘录（无异常或返回处理）

【状态切换】
- 切换完成状态从未完成到已完成
- 切换完成状态从已完成到未完成
- 切换置顶状态从未置顶到已置顶
- 切换置顶状态从已置顶到未置顶

【统计信息】
- 返回正确的已完成和总数统计

【技术要求】
1. 使用 JUnit 5 注解（@Test, @BeforeEach等）
2. 使用 Mockito 模拟 MemoService
3. 使用 @InjectMocks 注入 Controller
4. 使用 @Mock 注解模拟依赖
5. 使用 MockMvc 进行HTTP请求测试
6. 使用 assertEquals, assertNotNull 等断言

【测试文件位置】
在 backend/src/test/java/com/example/memo/controller/ 目录下创建 MemoControllerTest.java

请确保：
- 测试覆盖所有接口的所有主要场景
- 断言准确，验证响应状态码和返回内容
- 测试独立，每个测试方法不依赖其他测试的执行结果
```

### 评分标准

| 分数 | 定义 | 评分理由 |
|------|------|---------|
| 2 | 任务完美执行 | 测试覆盖全面，涵盖所有接口的所有主要场景，测试可独立运行，断言准确 |
| 1 | 任务完成但有瑕疵 | 测试用例基本完整但场景覆盖不足，或部分测试有缺陷 |
| 0 | 未完成任务 | 测试无法运行，编译失败，或严重偏离需求 |

---

## Prompt 6：分析用户认证流程并实现JWT认证

### 基本信息

| 字段 | 值 |
|------|-----|
| prompt_index | 6 |
| difficulty | 困难 |
| category | 代码理解与分析 |
| tech_stack | Java, Spring Boot, JWT, Security |
| module_tags | UserController, 认证流程, API安全 |

### Prompt 内容

```
当前备忘录系统存在安全隐患：UserController 的登录接口使用明文密码传输和比对，
没有任何认证机制保护 API 接口，任何人都可以访问和操作所有用户的数据。

请完成以下任务：

【第一步：分析当前实现】
分析 UserController 和 UserService 中的用户认证实现，指出以下安全隐患：
1. 密码以明文形式存储和比对（应使用哈希）
2. 登录接口没有任何加密（如HTTPS）
3. API 接口没有任何身份验证机制
4. 没有防止暴力破解的措施（如限流、锁定）
5. Session管理可能存在的问题

将分析结果写入 analysis.md 文件，包含：
- 当前认证流程的详细说明（带代码片段）
- 每个安全隐患的具体说明
- 风险等级评估（高/中/低）

【第二步：实现JWT认证】
基于分析结果，实现基于 JWT Token 的认证机制：

1. 添加 JWT 依赖到 pom.xml：
   - jjwt-api, jjwt-impl, jjwt-jackson (0.11.x版本)

2. 创建 JwtService 工具类：
   - generateToken(String username, Long userId) - 生成Token
   - validateToken(String token) - 验证Token
   - getUsernameFromToken(String token) - 从Token获取用户名
   - getUserIdFromToken(String token) - 从Token获取用户ID

3. 创建 SecurityConfig 配置类：
   - 配置哪些接口需要认证，哪些不需要
   - 添加 JWT 过滤器验证请求头中的 Token

4. 修改 UserController：
   - POST /api/users/register - 注册时返回 JWT Token
   - POST /api/users/login - 登录时返回 JWT Token

5. 修改其他 Controller：
   - 所有需要认证的接口，从请求头获取 JWT Token 并验证
   - Token 无效或过期时返回 401 Unauthorized

6. 添加登录限流机制：
   - 使用内存或Redis记录登录失败次数
   - 连续5次失败后锁定账户15分钟

【第三步：编写使用说明】
在 implementation_guide.md 中详细说明：
- JWT Token 的结构和内容
- 前端如何获取和携带 Token
- 如何处理 Token 过期和刷新
- 部署时的安全建议

请确保：
- 分析深入，指出所有安全隐患
- JWT 实现正确，Token 包含必要信息
- 接口认证保护完整，无遗漏
- 限流机制有效
```

### 评分标准

| 分数 | 定义 | 评分理由 |
|------|------|---------|
| 2 | 任务完美执行 | 分析深入完整，JWT实现正确且安全，所有接口保护完整，文档清晰 |
| 1 | 任务完成但有瑕疵 | 分析基本完整，JWT基本功能可用但有安全漏洞或部分接口未保护 |
| 0 | 未完成任务 | 分析偏离主题，JWT实现有严重安全漏洞，或核心功能无法使用 |

---

## Prompt 7：优化Dockerfile并配置docker-compose

### 基本信息

| 字段 | 值 |
|------|-----|
| prompt_index | 7 |
| difficulty | 中等 |
| category | DevOps / 工程化 |
| tech_stack | Docker, docker-compose, Maven, Node.js |
| module_tags | Dockerfile, 容器化, 自动化部署 |

### Prompt 内容

```
当前项目已有基础的 Dockerfile，但存在以下问题：
1. 多阶段构建可以优化，减少镜像层数
2. 缺少 .dockerignore 文件，构建上下文过大
3. 没有健康检查，容器健康状态不可知
4. 使用 root 用户运行，存在安全风险
5. 没有使用构建缓存，重复构建耗时

请完成以下优化：

【优化 Dockerfile】
1. 创建 .dockerignore 文件，排除不需要的文件：
   - .git
   - node_modules（前端构建时单独安装）
   - target（后端构建产物，镜像内重新构建）
   - *.log
   - .dockerignore, Dockerfile, docker-compose.yml

2. 优化多阶段构建：
   - 阶段一：使用 node:18-bookworm 构建前端
   - 阶段二：使用 eclipse-temurin:17-jdk-bookworm 运行后端
   - 阶段三：使用 jre-slim 运行最终应用（减少镜像体积）

3. 添加健康检查：
   - 后端添加 HEALTHCHECK 指令
   - 检查 /api/memos/stats 接口或自定义 /health 端点

4. 使用非 root 用户运行：
   - 创建 app 用户
   - 使用 USER 指令切换到非 root 用户

5. 优化构建缓存：
   - 按正确顺序排列 Dockerfile 指令，充分利用缓存
   - 先复制依赖文件（package.json, pom.xml），安装依赖，再复制源码

【创建 docker-compose.yml】
创建 docker-compose.yml 实现一键启动：

1. 应用服务：
   - image: memo-system:latest
   - build: .
   - ports: "8080:8080"
   - volumes: 挂载数据库文件到宿主机
   - environment: 环境变量配置
   - restart: unless-stopped
   - healthcheck: 配置健康检查

2. 可选：添加 MySQL/PostgreSQL 服务（替代 SQLite）
   - 使用 external: true 链接外部数据库
   - 或使用 docker-compose 内置的 mysql 服务

3. 添加 nginx 反向代理（可选）：
   - 前端静态文件服务
   - API 反向代理到后端
   - SSL 配置（用于生产环境）

【创建 start.sh 启动脚本】
创建便捷的启动脚本：
- start.sh - 一键启动所有服务
- stop.sh - 停止所有服务
- rebuild.sh - 重新构建并启动

【创建部署文档】
在 deployment.md 中说明：
- 开发环境快速启动步骤
- 生产环境部署注意事项
- 数据备份和恢复方法
- 常见问题排查

请确保：
- Dockerfile 符合最佳实践，镜像体积小，构建快
- docker-compose 配置完整，一键启动
- 启动脚本可用，文档清晰
```

### 评分标准

| 分数 | 定义 | 评分理由 |
|------|------|---------|
| 2 | 任务完美执行 | Dockerfile 优化完整符合最佳实践，docker-compose 配置正确，启动脚本可用，文档清晰 |
| 1 | 任务完成但有瑕疵 | 基本功能可用但有改进空间（如镜像体积仍较大，配置不够健壮） |
| 0 | 未完成任务 | 配置错误无法构建成功，或严重偏离需求 |

---

## 附录：难度映射表

| 总分范围 | 难度 | 说明 |
|---------|------|------|
| 0-2 | 简单 | 需求明确，单文件或局部改动，环境简单，验证直接 |
| 3-5 | 中等 | 需求基本明确，多文件改动，需要测试/起服务，验证需要多步 |
| 6-8 | 困难 | 需求模糊，跨模块改动，依赖复杂环境，验证链路长 |

### Prompt 1（Bug修复）：4分 - 中等
- 需求清晰度：2（Bug明确，但需要推断分页问题的根因）
- 修改范围：2（涉及Repository、Service、Controller、前端多处修改）
- 环境复杂度：1（基本不需要额外环境）
- 验证复杂度：2（需要多页切换验证）

### Prompt 2（功能迭代）：4分 - 中等
- 需求清晰度：1（功能目标明确）
- 修改范围：3（前后端都需要修改，多个模块）
- 环境复杂度：1（基本不需要额外环境）
- 验证复杂度：2（需要测试置顶、排序、多条置顶场景）

### Prompt 3（功能迭代）：7分 - 困难
- 需求清晰度：2（功能复杂，需要实现多对多关联）
- 修改范围：4（后端多个Controller+Repository，前端多处修改）
- 环境复杂度：2（需要测试多对多关系，关联查询）
- 验证复杂度：3（需要测试标签创建、关联、筛选、删除关联等场景）

### Prompt 4（代码重构）：2分 - 简单
- 需求清晰度：1（重构目标明确）
- 修改范围：2（只涉及一个Service类）
- 环境复杂度：1（不需要额外环境）
- 验证复杂度：2（需要运行测试验证重构正确性）

### Prompt 5（测试）：4分 - 中等
- 需求清晰度：1（测试需求明确）
- 修改范围：2（只创建测试类）
- 环境复杂度：1（测试框架已配置）
- 验证复杂度：3（需要覆盖所有场景，验证断言正确）

### Prompt 6（代码理解）：7分 - 困难
- 需求清晰度：2（需要分析安全问题和实现方案）
- 修改范围：4（多个Service、Controller、配置文件）
- 环境复杂度：3（需要理解认证机制、添加JWT依赖）
- 验证复杂度：3（需要测试Token生成、验证、保护接口等）

### Prompt 7（DevOps）：4分 - 中等
- 需求清晰度：1（优化目标明确）
- 修改范围：3（Dockerfile、docker-compose、脚本）
- 环境复杂度：2（需要Docker环境）
- 验证复杂度：2（需要测试构建、启动、停止）