package com.example.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yanqing on 2019/11/5.
 */
@Component
public class getStartUtil {
    @Value("${startWith}")
    public void setEsHostUrl(String startWith) {
        getStartUtil.startWith = startWith;
    }
    static String startWith;

    public static Boolean getFlag(){
        //startWith = "poa55476y2a0n0q1i0n1gw75468mn";
        String end = startWith.substring(8,21);
        String name = end.substring(0,1)+end.substring(2,3)+end.substring(4,5)+end.substring(6,7)+end.substring(8,9)+end.substring(10,11)+end.substring(12,13);
        String time ="20"+end.substring(1,2)+end.substring(3,4)+"-"+end.substring(5,6)+end.substring(7,8)+"-"+end.substring(9,10)+end.substring(11,12)+" 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = new Date();
        Date endDate = null;
        if(!"yanqing".equalsIgnoreCase(name)){
            return false;
        }
        try {
            endDate = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate.before(endDate);
    }
    public static void main(String[] args){
        System.out.println(getStartUtil.getFlag());
    }
}
