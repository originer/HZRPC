package hzr.common.util;

import static hzr.common.serialization.SerializerHolder.serializerImpl;

/**
 * 序列化工具（单例）
 */
public class SerializationUtil {
    private SerializationUtil() {
    }

    /**
     * 序列化（对象 -> 字节数组）
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        return serializerImpl().writeObject(obj);
    }

    /**
     * 反序列化（字节数组 -> 对象）
     */
    public static <T> T deserialize(byte[] data, Class<T> cls) {
        return serializerImpl().readObject(data,cls);
    }

}
