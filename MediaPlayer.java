import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MediaPlayer implements LineListener {
    // Private class variable to check if media has finished playing
    private static boolean mediaFinished;

    // Play media from the specified file
    public static void play(File mediaFile)
    {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(mediaFile);
            
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip audioClip = (Clip)AudioSystem.getLine(info);

            audioClip.open(audioStream);
            audioClip.start();

            // Delay until media has finished playing
            while(!(mediaFinished)) 
            {
                if(!(FileHandler.currentMode.equals("Zen Mode")))
                {
                    break;
                }

                Thread.sleep(1000);

                // Delay an extra 5 seconds when switching songs
                if(mediaFinished)
                {
                    Thread.sleep(5000);
                }
            }

            audioClip.close();
        } 
        catch (UnsupportedAudioFileException e1) {
            e1.printStackTrace();
        } catch (LineUnavailableException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        } catch (InterruptedException e4) {
            e4.printStackTrace();
        }
    }

    // Overrided LineEventListener.update() method to check playing time of media
    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
         
        if (type == LineEvent.Type.START) {
            System.out.println("Playback started.");
             
        } else if (type == LineEvent.Type.STOP) {
            mediaFinished = true;
            System.out.println("Playback completed.");
        }
    }
}
