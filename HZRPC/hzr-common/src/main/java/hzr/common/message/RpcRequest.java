package hzr.common.message;

import lombok.Data;

/**
 * 封装 RPC 请求
 */
@Data
public class RpcRequest {

    private String requestId;
    private String interfaceName;
    private String serviceVersion;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
