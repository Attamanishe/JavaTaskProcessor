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
     * @param sleepTime   the time to pause thread between execute cycles
     */
    protected DiagnosticallyTaskProcessor(String processName, int sleepTime)
    {
        super(processName, sleepTime);
        DiagnosticsTaskProcessorsManager.getInstance().addProcessor(this, getGroupName());
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

    @Override
    protected final void task()
    {
        long startTime = System.currentTimeMillis();
        taskCycle();
        lastProcessTime = System.currentTimeMillis() - startTime;
    }

    /**
     * The task method to override that will execute every update in the thread
     */
    protected abstract void taskCycle();
}
