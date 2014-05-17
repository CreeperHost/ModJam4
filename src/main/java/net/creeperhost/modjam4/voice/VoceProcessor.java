package net.creeperhost.modjam4.voice;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import voce.SpeechSynthesizer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul on 16/05/2014.
 */
public class VoceProcessor extends Thread {

    public static String _s = "";

    public VoceProcessor(String s)
    {
        _s = s;//Collect the text version of voice input into a variable the thread can access safely.
    }
    public void run()
    {
        //Grab basic movement initiator and send command onto correct function.
        if (_s.substring(0, 5).equals("steve")) {
            //Catch ALL the exceptions!
            try {
                coreControls(_s);
            }
            catch (AWTException e)
            {

            }
            return;
        }
        //Advanced commands outside base requirements, usable only with glasses
        if (((_s.length() >= 7) && _s.substring(0, 7).equals("heroine")) || ((_s.length() >= 9) && _s.substring(0, 9).equals("hero brine"))) {
            additionalControls(_s);
            return;
        }
    }
    public static KeyBinding keypress;

    public static Minecraft mc = Minecraft.getMinecraft();

    static Map<String, Integer> spkntoint = new HashMap<String, Integer>();

    static {
        spkntoint.put("one", 1);
        spkntoint.put("two", 2);
        spkntoint.put("three", 3);
        spkntoint.put("four", 4);
        spkntoint.put("five", 5);
        spkntoint.put("six", 6);
        spkntoint.put("seven", 7);
        spkntoint.put("eight", 8);
        spkntoint.put("nine", 9);
        spkntoint.put("ten", 10);
        spkntoint.put("eleven", 11);
        spkntoint.put("twelve", 12);
        spkntoint.put("thirteen", 13);
        spkntoint.put("fourteen", 14);
        spkntoint.put("fifteen", 15);
        spkntoint.put("sixteen", 16);
        spkntoint.put("seventeen", 17);
        spkntoint.put("eighteen", 18);
        spkntoint.put("nineteen", 19);
        spkntoint.put("twenty", 20);
        spkntoint.put("thirty", 30);
        spkntoint.put("forty", 40);
        spkntoint.put("fifty", 50);
        spkntoint.put("sixty", 60);
        spkntoint.put("seventy", 70);
        spkntoint.put("eighty", 80);
        spkntoint.put("ninety", 90);
    }

    public static int number_spoken_to_int(String number) { return spkntoint.containsKey(number) ? spkntoint.get(number) : 0; }
    public synchronized void coreControls(String command) throws AWTException
    {
        //Movement and game controls
        //Input emulation, need to fetch Minecraft key bindings and adjust as required
        int length = 350;//Need to calculate length of time holding a key to pass 1 block
        int number = 0;
        String[] data = command.split(" ");
        number = number_spoken_to_int(data[(data.length-1)]);

        if (command.contains("forward")) {
            keypress = mc.gameSettings.keyBindForward;
        }
        else if (command.contains("backward")) {
            keypress = mc.gameSettings.keyBindBack;
        }
        else if (command.contains("left")) {
            keypress = mc.gameSettings.keyBindLeft;
        }
        else if (command.contains("right")) {
            keypress = mc.gameSettings.keyBindRight;
        }
        else if(command.contains("mine"))
        {
            keypress = mc.gameSettings.keyBindAttack;
        }
        else if(command.contains("use")) {
            try {
                if (number == 0) number++;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                Thread.sleep(number * 50);//Amount of seconds to hold the left button
            } catch (Exception e) {

            } finally {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            }
        }
        else if(command.contains("menu")) {
            Robot simulator = new Robot();
            try {
                simulator.keyPress(KeyEvent.VK_ESCAPE);
                Thread.sleep(20);
                simulator.keyRelease(KeyEvent.VK_ESCAPE);
            } catch (Exception e) {

            } finally {
                simulator.keyRelease(KeyEvent.VK_ESCAPE);
            }
        }
        else if(command.contains("walk") || command.contains("mine")) {
            try {
                if(number >= 1) { // If you've asked for "a little" or "a bit" or "one"
                    KeyBinding.setKeyBindState(keypress.getKeyCode(), true);
                    Thread.sleep(length*(number*2));
                    KeyBinding.setKeyBindState(keypress.getKeyCode(), false);
                } else {
                    while (!isInterrupted()) {//The normal loop for walking
                        KeyBinding.setKeyBindState(keypress.getKeyCode(), true);
                        Thread.sleep(length);
                        KeyBinding.setKeyBindState(keypress.getKeyCode(), false);
                    }
                }
            } catch (Exception e) {

            } finally {
                KeyBinding.setKeyBindState(keypress.getKeyCode(), false);
            }
        }
        else if(command.contains("select")) {

            number = number -1;
            mc.thePlayer.inventory.currentItem = number; // not thread safe but fuck it for now

        }
    }

    public synchronized void additionalControls(String command)
    {
        if(command.length() <= 7 || !command.contains(" ")) return; //Enough of recognizing just 'heroine', kthxbai
        String[] data = command.split(" ");
        command = command.replace(data[0],"");
        voce.SpeechInterface.synthesize("I am sorry, I do not understand "+ command);
        //Jarvis like functions
    }
}

