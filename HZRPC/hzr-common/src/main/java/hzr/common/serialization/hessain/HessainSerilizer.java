package hzr.common.serialization.hessain;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import hzr.common.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Zz
 **/
public class HessainSerilizer implements Serializer {


    @Override
    public byte[] writeObject(Object data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output out = new Hessian2Output(bos);
        try {
            out.writeObject(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readObject(byte[] data, Class<T> clz) {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        try {
            return (T) input.readObject(clz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
