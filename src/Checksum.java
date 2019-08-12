import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Checksum
{
    public static final String DEFAULT_CHECKSUM_ALGORITHM = System.getProperty("DEFAULT_CHECKSUM_ALGORITHM", "MD5");
    public static final String MD5SUM_PATH = System.getProperty("MD5SUM_PATH", "/usr/bin/md5sum");

    public static void main(String[] args) throws Throwable
    {
        File file = new File(args[0]);
        System.out.println(checksum(file));
        System.out.println(osChecksum(file));
    }

    public static String checksum(File file) throws NoSuchAlgorithmException, IOException
    {
        return checksum(file, DEFAULT_CHECKSUM_ALGORITHM);
    }

    public static String checksum(File file, String algorithm) throws NoSuchAlgorithmException, IOException
    {
        byte[] data = new byte[4096];
        MessageDigest md = MessageDigest.getInstance(algorithm);
        try (InputStream is = Files.newInputStream(Paths.get(file.getAbsolutePath()));
                        DigestInputStream dis = new DigestInputStream(is, md))
        {
            while (dis.read(data) != -1)
            {}
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest)
        {
            sb.append(String.format("%02x", b));
        }
        sb.append("  ");
        sb.append(file.getAbsolutePath());
        return sb.toString();
    }

    public static String osChecksum(File file) throws IOException, InterruptedException
    {
        return ShellCommand.exec(MD5SUM_PATH, file.getAbsolutePath());
    }
}
