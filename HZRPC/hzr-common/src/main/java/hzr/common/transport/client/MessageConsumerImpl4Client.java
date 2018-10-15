package hzr.common.transport.client;


import hzr.common.disruptor.MessageConsumer;
import hzr.common.protocol.Response;
import hzr.common.protocol.TranslatorDataWapper;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

import static hzr.common.util.ResponseMapCache.responseMap;

@Slf4j
public class MessageConsumerImpl4Client extends MessageConsumer {

	public MessageConsumerImpl4Client(String consumerId) {
		super(consumerId);
	}

	public void onEvent(TranslatorDataWapper event) throws Exception {
		Response response = (Response) event.getData();
		//业务逻辑处理:
		try {
			BlockingQueue<Response> responseQueue = responseMap.get(response.getRequestId());
			if (responseQueue != null) {
				responseQueue.put(response);
			} else {
				throw new RuntimeException("responseQueue is null");
			}
			log.info("服务端消费 consumerID：" + this.consumerId + "event:" + event.getData());
		} finally {
			//ReferenceCountUtil.release(response);
		}
	}

}
