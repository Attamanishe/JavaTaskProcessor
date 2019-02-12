package processors.diagnostically;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
<h1>DiagnosticsTaskProcessorsManager</h1>
The manager knows about all diagnostically processors and can get their load value
 */
public class DiagnosticsTaskProcessorsManager
{
    private static DiagnosticsTaskProcessorsManager ourInstance = new DiagnosticsTaskProcessorsManager();

    public static DiagnosticsTaskProcessorsManager getInstance()
    {
        return ourInstance;
    }

    private Map<String, List<IDiagnostically>> diagnosticallyProcessors;

    private DiagnosticsTaskProcessorsManager()
    {
        diagnosticallyProcessors = new ConcurrentHashMap<>();
    }

    public void addProcessor(IDiagnostically diagnostically, String diagnosticallyGroup)
    {
        if (!diagnosticallyProcessors.containsKey(diagnosticallyGroup))
        {
            diagnosticallyProcessors.put(diagnosticallyGroup, new ArrayList<>());
        }
        diagnosticallyProcessors.get(diagnosticallyGroup).add(diagnostically);
    }

    /**
     * @return the array that contains string information about load value of every diagnostically group
     */
    public List<String> getDiagnostics()
    {
        ArrayList<String> diagnostics = new ArrayList<>();
        for (String group : diagnosticallyProcessors.keySet())
        {
            List<IDiagnostically> diagnosticians = diagnosticallyProcessors.get(group);
            long allTimes = 0;
            for (int i = diagnosticians.size() - 1; i >= 0; i--)
            {
                if (!diagnosticians.get(i).isWorking())
                {
                    diagnosticallyProcessors.get(group).remove(i);
                } else
                {
                    allTimes += diagnosticians.get(i).getPerformTime();
                }
            }
            diagnostics.add(group + "(" + diagnosticallyProcessors.get(group).size() + "): " +
                    allTimes / (diagnosticallyProcessors.get(group).size() == 0 ? 1 : diagnosticallyProcessors.get(group).size()));
        }
        return diagnostics;
    }
}
