package processors.base;

public interface IProcessor
{
    /**
     * The method that stops processor
     */
    void stop();

    /**
     * The method that start processor
     */
    void start();

    /**
     * The method that pause processor
     */
    void pause();

    /**
     * The method that resume processor
     */
    void resume();
}
