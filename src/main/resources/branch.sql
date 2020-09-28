CREATE TABLE `branch`
(
    `id`             int(20)      NOT NULL AUTO_INCREMENT,
    `branch_no`      varchar(32)  NOT NULL,
    `branch_name`    varchar(256) NOT NULL,
    `main_branch_id` int(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;