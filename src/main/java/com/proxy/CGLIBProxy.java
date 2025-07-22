package com.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLIB 动态代理实现
 * 如果抛出异常  java.lang.reflect.InaccessibleObjectException-->Unable to make protected final java.lang.Class
 * 解决方法：
 * 1、添加 vm参数  --add-opens java.base/java.lang=ALL-UNNAMED
 * 2、切换jdk版本为 1.8
 */
public class CGLIBProxy {
    public static void main(String[] args) {
        // 创建对象
        HelloImpl hello = new HelloImpl();

        // 创建 CGLIB 的代理对象
        HelloImpl proxyHello = (HelloImpl) Enhancer.create(HelloImpl.class, new MyMethodInterceptor(hello));

        // 调用代理对象的方法
        proxyHello.sayHello("World");
    }
}

/**
 * CGLIB 的 MethodInterceptor 实现
 */
class MyMethodInterceptor implements MethodInterceptor {

    private final Object target;

    public MyMethodInterceptor(Object target) {
        this.target = target;
    }

    // 重写 intercept 方法来增加额外的逻辑
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 执行方法前的逻辑
        System.out.println("执行方法之前: " + method.getName());

        // 调用目标对象的方法
        Object result = method.invoke(target, args);

        // 执行方法后的逻辑
        System.out.println("执行方法之后: " + method.getName());

        return result;
    }
}
