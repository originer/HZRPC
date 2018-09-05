package hzr.common.proxy;

import hzr.common.transport.client.Client;
import hzr.common.transport.client.ClientImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK代理
 * @author Zz
 **/
public class JDKProxy implements RPCProxy {
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;

    static {
        try {
            hashCodeMethod = Object.class.getMethod("hashCode");
            equalsMethod = Object.class.getMethod("equals", Object.class);
            toStringMethod = Object.class.getMethod("toString");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    /**
     * 对于hashCode,euqals,toString方法,默认采用Object自带的方法
     * @param client
     * @param serviceInterface
     * @param <T>
     * @return
     */
    @Override
    public <T> T proxyInterface(final Client client, final Class<T> serviceInterface) {
        Object proxyInstance = Proxy.newProxyInstance(ClientImpl.class.getClassLoader(),
                new Class[]{serviceInterface}, (proxy, method, args) -> {
                    if (hashCodeMethod.equals(method)) {
                        return proxyHashCode(proxy);
                    }
                    if (equalsMethod.equals(method)) {
                        return proxyEquals(proxy, args[0]);
                    }
                    if (toStringMethod.equals(method)) {
                        return proxyToString(proxy);
                    }
                    return client.invokeMethod(serviceInterface, method, args).getResponse();
                });
        return (T) proxyInstance;
    }

    private int proxyHashCode(Object proxy) {
        return System.identityHashCode(proxy);
    }

    private boolean proxyEquals(Object proxy, Object other) {
        return (proxy == other);
    }

    private String proxyToString(Object proxy) {
        return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
    }
}
