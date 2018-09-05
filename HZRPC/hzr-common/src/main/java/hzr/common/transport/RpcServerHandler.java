package hzr.common.transport;

import com.google.common.base.Preconditions;
import hzr.common.protocol.Request;
import hzr.common.protocol.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RpcServerHandler extends SimpleChannelInboundHandler<Request> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);
    private final Map<String, Object> serviceMap;
    private static Map<String, Method> methodCache = new HashMap<>();

    //此处传入service的实现类对象
    public RpcServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        //通过serviceName从serviceMap中取出实例
        LOGGER.info("请求服务 requestId：{}，serviceName：{}", request.getRequestId(), request.getServiceName());
        Object service = serviceMap.get(request.getServiceName());
        Preconditions.checkNotNull(service);

        //通过反射来获取客户端所要调用的方法并执行
        String methodName = request.getMethod();
        Object[] params = request.getParams();
        Class<?>[] parameterTypes = request.getParameterTypes();
        long requestId = request.getRequestId();

        Object invokeResult;
        if (methodCache.containsKey(methodName)) {
            invokeResult = methodCache.get(methodName).invoke(service, params);
        } else {
            Method method = service.getClass().getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            invokeResult = method.invoke(service, params);
            methodCache.put(methodName, method);
        }

        //封装响应
        Response response = new Response();
        response.setRequestId(requestId);
        response.setResponse(invokeResult);
        channelHandlerContext.pipeline().writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Exception caught on {}, ", ctx.channel(), cause);
        ctx.channel().close();
    }
}