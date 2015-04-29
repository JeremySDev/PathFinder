package cs497.cs.wcu.edu.pathfinder;
/**� 20011 CEMAS � Centre for Excellence in Mobile Applications and Services**/
/*
* Revision: 1.0
* Author: Andrew Scott
* Date: 6/6/2011
* Description: Provided the ability to globally and easily play the 
* notification sound for various user informing purposes.
* 
* Revision: 1.1
* Author: Andrew Scott
* Date: 20/6/2011
* Description: Added the ability to play predefined sound files that
* are incorporated into the raw folder of the Android project.
* 
* Revision: 1.1
* Author: Andrew Scott
* Date: 5/9/2011
* Description: Added further sounds and the ability to loop a sound
* until requested to stop.
*/

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;

//#####################################################################

/**
 * A collection of simple static methods for playing a sound file.
 */
//#####################################################################
public class SoundPlayer
{

    /*public static final int SOUND_BLIP1 = R.raw.blip1;//Woowap - High frq
    public static final int SOUND_BLIP2 = R.raw.blip2;//Woowap - Low frq
    public static final int SOUND_BLIP3 = R.raw.blip3;//Didle-de
    public static final int SOUND_BLIP4 = R.raw.blip4;//beep beep
    public static final int SOUND_BLIP5 = R.raw.blip5;//Single beep - high frq
    public static final int SOUND_BLIP6 = R.raw.blip6;//Single beep low frq
    public static final int SOUND_BLIP7 = R.raw.blip7;//Three thick high frq beeps
    public static final int SOUND_BLIP8 = R.raw.blip1;
    public static final int SOUND_BLIP9 = R.raw.blip2;

    //public static final int SOUND_BELL1 = ;
    public static final int SOUND_FANFAIRE = R.raw.fanfare;
    public static final int SOUND_BUS_STOP = R.raw.bus;
    public static final int SOUND_SPEAK_NOW = R.raw.speak_now;
    public static final int SOUND_PROC_SOUND = R.raw.proc_sound2;
    public static final int SOUND_PROC_SOUND1 = R.raw.proc_sound1;*/

    private static MediaPlayer mp;

    //========================================================================

    /**
     * Makes notification sound. Used to alert the user of important events.
     */
    //=========================================================================
    public static void makeNotificationSound(Context context)
    {

        //Context context = AppConstraints.getInstance();

        Uri alert = RingtoneManager.getDefaultUri
                (RingtoneManager.TYPE_NOTIFICATION);


        mp = new MediaPlayer();

        try
        {
            mp.setDataSource(context, alert);
            //player.create(this, alert);
            final AudioManager audioManager =
                    (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0)
            {
                mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                mp.setLooping(false);
                mp.prepare();
                mp.start();
            }//end if
        }//end try
        catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
        }// catch

    }//======================================================================

    //========================================================================

    /**
     * Plays the sound specified in soundID
     */
    //=========================================================================
    public static void makeSound(int soundID, Context context)
    {
        //	 if (soundID == SOUND_BLIP1 || soundID == SOUND_BLIP2) return;

        try
        {
            //Context context = AppConstraints.getInstance();
            mp = MediaPlayer.create(context, soundID);
            mp.start();
        }//end try
        catch (Exception e)
        {
            e.printStackTrace();
        }//end catch

    }//======================================================================

    //=======================================================================

    /**
     * Play a sound that loops. To stop playing the sound use the  metod
     * stopLoopedSound().
     *
     * @param soundID The ID of the sound to be looped.
     */
    //=======================================================================
    public static void playLoopedSound(int soundID, Context context)
    {
        try
        {
            mp = MediaPlayer.create(context, soundID);
            mp.setLooping(true);
            mp.start();
        }//end try
        catch (Exception e)
        {
            e.printStackTrace();
        }//end catch
    }//======================================================================


    //=======================================================================

    /**
     * Stop playing looped sound.
     */
    //=======================================================================
    public static void stopLoopedSound()
    {
        try
        {
            if (mp != null && mp.isPlaying())
            {
                mp.stop();
                mp.release();
            }//end if
        }//end try
        catch (IllegalStateException ex)
        {
            ex.printStackTrace();
        }//end catch
    }//=======================================================================


    /**
     * Makes the phone vibrate for the specfied time providing the vibrate
     * permissions are enabled in the manifest.
     */
    /*public static void vibrate(int timeMs, Context context)
    {

        //Context context = AppConstraints.getInstance();
        try
        {
            Vibrator vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(timeMs);
        }//end catch
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }//end catch
    }*///======================================================================
}//#####################################################################
