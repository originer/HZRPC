package hzr.common.transport;

import hzr.common.protocol.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

import static hzr.common.util.ResponseMapCache.responseMap;

@ChannelHandler.Sharable
@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<Response> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        //根据请求ID，获取到响应队列，然后把response放到返回队列中
        BlockingQueue<Response> responseQueue = responseMap.get(msg.getRequestId());
        if (responseQueue != null) {
            responseQueue.put(msg);
        } else {
            throw new RuntimeException("responseQueue is null");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception caught on {}, ", ctx.channel(), cause);
        ctx.channel().close();
    }
}