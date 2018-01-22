package hzr.common.serialization;

/**
 * @author Zz
 * 序列化方式基于SPI方式加载
 **/
public final class SerializerHolder {
    // SPI
    private static final Serializer serializer = BaseServiceLoader.load(Serializer.class);

    public static Serializer serializerImpl() {
        return serializer;
    }
}
