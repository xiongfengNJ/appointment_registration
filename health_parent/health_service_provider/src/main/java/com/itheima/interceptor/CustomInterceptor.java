package com.itheima.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import java.util.Properties;

/**
 * 自定义mybatis拦截器
 */
@Intercepts(@Signature(method = "query",
                type = Executor.class,
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}))
public class CustomInterceptor implements Interceptor {
    /**
     * 拦截时执行的逻辑
     * @param invocation
     * @return
     * @throws Throwable
     */
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("被拦截方法执行前的操作。。。");
        Object proceed = invocation.proceed();
        System.out.println("被拦截方法执行后的操作。。。");
        return proceed;
    }

    /**
     * 拦截器用于封装目标对象

     * 通过该方法我们可以返回目标对象本身，也可以返回一个它的代理
     * @param o
     * @return
     */
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    /**
     * 用于在 Mybatis 配置文件中指定一些属性
     * @param properties
     */
    public void setProperties(Properties properties) {
    }
}