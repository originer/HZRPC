package hzr.common.proxy;

import hzr.common.transport.client.Client;
import hzr.common.transport.client.ClientImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
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
     * 不是所有的响应都具备equals,toString,hashCode方法，为了防止调用者调用产生错误，对这三个方法做特殊处理
     * @param client
     * @param serviceInterface
     * @param <T>
     * @return
     */
    @Override
    public <T> T proxyInterface(final Client client, final Class<T> serviceInterface) {
        Object proxyInstance = Proxy.newProxyInstance(ClientImpl.class.getClassLoader(),
                new Class[]{serviceInterface}, new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (hashCodeMethod.equals(method)) {
                            return proxyHashCode(proxy);
                        }
                        if (equalsMethod.equals(method)) {
                            return proxyEquals(proxy, args[0]);
                        }
                        if (toStringMethod.equals(method)) {
                            return proxyToString(proxy);
                        }
                        try {
                            return client.sendMessage(serviceInterface, method, args).getResponse();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
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
