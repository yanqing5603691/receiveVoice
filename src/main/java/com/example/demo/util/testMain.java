package com.example.demo.util;

import com.example.demo.model.VoiceJsonInfo;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by 86182 on 2019/8/7.
 */
public class testMain {
    public static void main(String[] args) throws EncoderException {
        /*VoiceJsonInfo vj = new VoiceJsonInfo();
        vj.calledid = "E1109020292111111";
        System.out.println(vj);*/
        String path = "D:\\Voice\\bbb.wav";
        //getAttribute2(path);
        File file = new File(path);
        Encoder encoder = new Encoder();
        long ls = 0;
        MultimediaInfo m;
        m = encoder.getInfo(file);
        ls = m.getDuration()/1000;
        System.out.println(ls);

    }
   /* public static void getAttribute2(String path) {
        Path fp = Paths.get(path);
        try {
            Files.getAttribute(fp, "basic:size");
            System.out.println("CREATION TIME:"+ Files.getAttribute(fp, "basic:creationTime"));//创建时间
            System.out.println("LAST ACCESS TIME:"+ Files.getAttribute(fp, "basic:lastAccessTime"));//访问时间
            System.out.println("FILE SIZE:"+ Files.getAttribute(fp, "basic:size").toString());
            System.out.println("LAST MODIFIED:"+ Files.getAttribute(fp, "basic:lastModifiedTime"));//修改时间
            System.out.println("IS SYSBOLIC LINK:"+ Files.getAttribute(fp, "basic:isSymbolicLink"));
            System.out.println("IS FOLDER:"+ Files.getAttribute(fp, "basic:isDirectory"));
            System.out.println("IS FILE:"+ Files.getAttribute(fp, "basic:isRegularFile"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

}
