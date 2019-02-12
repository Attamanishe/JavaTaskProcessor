package processors.changed;

import processors.diagnostically.DiagnosticallyTaskProcessor;

import java.util.function.Function;

public class ChangedTaskProcessor extends DiagnosticallyTaskProcessor
{

    private Function<Object, Integer> taskToPerform;

    /**
     * Constructor
     *
     * @param processName the name of thread that will be started
     */
    public ChangedTaskProcessor(String processName, Function<Object, Integer> taskToPerform)
    {
        super(processName);
        this.taskToPerform = taskToPerform;
    }

    @Override
    public String getDiagnosticallyGroup()
    {
        return processName;
    }

    public void setTaskToPerform(Function<Object, Integer> task)
    {
        taskToPerform = task;
        resume();
    }

    public void stopPerforming()
    {
        pause();
    }

    @Override
    protected int task()
    {
        if (taskToPerform != null)
        {
            return taskToPerform.apply(null);
        }
        return 10;
    }
}
