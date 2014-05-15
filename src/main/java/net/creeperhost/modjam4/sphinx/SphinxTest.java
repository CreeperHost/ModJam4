package net.creeperhost.modjam4.sphinx;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

/**
 * Created by Minion 2 on 15/05/2014.
 */
public class SphinxTest {

    public static String configLoc = "sphinx.config.xml";

    public static void test()
    {
        ConfigurationManager cm = new ConfigurationManager (SphinxTest.class.getResource(configLoc));
        Recognizer recognizer = (Recognizer) cm.lookup ("recognizer");
        recognizer.allocate ();

        Microphone microphone = (Microphone) cm.lookup("microphone");

        if (!microphone.startRecording()) {
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
            System.exit(1);
        }

    }

}
