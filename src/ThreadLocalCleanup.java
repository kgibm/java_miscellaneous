/**
 * @author kevin.grigorenko@us.ibm.com
 */
public class ThreadLocalCleanup {
	private static ThreadLocal<Object> myThreadLocal = new ThreadLocal<Object>() {
		protected Object initialValue() {
			return "Initial Value";
		};
	};

	public static void main(String[] args) throws Throwable {
		System.out.println("ThreadLocalCleanup");

		try {
			// Set the ThreadLocal value
			myThreadLocal.set("Hello World");

			// Do application work
		} finally {
			myThreadLocal.remove();
		}
	}
}
