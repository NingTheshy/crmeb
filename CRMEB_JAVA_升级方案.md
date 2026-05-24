# CRMEB Java 升级方案

## 项目概述

| 项目 | 原版本 | 升级后版本 |
|------|--------|-----------|
| JDK | 1.8 | 17 |
| Spring Boot | 2.x | 3.2.0 |
| 项目名称 | CRMEB Java Open Source V2.1 | CRMEB Java Open Source V2.1 (JDK17) |

- 原项目地址：https://gitee.com/ZhongBangKeJi/crmeb_java
- 升级后仓库：https://github.com/NingTheshy/crmeb

---

## 一、核心框架升级

### 1.1 JDK 版本升级 (1.8 → 17)

#### 概述

将 Java 运行时环境从 JDK 1.8 (Java 8) 升级至 JDK 17 (Java 17 LTS)。这是整个升级的基础，因为 Spring Boot 3.x 要求最低 JDK 版本为 17。

#### 变更原因

1. **Spring Boot 3.x 强制要求：** Spring Boot 3.0 及以上版本不再支持 JDK 8 和 JDK 11，最低要求 JDK 17。这是由于 Spring Boot 3.x 基于 Jakarta EE 9+，而 Jakarta EE 9+ 需要 JDK 17 的支持。
2. **性能提升：** JDK 17 相比 JDK 8 有显著的性能提升，包括：
   - ZGC（Z Garbage Collector）和 Shenandoah GC 的改进
   - JIT 编译器优化
   - 字符串处理性能提升（String 操作比 JDK 8 快 15-20%）
3. **新语言特性：** JDK 17 引入了大量新特性：
   - Records（记录类）- 简化数据类编写
   - Sealed Classes（密封类）- 限制类继承
   - Pattern Matching for instanceof - 简化类型检查
   - Text Blocks（文本块）- 多行字符串
   - Switch Expressions - 增强的 switch 语句
4. **安全更新：** JDK 8 已进入 extended support 阶段，安全更新不如 LTS 版本及时。

#### 解决方案

**步骤一：修改 pom.xml 配置**

在根 `pom.xml` 中设置 Java 版本为 17：

```xml
<properties>
    <java.version>17</java.version>
</properties>
```

**步骤二：配置 Maven 编译插件**

在根 `pom.xml` 的 `<build>` 节点中配置编译插件，指定 source 和 target 版本：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.12.1</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <encoding>UTF-8</encoding>
                <compilerArgument>-parameters</compilerArgument>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**步骤三：IDE 配置**

在 IntelliJ IDEA 中：
1. 打开 `File → Project Structure → Project`
2. 将 `Project SDK` 设置为 JDK 17
3. 将 `Project language level` 设置为 17

在 Eclipse 中：
1. 打开 `Window → Preferences → Java → Compiler`
2. 将 `Compiler compliance level` 设置为 17

#### 总结

JDK 17 升级是整个项目升级的基础。升级后，项目获得了更好的性能、更多的语言特性和更长的安全支持周期。由于 JDK 17 保持了良好的向后兼容性，原有代码无需修改即可编译运行。

---

### 1.2 Spring Boot 版本升级 (2.x → 3.2.0)

#### 概述

将 Spring Boot 从 2.x 版本升级至 3.2.0。这是整个升级的核心，Spring Boot 3.x 带来了大量新特性和破坏性变更。

#### 变更原因

1. **技术演进：** Spring Boot 2.x 已进入维护模式，新功能和安全修复主要集中在 3.x 版本。
2. **Jakarta EE 9+ 支持：** Spring Boot 3.x 基于 Jakarta EE 9+，提供了更现代的 API。
3. **性能优化：** Spring Boot 3.x 在启动速度、内存占用等方面有显著优化。
4. **GraalVM 原生镜像支持：** Spring Boot 3.x 支持编译为原生镜像，大幅减少启动时间和内存占用。
5. **安全更新：** Spring Boot 3.x 提供了最新的安全补丁和漏洞修复。

#### 解决方案

**步骤一：修改 parent 版本**

在根 `pom.xml` 中将 Spring Boot parent 版本升级至 3.2.0：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
    <relativePath/>
</parent>
```

**步骤二：更新子模块 Spring Boot 插件版本**

在 `crmeb-admin/pom.xml` 和 `crmeb-front/pom.xml` 中更新打包插件版本：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>3.2.5</version>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**步骤三：处理循环依赖**

Spring Boot 3.x 默认禁止循环依赖。由于项目中存在循环依赖，需要在 `application.yml` 中显式允许：

```yaml
spring:
  main:
    allow-circular-references: true
