package com.uec.demo.util;

import org.mapstruct.Mapping;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 获取spring容器
 * 当一个类实现了这个接口ApplicationContextAware之后，这个类就可以方便获得ApplicationContext中的所有bean。
 * 换句话说，这个类可以直接获取spring配置文件中所有有引用到的bean对象
 * 前提条件需作为一个普通的bean在spring的配置文件中进行注册
 */
@Component
public class BeanFetcher implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanFetcher.applicationContext = applicationContext;
    }


    public <T> T getBean(Class<T> type) {
        try {
            return applicationContext.getBean(type);
        } catch (NoUniqueBeanDefinitionException e) {   //出现多个，选第一个
            String beanName = applicationContext.getBeanNamesForType(type)[0];
            return applicationContext.getBean(beanName, type);
        }
    }

    public <T> T getBean(String beanName, Class<T> type) {
        return applicationContext.getBean(beanName, type);
    }

    public <T> T getBeanOfType(Class<T> type){
        Map<String, T> result = applicationContext.getBeansOfType(type);
        String[] names = applicationContext.getBeanNamesForType(type);
        return result.get(names[0]);
    }
}

