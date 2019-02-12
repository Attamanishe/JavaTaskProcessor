package processors.base;

import java.util.ArrayList;
import java.util.List;

/*
<h1>ProcessorManager</h1>
The processor manager knows about all processors and can stop them
 */
public class ProcessorsManager
{
    private static ProcessorsManager ourInstance = new ProcessorsManager();

    public static ProcessorsManager getInstance()
    {
        return ourInstance;
    }

    private List<IProcessor> processors;

    private ProcessorsManager()
    {
        processors = new ArrayList<>();
    }

    public void addProcessor(IProcessor processor)
    {
        processors.add(processor);
    }
}
