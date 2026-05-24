# CRMEB Java 电商系统 (JDK17 + Spring Boot 3.x)

基于 Spring Boot 3.2.0 + JDK 17 的多模块电商管理系统，由 CRMEB Java Open Source V2.1 升级而来。

> 原项目地址：https://gitee.com/ZhongBangKeJi/crmeb_java

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 17 LTS | 最低要求版本 |
| Spring Boot | 3.2.0 | 核心框架 |
| MyBatis-Plus | 3.5.9 | ORM 框架 |
| Spring Security | 6.x | 安全框架 |
| SpringDoc OpenAPI | 2.3.0 | API 文档 (替代 Swagger) |
| Druid | 1.2.20 | 数据库连接池 |
| MySQL | 8.x | 数据库 |
| Redis | 6.x+ | 缓存 |
| Hutool | 5.8.25 | 工具集 |
| JJWT | 0.12.3 | JWT 认证 |

## 项目结构

```
crmeb/
├── crmeb-common        # 公共模块 (配置、常量、工具类、通用依赖)
├── crmeb-service       # 业务层 (Service 接口与实现、DAO、Mapper)
├── crmeb-admin         # 后台管理端 (管理员 API、Spring Security 配置)
├── crmeb-front         # 前台用户端 (用户 API、微信接口)
└── CRMEB_JAVA_升级方案.md  # 升级方案文档
```

- **crmeb-admin** 和 **crmeb-front** 是两个独立的 Spring Boot 启动模块
- **crmeb-service** 依赖 **crmeb-common**
- **crmeb-admin** / **crmeb-front** 依赖 **crmeb-service**

## 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.x+
- Maven 3.8+

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/NingTheshy/crmeb.git
cd crmeb
```

### 2. 导入数据库

项目使用的数据库为 MySQL，导入前请先创建数据库并执行 SQL 脚本（如有的话）。

### 3. 配置文件

项目使用多环境配置，在 `src/main/resources/` 下创建环境配置文件：

- `application-dev.yml` - 开发环境
- `application-prod.yml` - 生产环境

主要配置项：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_db?characterEncoding=utf-8&useSSL=false&serverTimeZone=GMT+8
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_password
```

> 注意：`application.yml` 等配置文件已通过 `.gitignore` 排除，不会提交到仓库。

### 4. 编译运行

```bash
# 编译整个项目
mvn clean package -DskipTests

# 启动后台管理端 (默认端口 8080)
java -jar crmeb-admin/target/crmeb-admin-*.jar

# 启动前台用户端 (默认端口 8081)
java -jar crmeb-front/target/crmeb-front-*.jar
```

或在 IDE 中分别运行：
- `com.zbkj.admin.CrmebAdminApplication` (后台)
- `com.zbkj.front.CrmebFrontApplication` (前台)

### 5. 访问

- 后台管理 API 文档：`http://localhost:8080/doc.html`
- 前台用户 API 文档：`http://localhost:8081/doc.html`
- Druid 监控：`http://localhost:8080/druid/`

## 主要功能模块

- 商品管理（SPU/SKU、分类、品牌、属性）
- 订单管理（下单、支付、退款、发货）
- 用户管理（注册、登录、会员等级）
- 营销活动（优惠券、拼团、秒杀、砍价）
- 内容管理（文章、Banner、DIY 页面）
- 财务管理（提现、结算、对账）
- 数据统计（销售报表、用户分析）
- 系统设置（配置、权限、角色）

## 相对原版的升级内容

本项目由 CRMEB Java V2.1 (JDK8 + Spring Boot 2.x) 升级而来，主要变更：

| 变更项 | 原版本 | 升级后 |
|--------|--------|--------|
| JDK | 1.8 | 17 |
| Spring Boot | 2.x | 3.2.0 |
| javax.* | javax.servlet 等 | jakarta.servlet 等 |
| Spring Security | WebSecurityConfigurerAdapter | SecurityFilterChain Bean |
| API 文档 | Springfox (Swagger 2) | SpringDoc OpenAPI 3.x |
| MySQL 驱动 | mysql-connector-java | mysql-connector-j |
| Druid | druid-spring-boot-starter | druid-spring-boot-3-starter |
| MyBatis-Plus | mybatis-plus-boot-starter | mybatis-plus-spring-boot3-starter |
| JJWT | 0.9.x | 0.12.3 |

详细升级方案请参阅 [CRMEB_JAVA_升级方案.md](CRMEB_JAVA_升级方案.md)。

## 开发说明

### 上传文件存储

支持多种存储方式（通过 `CrmebConfig` 配置）：
- 本地存储
- 阿里云 OSS
- 腾讯云 COS
- 七牛云
- 京东云存储 (AWS S3 兼容)

### 双数据源

项目配置了两个 Redis 数据源：
- 主数据源：业务缓存
- 第二数据源：微信 AccessToken 存储

### 验证码

使用 `spring-boot-starter-captcha` 实现行为验证码（滑动拼图、点选文字等），支持 Redis 分布式缓存。

## License

CRMEB 并不是自由软件，未经许可不能去掉 CRMEB 相关版权。