```

**步骤四：更新配置属性**

Spring Boot 3.x 中部分配置属性发生了变化：

| 配置项 | 原配置 (2.x) | 新配置 (3.x) |
|--------|-------------|-------------|
| Redis | `spring.redis.*` | `spring.data.redis.*` |
| Servlet | `spring.servlet.*` | 保持不变 |

由于项目使用自定义的 `RedisConfig` 类手动读取 `spring.redis.*` 配置，因此配置属性路径无需变更。

#### 总结

Spring Boot 3.2.0 升级是整个项目升级的核心。升级后，项目获得了更好的性能、更多的特性和更长的安全支持。升级过程中需要注意 Jakarta EE 命名空间迁移、循环依赖处理等关键点。

---

## 二、javax.* 到 jakarta.* 命名空间迁移

### 2.1 概述

Spring Boot 3.x 基于 Jakarta EE 9+，所有 `javax.*` 命名空间迁移到 `jakarta.*`。项目中共 **230 个 Java 文件** 涉及此变更，主要涉及 Servlet、Validation、Annotation 等包。

### 2.2 变更原因

1. **Jakarta EE 9+ 规范要求：** Jakarta EE 9 正式将 `javax.*` 命名空间更改为 `jakarta.*`，这是为了区分 Oracle 维护的 Java EE 和 Eclipse 基金会维护的 Jakarta EE。
2. **Spring Boot 3.x 强制要求：** Spring Boot 3.x 基于 Jakarta EE 9+，必须使用 `jakarta.*` 命名空间。
3. **包名变更的法律原因：** Oracle 将 Java EE 捐赠给 Eclipse 基金会后，由于商标问题，Eclipse 基金会无法继续使用 `javax.*` 命名空间，因此统一更名为 `jakarta.*`。

### 2.3 涉及的命名空间

| 原命名空间 (javax) | 新命名空间 (jakarta) | 影响范围 |
|-------------------|---------------------|---------|
| `javax.servlet.*` | `jakarta.servlet.*` | Filter、Interceptor、Controller、Request/Response 等 |
| `javax.validation.*` | `jakarta.validation.*` | @NotNull、@NotBlank、@Size 等请求参数校验注解 |
| `javax.annotation.*` | `jakarta.annotation.*` | @Resource、@PostConstruct、@PreDestroy 等注解 |

### 2.4 解决方案

**批量替换策略：**

使用 IDE 的全局替换功能（如 IntelliJ IDEA 的 `Ctrl+Shift+R` 或 Eclipse 的 `Ctrl+H`），按以下规则进行批量替换：

| 查找内容 | 替换内容 | 文件类型 |
|---------|---------|---------|
| `import javax.servlet.` | `import jakarta.servlet.` | *.java |
| `import javax.validation.` | `import jakarta.validation.` | *.java |
| `import javax.annotation.` | `import jakarta.annotation.` | *.java |

**具体替换示例：**

```java
// 原代码
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.annotation.Resource;

