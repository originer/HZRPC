package hzr.common.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import hzr.common.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Zz
 * @description 使用fastjson序列化需要实现java.io.Serializable接口
 **/
public class KryoSerializer implements Serializer {
    @Override
    public <T> byte[] writeObject(T obj) {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output output = new Output(baos);
        kryo.writeClassAndObject(output,obj);
        output.flush();
        output.clear();

        byte[] b = baos.toByteArray();

        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }

    @Override
    public <T> T readObject(byte[] bytes, Class<T> clazz) {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.register(clazz, new JavaSerializer());

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Input input = new Input(bais);

        return (T) kryo.readClassAndObject(input);
    }
}
