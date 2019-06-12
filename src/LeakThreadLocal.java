public class LeakThreadLocal
{
    private static ThreadLocal<String> myThreadLocal = new ThreadLocal<String>()
    {
        @Override
        protected String initialValue()
        {
            return "Initial Value";
        }
    };

    private static ThreadLocalSubclass myThreadLocalFromSubclass = new ThreadLocalSubclass();

    public static void main(String[] args) throws Throwable
    {
        System.out.println("Started ThreadLocal: " + myThreadLocal.get());

        MyThread thread = new MyThread();
        thread.start();
        thread.join();
    }

    public static class MyThread extends Thread
    {
        @Override
        public void run()
        {
            myThreadLocal.set("Hello World");
            myThreadLocalFromSubclass.set("Hello World");

            System.out.println("MyThread ThreadLocal: " + myThreadLocal.get());

            System.out.println("Waiting indefinitely...");

            Object o = new Object();
            synchronized (o)
            {
                try
                {
                    o.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class ThreadLocalSubclass extends ThreadLocal<String>
    {
        @Override
        protected String initialValue()
        {
            return "ThreadLocalSubclass initialValue";
        }
    }
}
