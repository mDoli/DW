package stretl.etl.outputReady;

/**
 * OutputReady event listener.
 * @author Artur
 */
public interface IOutputReadyListener {

    /**
     * Update method from sender.
     * @param sender Event sender.
     * @param args Event args.
     */
    void outputReady(IOutputReadySender sender, OutputReadyArgs args);
}
