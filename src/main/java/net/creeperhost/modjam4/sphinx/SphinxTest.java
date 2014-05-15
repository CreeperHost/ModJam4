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

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //Loop until Minecraft exits.
                while (true) {
                    try
                    {
                        Thread.sleep(200);
                    }
                    catch (InterruptedException e)
                    {
                    }

                    while (voce.SpeechInterface.getRecognizerQueueSize() > 0)
                    {
                        //Grab what has been said and return a string
                        String s = voce.SpeechInterface.popRecognizedString();
                        //Let's ignore it if it's shorter than either of our initiators.
                        if(s.length() < 5) continue;
                        //Grab basic movement initiator and send command onto correct function.
                        if(s.substring(0,5).equals("steve")) {
                            coreControls(s);
                            continue;
                        }
                        //Advanced commands outside base requirements, useable only with glasses
                        if(((s.length() >= 7) && s.substring(0,7).equals("heroine")) || ((s.length() >= 9) && s.substring(0,9).equals("hero brine"))){
                            additionalControls(s);
                            continue;
                        }
                    }
                }
            }

        };

        new Thread(runnable).start();

    }
    public static void coreControls(String command)
    {
        System.out.println("Core movement: " + command);
        //Movement and game controls
    }
    public static void additionalControls(String command)
    {
        System.out.println("Additional functions: " + command);
        //Jarvis like functions
    }
}
