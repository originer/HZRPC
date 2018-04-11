import api.TestImpl;
import api.TestImpl2;
import hzr.common.bootstrap.ServerBuilder;
import hzr.common.transport.server.Server;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ServerBuilderTest {

	@Test
	public void testServerSetWithBuilder() throws InterruptedException {
		Map<String, Object> serviceMap1 = new HashMap<>();
		serviceMap1.put("s1",new TestImpl());
		serviceMap1.put("s2",new TestImpl2());


		Server testBuilder = ServerBuilder.builder()
				.port(8998)
				.zkConn("127.0.0.1:2181")
				.serviceMap(serviceMap1)
				.build2();
		testBuilder.start();
		testBuilder.shutdown();
	}



}