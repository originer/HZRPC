package api;

/**
 * @author Zz
 **/
public class TestImpl2 implements ITest {
    public String say(String hello) {
        return "HELLOIMPL 2 " + hello;
    }

    public int sum(int a, int b) {
        return a + b;
    }

    public int sum(Integer a, Integer b) {
        return a + b * 3;
    }

}
