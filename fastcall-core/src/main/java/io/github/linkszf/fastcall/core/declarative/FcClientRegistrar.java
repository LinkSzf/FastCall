package io.github.linkszf.fastcall.core.declarative;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import io.github.linkszf.fastcall.common.exception.FastCallException;
import io.github.linkszf.fastcall.core.declarative.annotation.EnableFastCallClients;
import io.github.linkszf.fastcall.core.declarative.annotation.FcClient;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.beans.Introspector;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FcClientRegistrar implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> basePackages = resolveBasePackages(importingClassMetadata);
        ClassPathScanningCandidateComponentProvider scanner = buildScanner();
        scanner.addIncludeFilter(new AnnotationTypeFilter(FcClient.class));
        Set<String> scannedClassNames = new LinkedHashSet<>();

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                String className = candidate.getBeanClassName();
                if (StrUtil.isBlank(className) || !scannedClassNames.add(className)) {
                    continue;
                }
                registerFcClient(candidate, registry);
            }
        }
    }

    private void registerFcClient(BeanDefinition candidate, BeanDefinitionRegistry registry) {
        String className = candidate.getBeanClassName();
        if (StrUtil.isBlank(className)) {
            return;
        }

        Class<?> interfaceType;
        try {
            ClassLoader classLoader = (resourceLoader == null) ? ClassUtils.getDefaultClassLoader() : resourceLoader.getClassLoader();
            interfaceType = ClassUtils.forName(className, classLoader);
        } catch (ClassNotFoundException e) {
            throw new FastCallException(e, "Failed to load FcClient interface class[{}]", className);
        }

        if (!interfaceType.isInterface()) {
            throw new FastCallException("Type[{}] annotated with @FcClient must be an interface", className);
        }
        if (isInterfaceTypeRegistered(registry, interfaceType)) {
            return;
        }

        FcClient fcClient = interfaceType.getAnnotation(FcClient.class);
        String beanName = StrUtil.isNotBlank(fcClient.value())
                ? fcClient.value()
                : Introspector.decapitalize(interfaceType.getSimpleName());
        if (registry.containsBeanDefinition(beanName)) {
            beanName = interfaceType.getName();
        }
        if (registry.containsBeanDefinition(beanName)) {
            throw new FastCallException(
                    "Bean name[{}] already exists while registering @FcClient interface[{}], please set unique @FcClient.value",
                    beanName, interfaceType.getName()
            );
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FcClientFactoryBean.class);
        builder.addConstructorArgValue(interfaceType);
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        // Explicitly declare the FactoryBean product type to help Spring type inference and IDE recognition
        builder.getRawBeanDefinition().setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, interfaceType);
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    private boolean isInterfaceTypeRegistered(BeanDefinitionRegistry registry, Class<?> interfaceType) {
        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            Object objectTypeAttr = beanDefinition.getAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE);
            if (Objects.equals(objectTypeAttr, interfaceType)) {
                return true;
            }
        }
        return false;
    }

    private ClassPathScanningCandidateComponentProvider buildScanner() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false, environment) {
            @Override
            protected boolean isCandidateComponent(org.springframework.beans.factory.annotation.AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent();
            }
        };
        scanner.setResourceLoader(resourceLoader);
        return scanner;
    }

    private Set<String> resolveBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attrs = importingClassMetadata.getAnnotationAttributes(EnableFastCallClients.class.getName());
        Set<String> basePackages = new LinkedHashSet<>();
        if (attrs == null) {
            return basePackages;
        }

        String[] packageNames = (String[]) attrs.get("basePackages");
        if (packageNames != null) {
            for (String packageName : packageNames) {
                if (StrUtil.isNotBlank(packageName)) {
                    basePackages.add(packageName);
                }
            }
        }

        Class<?>[] packageClasses = (Class<?>[]) attrs.get("basePackageClasses");
        if (packageClasses != null) {
            for (Class<?> clazz : packageClasses) {
                basePackages.add(ClassUtils.getPackageName(clazz));
            }
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
