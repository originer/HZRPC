import hzr.rpc.HelloServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @author Zz
 **/

@Slf4j
public class CGlibTest {

    /**
     * 实现MethodInterceptor接口生成方法拦截器
     */
    static class HelloMethodInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            log.info("Before: {}",method.getName());
            Object object = methodProxy.invokeSuper(o,objects);
            log.info("After: {}",method.getName());
            return object;
        }
    }

    @Test
    public void testProxy(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloServiceImpl.class); //继承被代理类
        enhancer.setCallback(new HelloMethodInterceptor()); //设置回调
        HelloServiceImpl helloService = (HelloServiceImpl) enhancer.create(); //生成代理类对象

        String s = helloService.hello("123");
        System.out.println(s);
        System.out.println("\uD83D\uDC7F");
    }
}
