## 项目描述

- JDK版本：17
- 框架版本（SpringBoot）：2.6.13

## 包含功能：

- 邮件服务
- 日志记录
- 验证码服务
- 各种工具类
- 全局异常拦截

## 使用方法

将项目拉取到本地--》mvn clean install 安装到本地并在项目中添加依赖

```pom.xml
<dependency>
    <groupId>cn.shuniverse</groupId>
    <artifactId>shuniverse-base</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## 部分配置以及依赖
### 邮件配置
```yaml
spring:
  # 邮件配置
  mail:
    host: smtp.163.com
    username: username
    password: password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```
### 数据库配置
```pom.xml
<!--=========数据库相关依赖-开始=========-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.2.20</version>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
<!--=========数据库相关依赖-结束=========-->
```

```yaml
spring:
  datasource:
    url: jdbc:mysql://10.10.10.177:3306/life-manager?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSl=false
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password:
    type: com.alibaba.druid.pool.DruidDataSource # 配置阿里的连接池
    # Druid 【监控】相关的全局配置
    druid:
      initial-size: 5
      minIdle: 10
      max-active: 20
      # 配置获取连接等待超时的时间(单位：毫秒)
      max-wait: 60000
      filters: stat,wall,log4j2
      web-stat-filter:
        enabled: true
      stat-view-servlet:
        enabled: true
        # 设置白名单，不填则允许所有访问
        allow:
        url-pattern: /druid/*
        # 控制台管理用户名和密码
        login-username: aikuiba
        login-password: 
      filter:
        stat:
          enabled: true
          # 慢 SQL 记录
          log-slow-sql: true
          slow-sql-millis: 2000
          merge-sql: true
        wall:
          config:
            multi-statement-allow: true
```
