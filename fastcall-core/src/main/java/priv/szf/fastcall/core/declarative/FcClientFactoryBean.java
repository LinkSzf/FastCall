package priv.szf.fastcall.core.declarative;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.FastCall;
import priv.szf.fastcall.core.declarative.annotation.FcClient;

import java.lang.reflect.Proxy;
import java.util.Objects;

public class FcClientFactoryBean implements FactoryBean<Object>, BeanFactoryAware, InitializingBean {

    private final Class<?> interfaceType;

    private ConfigurableListableBeanFactory beanFactory;

    private volatile Object singletonProxy;

    public FcClientFactoryBean(Class<?> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new FastCallException("BeanFactory must be ConfigurableListableBeanFactory");
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() {
        if (Objects.isNull(interfaceType)) {
            throw new FastCallException("FcClient interface type cannot be null");
        }
        if (!interfaceType.isInterface()) {
            throw new FastCallException("Type[{}] is not an interface", interfaceType.getName());
        }
        if (Objects.isNull(interfaceType.getAnnotation(FcClient.class))) {
            throw new FastCallException("Interface[{}] must be annotated with @FcClient", interfaceType.getName());
        }
    }

    @Override
    public Object getObject() {
        if (Objects.nonNull(singletonProxy)) {
            return singletonProxy;
        }
        synchronized (this) {
            if (Objects.nonNull(singletonProxy)) {
                return singletonProxy;
            }

            FastCall fastCall = beanFactory.getBean(FastCall.class);
            IFcSource source = beanFactory.getBean(IFcSource.class);

            FcClientInvocationHandler handler = new FcClientInvocationHandler(interfaceType, fastCall, source);
            singletonProxy = Proxy.newProxyInstance(
                    interfaceType.getClassLoader(),
                    new Class<?>[]{interfaceType},
                    handler
            );
            return singletonProxy;
        }
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
