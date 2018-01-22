package hzr.common.serialization;

import static hzr.common.serialization.SerializerHolder.serializerImpl;

/**
 * @author Zz
 **/

public class TestSerializerSPI {

    public static void main(String[] args) {
        for (int j = 0; j < 10; j++) {
            long beginTime = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                TestCommonCustomBody.ComplexTestObj complexTestObj = new TestCommonCustomBody.ComplexTestObj("attr1", 2);
                TestCommonCustomBody commonCustomHeader = new TestCommonCustomBody(1, "test", complexTestObj);
                byte[] bytes = serializerImpl().writeObject(commonCustomHeader);
                TestCommonCustomBody body = serializerImpl().readObject(bytes, TestCommonCustomBody.class);
            }
            long endTime = System.currentTimeMillis();

            System.out.println((endTime - beginTime));

        }
    }
}
