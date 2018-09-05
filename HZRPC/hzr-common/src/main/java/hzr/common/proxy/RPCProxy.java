package hzr.common.proxy;

import hzr.common.transport.client.Client;

/**
 * 代理类接口
 */
public interface RPCProxy {
    <T> T proxyInterface(Client client, final Class<T> serviceInterface);
}