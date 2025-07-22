# demo_project 创建数据库
CREATE DATABASE `demo_project` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION = 'N' */

CREATE TABLE IF NOT EXISTS `trading_data`
(
    `id`             int          NOT NULL AUTO_INCREMENT COMMENT '自增主键，用于唯一标识每一行数据',
    `seat`           varchar(255) NOT NULL COMMENT '席位信息，类型为字符串',
    `long_position`  int          NOT NULL COMMENT '多单数量，允许负数',
    `change_num`     int          NOT NULL COMMENT '增减量，允许负数',
    `net_long_short` int          NOT NULL COMMENT '净多空数据，允许负数',
    `commodity`      varchar(255) NOT NULL COMMENT '品种名，类型为字符串',
    `date`           date         NOT NULL COMMENT '日期，类型为日期格式（YYYY-MM-DD）',
    `type`           varchar(255) NOT NULL COMMENT '类型信息，类型为字符串',
    `createDate`     datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='期货数据表';


CREATE TABLE IF NOT EXISTS `chapter_answers`
(
    `ID`           int          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `topicId`      varchar(255) NOT NULL COMMENT '章节ID',
    `indexChapter` int               DEFAULT NULL COMMENT '第几题的da''a',
    `courseId`     varchar(255) NOT NULL COMMENT '课程ID',
    `topicTitle`   varchar(255) NOT NULL COMMENT '章节名称',
    `answer`       varchar(255) NOT NULL COMMENT '答案',
    `created_at`   timestamp    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   timestamp    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='章节答案表';

CREATE TABLE IF NOT EXISTS `demo_love_talk`
(
    `id`      bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content` varchar(255) NOT NULL COMMENT '内容',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='土味情话表';

CREATE TABLE IF NOT EXISTS `demo_menu`
(
    `menu_id`     bigint      NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`   varchar(50) NOT NULL COMMENT '菜单名称',
    `parent_id`   bigint               DEFAULT '0' COMMENT '父菜单ID',
    `order_num`   int                  DEFAULT '0' COMMENT '显示顺序',
    `path`        varchar(200)         DEFAULT '' COMMENT '路由地址',
    `component`   varchar(255)         DEFAULT NULL COMMENT '组件路径',
    `query`       varchar(255)         DEFAULT NULL COMMENT '路由参数',
    `route_name`  varchar(50)          DEFAULT '' COMMENT '路由名称',
    `is_frame`    int                  DEFAULT '2' COMMENT '是否为外链（1是 2否）',
    `is_cache`    int                  DEFAULT '2' COMMENT '是否缓存（1缓存 2不缓存）',
    `menu_type`   char(1) COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `visible`     char(1)              DEFAULT '1' COMMENT '菜单状态（1显示 2隐藏）',
    `status`      char(1)              DEFAULT '1' COMMENT '菜单状态（1正常 2停用）',
    `deleted`     int         NOT NULL DEFAULT '1' COMMENT '删除标志（1代表存在 2代表删除）',
    `version`     int                  DEFAULT '0' COMMENT '版本号',
    `perms`       varchar(100)         DEFAULT NULL COMMENT '权限标识',
    `icon`        varchar(100)         DEFAULT '#' COMMENT '菜单图标',
    `create_by`   varchar(64)          DEFAULT '' COMMENT '创建者',
    `create_date` datetime             DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64)          DEFAULT '' COMMENT '更新者',
    `update_date` datetime             DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`menu_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='菜单权限表';

CREATE TABLE IF NOT EXISTS `demo_role`
(
    `role_id`     bigint       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   varchar(30)  NOT NULL COMMENT '角色名称',
    `role_key`    varchar(100) NOT NULL COMMENT '角色权限字符串',
    `role_sort`   int          NOT NULL COMMENT '显示顺序',
    `status`      char(1)      NOT NULL COMMENT '角色状态（1正常 2停用）',
    `deleted`     char(1)     DEFAULT '1' COMMENT '删除标志（1代表存在 2代表删除）',
    `version`     int         DEFAULT '0' COMMENT '版本号',
    `create_by`   varchar(64) DEFAULT '' COMMENT '创建者',
    `create_date` datetime    DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) DEFAULT '' COMMENT '更新者',
    `update_date` datetime    DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='角色信息表';

CREATE TABLE IF NOT EXISTS `demo_role_menu`
(
    `role_id` bigint NOT NULL DEFAULT '0' COMMENT '角色ID',
    `menu_id` bigint NOT NULL DEFAULT '0' COMMENT '菜单ID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='角色菜单表';

CREATE TABLE IF NOT EXISTS `demo_user`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`         varchar(50)  NOT NULL COMMENT '用户名',
    `password`         varchar(100) NOT NULL COMMENT '密码',
    `nickName`         varchar(50)  DEFAULT NULL COMMENT '用户昵称',
    `third_party_id`   varchar(100) DEFAULT NULL COMMENT '第三方程序ID',
    `avatar`           varchar(255) DEFAULT NULL COMMENT '用户头像',
    `avatar_thumbnail` varchar(255) DEFAULT NULL COMMENT '用户头像缩略图',
    `gender`           tinyint(1)   DEFAULT NULL COMMENT '性别',
    `user_type`        varchar(50)  DEFAULT '1' COMMENT '用户类型',
    `last_login_time`  datetime     DEFAULT NULL COMMENT '最后登录时间',
    `registration_ip`  varchar(45)  DEFAULT NULL COMMENT '注册IP地址',
    `deleted`          tinyint      DEFAULT '1' COMMENT '删除标志 1 有效，其他无效',
    `version`          int          DEFAULT '0' COMMENT '版本',
    `create_date`      datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`        varchar(50)  DEFAULT 'System' COMMENT '创建人',
    `update_date`      datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`        varchar(50)  DEFAULT 'System' COMMENT '更新人',
    `createUser`       varchar(100) DEFAULT NULL,
    `modifyUser`       varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `userName` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户表';

INSERT INTO demo_user (username, password, nickName, third_party_id, avatar, avatar_thumbnail, gender, user_type,
                       last_login_time, registration_ip, deleted, version, create_date, create_by, update_date,
                       update_by, createUser, modifyUser)
VALUES ('admin', '$2a$10$Sdb66z6J.6RMdxVebR5aKOA4q/mfVlS28RNbKr2M97pXmCO0b.YF.', 'admin', null, '', '', 1, '1', null,
        '127.0.0.1', 1, 0, '2024-12-26 16:59:48', 'System', '2024-12-26 16:59:48', 'System', 'System', 'System');
INSERT INTO demo_user (username, password, nickName, third_party_id, avatar, avatar_thumbnail, gender, user_type,
                       last_login_time, registration_ip, deleted, version, create_date, create_by, update_date,
                       update_by, createUser, modifyUser)
VALUES ('hylogan', '$2a$10$g7tJTm308Ab8VlKGboZzluSt9TmkP4ISNyoYLD.8vmBhTut9.mNGu', 'hyloganNickName0001', null, '12',
        '333', 1, null, null, '127.0.0.1', 1, 1, '2024-12-26 16:59:50', 'System', '2024-12-26 16:59:50', 'me', 'System',
        'System');


CREATE TABLE IF NOT EXISTS `demo_user_role`
(
    `user_id` bigint NOT NULL DEFAULT '0' COMMENT '用户ID',
    `role_id` bigint NOT NULL DEFAULT '0' COMMENT '角色ID'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='用户角色表';