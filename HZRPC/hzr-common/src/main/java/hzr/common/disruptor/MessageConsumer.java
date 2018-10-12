package hzr.common.disruptor;


import com.lmax.disruptor.WorkHandler;
import hzr.common.protocol.TranslatorDataWapper;

public abstract class MessageConsumer implements WorkHandler<TranslatorDataWapper> {

	protected String consumerId;
	
	public MessageConsumer(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

}
