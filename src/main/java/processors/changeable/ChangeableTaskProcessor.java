package processors.changeable;

import processors.diagnostically.DiagnosticallyTaskProcessor;

import java.util.function.Function;

public class ChangeableTaskProcessor extends DiagnosticallyTaskProcessor
{

    private Function taskToExecute;

    /**
     * Constructor
     *
     * @param processName   the name of thread that will be started
     * @param taskToExecute the task to execute
     * @param sleepTime     the time to pause thread between execute cycles
     */
    public ChangeableTaskProcessor(String processName, Function taskToExecute, int sleepTime)
    {
        super(processName, sleepTime);
        this.taskToExecute = taskToExecute;
    }

    @Override
    public String getGroupName()
    {
        return processName;
    }

    /**
     * Method to change target task
     *
     * @param task      the task to execute
     * @param sleepTime the time to pause thread between execute cycles
     */
    public void setTaskToExecute(Function task, int sleepTime)
    {
        taskToExecute = task;
        this.sleepTime = sleepTime;
        resume();
    }

    public void stopCurrentTask()
    {
        pause();
    }

    @Override
    protected void taskCycle()
    {
        if (taskToExecute != null)
        {
            taskToExecute.apply(null);
        }
    }
}
