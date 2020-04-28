import java.nio.charset.StandardCharsets;

public class WriteHelloWorld {

	public static void main(String[] args) throws Throwable {
		FileUtilities.writeBytes(("Hello World" + System.lineSeparator()).getBytes(StandardCharsets.US_ASCII), args[0]);
	}

}
