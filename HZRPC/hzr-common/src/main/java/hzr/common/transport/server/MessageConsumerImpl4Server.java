package hzr.common.transport.server;


import com.google.common.base.Preconditions;
import hzr.common.disruptor.MessageConsumer;
import hzr.common.protocol.Request;
import hzr.common.protocol.Response;
import hzr.common.protocol.TranslatorData;
import hzr.common.protocol.TranslatorDataWapper;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MessageConsumerImpl4Server extends MessageConsumer {

	private static Map<String, Method> methodCache = new HashMap<>();

	public MessageConsumerImpl4Server(String consumerId) {
		super(consumerId);
	}

	public void onEvent(TranslatorDataWapper event) throws Exception {
		Request request = (Request) event.getData();
		ChannelHandlerContext ctx = event.getCtx();
        Map<String, Object> serviceMap = event.getServiceMap();
		//通过serviceName从serviceMap中取出实例
		log.info("请求服务 requestId：{}，serviceName：{}", request.getRequestId(), request.getServiceName());
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

		ctx.writeAndFlush(response);
	}

}
