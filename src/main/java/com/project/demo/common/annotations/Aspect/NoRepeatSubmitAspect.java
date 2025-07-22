package com.project.demo.common.annotations.Aspect;

import com.project.demo.common.annotations.RepeatSubmit;
import com.project.demo.common.util.IpAddressUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Aspect
public class NoRepeatSubmitAspect {


    // 并发包
    private final static ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.project.demo.common.annotations.RepeatSubmit)")
    public void preventDuplication() {
    }

    @Around("preventDuplication()")
    public Object around(ProceedingJoinPoint joinPoint) throws Exception {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            String key = request.getRequestURI();
            // 获取执行方法
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            log.info("method : " + method.getName());

            //获取防重复提交注解
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            String url = IpAddressUtil.getClientIp(request) + request.getRequestURI();
            log.info("url : " + url);
            // 是否存在
            long value = System.currentTimeMillis();
            if (map.containsKey(url)) {
                // 存在 校验是否过期
                if ((value - map.get(url)) > annotation.expireSeconds() * 1000) {
                    map.put(url, value);
                } else if ((value - map.get(url)) < annotation.expireSeconds() * 1000) {
                    throw new RuntimeException("重复提交");
                }
            } else {
                map.put(url, value);
            }
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                //确保方法执行异常实时释放限时标记(异常后置通知)
                throw new RuntimeException(throwable);
            }
        } else {
            throw new RuntimeException("切面异常");
        }
    }
}
