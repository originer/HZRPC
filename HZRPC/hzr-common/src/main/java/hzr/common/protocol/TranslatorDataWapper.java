package hzr.common.protocol;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

@Data
public class TranslatorDataWapper {

	private TranslatorData data;
	
	private ChannelHandlerContext ctx;

}
