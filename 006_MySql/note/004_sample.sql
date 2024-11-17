 CREATE TABLE `employees` (
 `id` INT(11) NOT NULL AUTO_INCREMENT,
`name` VARCHAR(24) NOT NULL DEFAULT '' COMMENT '姓名',
 `age` INT(11) NOT NULL DEFAULT '0' COMMENT '年龄',
 `position` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '职位',
 `hire_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入职时间',
 PRIMARY KEY (`id`),
 KEY `idx_name_age_position` (`name`,`age`,`position`) USING BTREE
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='员工记录表';

INSERT INTO employees(NAME,age,POSITION,hire_time) VALUES('LiLei',22,'manager',NOW());
INSERT INTO employees(NAME,age,POSITION,hire_time) VALUES('HanMeimei', 23,'dev',NOW());
INSERT INTO employees(NAME,age,POSITION,hire_time) VALUES('Lucy',23,'dev',NOW());


DROP PROCEDURE IF EXISTS insert_emp;
DELIMITER ;;
 CREATE PROCEDURE insert_emp()
BEGIN
 DECLARE i INT;

SET i=1;
WHILE(i<=100000)DO
INSERT INTO employees(NAME,age,POSITION) VALUES(CONCAT('zhuge',i),i,'dev');
SET i=i+1;
END WHILE;
END;;
DELIMITER ;
CALL insert_emp()