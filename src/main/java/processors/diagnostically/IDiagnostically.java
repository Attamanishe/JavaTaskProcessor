package processors.diagnostically;

public interface IDiagnostically
{
    /**
     * @return time use for perform something
     */
    long getPerformTime();

    /**
     * @return name of diagnostically group
     */
    String getGroupName();

    /**
     * @return true if processor is working
     */
    boolean isWorking();
}
