package processors.diagnostically;

import processors.base.TaskProcessor;

/*
<h1>DiagnosticallyTaskProcessor</h1>
The implementation gives you TaskProcessor that can be diagnostically by time to performing task
 */
public abstract class DiagnosticallyTaskProcessor extends TaskProcessor implements IDiagnostically
{
    private long lastProcessTime;

    /**
     * Constructor
     *
     * @param processName the name of thread that will be started
     */
    protected DiagnosticallyTaskProcessor(String processName)
    {
        super(processName);
        DiagnosticsTaskProcessorsManager.getInstance().addProcessor(this, getDiagnosticallyGroup());
    }

    @Override
    protected void taskCycle() throws InterruptedException
    {
        long startTime = System.currentTimeMillis();
        long sleepTime = task();
        lastProcessTime = System.currentTimeMillis() - startTime;
        Thread.sleep(sleepTime);
    }

    @Override
    public long getPerformTime()
    {
        return lastProcessTime;
    }

    @Override
    public boolean isWorking()
    {
        return isAlive;
    }
}
