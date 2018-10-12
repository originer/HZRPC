package hzr.common.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * RPC请求
 */
@Data
public class Request extends TranslatorData{
    private String serviceName;              //请求调用的服务名称
    private long requestId;                  //唯一的请求ID
    private Class<?> clazz;                  //请求调用的服务接口
    private String method;                   //请求调用的方法名
    private Class<?>[] parameterTypes;       //参数类型&参数值
    private Object[] params;                 //调用服务的参数
    private long requestTime;                //请求超时时间
}

