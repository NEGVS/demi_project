create table if not exists demo_order
(
    order_id bigint auto_increment not null comment '订单id'
        primary key,
    user_id          bigint                                   not null comment '用户id',
    merchant_id      int                                      not null comment '商家id',
    total_amount     decimal(10, 2)                           not null comment '订单总金额',
    discount_amount  decimal(10, 2) default 0 comment '优惠金额',
    final_amount     decimal(10, 2)                           not null comment '实际支付金额',
    delivery_fee     decimal(10, 2) default 0 comment '配送费',
    status           tinyint                                  null comment '订单状态 0：待支付 1：已支付 2：订单超时取消 3：用户手动取消 4：订单已完成 5：订单退款中 6：订单退款完成',
    is_in_settlement tinyint        default 0 comment '是否已进入结算 （0：未结算 , 1：结算中 , 2：结算完成）',
    payment_status   tinyint        default 0                 not null comment '支付状态 0：待支付 1：已支付 2：已退款 3：订单结束',
    deleted          tinyint        default 1                 null comment '删除标志 1 有效，其他无效',
    version          int            default 0                 null comment '版本',
    create_date      datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    create_by        varchar(50)    default 'System'          null comment '创建人',
    update_date      datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by        varchar(50)    default 'System'          null comment '更新人'
)
    comment '订单表';

CREATE INDEX index_user_id ON demo_order (user_id);
CREATE INDEX index_merchant_id ON demo_order (merchant_id);
CREATE INDEX index_status ON demo_order (status);
CREATE INDEX index_payment_status ON demo_order (payment_status);

create table if not exists demo_order_item
(
    order_item_id   bigint auto_increment                    not null comment '订单明细id'
        primary key,
    order_id        bigint                                   not null comment '订单ID',
    product_id      int                                      not null comment '商品ID',
    quantity        int                                      not null comment '商品数量',
    unitPrice       decimal(10, 2)                           not null comment '商品单价',
    total_amount    decimal(10, 2)                           not null comment '当前商品的小计金额',
    discount_amount decimal(10, 2) default 0 comment '优惠金额',
    deleted         tinyint        default 1                 null comment '删除标志 1 有效，其他无效',
    version         int            default 0                 null comment '版本',
    create_date     datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    create_by       varchar(50)    default 'System'          null comment '创建人',
    update_date     datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by       varchar(50)    default 'System'          null comment '更新人'
)
    comment '订单明细表';

CREATE INDEX index_order_id ON demo_order_item (order_id);
CREATE INDEX index_product_id ON demo_order_item (product_id);

create table if not exists demo_payment_records
(
    payment_id bigint auto_increment not null comment '支付记录ID'
        primary key,
    order_id       bigint                                not null comment '对应的订单ID',
    payment_method int                                   not null comment '支付方式 0：微信 1：支付宝',
    payment_amount decimal(10, 2)                        not null comment '支付金额',
    status         tinyint     default 0 comment '支付状态 支付状态 0：待支付 1：已支付 2：已退款 3：订单结束',
    transaction_id varchar(50)                           not null comment '第三方支付平台的交易流水号',
    final_date     datetime    default null comment '支付完成时间',
    deleted        tinyint     default 1                 null comment '删除标志 1 有效，其他无效',
    version        int         default 0                 null comment '版本',
    create_date    datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    create_by      varchar(50) default 'System'          null comment '创建人',
    update_date    datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by      varchar(50) default 'System'          null comment '更新人'
)
    comment '支付记录表';
CREATE INDEX index_order_id ON demo_payment_records (order_id);
CREATE INDEX index_transaction_id ON demo_payment_records (transaction_id);

create table if not exists demo_settlements
(
    settlement_id bigint auto_increment not null comment '结算单ID'
        primary key,
    merchant_id         bigint                                not null comment '商家ID',
    total_order_count   int                                   not null comment '总订单数',
    total_amount        decimal(10, 2)                        not null comment '总收入金额',
    merchant_income     decimal(10, 2)                        not null comment '商家实际收入',
    platform_commission decimal(10, 2)                        not null comment '平台佣金',
    status              tinyint     default 0 comment '结算状态 0：待结算 1：已结算',
    final_date          datetime    default null comment '结算完成时间',
    deleted             tinyint     default 1                 null comment '删除标志 1 有效，其他无效',
    version             int         default 0                 null comment '版本',
    create_date         datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    create_by           varchar(50) default 'System'          null comment '创建人',
    update_date         datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by           varchar(50) default 'System'          null comment '更新人'
)
    comment '结算单表';
