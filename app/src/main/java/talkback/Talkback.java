package talkback;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Technow i3 on 30/03/2016.
 */
public class Talkback implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private Context context;

    public Talkback(Context context) {
        this.context = context;
        tts = new TextToSpeech(context, this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void comunicar(String titulo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(titulo, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(titulo, TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale locale = new Locale("es", "ES");
            if (tts.isLanguageAvailable(locale) == TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(locale);
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(context, "Error intentando comunicar", Toast.LENGTH_LONG).show();
        }
    }

    public void shutdown(){
        tts.shutdown();
    }
}
