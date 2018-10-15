package hzr.common.transport;

import hzr.common.disruptor.MessageProducer;
import hzr.common.disruptor.RingBufferWorkerPoolFactory;
import hzr.common.protocol.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<Request> {
    private final Map<String, Object> serviceMap;
    private static Map<String, Method> methodCache = new HashMap<>();

    public RpcServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        //通过serviceName从serviceMap中取出实例
        //log.info("请求服务 requestId：{}，serviceName：{}", request.getRequestId(), request.getServiceName());
        //Object service = serviceMap.get(request.getServiceName());
        //Preconditions.checkNotNull(service);
        //
        ////通过反射来获取客户端所要调用的方法并执行
        //String methodName = request.getMethod();
        //Object[] params = request.getParams();
        //Class<?>[] parameterTypes = request.getParameterTypes();
        //long requestId = request.getRequestId();
        //
        //Object invokeResult;
        //if (methodCache.containsKey(methodName)) {
        //    invokeResult = methodCache.get(methodName).invoke(service, params);
        //} else {
        //    Method method = service.getClass().getDeclaredMethod(methodName, parameterTypes);
        //    method.setAccessible(true);
        //    invokeResult = method.invoke(service, params);
        //    methodCache.put(methodName, method);
        //}
        //
        ////封装响应
        //Response response = new Response();
        //response.setRequestId(requestId);
        //response.setResponse(invokeResult);
        //ctx.pipeline().writeAndFlush(response);

        //TODO 后续添加多生产者
        String producerId = "code:sessionId:001";
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance("server").getMessageProducer(producerId);
        messageProducer.onData(request, ctx, serviceMap);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception caught on {}, ", ctx.channel(), cause);
        ctx.channel().close();
    }
}