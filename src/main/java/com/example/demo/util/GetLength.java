package com.example.demo.util;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;

import java.io.File;

/**
 * Created by 86182 on 2019/9/16.
 */
public class GetLength {
    public static   Long getLengthByPath(String path){
        File file = new File(path);
        Encoder encoder = new Encoder();
        long ls = 0;
        MultimediaInfo m = null;
        try {
            m = encoder.getInfo(file);
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        ls = m.getDuration()/1000;
        //System.out.println(ls);
        return ls;
    }
}
