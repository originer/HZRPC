package hzr.common.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import hzr.common.serialization.Serializer;

/**
 * @author Zz
 * @description 使用fastjson序列化需要有无参构造函数
 *
 **/
public class FastJsonSerializer implements Serializer {
    @Override
    public <T> byte[] writeObject(T obj) {
        return JSON.toJSONBytes(obj, SerializerFeature.SortField);
    }

    @Override
    public <T> T readObject(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz, Feature.SortFeidFastMatch);
    }

}
