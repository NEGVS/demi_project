package com.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk动态代码
 */
public class JDKProxy {
    public static void main(String[] args) throws Throwable {
        // 创建目标对象
        Hello hello = new HelloImpl();

        // 创建 InvocationHandler 实现类
        MyInvocationHandler handler = new MyInvocationHandler(hello);
        // 手动触发代理方法
        Object[] args1 = new Object[]{"World111"};

        handler.invoke(hello, hello.getClass().getMethods()[0], args1);

        // 使用 Proxy 创建代理对象
        Hello proxyHello = (Hello) Proxy.newProxyInstance(
                hello.getClass().getClassLoader(),       // 类加载器
                hello.getClass().getInterfaces(),        // 目标接口
                handler                              // InvocationHandler
        );

        // 调用代理对象的方法（自动触发代理方法）
        proxyHello.sayHello("World");
    }
}

/**
 * 1、定义接口
 */
interface Hello {
    void sayHello(String sayStr);
}

/**
 * 2、实现接口
 */
class HelloImpl implements Hello {
    @Override
    public void sayHello(String sayStr) {
        System.out.println("hello" + sayStr);
    }
}

/**
 * 3、创建 InvocationHandler 接口的实现类
 */
class MyInvocationHandler implements InvocationHandler {

    private final Object target;

    // 构造函数，接收目标对象
    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    // 通过反射执行目标方法并增强
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("执行方法之前: " + method.getName());

        // 调用目标对象的方法
        Object result = method.invoke(target, args);

        System.out.println("执行方法之后: " + method.getName());
        return result;
    }
}