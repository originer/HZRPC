package hzr.common.disruptor;


import com.lmax.disruptor.RingBuffer;
import hzr.common.protocol.TranslatorData;
import hzr.common.protocol.TranslatorDataWrapper;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

public class MessageProducer {

	private String producerId;
	
	private RingBuffer<TranslatorDataWrapper> ringBuffer;
	
	public MessageProducer(String producerId, RingBuffer<TranslatorDataWrapper> ringBuffer) {
		this.producerId = producerId;
		this.ringBuffer = ringBuffer;
	}
	
	public void onData(TranslatorData data, ChannelHandlerContext ctx) {
		long sequence = ringBuffer.next();
		try {
			TranslatorDataWrapper wapper = ringBuffer.get(sequence);
			wapper.setData(data);
			wapper.setCtx(ctx);
		} finally {
			ringBuffer.publish(sequence);
		}
	}

	public void onData(TranslatorData data, ChannelHandlerContext ctx,	Map<String, Object> serviceMap) {
		long sequence = ringBuffer.next();
		try {
			TranslatorDataWrapper wapper = ringBuffer.get(sequence);
			wapper.setData(data);
			wapper.setCtx(ctx);
			wapper.setServiceMap(serviceMap);
		} finally {
			ringBuffer.publish(sequence);
		}
	}
}