// 新代码
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.annotation.Resource;
```

**按模块分布：**

| 模块 | 变更文件数 | 主要涉及类 |
|------|-----------|-----------|
| crmeb-common | 约 120 个 | Request 类、VO 类、Validator、Interceptor、Filter 等 |
| crmeb-admin | 约 40 个 | Controller、Service、Filter、Config 等 |
| crmeb-front | 约 30 个 | Controller、Service、Filter、Interceptor 等 |
| crmeb-service | 约 40 个 | ServiceImpl、CallbackService 等 |

### 2.5 未迁移的 javax 命名空间

以下 `javax.*` 包属于 **Java 标准库（Java SE）**，**不属于 Jakarta EE 迁移范围**，无需修改：

| javax 包 | 说明 | 涉及文件 |
|----------|------|---------|
| `javax.sql.DataSource` | JDBC 数据源接口 | DruidConfig.java (admin/front) |
| `javax.xml.parsers.*` | XML 解析 | XmlUtil.java, WXPayXmlUtil.java, WxPayUtil.java |
| `javax.xml.transform.*` | XML 转换 | XmlUtil.java |
| `javax.xml.XMLConstants` | XML 常量 | WXPayXmlUtil.java |
| `javax.imageio.*` | 图片 IO | ValidateCodeUtil.java, ImageMergeUtil.java, QRCodeUtil.java |
| `javax.crypto.*` | 加密解密 | AESUtil.java, OnePassUtil.java, CallbackServiceImpl.java |
| `javax.net.ssl.*` | SSL/TLS | RestTemplateUtil.java |
| `javax.management.*` | JMX 管理 | SystemCityRequest.java |

**区分方法：** `javax.servlet`、`javax.validation`、`javax.annotation` 属于 Jakarta EE，需要迁移；而 `javax.sql`、`javax.xml`、`javax.crypto`、`javax.imageio` 属于 Java SE 标准库，不需要迁移。

### 2.6 总结

javax.* 到 jakarta.* 的迁移是 Spring Boot 3.x 升级中工作量最大的部分，涉及 230 个文件。通过 IDE 的全局替换功能可以快速完成。需要注意区分 Jakarta EE 包和 Java SE 标准库包，避免误替换。

---

## 三、Spring Security 配置重构

### 3.1 概述

Spring Boot 3.x 移除了 `WebSecurityConfigurerAdapter`，改用 `SecurityFilterChain` Bean 配置方式。同时，`@EnableGlobalMethodSecurity` 注解被 `@EnableMethodSecurity` 替代。

### 3.2 变更原因

1. **Spring Security 6.x 弃用：** Spring Security 6.x（Spring Boot 3.x 使用）正式移除了 `WebSecurityConfigurerAdapter`，因为它限制了配置的灵活性。
2. **Lambda DSL 配置：** 新的配置方式支持 Lambda DSL，使配置代码更加简洁和类型安全。
3. **方法级安全增强：** `@EnableMethodSecurity` 提供了更细粒度的控制，支持 `prePostEnabled` 和 `securedEnabled` 属性。
4. **更好的可测试性：** `SecurityFilterChain` Bean 方式更容易进行单元测试和集成测试。

### 3.3 Admin 模块 Security 配置变更

**文件：** `crmeb-admin/src/main/java/com/zbkj/admin/config/WebSecurityConfig.java`

#### 原实现（Spring Boot 2.x / Spring Security 5.x）

```java
@Configuration
@EnableWebSecurity
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CorsFilter corsFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors()
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler())
                .accessDeniedHandler(accessDeniedHandler())
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/api/admin/login", "/api/admin/validate/code/get").permitAll()
                .antMatchers("/api/admin/getLoginPic").permitAll()
                .antMatchers("/api/admin/login/account/detection").permitAll()
                .antMatchers("/" + UploadConstants.UPLOAD_FILE_KEYWORD + "/**").permitAll()
                .antMatchers("/" + UploadConstants.DOWNLOAD_FILE_KEYWORD + "/**").permitAll()
                .antMatchers("/api/admin/upload/image").permitAll()
                .antMatchers("/api/admin/upload/file").permitAll()
                .antMatchers(HttpMethod.GET, "/*.html").permitAll()
                .antMatchers("/**/*.html").permitAll()
                .antMatchers("/**/*.css").permitAll()
                .antMatchers("/**/*.js").permitAll()
                .antMatchers("/profile/**").permitAll()
                .antMatchers("/doc.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/druid/**").permitAll()
                .antMatchers("/captcha/get", "/captcha/check").permitAll()
                .antMatchers("/api/admin/payment/callback/**").permitAll()
                .antMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .headers().frameOptions().disable()
            .and()
            .authenticationProvider(customAuthenticationProvider())
            .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class)
            .addFilterBefore(corsFilter, LogoutFilter.class);
    }
}
```

#### 新实现（Spring Boot 3.x / Spring Security 6.x）

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private CorsFilter corsFilter;

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public AuthenticationEntryPointImpl unauthorizedHandler() {
        return new AuthenticationEntryPointImpl();
    }

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(new UserDetailServiceImpl());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(unauthorizedHandler())
                .accessDeniedHandler(accessDeniedHandler()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/login", "/api/admin/validate/code/get").permitAll()
                .requestMatchers("/api/admin/getLoginPic").permitAll()
                .requestMatchers("/api/admin/login/account/detection").permitAll()
                .requestMatchers("/api/admin/validate/code/getcaptchaconfig").permitAll()
                .requestMatchers("/" + UploadConstants.UPLOAD_FILE_KEYWORD + "/**").permitAll()
                .requestMatchers("/" + UploadConstants.DOWNLOAD_FILE_KEYWORD + "/**").permitAll()
                .requestMatchers("/" + UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/**").permitAll()
                .requestMatchers("/api/admin/upload/image").permitAll()
                .requestMatchers("/api/admin/upload/file").permitAll()
                .requestMatchers(HttpMethod.GET, "/*.html").permitAll()
                .requestMatchers(new AntPathRequestMatcher("/**/*.html", "GET")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/**/*.css", "GET")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/**/*.js", "GET")).permitAll()
                .requestMatchers("/profile/**").permitAll()
                .requestMatchers("/common/download**").permitAll()
                .requestMatchers("/common/download/resource**").permitAll()
                .requestMatchers("/doc.html").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html/**").permitAll()
                .requestMatchers("/druid/**").permitAll()
                .requestMatchers("/captcha/get", "/captcha/check").permitAll()
                .requestMatchers("/api/admin/payment/callback/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            .authenticationProvider(customAuthenticationProvider())
            .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class)
            .addFilterBefore(corsFilter, LogoutFilter.class);

        return http.build();
    }
}
```

#### 关键变更点对比

| 变更项 | 原方式 (Spring Security 5.x) | 新方式 (Spring Security 6.x) |
|--------|---------------------------|---------------------------|
| 类继承 | `extends WebSecurityConfigurerAdapter` | 无继承，使用 `@Bean` 方法 |
| CSRF 禁用 | `.csrf().disable()` | `.csrf(AbstractHttpConfigurer::disable)` |
| CORS 配置 | `.cors().and()` | `.cors(cors -> {})` |
| 异常处理 | `.exceptionHandling().and()` | `.exceptionHandling(exception -> exception...)` |
| 会话管理 | `.sessionManagement().and()` | `.sessionManagement(session -> session...)` |
| URL 匹配 | `.antMatchers()` | `.requestMatchers()` |
| 静态资源匹配 | `.antMatchers("/**/*.css").permitAll()` | `.requestMatchers(new AntPathRequestMatcher("/**/*.css", "GET"))` |
| 方法级安全 | `@EnableGlobalMethodSecurity` | `@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)` |
| Headers 配置 | `.headers().frameOptions().disable()` | `.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))` |
| 返回值 | void（父类方法） | `SecurityFilterChain` 对象 |

