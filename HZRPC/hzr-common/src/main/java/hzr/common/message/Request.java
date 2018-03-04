package hzr.common.message;

import lombok.Data;

/**
 * RPC请求POJO
 */
@Data
public class Request {
    private String serviceName;
    private long requestId;
    private Class<?> clazz;
    private String method;
    private Class<?>[] parameterTypes;
    private Object[] params;
    private long requestTime;
}