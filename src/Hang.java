/**
 * @author kevin.grigorenko@us.ibm.com
 */
public class Hang {
	public static void main(String[] args) throws Throwable {
		System.out.println("Starting infinite hang");
		Object o = new Object();
		synchronized (o) {
			o.wait();
		}
	}
}
