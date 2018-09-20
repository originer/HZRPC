package api;

/**
 *
 */

public class TestImpl implements ITest {

	private String service;

	public TestImpl(String service) {
		this.service = service;
	}

	public TestImpl() {
	}

	public String say(String hello) {
		System.out.println(hello);
		return "service:"+service + hello;
	}

	public int sum(int a, int b) {
		return a + b;
	}

//	@Override
//	public String toString() {
//		return super.toString();
//	}
}
