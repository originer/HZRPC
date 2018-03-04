package hzr.common.message;

import lombok.Getter;
import lombok.Setter;

/**
 * RPC响应POJO
 */
@Setter
@Getter
public class Response {
    private long requestId;
    private Object response;
    private Throwable throwable;
}