package hzr.common.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * RPC响应
 */
@Data
public class Response extends TranslatorData{
    private long requestId;             //请求ID
    private Object response;            //响应内容
    private Throwable throwable;        //异常信息
}