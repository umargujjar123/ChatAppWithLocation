package comalpha4.chatappwithlocation;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Adnan Bashir manak on 17,May,2023
 * AIS company,
 * Krachi, Pakistan.
 */
public class LoggerGenratter {
    private static LoggerGenratter logger;

    public static LoggerGenratter getInstance() {
        if (logger==null) {
            logger = new LoggerGenratter();
            return logger;
        }else
        {
            return logger;
        }

    }

    public void showToast(Context context,String message){
        if (BuildConfig.DEBUG)
        {
            try {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            }catch (Exception e)
            {
                printLog("TOAST ERROR",e.getMessage());
            }

        }

    }
    public void  printLog(String tag,String message){
        try {
            if (BuildConfig.DEBUG) {
                Log.println(Log.DEBUG,tag, ".\n#########################################\n" +
                        "##\n" +
                        "##         TAG     : " + tag + "" +
                        "\n##\n" +
                        "##         Time    : " + "" +
                        "\n##\n" +
                        "##         Message :" + message + "" +
                        "\n##\n" +
                        "##\n" +
                        "##\n" +
                        "############################################")
                ;
            }
        }catch (Exception e)
        {
          Log.d("PRINT LOG",e.getMessage())  ;
        }

    }
}
