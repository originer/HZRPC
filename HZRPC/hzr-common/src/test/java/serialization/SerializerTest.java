package serialization;

import hzr.common.protocol.Request;
import hzr.common.protocol.Response;
import hzr.common.util.SerializationUtil;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SerializerTest {


	@Test
    public void serializeTime() {
		List<Long> arr = new ArrayList<>();
		Integer len = null;
		TestCommonCustomBody.ComplexTestObj complexTestObj = new TestCommonCustomBody.ComplexTestObj("attr1", 2);
		TestCommonCustomBody commonCustomHeader = new TestCommonCustomBody(1, "test",complexTestObj);
		for(int j=0; j < 10; j++) {
			long beginTime = System.currentTimeMillis();
			for(int i = 0;i < 1000;i++){
			byte[] bytes = SerializationUtil.serialize(commonCustomHeader);
				len = bytes.length;
				TestCommonCustomBody body = SerializationUtil.deserialize(bytes, TestCommonCustomBody.class);
			}
			long endTime = System.currentTimeMillis();
//			System.out.println((endTime - beginTime));
			arr.add(endTime - beginTime);
		}
		System.out.println("ProtoStuffSerializer:"+arr.toString());
		System.out.println("序列化后的字节大小:"+len.toString());


	}
	@Test
    public void serializeJavaTest() throws IOException, ClassNotFoundException {
		List<Long> arr = new ArrayList<>();
		Integer len = null;
		Request request = new Request();
		request.setServiceName("request");
		request.setRequestId(141276587L);
		for(int j=0; j < 10; j++) {
			long beginTime = System.currentTimeMillis();
			for(int i = 0;i < 100000;i++){
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				ObjectOutputStream oo = new ObjectOutputStream(bo);
				oo.writeObject(request);
				byte[] bytes = bo.toByteArray();
				len = bytes.length;
				ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
				ObjectInputStream oi = new ObjectInputStream(bi);
				Request body = (Request) oi.readObject();
			}
			long endTime = System.currentTimeMillis();
//			System.out.println((endTime - beginTime));
			arr.add(endTime - beginTime);
		}
		System.out.println("Java原生序列化:"+arr.toString());
		System.out.println("序列化后的字节大小:"+len.toString());


	}



	@Test
	public void testRequest() {
		Request request = new Request();
		request.setServiceName("request");
		request.setRequestId(141276587L);

		byte[] bytes = SerializationUtil.serialize(request);
		Request body = SerializationUtil.deserialize(bytes, Request.class);
		System.out.println(body);
	}
	@Test
	public void testReponse() {
		Response response = new Response();
		response.setRequestId(1235123L);
		response.setResponse("Object");
		response.setThrowable(new RuntimeException("runtime error"));
		byte[] bytes = SerializationUtil.serialize(response);
		Response body = SerializationUtil.deserialize(bytes, Response.class);
		System.out.println(body);
	}


	@Test
	public void tt() {
		class s<T> {
			public T ss() {
				String s = null;
				return (T) s;
			};
		}

		s<String> s = new s<String>();
		s.ss();
	}


}