package com.mmp.mmp.util;

/**
 * Created by DELL on 2018/6/12.
 */

public class PlayTimer {
    final private static String splash =":";
    private static StringBuilder s_time =new StringBuilder("00"+splash+"00");
    private static long hours = 0;
    private static long  minutes = 0;
    private static long seconds =0;
    public synchronized static String timeTo(long time){
        synchronized (s_time) {
            hours = time/3600;
            minutes = (time/60)%60;
            seconds = time%60;
            if(hours>0){
                if(hours<10) {
                    if(s_time.length()<7) {
                        s_time.insert(0, ("0" + hours + splash));
                    }
                    else {
                        s_time.replace(0, s_time.indexOf(splash), "0" + hours);
                    }
                }else{
                    if(s_time.length()<7) {
                        s_time.insert(0, "0" + hours + splash);
                    }
                    else {
                        s_time.replace(0, s_time.indexOf(splash), Long.toString(hours));
                    }
                }
            }else{
                if(s_time.length()>5){
                    s_time.delete(0,s_time.indexOf(splash)+1);
                    //   Log.d("PlayTimer","here is >>>>>>>>>>>>>>>");
                }
            }
            int lst = s_time.lastIndexOf(splash);
            if (minutes < 10) {
                s_time.replace(lst - 2, lst, "0" + minutes);
            }
            else {
                s_time.replace(lst - 2, lst, Long.toString(minutes));
            }
            if (seconds < 10) {
                s_time.replace(lst + 1, lst + 3, "0" + seconds);
            }
            else {
                s_time.replace(lst + 1, lst + 3, Long.toString(seconds));
            }
        }
        return s_time.toString();
    }
}
