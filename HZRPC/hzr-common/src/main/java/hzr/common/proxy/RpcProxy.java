package hzr.common.proxy;

import hzr.common.transport.client.Client;

public interface RpcProxy {
    <T> T proxyInterface(Client client, final Class<T> serviceInterface);
}