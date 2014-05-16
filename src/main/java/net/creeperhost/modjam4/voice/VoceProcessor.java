package net.creeperhost.modjam4.voice;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul on 16/05/2014.
 */
public class VoceProcessor extends Thread {
    public static String _s = "";
    public static byte forward = KeyEvent.VK_W;
    public static byte left = KeyEvent.VK_A;
    public static byte right = KeyEvent.VK_D;
    public static byte backward = KeyEvent.VK_S;
    public static byte jump = KeyEvent.VK_SPACE;
    public static byte menu = KeyEvent.VK_ESCAPE;
    public VoceProcessor(String s)
    {
        _s = s;//Collect the text version of voice input into a variable the thread can access safely.
    }
    public void run()
    {
        //Grab basic movement initiator and send command onto correct function.
        if (_s.substring(0, 5).equals("steve")) {
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
    public static int keypress = 0;
    public static int number_spoken_to_int(String number)
    {
        Map<String, Integer> tmp = new HashMap<String, Integer>();
        tmp.put("one", 1);
        tmp.put("two", 2);
        tmp.put("three", 3);
        tmp.put("four", 4);
        tmp.put("five", 5);
        tmp.put("six", 6);
        tmp.put("seven", 7);
        tmp.put("eight", 8);
        tmp.put("nine", 9);
        tmp.put("ten", 10);
        tmp.put("eleven", 11);
        tmp.put("twelve", 12);
        tmp.put("thirteen", 13);
        tmp.put("fourteen", 14);
        tmp.put("fifteen", 15);
        tmp.put("sixteen", 16);
        tmp.put("seventeen", 17);
        tmp.put("eighteen", 18);
        tmp.put("nineteen", 19);
        tmp.put("twenty", 20);
        tmp.put("thirty", 30);
        tmp.put("forty", 40);
        tmp.put("fifty", 50);
        tmp.put("sixty", 60);
        tmp.put("seventy", 70);
        tmp.put("eighty", 80);
        tmp.put("ninety", 90);
        return tmp.containsKey(number) ? tmp.get(number) : 0;
    }
    public synchronized void coreControls(String command) throws AWTException
    {
        //Movement and game controls
        //Input emulation, need to fetch Minecraft key bindings and adjust as required
        int length = 350;//Need to calculate length of time holding a key to pass 1 block
        int number = 0;
        String[] data = command.split(" ");
        number = number_spoken_to_int(data[(data.length-1)]);
        if (command.contains("forward")) {
            keypress = forward;
        }
        if (command.contains("backward")) {
            keypress = backward;
        }
        if (command.contains("menu")) {
            keypress = menu;
        }
        if (command.contains("left")) {
            keypress = left;
        }
        if (command.contains("right")) {
            keypress = right;
        }
        if(command.contains("hit"))
        {
            Robot simulator = new Robot();
            try {
                simulator.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
                Thread.sleep(number*1000);//Amount of seconds to hold the left button
            } catch (Exception e) {

            } finally {
                simulator.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
            }
        }
        if(command.contains("use")) {
            Robot simulator = new Robot();
            try {

                simulator.mousePress(MouseEvent.BUTTON2_DOWN_MASK);
                Thread.sleep(number*1000);//Amount of seconds to hold the left button
            } catch (Exception e) {

            } finally {
                simulator.mouseRelease(MouseEvent.BUTTON2_DOWN_MASK);
            }
        }
        if(command.contains("menu")) {
            Robot simulator = new Robot();
            try {
                simulator.keyPress(menu);
                Thread.sleep(20);
                simulator.keyRelease(menu);
            } catch (Exception e) {

            } finally {
                simulator.keyRelease(menu);
            }
        }
        if(command.contains("walk")) {
            Robot simulator = new Robot();
            try {
                int wln = 0;
                if(number >= 1) { // If you've asked for "a little" or "a bit" or "one"
                    simulator.keyPress(keypress);
                    Thread.sleep(length*(number*2));
                    simulator.keyRelease(keypress);
                } else {
                    while (!isInterrupted()) {//The normal loop for walking
                        simulator.keyPress(keypress);
                        Thread.sleep(length);
                        simulator.keyRelease(keypress);
                    }
                }
            } catch (Exception e) {

            } finally {
                simulator.keyRelease(keypress);
            }
        }
        if(command.contains("select")) {
            Robot simulator = new Robot();
            try {
                if(number == 1) keypress = KeyEvent.VK_1;
                if(number == 2) keypress = KeyEvent.VK_2;
                if(number == 3) keypress = KeyEvent.VK_3;
                if(number == 4) keypress = KeyEvent.VK_4;
                if(number == 5) keypress = KeyEvent.VK_5;
                if(number == 6) keypress = KeyEvent.VK_6;
                if(number == 7) keypress = KeyEvent.VK_7;
                if(number == 8) keypress = KeyEvent.VK_8;
                if(number == 9) keypress = KeyEvent.VK_9;
                simulator.keyPress(keypress);
                Thread.sleep(10);
                simulator.keyRelease(keypress);
            } catch (Exception e) {

            } finally {
                simulator.keyRelease(keypress);
            }
        }
    }
    public synchronized void additionalControls(String command)
    {
        if(command.length() <= 7 || !command.contains(" ")) return; //Enough of recognizing just 'heroine', kthxbai
        System.out.println("Additional functions: " + command);
        //Jarvis like functions
    }
}

