package software.plusminus.spring;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public <T, B> Class<T> getGenericType(B bean, Class<? super B> beanType) {
        ConfigurableListableBeanFactory beanFactory =
                (ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        Map<String, ? super B> candidates = beanFactory.getBeansOfType(beanType);
        String beanName = null;

        for (Map.Entry<String, ? super B> candidate : candidates.entrySet()) {
            if (candidate.getValue() == bean) {
                beanName = candidate.getKey();
                break;
            }
        }

        if (beanName == null) {
            throw new IllegalStateException("Cannot determine bean name for '" + beanType + "' bean: ");
        }

        BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
        ResolvableType rt = bd.getResolvableType().as(beanType);
        Class<?> genericType = rt.getGeneric(0).resolve();

        if (genericType == null) {
            throw new IllegalStateException("Cannot resolve generic type for '" + beanType + "' bean: ");
        }

        return (Class<T>) genericType;
    }

    @SuppressWarnings("java:S2696")
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    public static boolean isRunningInSpring() {
        return SpringUtil.applicationContext != null;
    }
}