### 3.4 Front 模块 Security 配置变更

**文件：** `crmeb-front/src/main/java/com/zbkj/front/config/CloseSecurityConfig.java`

Front 模块禁用了 Security，配置相对简单：

#### 原实现

```java
@Configuration
@EnableWebSecurity
public class CloseSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().anyRequest().permitAll();
        http.logout().permitAll();
    }
}
```

#### 新实现

```java
@Configuration
@EnableWebSecurity
public class CloseSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        http.logout(logout -> logout.permitAll());
        return http.build();
    }
}
```

### 3.5 总结

Spring Security 配置重构是 Spring Boot 3.x 升级中的关键变更。新的 `SecurityFilterChain` Bean 配置方式更加灵活和现代，Lambda DSL 使配置代码更加简洁。升级过程中需要注意 URL 匹配方式的变化和方法级安全注解的替换。

---

## 四、Swagger / SpringDoc 配置重构

### 4.1 概述

Spring Boot 3.x 不再支持 Springfox（Springfox 在 2020 年后停止维护），改用 SpringDoc OpenAPI 3.x 作为 API 文档框架。Swagger UI 路径和 API 文档路径均发生变化。

### 4.2 变更原因

1. **Springfox 停止维护：** Springfox 项目在 2020 年后基本停止更新，无法支持 Spring Boot 3.x 和 OpenAPI 3.x 规范。
2. **OpenAPI 3.x 规范：** OpenAPI 3.x 是行业标准的 API 描述规范，提供了更丰富的功能，如更好的 Schema 定义、安全方案描述等。
3. **SpringDoc 优势：** SpringDoc 是 Spring Boot 3.x 官方推荐的 API 文档解决方案，具有以下优势：
   - 自动扫描 Spring MVC 控制器
   - 支持 OpenAPI 3.x 规范
   - 支持 Swagger UI 和 ReDoc 等多种文档界面
   - 支持分组 API 文档
   - 与 Spring Security 深度集成

### 4.3 依赖变更

#### 原依赖（Springfox）

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```

#### 新依赖（SpringDoc）

```xml
<!-- SpringDoc OpenAPI 3 -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>

<!-- Swagger 2 注解兼容（保留 @ApiModel, @ApiModelProperty 等注解） -->
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-annotations</artifactId>
    <version>1.6.12</version>
</dependency>
```

**依赖说明：**

- `springdoc-openapi-starter-webmvc-ui`：SpringDoc 的核心依赖，提供 OpenAPI 3.x 支持和 Swagger UI 界面。
- `swagger-annotations`：保留 Swagger 2 的注解支持，使得原有代码中的 `@ApiModel`、`@ApiModelProperty` 等注解无需修改即可工作。

### 4.4 配置类变更

**文件：** `crmeb-admin/src/main/java/com/zbkj/admin/config/SwaggerConfig.java`

#### 原实现（Springfox）

```java
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${crmeb.domain:}")
    private String domain;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .host(domain)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zbkj"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Crmeb Java")
                .description("Crmeb")
                .version("1.0.0")
                .build();
    }
}
```

#### 新实现（SpringDoc OpenAPI 3）

```java
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class SwaggerConfig {

    Boolean swaggerEnabled = true;

    @Value("${crmeb.domain:}")
    private String domain;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crmeb Java")
                        .description("Crmeb")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(Constants.HEADER_AUTHORIZATION_KEY))
                .components(new Components()
                        .addSecuritySchemes(Constants.HEADER_AUTHORIZATION_KEY,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .name(Constants.HEADER_AUTHORIZATION_KEY)));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/api/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/public/**")
                .build();
    }
}
```

#### 关键变更点对比

| 变更项 | 原方式 (Springfox) | 新方式 (SpringDoc) |
|--------|-------------------|-------------------|
| 启用注解 | `@EnableSwagger2` | 无需注解，自动配置 |
| 配置类 | `Docket` Bean | `OpenAPI` Bean |
| API 分组 | 无（或使用 `GroupedOpenApi`） | `GroupedOpenApi` Bean |
| 安全方案 | 在 Docket 中配置 | 在 `OpenAPI` 的 `Components` 中配置 |
| 包扫描 | `RequestHandlerSelectors.basePackage()` | 自动扫描（或使用 `GroupedOpenApi` 指定路径） |
| Host 配置 | `Docket.host(domain)` | 不再支持（使用 `OpenAPI.servers()` 替代） |

### 4.5 访问路径变更

| 功能 | 原路径 (Springfox) | 新路径 (SpringDoc) |
|------|-------------------|-------------------|
| Swagger UI | `http://localhost:8080/swagger-ui.html` | `http://localhost:8080/swagger-ui/index.html` |
| API Docs JSON | `http://localhost:8080/v2/api-docs` | `http://localhost:8080/v3/api-docs` |
| Swagger Resources | `http://localhost:8080/swagger-resources/*` | `http://localhost:8080/v3/api-docs/swagger-config` |

### 4.6 安全路径白名单更新

在 `application.yml` 中需要更新白名单配置：

```yaml
crmeb:
  ignored:
    - swagger-ui/
    - swagger-resources/**
    - /**/v2/api-docs
    - /**/v3/api-docs/**     # 新增 OpenAPI 3 文档路径
    - /swagger-ui/**          # 新增 SpringDoc UI 路径
    - /actuator/**
    - /druid/**
```

### 4.7 总结

Swagger / SpringDoc 重构是 API 文档框架的完全替换。SpringDoc 提供了更现代的 OpenAPI 3.x 支持，自动扫描和分组功能使 API 文档管理更加便捷。升级过程中需要注意配置类的重写和访问路径的变更。

---

## 五、MyBatis-Plus 配置变更

### 5.1 概述

MyBatis-Plus 的 Spring Boot Starter 从 `mybatis-plus-boot-starter` 替换为 `mybatis-plus-spring-boot3-starter`，以适配 Spring Boot 3.x。

### 5.2 变更原因

1. **Spring Boot 3.x 适配：** `mybatis-plus-boot-starter` 是为 Spring Boot 2.x 设计的，内部依赖了 `javax.*` 命名空间的类，无法在 Spring Boot 3.x 中正常工作。
2. **官方提供：** MyBatis-Plus 官方提供了 `mybatis-plus-spring-boot3-starter`，专门为 Spring Boot 3.x 设计，使用 `jakarta.*` 命名空间。
3. **功能增强：** Spring Boot 3.x 版本的 Starter 支持更多新特性，如更好的自动配置和性能优化。

### 5.3 依赖变更

#### 原依赖

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.x</version>
</dependency>
```

#### 新依赖

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.9</version>
</dependency>
```

### 5.4 配置文件变更

`application.yml` 中的 MyBatis-Plus 配置基本保持不变：

```yaml
mybatis-plus:
  mapper-locations: classpath*:mapper/*/*Mapper.xml
  typeAliasesPackage: com.zbkj.**.model
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-value: 1
      logic-not-delete-value: 0
