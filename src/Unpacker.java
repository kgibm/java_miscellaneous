import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Unpacker
{
    private static final byte[] BUFFER = new byte[Integer.getInteger("BUFFER_SIZE", 4096)];
    public static final boolean DEBUG = Boolean.getBoolean("DEBUG");

    public static void println(Object message)
    {
        System.out.println("[" + new Date() + "]: " + message);
    }

    public static void main(String[] args) throws Throwable
    {
        println("Unpacker version 0.1.0");
        String dir = ".";
        if (args.length > 0)
        {
            dir = args[0];
        }

        File dirFile = new File(dir);

        println("Processing " + dirFile.getAbsolutePath());

        LinkedList<File> filesToProcess = new LinkedList<File>();
        filesToProcess.add(dirFile);

        drain(filesToProcess);

        println("Done");
    }

    private static void drain(LinkedList<File> filesToProcess) throws Throwable
    {
        while (filesToProcess.size() > 0)
        {
            process(filesToProcess, filesToProcess.poll());
        }
    }

    private static void process(LinkedList<File> filesToProcess, File file) throws Throwable
    {
        if (file.isDirectory())
        {
            for (File child : file.listFiles())
            {
                filesToProcess.add(child);
            }
        }
        else
        {
            if (isZipPacked(file))
            {
                unpack(filesToProcess, file);
            }
        }
    }

    private static boolean isZipPacked(File file)
    {
        String lowerName = file.getName().toLowerCase();
        return lowerName.endsWith(".zip") || lowerName.endsWith(".jar") || lowerName.endsWith(".war")
                        || lowerName.endsWith(".ear");
    }

    private static void unpack(LinkedList<File> filesToProcess, File file) throws Throwable
    {
        println("Unpacking " + file.getAbsolutePath());

        ZipFile zip = null;
        try
        {
            zip = new ZipFile(file);

            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry zipEntry = entries.nextElement();
                File target = new File(file.getParentFile(), zipEntry.getName());
                if (DEBUG)
                {
                    println("Entry: " + zipEntry.getName() + ", Target: " + target.getAbsolutePath());
                }
                if (zipEntry.isDirectory())
                {
                    ensureDirectories(target);
                }
                else
                {
                    if (!target.exists() || zipEntry.getSize() != target.length())
                    {
                        ensureDirectories(target);

                        InputStream is = null;
                        try
                        {
                            is = zip.getInputStream(zipEntry);
                            BufferedInputStream bis = null;
                            try
                            {
                                bis = new BufferedInputStream(is);

                                OutputStream os = null;
                                try
                                {
                                    os = new FileOutputStream(target);

                                    BufferedOutputStream bos = null;
                                    try
                                    {
                                        bos = new BufferedOutputStream(os);

                                        int bytesRead = bis.read(BUFFER);
                                        while (bytesRead != -1)
                                        {
                                            bos.write(BUFFER, 0, bytesRead);

                                            bytesRead = bis.read(BUFFER);
                                        }
                                    }
                                    finally
                                    {
                                        if (bos != null)
                                        {
                                            bos.close();
                                        }
                                    }
                                }
                                finally
                                {
                                    if (os != null)
                                    {
                                        os.close();
                                    }
                                }
                            }
                            finally
                            {
                                if (bis != null)
                                {
                                    bis.close();
                                }
                            }
                        }
                        finally
                        {
                            if (is != null)
                            {
                                is.close();
                            }
                        }
                    }

                    if (isZipPacked(target))
                    {
                        filesToProcess.add(target);
                    }
                }
            }
        }
        finally
        {
            if (zip != null)
            {
                zip.close();
            }
        }
    }

    private static void ensureDirectories(File target) throws Error
    {
        if (!target.isDirectory())
        {
            target = target.getParentFile();
        }
        if (!target.exists())
        {
            if (!target.mkdirs()) { throw new Error("Could not make the directory and any intermediate directories for "
                            + target.getAbsolutePath()); }
        }
    }
}