CREATE INDEX index_merchant_id ON demo_settlements (merchant_id);
CREATE INDEX index_status ON demo_settlements (status);


create table if not exists demo_settlement_detail
(
    detail_id bigint auto_increment not null comment '结算明细ID'
        primary key,
    settlement_id       bigint                                not null comment '结算单ID',
    order_id            bigint                                not null comment '订单ID',
    merchant_income     decimal(10, 2)                        not null comment '商家从该订单中获得的收入',
    platform_commission decimal(10, 2)                        not null comment '平台从该订单中收取的佣金',
    delivery_fee        decimal(10, 2)                        not null comment '配送员收入',
    deleted             tinyint     default 1                 null comment '删除标志 1 有效，其他无效',
    version             int         default 0                 null comment '版本',
    create_date         datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    create_by           varchar(50) default 'System'          null comment '创建人',
    update_date         datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by           varchar(50) default 'System'          null comment '更新人'
)
    comment '结算单明细表';

CREATE INDEX index_settlement_id ON demo_settlement_detail (settlement_id);
CREATE INDEX index_order_id ON demo_settlement_detail (order_id);

create table if not exists demo_user_addresses
(
    address_id bigint auto_increment not null comment '地址ID'
        primary key,
    user_id          bigint                                not null comment '用户ID',
    receiver_name    varchar(50)                           not null comment '收货人姓名',
    phone_number     varchar(11)                           not null comment '收货人手机号',
    province         varchar(50)                           not null comment '省份',
    city             varchar(50)                           not null comment '城市',
    district         varchar(50)                           not null comment '区/县',
    detailed_address varchar(255)                          not null comment '详细地址',
    postal_code      varchar(255)                          not null comment '邮政编码',
    is_default       tinyint     default 0                 null comment '是否为默认地址（1：是；0：否）',
    deleted          tinyint     default 1                 null comment '删除标志 1 有效，其他无效',
    version          int         default 0                 null comment '版本',
    create_date      datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    create_by        varchar(50) default 'System'          null comment '创建人',
    update_date      datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by        varchar(50) default 'System'          null comment '更新人'
)
    comment '用户收货地址表';
CREATE INDEX index_user_id ON demo_user_addresses (user_id);
CREATE INDEX index_province ON demo_user_addresses (province);
CREATE INDEX index_city ON demo_user_addresses (city);
CREATE INDEX index_district ON demo_user_addresses (district);

create table if not exists demo_cart_items
(
    cart_item_id bigint auto_increment not null comment '购物车明细ID'
        primary key,
    cart_id      bigint                                not null comment '购物车元数据信息ID',
    user_id      bigint                                not null comment '用户ID',
    product_id   bigint                                not null comment '商品ID',
    merchant_id  bigint                                not null comment '商家ID',
    quantity     int                                   not null comment '商品数量',
    unit_price   decimal(10, 2)                        not null comment '商品单价',
    total_price  decimal(10, 2)                        not null comment '商品总价 = 单价 x 数量',
    deleted      tinyint     default 1                 null comment '删除标志 1 有效，其他无效',
    version      int         default 0                 null comment '版本',
    create_date  datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    create_by    varchar(50) default 'System'          null comment '创建人',
    update_date  datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by    varchar(50) default 'System'          null comment '更新人'
)
    comment '购物车明细表';

CREATE INDEX index_user_id ON demo_cart_items (user_id);
CREATE INDEX index_merchant_id ON demo_cart_items (merchant_id);
CREATE INDEX index_product_id ON demo_cart_items (product_id);
CREATE INDEX index_cart_id ON demo_cart_items (cart_id);

create table if not exists demo_cart_metadata
(
    cart_id bigint auto_increment not null comment '购物车元数据信息ID'
        primary key,
    user_id     bigint                                not null comment '用户ID',
    coupon_id   bigint                                null comment '优惠券ID',
    merchant_id bigint                                not null comment '商家ID',
    notes       text comment '用户备注',
    deleted     tinyint     default 1                 null comment '删除标志 1 有效，其他无效',
    version     int         default 0                 null comment '版本',
    create_date datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    create_by   varchar(50) default 'System'          null comment '创建人',
    update_date datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by   varchar(50) default 'System'          null comment '更新人'
)
    comment '购物车元信息表';
