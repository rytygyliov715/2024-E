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

```java
// 登录验证
    public Employee login(String id, String password) {// 接受id和password参数
        Employee employee = employeeRepository.findById(Integer.parseInt(id)).orElse(null);// 根据id查找员工
        if (employee == null) {
            return null;
        }
        String encryptedPassword = PasswordUtil.encryptPassword(password, employee.getSalt());// 加密输入的密码
        if (encryptedPassword.equals(employee.getPassword())) {// 比较加密后的密码
            return employee;
        }
        return null;
    }
```
这个方法在EmployeeService类中，在EmployeeService删除这个方法，在shiro的LoginRealm类实现同样的功能