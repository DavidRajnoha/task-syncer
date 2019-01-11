package com.redhat.tasksyncer;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Filip Cap
 */
@Service
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static <T> T inject(T object) {
        if(object == null)
            return null;

        context.getAutowireCapableBeanFactory().autowireBean(object);
        try {
            object.getClass().getMethod("init").invoke(object);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {

        }
        return object;
    }
}
