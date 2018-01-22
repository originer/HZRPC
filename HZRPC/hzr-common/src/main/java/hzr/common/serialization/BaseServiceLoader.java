package hzr.common.serialization;

import java.util.ServiceLoader;

/**
 * @author Zz
 **/
public class BaseServiceLoader {
    public static <S> S load(Class<S> serviceClass) {
        return ServiceLoader.load(serviceClass).iterator().next();
    }
}
