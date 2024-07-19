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


| 页面                          | 地址                    |
| ----------------------------- | ----------------------- |
| 主页                          | /index                  |
| hw                            | /test/helloworld        |
| 文件上传                      | /test/fileupload        |
| 基础excel导入                 | /test/excelimport       |
| 进阶excel导入                 | /test/excelimportTest   |
| 高级excel导入                 | /test/excelimportTestCN |
| excel导出                     | /export                 |
| 进阶excel导出                 | /exportLIMIT            |
| 登录                          | /login                  |
| 员工名单展示                  | /employees              |
| 新增员工(注册)                | /showNewEmployeeForm    |
| 员工excel导入(已无法正常使用) | /emp_excelimport        |
