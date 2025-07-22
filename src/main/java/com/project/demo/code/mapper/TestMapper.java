package com.project.demo.code.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface TestMapper {


    /**
     * 查询表字段
     *
     * @param tableName 表名称
     * @return 结果
     */
    @Select("  select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra,IS_NULLABLE isNullAble\n" +
            "        from information_schema.columns\n" +
            "        where table_name = #{tableName}\n" +
            "          and table_schema = (select database())\n" +
            "        order by ordinal_position")
    List<Map<String, String>> queryColumns(@Param("tableName") String tableName);
}
