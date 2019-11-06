package com.example.demo.model;

/**
 * @Auther: fujian
 * @Date: 2018/12/28 17:02
 * @Description: 转写结果实体类
 */
public class Sentence implements Comparable<Sentence>{
    private String role;
    private long begin;
    private long end;
    private String text;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int compareTo(Sentence o) {
        int begin1 = Integer.parseInt(this.getBegin()+"");
        int begin2 = Integer.parseInt(o.getBegin()+"");
        return begin1-begin2;
    }
}
