package hzr.common.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 封装 RPC 响应
 */

@Getter
@Setter
public class RpcResponse {

    private String requestId;
    private Exception exception;
    private Object result;

    public boolean hasException() {
        return exception != null;
    }

}
