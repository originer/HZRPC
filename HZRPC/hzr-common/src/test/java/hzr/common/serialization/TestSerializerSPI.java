package hzr.common.serialization;

import hzr.common.util.SerializationUtil;
import lombok.extern.slf4j.Slf4j;

import static hzr.common.serialization.SerializerHolder.serializerImpl;

/**
 * @author Zz
 **/
@Slf4j
public class TestSerializerSPI {

    public static void main(String[] args) {
//        for (int j = 0; j < 10; j++) {
//            long beginTime = System.currentTimeMillis();
//            for (int i = 0; i < 100000; i++) {
//                TestCommonCustomBody.ComplexTestObj complexTestObj = new TestCommonCustomBody.ComplexTestObj("attr1", 2);
//                TestCommonCustomBody commonCustomHeader = new TestCommonCustomBody(1, "test", complexTestObj);
//                byte[] bytes = serializerImpl().writeObject(commonCustomHeader);
//                TestCommonCustomBody body = serializerImpl().readObject(bytes, TestCommonCustomBody.class);
//            }
//            long endTime = System.currentTimeMillis();
//
//            System.out.println((endTime - beginTime));
//        }

        TestCommonCustomBody.ComplexTestObj complexTestObj = new TestCommonCustomBody.ComplexTestObj("attr1", 2);
        TestCommonCustomBody commonCustomHeader = new TestCommonCustomBody(1, "test", complexTestObj);
//        byte[] bytes = serializerImpl().writeObject(commonCustomHeader);
        byte[] bytes = SerializationUtil.serialize(commonCustomHeader);
//        TestCommonCustomBody body = serializerImpl().readObject(bytes, TestCommonCustomBody.class);
        TestCommonCustomBody body = SerializationUtil.deserialize(bytes, TestCommonCustomBody.class);
        log.info("bodyName {}, body2String {}",body.getName(),body.toString());
//        System.out.println(body.toString());
    }
}