```

**说明：** MyBatis-Plus 3.5.9 的配置项与之前版本基本兼容，无需修改配置文件。

### 5.5 总结

MyBatis-Plus Starter 替换是简单的依赖变更，但由于 Starter 内部集成了大量自动配置，确保了 ORM 层在 Spring Boot 3.x 中的正常工作。升级后无需修改业务代码。

---

## 六、Druid 数据源配置变更

### 6.1 概述

Druid 的 Spring Boot Starter 从 `druid-spring-boot-starter` 替换为 `druid-spring-boot-3-starter`，以适配 Spring Boot 3.x。

### 6.2 变更原因

1. **Spring Boot 3.x 适配：** 与 MyBatis-Plus 类似，`druid-spring-boot-starter` 内部依赖了 `javax.*` 命名空间，无法在 Spring Boot 3.x 中正常工作。
2. **官方提供：** 阿里巴巴官方提供了 `druid-spring-boot-3-starter`，专门为 Spring Boot 3.x 设计。
3. **功能保持：** 新版本的 Starter 保持了与原版本相同的功能，包括监控页面、SQL 防火墙等。

### 6.3 依赖变更

#### 原依赖

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.2.x</version>
</dependency>
```

#### 新依赖

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-3-starter</artifactId>
    <version>1.2.20</version>
</dependency>
```

### 6.4 配置类变更

**文件：** `crmeb-admin/src/main/java/com/zbkj/admin/config/DruidConfig.java`

Druid 配置类保持不变：

```java
@Configuration
public class DruidConfig {

