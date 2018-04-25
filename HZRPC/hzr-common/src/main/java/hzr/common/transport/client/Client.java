package hzr.common.transport.client;

import hzr.common.protocol.Response;

import java.lang.reflect.Method;

public interface Client {
    Response sendMessage(Class<?> clazz, Method method, Object[] args);
    <T> T proxyInterface(Class<T> serviceInterface);
    void close();
}