package net.creeperhost.harken.voice;

import net.creeperhost.harken.MCBridge.MCInformation;
import net.creeperhost.harken.handler.SoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul on 16/05/2014.
 */
public class VoceProcessor extends Thread {

    public String _s = "";
    public Boolean _wearing = false;

    public VoceProcessor(String s, Boolean wearing)
    {
        _s = s;//Collect the text version of voice input into a variable the thread can access safely.
        _wearing = wearing;//If the user is wearing their glasses
    }

    public void run()
    {
        if (MCInformation.isMainMenu()) return;

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
        if(!_wearing) return;
        //Advanced commands outside base requirements, usable only with glasses
        if (((_s.length() >= 7) && _s.substring(0, 7).equals("heroine")) || ((_s.length() >= 9) && _s.substring(0, 9).equals("hero brine")) || ((_s.length() >= 9) && _s.substring(0, 9).equals("hero bream"))) {
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
        keypress = mc.gameSettings.keyBindForward;
        if (command.contains("backward")) {
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
    public boolean isSassy()
    {
        return (Math.random() * 100) > 80 ? true : false;
    }
    public synchronized void additionalControls(String command)
    {
        System.out.println(MCInformation.canMineBlock());
        if(command.length() <= 7 || !command.contains(" ")) return; //Enough of recognizing just 'heroine', kthxbai
        String[] data = command.split(" ");
        command = command.replace(data[0],"");
        if(command.contains("weather"))
        {
            if (!isSassy()) {
                playSound("herobrine.weather.prefix");
                playSound("herobrine.weather."+ ((MCInformation.biome != null && MCInformation.biome.equals("Desert")) ? 0 : MCInformation.weather.ordinal()));
            } else {
                playSound("herobrine.weather.sassy");
            }
            return;
        }
        if(command.contains("where am i")||command.contains("location")||command.contains("ordinates"))
        {
            playSound("herobrine.misc.x");
            speakNumber(MCInformation.x);
            playSound("herobrine.misc.y");
            speakNumber(MCInformation.y);
            playSound("herobrine.misc.z");
            speakNumber(MCInformation.z);
            return;
        }
        playSound("herobrine.fail."+randInt(1,5));
        //Jarvis like functions
    }
    public static int randInt(int min, int max) {
        java.util.Random rand = new java.util.Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    public static void playerDead()
    {
        playSound("herobrine.death."+randInt(1,5));
    }
    public boolean speakNumber(int Number)
    {
        try {
            if (Number < 0)
            {
                playSound("herobrine.numbers.minus");
                Number = -Number;
            }
            int ones, tens, hundreds, thousands = 0;
            thousands = (Number / 1000) % 10;
            hundreds = ((Number / 100) % 100) % 10;
            if (thousands != 0) {
                playSound("herobrine.numbers." + thousands);
                playSound("herobrine.misc.thousand");
                if (hundreds == 0) playSound("herobrine.trivial.and");
            }
            if (hundreds != 0) {
                playSound("herobrine.numbers." + hundreds);
                playSound("herobrine.misc.hundred");
                playSound("herobrine.trivial.and");
            }
            tens = (Number / 10) % 10;
            if (tens != 0) {
                playSound("herobrine.numbers." + tens);
            }
            ones = Number % 10;
            if (ones != 0) {
                playSound("herobrine.numbers." + ones);
            }
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }
    public static boolean playSound(String path)
    {
        try {
            SoundHandler.onEntityPlay(path, 1.5F, 1.5F);
        } catch(Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}

