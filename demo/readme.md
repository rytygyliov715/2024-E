员工表Employee结构
| 字段名   | 中文含义 | 类型        | 约束                                   | 备注 |
| -------- | -------- | ----------- | -------------------------------------- | ---- |
| id       | 员工编号 | int         | primary key                            |      |
| name     | 姓名     | varchar(20) |                                        |      |
| male     | 性别     | varchar(2)  | 约束为“男”和“女”                       | 中文 |
| posts    | 职位     | varchar(10) | 约束为“实习生”、“职工”、“组长”、“经理” | 中文 |
| on_date  | 入职日期 | date        |                                        |      |
| age      | 年龄     | int         |                                        |      |
| password | 密码     | varchar(20) | NOT NULL                               |      |

excel列名映射表excel_column_mapping
| id  | table_name | excel_header | db_colum |
| --- | ---------- | ------------ | -------- |
| 10  | Employee   | 员工编号     | id       |
| 11  | Employee   | 姓名         | name     |
| 12  | Employee   | 性别         | male     |
| 13  | Employee   | 入职日期     | on_date  |
| 14  | Employee   | 职位         | posts    |


1. 使用shiro框架实现员工登录，登录进入后展示当前登录用户的信息
2. 登录页增加验证码校验

Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.apache.shiro.UnavailableSecurityManagerException: No SecurityManager accessible to the calling code, either bound to the org.apache.shiro.util.ThreadContext or as a vm static singleton.  This is an invalid application configuration.] with root cause

Servlet.service（） for servlet [dispatcherServlet] 在路径 [] 的上下文中抛出异常 [请求处理失败;嵌套异常是 org.apache.shiro.UnavailableSecurityManagerException：没有 SecurityManager 可供调用代码访问，绑定到 org.apache.shiro.util.ThreadContext 或作为 vm 静态单例。 这是无效的应用程序配置。有根本原因

编写dashboard.html页面，展示员工信息，包括员工编号、姓名、性别、职位、入职日期、年龄

2024-07-18 09:03:25.134 ERROR 41796 --- [nio-8081-exec-5] org.thymeleaf.TemplateEngine             : [THYMELEAF][http-nio-8081-exec-5] Exception processing template "dashboard": Error resolving template [dashboard], template might not exist or might not be accessible by any of the configured Template Resolvers


2024-07-18 09:03:25.135 ERROR 41796 --- [nio-8081-exec-5] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.thymeleaf.exceptions.TemplateInputException: Error resolving template [dashboard], template might not exist or might not be accessible by any of the configured Template Resolvers] with root cause
