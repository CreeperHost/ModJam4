package net.creeperhost.modjam4.sphinx;



import java.util.Collection;
import java.util.List;

/**
 * Created by Minion 2 on 15/05/2014.
 */
public class SphinxTest {

    public static void test() {

        voce.SpeechInterface.init("../lib", false, true,
                "../grammar", "digits");

        System.out.println("This is a speech recognition test. "
                + "Speak digits from 0-9 into the microphone. "
                + "Speak 'quit' to quit.");

        boolean quit = false;

      // loop the recognition until the programm exits.
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // Normally, applications would do application-specific things
                    // here.  For this sample, we'll just sleep for a little bit.
                    try
                    {
                        Thread.sleep(200);
                    }
                    catch (InterruptedException e)
                    {
                    }

                    while (voce.SpeechInterface.getRecognizerQueueSize() > 0)
                    {
                        String s = voce.SpeechInterface.popRecognizedString();


                        System.out.println("You said: " + s);
                        //voce.SpeechInterface.synthesize(s);
                    }
                }
            }

        };

        new Thread(runnable).start();

    }
}
