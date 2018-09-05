package hzr.common.serialization;


import hzr.common.serialization.spi.BaseServiceLoader;

/**
 * 序列化的入口,基于SPI方式
 */
public final class SerializerHolder {

    private static final Serializer serializer = BaseServiceLoader.load(Serializer.class);

    public static Serializer serializerImpl() {
        return serializer;
    }
}