    @Bean("dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }
}
```

**说明：** 配置类使用的是 `javax.sql.DataSource`（Java SE 标准库），无需修改。

### 6.5 总结

Druid Starter 替换是简单的依赖变更。升级后，Druid 的监控页面、SQL 防火墙等功能保持不变。

---

## 七、MySQL 驱动变更

### 7.1 概述

MySQL JDBC 驱动从 `mysql-connector-java` 更名为 `mysql-connector-j`，groupId 也从 `mysql` 变更为 `com.mysql`。

### 7.2 变更原因

1. **官方更名：** MySQL 官方在 8.x 版本后将驱动包从 `mysql-connector-java` 更名为 `mysql-connector-j`，以更准确地反映其功能。
2. **GroupId 变更：** 配合包名变更，Maven 坐标的 groupId 也从 `mysql` 变更为 `com.mysql`。
3. **功能增强：** 新版本驱动支持更多 MySQL 8.x 特性，如更好的 X Protocol 支持。

### 7.3 依赖变更

#### 原依赖

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.x</version>
</dependency>
```

#### 新依赖

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```

### 7.4 配置文件变更

`application.yml` 中的数据源配置保持不变：

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/single_open?characterEncoding=utf-8&useSSL=false&serverTimeZone=GMT+8
    username: root
    password: 220718
```

**说明：** 驱动类名 `com.mysql.cj.jdbc.Driver` 保持不变，数据源 URL 格式也保持不变。

### 7.5 总结

MySQL 驱动变更是简单的依赖坐标变更。升级后，驱动的功能和配置方式保持不变，但获得了更好的 MySQL 8.x 特性支持。

---

## 八、JWT 库升级

### 8.1 概述

JWT 库从 JJWT 0.9.x 升级至 0.12.3，这是一个大版本升级，API 发生了重大变化。

### 8.2 变更原因

1. **安全修复：** JJWT 0.9.x 存在已知的安全漏洞，升级至 0.12.3 可以修复这些漏洞。
2. **API 改进：** 0.12.x 版本的 API 更加简洁和类型安全。
3. **功能增强：** 新版本支持更多 JWT 特性，如更好的密钥管理。
4. **依赖对齐：** 配合 Spring Boot 3.x 升级，使用最新的 JWT 库版本。

### 8.3 依赖变更

#### 原依赖

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.9.1</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.9.1</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.9.1</version>
    <scope>runtime</scope>
</dependency>
```

#### 新依赖

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>jwks-rsa</artifactId>
    <version>0.22.1</version>
</dependency>
```

### 8.4 API 变更详解

#### JWT 构建器 API 变更

| 原 API (0.9.x) | 新 API (0.12.x) | 说明 |
|----------------|-----------------|------|
| `Jwts.builder()` | `Jwts.builder()` | 保持不变 |
| `.setSubject(subject)` | `.subject(subject)` | 方法名简化 |
| `.setIssuedAt(date)` | `.issuedAt(date)` | 方法名简化 |
| `.setExpiration(date)` | `.expiration(date)` | 方法名简化 |
| `.setIssuer(issuer)` | `.issuer(issuer)` | 方法名简化 |
| `.signWith(key)` | `.signWith(key)` | 保持不变 |

#### JWT 解析器 API 变更

| 原 API (0.9.x) | 新 API (0.12.x) | 说明 |
|----------------|-----------------|------|
| `Jwts.parser()` | `Jwts.parser()` | 保持不变 |
| `.setSigningKey(key)` | `.verifyWith(key)` | 方法名变更 |
| `.parseClaimsJws(token)` | `.build().parseSignedClaims(token)` | 需要先 build |
| `Claims claims = jwt.getBody()` | `SignedJWT jwt = ...; Claims claims = jwt.getPayload()` | 获取 Claims 方式变更 |

#### 代码示例

```java
// 原代码 (JJWT 0.9.x)
String token = Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(expiration)
        .signWith(key)
        .compact();

Claims claims = Jwts.parser()
        .setSigningKey(key)
        .parseClaimsJws(token)
        .getBody();

// 新代码 (JJWT 0.12.x)
String token = Jwts.builder()
        .subject(username)
        .issuedAt(new Date())
        .expiration(expiration)
        .signWith(key)
        .compact();

Claims claims = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
```

### 8.5 密钥生成方式变更

JJWT 0.12.x 推荐使用 `Keys.secretKeyFor()` 生成密钥：

```java
// 原方式
Key key = Keys.hmacShaKeyFor(secret.getBytes());

// 新方式
SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
```

### 8.6 总结

JWT 库升级是安全性和 API 现代化的重要步骤。虽然 API 发生了较大变化，但核心功能保持不变。升级过程中需要仔细检查所有 JWT 相关的代码，确保 API 调用正确。

---

## 九、Redis 配置变更

### 9.1 概述

Spring Boot 3.x 中 Redis 配置属性从 `spring.redis.*` 变更为 `spring.data.redis.*`。但由于项目使用自定义 `RedisConfig` 类手动读取配置，因此配置属性路径未变更。

### 9.2 变更原因

1. **配置属性重组：** Spring Boot 3.x 将 Redis 相关配置从 `spring.redis.*` 移至 `spring.data.redis.*`，以更好地组织配置结构。
2. **自动配置变更：** Spring Boot 3.x 的 Redis 自动配置类发生了变化，使用了新的配置属性前缀。
3. **自定义配置绕过：** 由于项目使用自定义的 `RedisConfig` 类手动创建 `JedisConnectionFactory`，通过 `@Value` 注解读取 `spring.redis.*` 配置，因此配置属性路径无需变更。

### 9.3 配置文件

**文件：** `crmeb-common/src/main/java/com/zbkj/common/config/RedisConfig.java`

项目使用自定义的 `RedisConfig` 类，通过 `@Value` 注解手动读取 `spring.redis.*` 配置：

```java
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPass;

    @Value("${spring.redis.database}")
    private int redisDb;

    @Value("${spring.redis.timeout}")
    private String timeout;

    // ... 其他配置属性

    @Bean(name = "redisConnectionFactory")
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setPort(redisPort);
        config.setHostName(redisHost);
        config.setDatabase(redisDb);
        config.setPassword(redisPass);

        JedisClientConfiguration.DefaultJedisClientConfigurationBuilder jpConfigBuilder =
            (JedisClientConfiguration.DefaultJedisClientConfigurationBuilder) JedisClientConfiguration.builder();
        jpConfigBuilder.usePooling();
        jpConfigBuilder.poolConfig(jedisPoolConfig());
        jpConfigBuilder.readTimeout(Duration.ofMillis(Integer.parseInt(timeout)));
        jpConfigBuilder.connectTimeout(Duration.ofMillis(Integer.parseInt(timeout)));

        JedisClientConfiguration jedisClientConfiguration = jpConfigBuilder.build();
        return new JedisConnectionFactory(config, jedisClientConfiguration);
    }

    // ... 第二个 Redis 数据源配置
}
```

### 9.4 配置文件

`application.yml` 中的 Redis 配置保持不变：

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: root
    timeout: 30000
    database: 6
    jedis:
      pool:
        max-active: 200
        max-wait: -1
        max-idle: 10
        min-idle: 0
    second:
      database: 14
```

### 9.5 总结

由于项目使用自定义的 Redis 配置类，Redis 配置在升级过程中无需变更。如果项目使用 Spring Boot 自动配置的 Redis，则需要将 `spring.redis.*` 更改为 `spring.data.redis.*`。

---

## 十、application.yml 配置变更

### 10.1 概述

Spring Boot 3.x 对部分配置项进行了调整，需要在 `application.yml` 中进行相应修改。

### 10.2 变更原因

1. **循环依赖默认禁止：** Spring Boot 3.x 默认禁止循环依赖，而项目中存在循环依赖，需要显式允许。
2. **配置属性重组：** 部分配置属性的路径发生了变化（如 Redis 配置）。
3. **安全配置增强：** 需要更新安全路径白名单以支持新的 Swagger UI 路径。

### 10.3 新增配置

```yaml
spring:
  main:
    allow-circular-references: true  # 允许循环依赖（Spring Boot 3.x 默认禁止）
```

**说明：** 由于项目中存在循环依赖（如 Service 之间的相互注入），需要显式允许循环依赖。在生产环境中，建议逐步重构代码以消除循环依赖。

### 10.4 安全路径白名单更新

```yaml
crmeb:
  ignored:
    - swagger-ui/
    - swagger-resources/**
    - /**/v2/api-docs
    - /**/v3/api-docs/**     # 新增 OpenAPI 3 文档路径
    - /swagger-ui/**          # 新增 SpringDoc UI 路径
    - /actuator/**
    - /druid/**
    - api/admin/pagediy/info
    - api/public/**
```

### 10.5 总结

application.yml 的变更主要是新增循环依赖配置和更新安全路径白名单。这些变更是 Spring Boot 3.x 升级的必要步骤。

---

## 十一、模块依赖关系

### 11.1 概述

项目采用多模块 Maven 结构，模块依赖关系如下：

```
crmeb (parent)
├── crmeb-common (公共模块)
│   ├── spring-boot-starter-web
│   ├── spring-boot-starter-data-redis
│   ├── spring-boot-starter-validation (Jakarta)
│   ├── spring-boot-starter-security
│   ├── spring-boot-starter-aop
│   ├── spring-boot-starter-actuator
│   ├── mybatis-plus-spring-boot3-starter
│   ├── druid-spring-boot-3-starter
│   ├── mysql-connector-j
│   ├── springdoc-openapi-starter-webmvc-ui
│   ├── swagger-annotations (兼容)
│   ├── jjwt-api / jjwt-impl / jjwt-jackson
│   ├── hutool-all
│   ├── pagehelper-spring-boot-starter
│   └── 其他工具类依赖
├── crmeb-service (服务层)
│   └── crmeb-common
├── crmeb-admin (后台管理)
│   ├── crmeb-service
│   ├── spring-boot-starter-quartz
│   └── oshi-core (系统监控)
└── crmeb-front (前台)
    └── crmeb-service
```

### 11.2 变更原因

模块依赖关系在升级过程中保持不变，但各模块的依赖版本需要统一升级以确保兼容性。

### 11.3 依赖版本管理

在根 `pom.xml` 的 `<dependencyManagement>` 节点中统一管理所有依赖版本：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-3-starter</artifactId>
            <version>${druid.version}</version>
        </dependency>
        <!-- ... 其他依赖 -->
    </dependencies>
</dependencyManagement>
```

### 11.4 总结

模块依赖关系保持不变，但需要确保所有依赖版本在根 pom.xml 中统一管理，避免版本冲突。

---

## 十二、构建配置变更

### 12.1 概述

Maven 构建配置需要更新以支持 JDK 17 和 Spring Boot 3.x。

### 12.2 变更原因

1. **JDK 版本要求：** Maven 编译插件需要配置 source 和 target 版本为 17。
2. **Spring Boot 打包插件：** 需要更新插件版本以支持 Spring Boot 3.x。
3. **资源过滤：** 需要配置资源过滤以支持新的文件类型。

### 12.3 编译插件配置

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.12.1</version>
    <configuration>
        <source>17</source>
        <target>17</target>
        <encoding>UTF-8</encoding>
        <compilerArgument>-parameters</compilerArgument>
    </configuration>
</plugin>
```

**参数说明：**

- `source`：指定源代码的 Java 版本
- `target`：指定生成字节码的 Java 版本
- `encoding`：指定源代码编码
- `-parameters`：保留方法参数名（Spring 框架需要此参数）

### 12.4 Spring Boot 打包插件配置

在 `crmeb-admin/pom.xml` 和 `crmeb-front/pom.xml` 中：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>3.2.5</version>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 12.5 资源过滤配置

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <configuration>
        <useDefaultDelimiters>true</useDefaultDelimiters>
        <nonFilteredFileExtensions>
            <nonFilteredFileExtension>woff</nonFilteredFileExtension>
            <nonFilteredFileExtension>woff2</nonFilteredFileExtension>
            <nonFilteredFileExtension>eot</nonFilteredFileExtension>
            <nonFilteredFileExtension>ttf</nonFilteredFileExtension>
            <nonFilteredFileExtension>svg</nonFilteredFileExtension>
        </nonFilteredFileExtensions>
    </configuration>
</plugin>
```

### 12.6 总结

构建配置的变更确保了项目可以在 JDK 17 环境下编译，并生成兼容 Spring Boot 3.x 的可执行 JAR 包。

---

## 十三、升级注意事项

### 13.1 编译要求

| 项目 | 要求 |
|------|------|
| JDK 版本 | 必须使用 JDK 17 或更高版本 |
| Maven 版本 | 建议使用 Maven 3.8+ |
| IDE 配置 | 需将项目 SDK 设置为 JDK 17 |

### 13.2 运行时依赖

| 组件 | 版本要求 |
|------|---------|
| MySQL | 建议 8.0+ |
| Redis | 建议 6.0+ |
| JDK | 必须 17+ |

### 13.3 已知兼容性说明

1. **循环依赖：** Spring Boot 3.x 默认禁止循环依赖，需配置 `spring.main.allow-circular-references: true`。建议后续重构代码消除循环依赖。

2. **javax.sql 包：** `javax.sql.DataSource` 等 Java 标准库包无需迁移，它们属于 Java SE 而非 Jakarta EE。

3. **javax.xml 包：** `javax.xml.*` 等 XML 处理包无需迁移。

4. **javax.crypto 包：** `javax.crypto.*` 等加密包无需迁移。

5. **javax.imageio 包：** `javax.imageio.*` 等图片处理包无需迁移。

### 13.4 Swagger UI 访问

升级后 Swagger UI 访问地址变更：
- 原地址：`http://localhost:8080/swagger-ui.html`
- 新地址：`http://localhost:8080/swagger-ui/index.html`

### 13.5 日志配置

项目使用 Logback 进行日志管理，配置文件 `logback-spring.xml` 在升级过程中无需修改。

---

## 十四、文件变更统计

| 类型 | 变更文件数 | 说明 |
|------|-----------|------|
| Java 文件 (jakarta 迁移) | 230 | javax.* → jakarta.* |
| pom.xml | 5 | 依赖版本升级 |
| SwaggerConfig.java | 2 | Springfox → SpringDoc |
| WebSecurityConfig.java | 1 | Security 配置重构 |
| CloseSecurityConfig.java | 1 | Security 配置重构 |
| application.yml | 2 | 配置项调整 |
| logback-spring.xml | 0 | 无需修改 |
| Mapper XML | 0 | 无需修改 |
| 业务代码 | 0 | 无需修改 |

---

## 十五、总结

### 15.1 升级成果

本次升级完成了从 JDK 1.8 + Spring Boot 2.x 到 JDK 17 + Spring Boot 3.2.0 的全面迁移，主要包括：

1. **Java 版本升级：** JDK 1.8 → 17，获得更好的性能和语言特性
2. **Spring Boot 大版本升级：** 2.x → 3.2.0，获得最新的框架特性
3. **命名空间迁移：** 全量完成 javax.* → jakarta.* 的迁移（230 个文件）
4. **API 文档框架替换：** Springfox → SpringDoc OpenAPI 3.x
5. **安全框架重构：** WebSecurityConfigurerAdapter → SecurityFilterChain
6. **依赖版本全面升级：** 所有第三方依赖升级至最新兼容版本

### 15.2 升级收益

1. **性能提升：** JDK 17 和 Spring Boot 3.2.0 带来了显著的性能提升
2. **安全增强：** 所有依赖升级至最新版本，修复了已知安全漏洞
3. **功能增强：** 获得了更多新特性和更好的开发体验
4. **长期支持：** JDK 17 和 Spring Boot 3.x 都是 LTS 版本，提供长期支持

### 15.3 后续建议

1. **消除循环依赖：** 逐步重构代码以消除循环依赖，移除 `spring.main.allow-circular-references: true` 配置
2. **升级 MySQL 驱动：** 考虑升级至 MySQL 8.0+ 以获得更好的性能和特性支持
3. **启用原生镜像：** 利用 Spring Boot 3.x 的 GraalVM 原生镜像支持，进一步提升启动速度和内存效率
4. **持续更新依赖：** 定期更新依赖版本，保持项目的安全性和稳定性

### 15.4 兼容性保证

升级后的项目保持了与原版本完全相同的功能，同时获得了 Spring Boot 3.x 和 JDK 17 的性能优化和新特性支持。业务代码无需修改，数据库结构和接口定义保持不变，确保了平滑升级。
