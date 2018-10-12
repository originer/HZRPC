package hzr.common.transport.client;

import hzr.common.protocol.Response;

import java.lang.reflect.Method;

public interface Client {
    Response invokeMethod(Class<?> clazz, Method method, Object[] args) throws Exception;

    <T> T proxyInterface(Class<T> serviceInterface);

    void close();
}