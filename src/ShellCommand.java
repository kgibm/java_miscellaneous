import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShellCommand
{
    public static void main(String... args) throws Throwable
    {
        System.out.println(exec(args));
    }

    public static String exec(String... args) throws IOException, InterruptedException
    {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        processBuilder.redirectErrorStream(true);
        Process pid = processBuilder.start();
        StringBuilder sb = new StringBuilder();
        try (InputStream is = pid.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
        }
        pid.waitFor();
        return sb.toString();
    }
}
