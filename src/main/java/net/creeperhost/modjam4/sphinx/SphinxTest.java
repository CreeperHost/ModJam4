package net.creeperhost.modjam4.sphinx;



import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

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
                        //Advanced commands outside base requirements, usable only with glasses
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
    public static int keypress = 0;
    public static void coreControls(String command)
    {
        //Movement and game controls
        //Input emulation, need to fetch Minecraft key bindings and adjust as required
        int length = 350;
        int number = 1;
        String[] data = command.split(" ");
        System.out.println(data[(data.length-1)]);
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
        length = length*number;
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
            try {
                Robot simulator = new Robot();
                simulator.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
                Thread.sleep(number*1000);//Amount of seconds to hold the left button
                simulator.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
                simulator.keyRelease(keypress);
            } catch (Exception e) {

            }
        }
        if(command.contains("walk")) {
            try {
                Robot simulator = new Robot();
                simulator.keyPress(keypress);
                Thread.sleep(length);
                simulator.keyRelease(keypress);
            } catch (Exception e) {

            }
        }
        if(command.contains("select")) {
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
                Robot simulator = new Robot();
                simulator.keyPress(keypress);
                Thread.sleep(10);
                simulator.keyRelease(keypress);
            } catch (Exception e) {

            }
        }
        if(command.contains("jump")) {
            try {
                Robot simulator = new Robot();
                simulator.keyPress(keypress);
                Thread.sleep(length);
                simulator.keyPress(KeyEvent.VK_SPACE);
                Thread.sleep(length);
                simulator.keyRelease(KeyEvent.VK_SPACE);
                Thread.sleep(length);
                simulator.keyRelease(keypress);
            } catch (Exception e) {

            }
        }
    }
    public static void additionalControls(String command)
    {
        System.out.println("Additional functions: " + command);
        //Jarvis like functions
    }
}
