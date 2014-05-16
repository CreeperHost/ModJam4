package net.creeperhost.modjam4.voice;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

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
    public synchronized void coreControls(String command) throws AWTException
    {
        //Movement and game controls
        //Input emulation, need to fetch Minecraft key bindings and adjust as required
        int length = 350;//Need to calculate length of time holding a key to pass 1 block
        int number = 1;
        String[] data = command.split(" ");
        if(data[(data.length-1)].equals("one")) number = 1;
        if(data[(data.length-1)].equals("two")) number = 2;
        if(data[(data.length-1)].equals("three")) number = 3;
        if(data[(data.length-1)].equals("four")) number = 4;
        if(data[(data.length-1)].equals("five")) number = 5;
        if(data[(data.length-1)].equals("six")) number = 6;
        if(data[(data.length-1)].equals("seven")) number = 7;
        if(data[(data.length-1)].equals("eight")) number = 8;
        if(data[(data.length-1)].equals("nine")) number = 9;
        if(data[(data.length-1)].equals("ten")) number = 10;
        if(data[(data.length-1)].equals("fifteen")) number = 15;
        if(data[(data.length-1)].equals("twenty")) number = 20;
        if(data[(data.length-1)].equals("thirty")) number = 30;
        if(data[(data.length-1)].equals("forty")) number = 40;
        if(data[(data.length-1)].equals("fifty")) number = 50;
        if(data[(data.length-1)].equals("sixty")) number = 60;
        if(data[(data.length-1)].equals("seventy")) number = 70;
        if(data[(data.length-1)].equals("eighty")) number = 80;
        if(data[(data.length-1)].equals("ninety")) number = 90;
        if (command.contains("forward")) {
            keypress = KeyEvent.VK_W;
        }
        if (command.contains("backward")) {
            keypress = KeyEvent.VK_S;
        }
        if (command.contains("menu")) {
            keypress = KeyEvent.VK_ESCAPE;
        }
        if (command.contains("left")) {
            keypress = KeyEvent.VK_A;
        }
        if (command.contains("right")) {
            keypress = KeyEvent.VK_D;
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
                simulator.keyPress(KeyEvent.VK_ESCAPE);
                Thread.sleep(20);
                simulator.keyRelease(KeyEvent.VK_ESCAPE);
            } catch (Exception e) {

            } finally {
                simulator.keyRelease(KeyEvent.VK_ESCAPE);
            }
        }
        if(command.contains("walk")) {
            Robot simulator = new Robot();
            try {
                int wln = 0;
                while((wln < number)&&(!isInterrupted())) {
                    simulator.keyPress(keypress);
                    Thread.sleep(length);
                    simulator.keyRelease(keypress);
                    wln++;
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
        if(command.contains("jump")) {
            Robot simulator = new Robot();
            try {
                int jcnt=0;
                while((jcnt < number)&&(!isInterrupted())) {
                    simulator.keyPress(keypress);
                    Thread.sleep(length);
                    simulator.keyPress(KeyEvent.VK_SPACE);
                    Thread.sleep(length);
                    simulator.keyRelease(KeyEvent.VK_SPACE);
                    Thread.sleep(length);
                    simulator.keyRelease(keypress);
                    jcnt++;
                }
            } catch (Exception e) {

            } finally {
                simulator.keyRelease(keypress);
                simulator.keyRelease(KeyEvent.VK_SPACE);
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

