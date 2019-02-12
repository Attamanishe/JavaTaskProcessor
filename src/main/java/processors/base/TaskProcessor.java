package processors.base;

/*
<h1>Task processor</h1>
The task processor implementation give you thread where u can run your tasks
 */
public abstract class TaskProcessor implements IProcessor, Runnable
{
    protected boolean isAlive = true;
    protected boolean isPaused = false;
    protected String processName;

    /**
     * Constructor with parameter
     *
     * @param processName This is the name of thread that will be started
     */
    protected TaskProcessor(String processName)
    {
        ProcessorsManager.getInstance().addProcessor(this);
        new Thread(this, processName).start();
        this.processName = processName;
    }

    @Override
    public void stop()
    {
        isAlive = false;
    }

    public void pause()
    {
        isPaused = true;
    }

    public void resume()
    {
        isPaused = false;
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        while (isAlive)
        {
            try
            {
                if (!isPaused)
                {
                    taskCycle();
                } else
                {
                    Thread.sleep(10);
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
                stop();
            }
        }
    }

    protected void taskCycle() throws InterruptedException
    {
        Thread.sleep(task());
    }

    /**
     * The task method to override that will perform every update in the thread
     *
     * @return The delay before next update
     */
    protected abstract int task();
}
