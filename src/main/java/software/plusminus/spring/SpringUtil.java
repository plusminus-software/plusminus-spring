package software.plusminus.spring;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@SuppressWarnings("HideUtilityClassConstructor")
@Component
public class SpringUtil {

    private static ApplicationContext applicationContext;

    @SuppressWarnings({"java:S3010", "java:S1118"})
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public SpringUtil(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    public static <T, B extends T> Map<Class<?>, List<B>> groupBeansByGenericType(List<B> beans, Class<T> type) {
        return beans.stream()
                .collect(Collectors.groupingBy(bean -> resolveGenericType(bean, type)));
    }

    public static <T, B extends T> Map<Class<?>, B> beansToMapByGenericType(List<B> beans, Class<T> type) {
        return beans.stream()
                .collect(Collectors.toMap(bean -> resolveGenericType(bean, type), Function.identity()));
    }

    @Nullable
    public static <T, B extends T> Class<?> getGenericType(B bean, Class<T> type) {
        ResolvableType resolvableType = ResolvableType.forClass(bean.getClass())
                .as(type);
        return resolvableType.getGeneric(0).resolve();
    }

    public static <T, B> Class<T> resolveGenericType(B bean, Class<? super B> beanType) {
        Class<?> genericType = getGenericType(bean, beanType);
        if (genericType != null) {
            return (Class<T>) genericType;
        }

        genericType = SpringUtil.findBeanGenericType(bean, beanType);
        if (genericType == null) {
            throw new IllegalStateException("Cannot resolve generic type for '" + beanType + "' bean: ");
        }

        return (Class<T>) genericType;
    }

    public static boolean isRunningInSpring() {
        return SpringUtil.applicationContext != null;
    }

    private static <B> Class<?> findBeanGenericType(B bean, Class<? super B> beanType) {
        if (!SpringUtil.isRunningInSpring()) {
            throw new IllegalStateException("Cannot resolve generic type for '" + beanType + "' object: ");
        }

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
        return rt.getGeneric(0).resolve();
    }
}
