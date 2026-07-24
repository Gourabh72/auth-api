package com.auth.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import java.lang.module.Configuration;
import java.util.Arrays;

@SpringBootApplication
public class AuthApiApplication {

    public static void main(String[] args)
    {
        ConfigurableApplicationContext context=SpringApplication.run(AuthApiApplication.class, args);
//        System.out.println("Application started with context: " + context.getClass().getName());
//        System.out.println("Expose AutowireCapableBeanFactory functionality for this context: " + context.getAutowireCapableBeanFactory().getClass().getName());
//        System.out.println("Is this context a WebApplicationContext? " + (context instanceof WebApplicationContext));
//        System.out.println("Return a friendly name for this context.: " + context.getDisplayName());
//        System.out.println("Return the unique id of this application context.: " + context.getId());
//        System.out.println("Return the parent context, or null if there is no parent and this is the root of the context hierarchy.: " + context.getParent());
//        System.out.println("Return the startup date of this context.: " + context.getStartupDate());
        /*for (String beanName : context.getBeanDefinitionNames()) {
            System.out.println("Bean name: " + beanName);
        }*/
//        System.out.println("bean class:"+context.CLASSPATH_URL_PREFIX);
//        System.out.println("bean resource:"+context.getResource("classpath:application.properties"));
//        System.out.println("factory bean suffix:"+context.FACTORY_BEAN_PREFIX_CHAR);
    }
}
