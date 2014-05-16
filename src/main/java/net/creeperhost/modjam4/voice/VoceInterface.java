package net.creeperhost.modjam4.voice;



import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import java.io.*;

/**
 * Created by Minion 2 on 15/05/2014.
 */
public class VoceInterface {
    public static Thread commandprocessor = null;

    public static void init() {

        //Extract file from zip. We can actually use this same principle to download a file and write to disk, too!

        try {
            new File("./config/voice/").mkdirs();
            InputStream in = VoceInterface.class.getResourceAsStream("digits.gram");
            OutputStream out = new FileOutputStream(new File("./config/Voice/digits.gram"));
            byte[] buffer = new byte[10768];
            int len;

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();

            System.out.println("Written File");

            in = VoceInterface.class.getResourceAsStream("voce.config.xml");
            out = new FileOutputStream(new File("./config/Voice/voce.config.xml"));
            buffer = new byte[10768];

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        voce.SpeechInterface.init("./config/voice", false, true,
                "./config/voice/", "digits");



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
                        System.out.println(s);
                        //Command long enough, let's check if the command is the stop command

                        if((commandprocessor != null) && (commandprocessor.isAlive()) && (s.substring(0, 5).equals("steve") && s.contains("stop")))
                        {
                            //Let's stop everything we're doing
                            commandprocessor.interrupt();
                        } else {
                            //Lets migrate jump to make it work while your walking!
                            if(s.contains("jump")) {
                                try {
                                    KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode(), true);
                                    Thread.sleep(100);
                                    KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode(), false);
                                } catch (Exception e) {

                                }
                            } else if (s.contains("turn")) {
                                try {
                                    int turnTime = 2000;
                                    int sleepTime = 10;
                                    int time = 0;

                                    int changeAmount;
                                    if (s.contains("left")) {
                                        changeAmount = -600;
                                    } else if (s.contains("right")) {
                                        changeAmount = 600;
                                    } else {
                                        changeAmount = 1200;
                                    }

                                    int changeAmountIncremental = changeAmount / (turnTime / 50);

                                    while (time < turnTime)
                                    {
                                        time = time + 50;
                                        Thread.sleep(10);
                                        Minecraft.getMinecraft().thePlayer.setAngles(changeAmountIncremental, 0);

                                    }
                                } catch (Exception e)
                                {

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