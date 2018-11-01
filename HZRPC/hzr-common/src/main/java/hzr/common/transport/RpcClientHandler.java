package hzr.common.transport;

import hzr.common.disruptor.MessageProducer;
import hzr.common.disruptor.RingBufferWorkerPoolFactory;
import hzr.common.protocol.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import static hzr.common.util.ResponseMapCache.responseMap;

@ChannelHandler.Sharable
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<Response> {
    String producerId = "code:seesionId:002";
    MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws InterruptedException {
        //根据请求ID，获取到响应队列，然后把response放到返回队列中
        //BlockingQueue<Response> responseQueue = responseMap.get(response.getRequestId());
        //if (responseQueue != null) {
        //    responseQueue.put(response);
        //} else {
        //    throw new RuntimeException("responseQueue is null");
        //}

        messageProducer.onData(response, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught on {}, ", ctx.channel(), cause);
        ctx.channel().close();
    }
}