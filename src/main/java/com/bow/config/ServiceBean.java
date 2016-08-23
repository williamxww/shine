package com.bow.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by vv on 2016/8/19.
 */
public class ServiceBean<T> extends ServiceConfig implements InitializingBean, DisposableBean  {
    @Override
    public void destroy() throws Exception {
        //注销时取消注册
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //做配置校验
        System.out.println("begin");
    }
}
