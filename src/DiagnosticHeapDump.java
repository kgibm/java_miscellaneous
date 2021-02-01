public class DiagnosticHeapDump {
	private static final boolean QUIET = Boolean.parseBoolean(System.getProperty("Diagnostics.Quiet", "false"));
	private static final Class<?> j9Dump;
	private static final java.lang.reflect.Method j9triggerDump;

	static {
		j9Dump = loadJ9Dump();
		j9triggerDump = loadJ9TriggerDump(j9Dump);
	}

	private static Class<?> loadJ9Dump() {
		try {
			return Class.forName("com.ibm.jvm.Dump");
		} catch (Throwable t) {
			if (!QUIET) {
				t.printStackTrace();
			}
			return null;
		}
	}

	private static java.lang.reflect.Method loadJ9TriggerDump(Class<?> c) {
		if (c != null) {
			try {
				return c.getMethod("triggerDump", new Class<?>[] { String.class });
			} catch (Throwable t) {
				if (!QUIET) {
					t.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String requestJ9HeapDump() {
		try {
			return (String) j9triggerDump.invoke(null, new Object[] { "heap:request=exclusive+prepwalk" });
		} catch (Throwable t) {
			if (QUIET) {
				return null;
			} else {
				throw new RuntimeException(t);
			}
		}
	}

	public static void main(String[] args) throws Throwable {
		requestJ9HeapDump();
	}
}