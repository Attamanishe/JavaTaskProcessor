package processors.load;

import processors.base.IProcessor;
import processors.base.TaskProcessor;
import processors.changed.ChangedTaskProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
<h1>LoadBalancingTaskProcessor</h1>
The implementation gives you TaskProcessor that can make new thread to perform task if some threads cant perform task fast
 */
public abstract class LoadBalancingTaskProcessor implements IProcessor
{
    protected final int LOAD_LOW = -1, LOAD_NORMAL = 0, LOAD_HIGH = 1;
    private final int MAX_POOL_SIZE = 5;

    private class LoadChecker extends TaskProcessor
    {
        public LoadChecker(String groupName)
        {
            super(groupName);
        }

        @Override
        protected int task()
        {
            if (processors.size() < maxThreadsCount)
            {
                int loadValue = getLoadValue();
                if (loadValue == LOAD_HIGH)
                {
                    addWorker();
                } else if (loadValue == LOAD_LOW)
                {
                    stopWorker();
                }
            }
            return timeToUpdateLoadValue;
        }
    }

    /**
     * The task that will performing in threads
     *
     * @return delay time before next process update
     */
    protected abstract int process();

    /**
     * @return the value to determine load value of current processor
     */
    protected abstract int getLoadValue();

    private List<ChangedTaskProcessor> processors;
    private Queue<ChangedTaskProcessor> processorsPool;
    private String groupName;
    private int timeToUpdateLoadValue;
    private int maxThreadsCount;
    private LoadChecker checker;

    /**
     * Constructor
     *
     * @param maxThreadsCount        the threads count that is max to be processed for this TaskProcessor
     * @param timeForUpdateLoadValue the time between checking of load value
     * @param groupName              the name of thread that will be open for this task
     */
    protected LoadBalancingTaskProcessor(int maxThreadsCount, int timeForUpdateLoadValue, String groupName)
    {
        this.timeToUpdateLoadValue = timeForUpdateLoadValue;
        this.maxThreadsCount = maxThreadsCount;
        this.groupName = groupName;
        processors = new ArrayList<>();
        processorsPool = new ConcurrentLinkedQueue<>();
        checker = new LoadChecker(groupName + " load checker");
        addWorker();
    }

    private void addWorker()
    {
        if (processorsPool.isEmpty())
        {
            processors.add(
                    new ChangedTaskProcessor(groupName,
                            o -> process()));
        } else
        {
            ChangedTaskProcessor processor = processorsPool.poll();
            processor.setTaskToPerform(o -> process());
            processors.add(processor);
        }
    }

    private void stopWorker()
    {
        if (processors.size() > 1)
        {
            int last = processors.size() - 1;
            ChangedTaskProcessor processor = processors.remove(last);
            if (processorsPool.size() > MAX_POOL_SIZE)
            {
                processor.stop();
            } else
            {
                processor.stopPerforming();
                processorsPool.add(processor);
            }
        }
    }

    @Override
    public void stop()
    {
        checker.stop();
        for (int i = 0; i < processors.size(); i++)
        {
            processors.get(i).stop();
        }
        for (int i = 0; i < processorsPool.size(); i++)
        {
            processorsPool.poll().stop();
        }
    }
}
