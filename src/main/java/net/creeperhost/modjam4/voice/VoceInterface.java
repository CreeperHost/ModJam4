package net.creeperhost.modjam4.voice;



import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Created by Minion 2 on 15/05/2014.
 */
public class VoceInterface {
    public static Thread commandprocessor = null;
    public static void init() {

        voce.SpeechInterface.init("../libs", false, true,
                "../grammar", "digits");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //Loop until Minecraft exits.
                while (true) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                    if(commandprocessor != null) commandprocessor.getName();
                    while (voce.SpeechInterface.getRecognizerQueueSize() > 0) {
                        //Grab what has been said and return a string
                        String s = voce.SpeechInterface.popRecognizedString();
                        //Let's ignore it if it's shorter than any of our initiation strings (Steve and Herobrine).
                        if (s.length() < 5) continue;
                        //Command long enough, let's check if the command is the stop command

                        if((commandprocessor != null) && (commandprocessor.isAlive()) && (s.substring(0, 5).equals("steve") && s.contains("stop")))
                        {
                            //Let's stop everything we're doing
                            commandprocessor.interrupt();
                        } else {
                            //Lets migrate jump to make it work while your walking!
                            if(s.contains("jump")) {
                                try {
                                    Robot simulator = new Robot();
                                    simulator.keyPress(KeyEvent.VK_SPACE);
                                    Thread.sleep(200);
                                    simulator.keyRelease(KeyEvent.VK_SPACE);
                                } catch (Exception e) {

                                }
                            }
                            if(commandprocessor != null && commandprocessor.isAlive()) continue; //We probably shouldn't try and create a new VoceProcesor if the previous one is still running...
                            //Spawn us a thread to handle the actual processing so we can continue to monitor here for 'stop'
                            commandprocessor = new VoceProcessor(s);
                            commandprocessor.start();
                        }
                    }
                }
            }

        };
        new Thread(runnable).start();
    }
}