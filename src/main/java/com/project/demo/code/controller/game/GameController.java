package com.project.demo.code.controller.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.common.util.IpUtil;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/")
@Slf4j
public class GameController {

    @GetMapping("shuqi")
    public String shuqi() {
        return "shuqi";
    }

    @GetMapping("404")
    public String test404() {
        return "404";
    }

    @GetMapping("dive")
    public String dive() {
        return "dive";
    }

    @GetMapping("vx")
    public String vx() {
        return "vx";
    }

    @GetMapping("active")
    public String active() {
        return "active";
    }

    @GetMapping("snake")
    public String snake() {
        return "snake";
    }

    @GetMapping("image")
    public String image() {
        return "image";
    }
    @GetMapping("yanhua")
    public String yanhua() {
        return "yanhua";
    }
    @GetMapping("chickenSoup")
    public String chickenSoup() {
        return "chickenSoup";
    }

    @GetMapping("corn")
    public String charmAndCoin() {
        return "corn";
    }

    @GetMapping("activeCount")
    public String activeCount() {
        return "activeCount";
    }
    @GetMapping("bookMonth")
    public String bookMonth() {
        return "bookMonth";
    }

    @GetMapping("test")
    public String test() {
        return "AllWxRobot";
    }

    private static String getGeoLocation(String ip) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://ipinfo.io/widget/demo/" + ip; // 使用 ip-api.com 的免费 API

        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("Love")
    public String Love(HttpServletRequest request) {
        try {
            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
            String uri = request.getRequestURI();
            String method = request.getMethod();
            String os = userAgent.getOperatingSystem().getName();
            String browser = userAgent.getBrowser().getName();
            String deviceType = userAgent.getOperatingSystem().getDeviceType().getName();
            String osVersion = userAgent.getOperatingSystem().getGroup().getName();
            int remotePort = request.getRemotePort();
            String realIp = request.getHeader("X-Real-IP");
            long startTime = System.currentTimeMillis();

            // 调用外部API获取地理位置信息

            long duration = System.currentTimeMillis() - startTime;

            log.info("请求地址IP：[" + IpUtil.getIpAddr(request) + "]; 请求方式：[" + method +
                    "]; 请求路径：" + uri + "]; 客户端浏览器：" + browser +
                    " (版本：" + "); 客户端操作系统：" + os +
                    " (版本：" + osVersion + "); 设备类型：" + deviceType +
                    "; 远程端口：" + remotePort+
                    "; X-Real-IP：" + realIp + "; 请求持续时间：" + duration +
                    " ms;");

            // 调用外部API获取地理位置信息
            String geoInfo = getGeoLocation(IpUtil.getIpAddr(request));
            log.info("地理位置信息：" + geoInfo);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(geoInfo);
            JsonNode data = jsonNode.get("data");
            String city = data.get("city").asText();
            String region = data.get("region").asText();
            String loc = data.get("loc").asText();
            String postal = data.get("postal").asText();

            log.info("城市：" + city);
            log.info("城市：" + region);
            log.info("邮编：" + postal);
            // 其他用户信息处理...
            log.info("经纬度：" + loc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "index111";
    }

}
