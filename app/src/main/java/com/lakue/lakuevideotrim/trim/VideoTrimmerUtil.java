package com.lakue.lakuevideotrim.trim;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.lakue.lakuevideotrim.callback.SingleCallback;
import com.lakue.lakuevideotrim.thread.BackgroundExecutor;
import com.lakue.lakuevideotrim.util.DeviceUtil;
import com.lakue.lakuevideotrim.util.UnitConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

public class VideoTrimmerUtil {

    private static final String TAG = VideoTrimmerUtil.class.getSimpleName();
    public static final long MIN_SHOOT_DURATION = 3000L;// 동영상 최소길이
    public static final int VIDEO_MAX_TIME = 10;// 동영상 최대길이
    public static final long MAX_SHOOT_DURATION = VIDEO_MAX_TIME * 1000L;//동영상 최대길이

    public static final int MAX_COUNT_RANGE = 10;  //seekBar 영역에 사진 갯수
    private static final int SCREEN_WIDTH_FULL = DeviceUtil.getDeviceWidth();
    public static final int RECYCLER_VIEW_PADDING = UnitConverter.dpToPx(35);
    public static final int VIDEO_FRAMES_WIDTH = SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2;
    private static final int THUMB_WIDTH = (SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2) / VIDEO_MAX_TIME;
    private static final int THUMB_HEIGHT = UnitConverter.dpToPx(50);

    public static void trim(Context context, String inputFile, String outputFile, long startMs, long endMs, final VideoTrimListener callback) {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final String outputName = "trimmedVideo_" + timeStamp + ".mp4";
        outputFile = outputFile + "/" + outputName;

        Log.i("QWLKRJQLKWRJ" , "startMs" + startMs);
        Log.i("QWLKRJQLKWRJ" , "endMs" + endMs);
        String start = convertSecondsToMiliTime(startMs / 1000,String.valueOf(startMs));
        String end = convertSecondsToMiliTime(endMs / 1000,String.valueOf(endMs));
        String duration = convertSecondsToTime((endMs - startMs) / 1000);

        Log.i("VideoTrimmerView1", "start" + start);
        Log.i("VideoTrimmerView1", "duration" + duration);

        //String start = String.valueOf(startMs);
        //String duration = String.valueOf(endMs - startMs);

        /** 裁剪视频ffmpeg指令说明：
         * ffmpeg -ss START -t DURATION -i INPUT -codec copy -avoid_negative_ts 1 OUTPUT
         -ss 开始时间，如： 00:00:20，表示从20秒开始；
         -t 时长，如： 00:00:10，表示截取10秒长的视频；
         -i 输入，后面是空格，紧跟着就是输入视频文件；
         -codec copy -avoid_negative_ts 1 表示所要使用的视频和音频的编码格式，这里指定为copy表示原样拷贝；
         INPUT，输入视频文件；
         OUTPUT，输出视频文件
         */
        //TODO: Here are some instructions
        //https://trac.ffmpeg.org/wiki/Seeking
        //https://superuser.com/questions/138331/using-ffmpeg-to-cut-up-video

//    String cmd = "-ss " + start + " -t " + duration + " -accurate_seek" + " -i " + inputFile + " -codec copy -avoid_negative_ts 1 " + outputFile;
//    String cmd = "-ss " + start + " -i " + inputFile + " -to " + duration +  " -codec copy " + outputFile;
        String cmd = "-t##" +  end + "##-i##"  +inputFile +  "##-ss##" + start + "##" + outputFile;
        Log.i("QWLRKJWQRKL",cmd);
//        String cmd = "-t " +  "00:13.999" + " -i "  +inputFile +  " -ss " + "00:08.999" + " " + outputFile;

        //String cmd = "-ss " + start + " -i " + inputFile + " -ss " + start + " -t " + duration + " -vcodec copy " + outputFile;
        //{"ffmpeg", "-ss", "" + startTime, "-y", "-i", inputFile, "-t", "" + induration, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", outputFile}
        //String cmd = "-ss " + start + " -y " + "-i " + inputFile + " -t " + duration + " -vcodec " + "mpeg4 " + "-b:v " + "2097152 " + "-b:a " + "48000 " + "-ac " + "2 " + "-ar " + "22050 "+ outputFile;
        String[] command = cmd.split("##");
        for(int i=0;i<command.length;i++){
            Log.i("QWLKRJQLKWRJ" , "cmd[" + i + "]" + command[i]);
        }
        try {
            final String tempOutFile = outputFile;
            FFmpeg.getInstance(context).execute(command, new ExecuteBinaryResponseHandler() {

                @Override public void onSuccess(String s) {
                    callback.onFinishTrim(tempOutFile);
                }

                @Override public void onStart() {
                    callback.onStartTrim();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertSecondsToMiliTime(long seconds,String miliseconds){
        Log.i("QWLRKJWQRKL","miliseconds : " + miliseconds);
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if(Integer.valueOf(miliseconds) < 1){
            return "00:00";

        }
        int milisecond = Integer.valueOf(miliseconds.substring(miliseconds.length()-3));
        if (seconds <= 0) {
            return "00:00";
        } else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second) + "." + milisecond;
            } else {
                hour = minute / 60;
                if (hour > 99) return "99:59:59.999";
                minute = minute % 60;
                second = (int) (seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second) + "." + milisecond;
            }
        }
        return timeStr;
    }

    public static void shootVideoThumbInBackground(final Context context, final Uri videoUri, final int totalThumbsCount, final long startPosition,
                                                   final long endPosition, final SingleCallback<Bitmap, Integer> callback) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
            @Override public void execute() {
                try {
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(context, videoUri);
                    // Retrieve media data use microsecond
                    long interval = (endPosition - startPosition) / (totalThumbsCount - 1);
                    for (long i = 0; i < totalThumbsCount; ++i) {
                        long frameTime = startPosition + interval * i;
                        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        if(bitmap == null) continue;
                        try {
                            bitmap = Bitmap.createScaledBitmap(bitmap, THUMB_WIDTH, THUMB_HEIGHT, false);
                        } catch (final Throwable t) {
                            t.printStackTrace();
                        }
                        callback.onSingleCallback(bitmap, (int) interval);
                    }
                    mediaMetadataRetriever.release();
                } catch (final Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                }
            }
        });
    }

    public static String getVideoFilePath(String url) {
        if (TextUtils.isEmpty(url) || url.length() < 5) return "";
        if (url.substring(0, 4).equalsIgnoreCase("http")) {

        } else {
            url = "file://" + url;
        }

        return url;
    }

    private static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0) {
            return "00:00";
        } else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) return "99:59:59";
                minute = minute % 60;
                second = (int) (seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString(i);
        } else {
            retStr = "" + i;
        }
        return retStr;
    }
}
