-- 使用mysql数据库

-- 创建数据库，名字CTJ
CREATE DATABASE IF NOT EXISTS CTJ DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

CREATE TABLE test (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20),
    age INT,
    password VARCHAR(20)
);

CREATE TABLE test2 (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20),
    password VARCHAR(20),
    birthday DATE,
    email VARCHAR(20),
    phone VARCHAR(20)
);

--删除test2
DROP TABLE test2;

--中文对应表
CREATE TABLE excel_column_mapping (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    excel_header VARCHAR(255) NOT NULL,
    db_column VARCHAR(255) NOT NULL
);

--删除
DROP TABLE excel_column_mapping;

--创建员工表表
CREATE TABLE tb_EI (
    id INT(10) PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    male VARCHAR(2) CHECK (male IN ('男', '女')),
    OnDate DATE,
    posts VARCHAR(10) CHECK (
        posts IN ('实习生', '职工', '组长', '经理')
    )
);

DELIMITER / /

CREATE TRIGGER before_insert_tb_EI
BEFORE INSERT ON tb_EI
FOR EACH ROW
BEGIN
    IF NEW.OnDate IS NULL THEN
        SET NEW.OnDate = CURRENT_DATE;
    END IF;
END

DELIMITER;
--删除员工表
DROP TABLE tb_EI;