/**
 * @author kevin.grigorenko@us.ibm.com
 */
public class J9Dumps {
	public static void main(String... args) throws Throwable {
		requestCoreDump();
	}

	private static int threadDumpsTaken = 0, heapDumpsTaken = 0, coreDumpsTaken = 0;

	private static final int maxThreadDumps = Integer.parseInt(System.getProperty("MAXTHREADDUMPS", "-1"));
	private static final int maxHeapDumps = Integer.parseInt(System.getProperty("MAXHEAPDUMPS", "1"));
	private static final int maxCoreDumps = Integer.parseInt(System.getProperty("MAXCOREDUMPS", "1"));

	private static final boolean isIBMJava;
	private static final Class<?> ibmDumpClass;
	private static final java.lang.reflect.Method ibmJavacoreMethod;
	private static final java.lang.reflect.Method ibmHeapDumpMethod;
	private static final java.lang.reflect.Method ibmSystemDumpMethod;
	private static final Class<?> hotSpotMXBeanClass;
	private static final Object hotspotMXBean;
	private static final java.lang.reflect.Method hotspotMXBeanDumpHeap;
	private static final java.text.SimpleDateFormat hotspotDateFormat = new java.text.SimpleDateFormat(
			"yyyyMMdd'T'HHmmss");

	static {
		try {
			isIBMJava = isIBMJava();
			ibmDumpClass = isIBMJava ? Class.forName("com.ibm.jvm.Dump") : null;
			ibmHeapDumpMethod = isIBMJava ? ibmDumpClass.getMethod("HeapDump") : null;
			ibmJavacoreMethod = isIBMJava ? ibmDumpClass.getMethod("JavaDump") : null;
			ibmSystemDumpMethod = isIBMJava ? ibmDumpClass.getMethod("SystemDump") : null;
			hotSpotMXBeanClass = isIBMJava ? null : getHotSpotDiagnosticMXBeanClass();
			hotspotMXBean = isIBMJava ? null : getHotSpotDiagnosticMXBean();
			hotspotMXBeanDumpHeap = isIBMJava ? null : getHotSpotDiagnosticMXBeanDumpHeap();
		} catch (Throwable t) {
			throw new RuntimeException("Could not load Java dump classes", t);
		}
	}

	public static boolean isIBMJava() {
		try {
			// We could use System.getProperty, but that requires elevated
			// permissions in some cases.
			Class.forName("com.ibm.jvm.Dump");
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	private static Class<?> getHotSpotDiagnosticMXBeanClass() throws ClassNotFoundException {
		return Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
	}

	private static Object getHotSpotDiagnosticMXBean() throws ClassNotFoundException, java.io.IOException {
		javax.management.MBeanServer server = java.lang.management.ManagementFactory.getPlatformMBeanServer();
		return java.lang.management.ManagementFactory.newPlatformMXBeanProxy(server,
				"com.sun.management:type=HotSpotDiagnostic", hotSpotMXBeanClass);
	}

	private static java.lang.reflect.Method getHotSpotDiagnosticMXBeanDumpHeap()
			throws NoSuchMethodException, SecurityException {
		return hotSpotMXBeanClass.getMethod("dumpHeap", String.class, boolean.class);
	}

	public static synchronized void requestThreadDump() {
		if (maxThreadDumps == -1 || (maxThreadDumps > -1 && threadDumpsTaken++ < maxThreadDumps)) {
			try {
				ibmJavacoreMethod.invoke(ibmDumpClass);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}

	public static synchronized void requestHeapDump() {
		if (maxHeapDumps == -1 || (maxHeapDumps > -1 && heapDumpsTaken++ < maxHeapDumps)) {
			try {
				if (ibmHeapDumpMethod != null) {
					ibmHeapDumpMethod.invoke(ibmDumpClass);
				} else {
					requestHotSpotHPROF();
				}
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}

	public static synchronized void requestCoreDump() {
		if (maxCoreDumps == -1 || (maxCoreDumps > -1 && coreDumpsTaken++ < maxCoreDumps)) {
			try {
				if (ibmSystemDumpMethod != null) {
					ibmSystemDumpMethod.invoke(ibmDumpClass);
				} else {
					requestHotSpotHPROF();
				}
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}

	private static void requestHotSpotHPROF()
			throws IllegalAccessException, java.lang.reflect.InvocationTargetException {
		String fileName = "heap" + hotspotDateFormat.format(new java.util.Date()) + ".hprof";
		boolean live = true;
		hotspotMXBeanDumpHeap.invoke(hotspotMXBean, fileName, live);
	}
}
