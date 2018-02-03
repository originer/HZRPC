package hzr.common.message;

import lombok.Data;

/**
 * 封装 RPC 响应
 */

@Data
public class RpcResponse {

    private String requestId;
    private Exception exception;
    private Object result;

    public boolean hasException() {
        return exception != null;
    }

}
