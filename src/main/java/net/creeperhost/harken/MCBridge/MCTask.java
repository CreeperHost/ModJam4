package net.creeperhost.harken.MCBridge;

/**
 * Created by Acer on 18/05/2014.
 */
public class MCTask {

    protected Object result;

    public void run()
    {
    }

    public boolean hasResult()
    {
        return getResult() != null;
    }

    public Object getResult()
    {
        return result;
    }

}
