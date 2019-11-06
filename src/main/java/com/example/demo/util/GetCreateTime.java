package com.example.demo.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 86182 on 2019/9/16.
 */
public class GetCreateTime {
    public static String getAttribute2(String path) {
        Path fp = Paths.get(path);
        try {
            Files.getAttribute(fp, "basic:size");
            //System.out.println("CREATION TIME:"+ Files.getAttribute(fp, "basic:creationTime"));//创建时间
            //System.out.println("LAST ACCESS TIME:"+ Files.getAttribute(fp, "basic:lastAccessTime"));//访问时间
            //System.out.println("FILE SIZE:"+ Files.getAttribute(fp, "basic:size").toString());
            //System.out.println("LAST MODIFIED:"+ Files.getAttribute(fp, "basic:lastModifiedTime"));//修改时间
            //System.out.println("IS SYSBOLIC LINK:"+ Files.getAttribute(fp, "basic:isSymbolicLink"));
            //System.out.println("IS FOLDER:"+ Files.getAttribute(fp, "basic:isDirectory"));
            //System.out.println("IS FILE:"+ Files.getAttribute(fp, "basic:isRegularFile"));
            return Files.getAttribute(fp, "basic:lastModifiedTime").toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "获取创建时间失败";
        }
    }

    public static Date stringToDate(String time)  {
        String newDate = time.substring(0,10)+" "+time.substring(11,19);
        //System.out.println(newDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String DateToString(Date time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeString =  sdf.format(time);
        return timeString;
    }

    public static String secondeToDate(Long second,String patten){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten);
        String dateString = format.format(date);
        return dateString;
    }

    public static Date utcToDateString(String time){
        //String d = "2018-05-14T03:51:50.153Z";
        Long eventTime = 0L;
        //将ISO 8601 日期格式进行转换
        DateTimeFormatter isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
        DateTimeFormatter normalFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
//获取long类型的时间
        DateTimeFormatter parser = ISODateTimeFormat.dateTime();
        DateTime dt = parser.parseDateTime(time);
        eventTime = dt.getMillis();
        //System.out.println(eventTime);
        //System.out.println(dt.toString(isoFormat));
        return stringToDate(dt.toString(isoFormat));
    }
}
