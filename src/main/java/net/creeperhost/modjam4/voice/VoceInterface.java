package net.creeperhost.modjam4.voice;



import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.settings.KeyBinding;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Created by Minion 2 on 15/05/2014.
 */
public class VoceInterface {
    public static Thread commandprocessor = null;

    public static KeyBinding forward;
    public static KeyBinding back;
    public static KeyBinding left;
    public static KeyBinding right;
    public static KeyBinding jump;
    public static KeyBinding attack;
    public static KeyBinding use;
    public static KeyBinding select1;
    public static KeyBinding select2;
    public static KeyBinding select3;
    public static KeyBinding select4;
    public static KeyBinding select5;
    public static KeyBinding select6;
    public static KeyBinding select7;
    public static KeyBinding select8;
    public static KeyBinding select9;
    public static KeyBinding select10;

    public static void init() {

        List<KeyBinding> keys = ObfuscationReflectionHelper.getPrivateValue(KeyBinding.class, null, "keybindArray");

        for (KeyBinding key : keys)
        {
            System.out.println(key.getKeyDescription() + " " + key.getKeyCategory() + " " + key.getKeyCode());
            if (key.getKeyDescription().equals("key.forward"))
                forward = key;
            else if (key.getKeyDescription().equals("key.back"))
                back = key;
            else if (key.getKeyDescription().equals("key.left"))
                left = key;
            else if (key.getKeyDescription().equals("key.right"))
                right = key;
            else if (key.getKeyDescription().equals("key.jump"))
                jump = key;
            else if (key.getKeyDescription().equals("key.attack"))
                attack = key;
            else if (key.getKeyDescription().equals("key.hotbar.1"))
                select1 = key;
            else if (key.getKeyDescription().equals("key.hotbar.2"))
                select2 = key;
            else if (key.getKeyDescription().equals("key.hotbar.3"))
                select3 = key;
            else if (key.getKeyDescription().equals("key.hotbar.4"))
                select4 = key;
            else if (key.getKeyDescription().equals("key.hotbar.5"))
                select5 = key;
            else if (key.getKeyDescription().equals("key.hotbar.6"))
                select6 = key;
            else if (key.getKeyDescription().equals("key.hotbar.7"))
                select7 = key;
            else if (key.getKeyDescription().equals("key.hotbar.8"))
                select8 = key;
            else if (key.getKeyDescription().equals("key.hotbar.9"))
                select9 = key;
            else if (key.getKeyDescription().equals("key.hotbar.10"))
                select10 = key;
            else if (key.getKeyDescription().equals("key.use"))
                use = key;
        }

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
                                    KeyBinding.setKeyBindState(jump.getKeyCode(), true);
                                    Thread.sleep(100);
                                    KeyBinding.setKeyBindState(jump.getKeyCode(), false);
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