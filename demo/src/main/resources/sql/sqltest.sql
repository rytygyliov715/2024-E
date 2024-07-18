-- Active: 1717721004005@@127.0.0.1@3306@ctj
-- 给表
ALTER Table employee ADD COLUMN age INT;

ALTER Table employee ADD COLUMN wages INT;

SELECT * FROM employee LIMIT 10, 10;
-- 从第10行开始，取10行

ALTER Table employee ADD COLUMN password VARCHAR(20) NOT NULL;

--将表employee中的password列的数据全部设置为888888
UPDATE employee SET password = '888888';

INSERT INTO
    excel_column_mapping (
        table_name,
        excel_header,
        db_column
    )
VALUES ('employee', '密码', 'password');