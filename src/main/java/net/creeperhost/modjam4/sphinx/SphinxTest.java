package net.creeperhost.modjam4.sphinx;

import edu.cmu.sphinx.api.LiveSpeechRecognizer;
//import edu.cmu.sphinx.frontend.util.Microphone;
//import edu.cmu.sphinx.recognizer.Recognizer;
//import edu.cmu.sphinx.result.Result;
//import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;

/**
 * Created by Minion 2 on 15/05/2014.
 */
public class SphinxTest {

    public static String configLoc = "sphinx.config.xml";
    public static LiveSpeechRecognizer liverecog = null;
    public static boolean firststart = true;
    public static void test()
    {
        Configuration configuration = new Configuration();

        // Set path to acoustic model.
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/acoustic/wsj_8kHz");
        // Set path to dictionary.
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/acoustic/wsj_8kHz/dict/cmudict.0.6d");
        // Set language model.
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/language/en-us.lm.dmp");
        try {
            liverecog = new LiveSpeechRecognizer(configuration);
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        if(liverecog!=null) {
            liverecog.startRecognition(true);
        }
      // loop the recognition until the programm exits.
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(!firststart) liverecog.startRecognition(false);
                    SpeechResult result = liverecog.getResult();
                    processResult(result);
                    liverecog.stopRecognition();
                    firststart=false;
                }
            }

        };

        new Thread(runnable).start();
    }
    public static void processResult(SpeechResult output)
    {
        System.out.println("We need to do something with output.getWords()");
        //Need to do something with output.getWords();
    }

}
