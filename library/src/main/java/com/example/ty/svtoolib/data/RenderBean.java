package com.example.ty.svtoolib.data;

/*
* Created by TY on 2017/12/13.
* 渲染实体
*/
public class RenderBean  {

    public RenderBean(long time) {
        this.time = time;
    }

    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public void addTime(long time){
        this.time+=time;
    }
}
