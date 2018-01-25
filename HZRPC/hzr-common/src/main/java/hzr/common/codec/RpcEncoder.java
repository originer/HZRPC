package hzr.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static hzr.common.serialization.SerializerHolder.serializerImpl;

/**
 * RPC 编码器
 *
 * @author huangyong
 * @since 1.0.0
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = serializerImpl().writeObject(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}