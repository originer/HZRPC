package hzr.common.serializer;


public interface Serializer {
	byte[] serialize(Object obj);
	<T> T deserialize(byte[] bytes);
}