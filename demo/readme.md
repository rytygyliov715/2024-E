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

1. 用户密码加盐加密，加密使用MD5算法，保证使用shiro登录时能正常登录
2. 前端页面登录传密码给后台时，选择java某个对称加密算法进行加密，后台拿到密文后进行解密后再登录

要在登录期间使用 MD5 算法实现密码加盐和加密以用于 Shiro，请按照下列步骤操作：


员工表Employee结构
| 字段名   | 中文含义 | 类型        | 约束                                   | 备注 |
| -------- | -------- | ----------- | -------------------------------------- | ---- |
| id       | 员工编号 | int         | primary key                            |      |
| name     | 姓名     | varchar(20) |                                        |      |
| male     | 性别     | varchar(2)  | 约束为“男”和“女”                       | 中文 |
| posts    | 职位     | varchar(10) | 约束为“实习生”、“职工”、“组长”、“经理” | 中文 |
| on_date  | 入职日期 | date        |                                        |      |
| age      | 年龄     | int         |                                        |      |
| password | 密码     | char(32)    | NOT NULL                               |      |
| salt     | 盐值     | char(32)    |                                        |      |
注册页面
```html

<body>
    <form th:action="@{/Register}" th:object="${employee}" method="post">
        <table>
            <tr>
                <td></td>
                <td>
                    <h1>注册</h1>
                </td>
            </tr>
            <tr>
                <td><label for="name">姓名:</label></td>
                <td><input type="text" id="name" th:field="*{name}" /></td>
            </tr>
            <tr>
                <td><label for="password">密码:</label></td>
                <td><input type="text" id="password" th:field="*{password}" /></td>
            <tr>
            <tr>
                <td><label for="male">性别:</label></td>
                <td>
                    <select name="male" id="male" th:field="*{male}">
                        <option value="男">男</option>
                        <option value="女">女</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td><label for="onDate">入职日期:</label></td>
                <td><input type="date" id="onDate" th:field="*{on_date}" /></td>
            </tr>
            <tr>
                <td><label for="age">年龄:</label></td>
                <td><input type="text" id="age" th:field="*{age}" /></td>
            </tr>
            <tr>
                <td><label for="posts">职位:</label></td>
                <td><select name="posts" id="posts" th:field="*{posts}">
                        <option value="实习生">实习生</option>
                        <option value="职工">职工</option>
                        <option value="组长">组长</option>
                        <option value="经理">经理</option>
                    </select></td>
            </tr>
            <tr>
                <td><label for="wages">工资:</label></td>
                <td><input type="text" id="wages" th:field="*{wages}" /></td>
            </tr>
            <tr>
                <td></td>
                <td><button type="submit">保存</button></td>
            </tr>
        </table>
    </form>
</body>
```
相关服务类
```java
    // 保存或更新员工
    public Employee saveOrUpdateEmployee(Employee employee) {

        return employeeRepository.save(employee);
    }

    
    // 注册用户并加密保存密码
    public void register(Employee employee) {
        String salt = PasswordUtil.generateSalt();
        String encryptedPassword = PasswordUtil.encryptPassword(employee.getPassword(), salt);
        employee.setSalt(salt);
        employee.setPassword(encryptedPassword);
        employeeRepository.save(employee);
    }

```
编写对