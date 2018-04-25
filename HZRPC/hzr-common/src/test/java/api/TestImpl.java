package api;

/**
 *
 */

public class TestImpl implements ITest {
	public String say(String hello) {
		return "HELLOIMPL 1 " + hello;
	}

	public int sum(int a, int b) {
		return a + b;
	}

//	@Override
//	public String toString() {
//		return super.toString();
//	}
}
