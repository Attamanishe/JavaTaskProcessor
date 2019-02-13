package processors.base;

/*
<h1>Task processor</h1>
The task processor implementation give you thread where u can run your tasks
 */
public abstract class TaskProcessor implements IProcessor, Runnable
{
    protected boolean isAlive;
    protected boolean isOnPause;
    protected String processName;
    protected Thread threadInstance;
    protected int sleepTime;

    /**
     * Constructor with parameter
     *
     * @param processName This is the name of thread that will be started
     * @param sleepTime the time to pause thread between execute cycles
     */
    protected TaskProcessor(String processName, int sleepTime)
    {
        ProcessorsManager.getInstance().addProcessor(this);
        threadInstance = new Thread(this, processName);
        this.processName = processName;
        this.sleepTime = sleepTime;
    }

    @Override
    public void stop()
    {
        isAlive = false;
        ProcessorsManager.getInstance().removeProcessor(this);
    }

    @Override
    public void start()
    {
        isAlive = true;
        threadInstance.start();
    }

    @Override
    public void pause()
    {
        isOnPause = true;
    }

    @Override
    public void resume()
    {
        isOnPause = false;
    }

    @Override
    public void run()
    {
        while (isAlive)
        {
            try
            {
                if (!isOnPause)
                {
                    task();
                    Thread.sleep(sleepTime);
                } else
                {
                    //wait for resume
                    Thread.sleep(10);
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
                stop();
            }
        }
    }

    /**
     * The task method to override that will execute every update in the thread
     */
    protected abstract void task();
}
