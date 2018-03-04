package hzr.common.transport;

import hzr.common.message.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

import static hzr.common.util.ResponseMapCache.responseMap;

@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<Response> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        //此处的业务逻辑就是拿到对应id，讲返回信息放入相应blockingQueue中
        BlockingQueue<Response> blockingQueue = responseMap.get(msg.getRequestId());
        if (blockingQueue != null) {
            blockingQueue.put(msg);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Exception caught on {}, ", ctx.channel(), cause);
        ctx.channel().close();
    }
}