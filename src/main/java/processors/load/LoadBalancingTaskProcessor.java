package processors.load;

import processors.base.IProcessor;
import processors.base.TaskProcessor;
import processors.changeable.ChangeableTaskProcessor;
import processors.diagnostically.DiagnosticallyTaskProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
<h1>LoadBalancingTaskProcessor</h1>
The implementation gives you TaskProcessor that can make new thread to execute task if some threads cant execute task fast
 */
public abstract class LoadBalancingTaskProcessor implements IProcessor
{
    protected final int LOAD_LOW = -1, LOAD_NORMAL = 0, LOAD_HIGH = 1;
    private final int MAX_POOL_SIZE = 5;

    private class LoadChecker extends TaskProcessor
    {
        public LoadChecker(String groupName)
        {
            super(groupName, timeToUpdateLoadValue);
        }

        @Override
        protected void task()
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
        }
    }

    private List<DiagnosticallyTaskProcessor> processors;
    private Queue<DiagnosticallyTaskProcessor> processorsPool;
    private String groupName;
    private LoadChecker checker;
    private int timeToUpdateLoadValue;
    private int maxThreadsCount;
    private int sleepTime;

    /**
     * Constructor
     *
     * @param maxThreadsCount        the threads count that is max to be processed for this TaskProcessor
     * @param timeForUpdateLoadValue the time between checking of load value
     * @param groupName              the name of thread that will be open for this task
     */
    protected LoadBalancingTaskProcessor(int maxThreadsCount, int timeForUpdateLoadValue, String groupName, int sleepTime)
    {
        this.timeToUpdateLoadValue = timeForUpdateLoadValue;
        this.maxThreadsCount = maxThreadsCount;
        this.groupName = groupName;
        processors = new ArrayList<>();
        processorsPool = new ConcurrentLinkedQueue<>();
        this.sleepTime = sleepTime;
        checker = new LoadChecker(groupName + " load checker");
    }

    @Override
    public void pause()
    {
        for (int i = 0; i < processors.size(); i++)
        {
            processors.get(i).pause();
        }
    }

    @Override
    public void resume()
    {
        for (int i = 0; i < processors.size(); i++)
        {
            processors.get(i).resume();
        }
    }

    @Override
    public void start()
    {
        checker.start();
        addWorker();
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

    /**
     * The task that will performing in threads
     */
    protected abstract void process();

    /**
     * @return the value to determine load value of current processor
     */
    protected abstract int getLoadValue();

    private void addWorker()
    {
        DiagnosticallyTaskProcessor processor;
        if (processorsPool.isEmpty())
        {
            processor = new DiagnosticallyTaskProcessor(groupName, sleepTime)
            {
                @Override
                public String getGroupName()
                {
                    return groupName;
                }

                @Override
                protected void taskCycle()
                {
                    process();
                }
            };
            processor.start();
        } else
        {
            processor = processorsPool.poll();
            processor.resume();
        }
        processors.add(processor);
    }

    private void stopWorker()
    {
        if (processors.size() > 1)
        {
            int last = processors.size() - 1;
            DiagnosticallyTaskProcessor processor = processors.remove(last);
            if (processorsPool.size() > MAX_POOL_SIZE)
            {
                processor.stop();
            } else
            {
                processor.pause();
                processorsPool.add(processor);
            }
        }
    }
}