CREATE INDEX index_user_id ON demo_cart_metadata (user_id);
CREATE INDEX index_merchant_id ON demo_cart_metadata (merchant_id);


create table if not exists demo_merchants
(
    merchant_id bigint auto_increment not null comment '商家ID'
        primary key,
    merchant_name    varchar(255)                          not null comment '商家名称',
    contact_name     varchar(50)                           null comment '商家联系人',
    contact_phone    varchar(20)                           null comment '商家联系电话',
    email            varchar(100)                          null comment '商家邮箱',
    address          varchar(255)                          null comment '商家地址',
    business_license varchar(255)                          null comment '营业执照基本信息（存储文件路径或编号）',
    user_id          bigint                                not null comment '用户ID(当前店铺所有者)',
    status           tinyint     default 0                 not null comment '店铺状态（0：未激活；1：正常；2：暂停；3：关闭）',
    deleted          tinyint     default 1                 null comment '删除标志 1 有效，其他无效',
    version          int         default 0                 null comment '版本',
    create_date      datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    create_by        varchar(50) default 'System'          null comment '创建人',
    update_date      datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by        varchar(50) default 'System'          null comment '更新人'
)
    comment '商家表';
CREATE INDEX index_merchant_name ON demo_merchants (merchant_name);

create table if not exists demo_products
(
    product_id bigint auto_increment not null comment '商品ID'
        primary key,
    merchant_id    bigint                                   not null comment '商家ID',
    category_id    bigint                                   not null comment '商品分类ID',
    product_name   varchar(255)                             not null comment '商品名称',
    price          decimal(10, 2)                           not null comment '商品单价',
    discount_price decimal(10, 2) default 0                 not null comment '折扣价（如果没有为0）',
    stock          int                                      not null comment '商品库存',
    product_image  varchar(100)                             null comment '商品主图（存储文件路径或URL）',
    description    text                                     null comment '商品详细描述',
    status         tinyint        default 0                 not null comment '商品状态（0：下架；1：上架；2：售罄）',
    deleted        tinyint        default 1                 null comment '删除标志 1 有效，其他无效',
    version        int            default 0                 null comment '版本',
    create_date    datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    create_by      varchar(50)    default 'System'          null comment '创建人',
    update_date    datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by      varchar(50)    default 'System'          null comment '更新人'
)
    comment '商品表';

CREATE INDEX index_merchant_id ON demo_products (merchant_id);
CREATE INDEX index_category_id ON demo_products (category_id);
CREATE FULLTEXT INDEX index_product_name ON demo_products (product_name);


create table if not exists demo_categories
(
    category_id bigint auto_increment not null comment '分类ID'
        primary key,
    parent_category_id bigint                                not null comment '父级分类ID , 顶级分类为 0',
    category_name      varchar(255)                          not null comment '分类名称',
    description        text                                  null comment '分类描述',
    deleted            tinyint     default 1                 null comment '删除标志 1 有效，其他无效',
    version            int         default 0                 null comment '版本',
    create_date        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    create_by          varchar(50) default 'System'          null comment '创建人',
    update_date        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by          varchar(50) default 'System'          null comment '更新人'
)
    comment '商品分类表';

CREATE INDEX index_category_name ON demo_categories (category_name);

create table if not exists demo_merchant_schedules
(
    schedule_id bigint auto_increment not null comment '营业时间表ID'
        primary key,
    merchant_id bigint                                not null comment '商家ID',
    day_of_week TINYINT(1)                            not null comment '星期几（0：周日，1：周一，... 6：周六）',
    open_time   datetime                              null comment '当天的营业开始时间',
    close_time  datetime                              null comment '当天的营业结束时间',
    is_holiday  TINYINT                               not null comment '是否为节假日（0：否，1：是）',
    deleted     tinyint     default 1                 null comment '删除标志 1 有效，其他无效',
    version     int         default 0                 null comment '版本',
    create_date datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    create_by   varchar(50) default 'System'          null comment '创建人',
    update_date datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by   varchar(50) default 'System'          null comment '更新人'
)
    comment '商家营业时间表';

CREATE INDEX index_merchant_id ON demo_merchant_schedules (merchant_id);

