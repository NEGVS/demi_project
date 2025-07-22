package com.project.demo.common;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class JSONAuthentication {
    public static final List<String> errorList = new ArrayList<>();

    /**
     * 输出JSON
     *
     * @param response 响应头
     * @param obj      响应喜喜
     * @throws IOException      异常信息
     */
    protected void WriteJSON(HttpServletResponse response,
                             Object obj) throws IOException {
        //这里很重要，否则页面获取不到正常的JSON数据集
        response.setContentType("application/json;charset=UTF-8");

        //跨域设置
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Method", "POST,GET");
        //输出JSON
        PrintWriter out = response.getWriter();
        out.write(JSON.toJSONString(obj));
        out.flush();
        out.close();
    }
}
