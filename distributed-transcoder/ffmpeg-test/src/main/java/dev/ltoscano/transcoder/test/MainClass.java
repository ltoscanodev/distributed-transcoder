package dev.ltoscano.transcoder.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFmpegUtils;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegBuilder.Verbosity;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

/**
 *
 * @author ltosc
 */
public class MainClass 
{
    public static void main(String[] args)
    {
        try 
        {
            FFmpeg ffmpeg = new FFmpeg("D:\\ltosc\\Downloads\\ffmpeg-N-99908-gac5b45abab-win64-static\\ffmpeg.exe");
            FFprobe ffprobe = new FFprobe("D:\\ltosc\\Downloads\\ffmpeg-N-99908-gac5b45abab-win64-static\\ffprobe.exe");
            
            FFmpegBuilder builder = new FFmpegBuilder()
                .setInput("Test.ffcat")
                .setFormat("concat")
                .addExtraArgs("-safe", "0")
                .addOutput("merge-test.mp4")
                    .setVideoCodec("copy")
                    .done();
            
//            FFmpegBuilder builder = new FFmpegBuilder()
//                .setInput("D:\\ltosc\\Downloads\\ffmpeg-N-99908-gac5b45abab-win64-static\\CostaRica_720p.mp4")
//                .addOutput("D:\\ltosc\\Downloads\\ffmpeg-N-99908-gac5b45abab-win64-static\\Output\\output_%03d.mp4")
//                    .setVideoResolution(320, 240)
//                    .setFormat("segment")
//                    .addExtraArgs("-segment_list", "Test.ffcat")
//                    .done();
            
            builder.setVerbosity(Verbosity.DEBUG);
            
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            executor.createJob(builder).run();
            
//            FFmpegProbeResult in = ffprobe.probe("D:\\ltosc\\Downloads\\ffmpeg-N-99908-gac5b45abab-win64-static\\CostaRica_720p.mp4");
            
//            executor.createJob(builder, new ProgressListener() 
//            {
//                // Using the FFmpegProbeResult determine the duration of the input
//                final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);
//
//                @Override
//                public void progress(Progress progress) 
//                {
//                    double percentage = progress.out_time_ns / duration_ns;
//                    
//                    // Print out interesting information about the progress
//                    System.out.println(String.format(
//                            "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
//                            percentage * 100,
//                            progress.status,
//                            progress.frame,
//                            FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
//                            progress.fps.doubleValue(),
//                            progress.speed
//                    ));
//                }
//            }).run();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}