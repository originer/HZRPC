package hzr.common.protocol;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import java.util.Map;

@Data
public class TranslatorDataWrapper {

	private TranslatorData data;
	
	private ChannelHandlerContext ctx;

	private Map<String, Object> serviceMap;

}
