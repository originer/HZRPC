package hzr.common.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 封装 RPC 请求
 */

@Getter
@Setter
public class RpcRequest {

    private String requestId;
    private String interfaceName;
    private String serviceVersion;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

}
