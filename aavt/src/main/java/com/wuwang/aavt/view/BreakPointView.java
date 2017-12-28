package com.wuwang.aavt.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wuwang.aavt.media.hard.HardMediaData;
import com.wuwang.aavt.utils.ScreenUtils;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
* Created by TY on 2017/12/13.
*/
public class BreakPointView extends View {


    private Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint flashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 光标宽度
     */
    private int flashWidth;
    //闪烁光标
    private int showInt = 0;
    //是否闪烁
    private boolean record = false;
    /**
     * 总的录制时间
     */
    private float totalMilles = 15000;

    /**
     * 时间数据
     */
    private Queue<LinkedList<HardMediaData>> renderBeans ;

    /**
     * 时间间隔
     */
    private long timeStep = 50;

    private float space;
    private ValueAnimator animator;

    public BreakPointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        progressPaint.setColor(0xfff29600);//黄色
        flashPaint.setColor(0xff333333);//黑色
        flashWidth = ScreenUtils.dp2px(getContext(), 4);
        space = ScreenUtils.dp2px(getContext(), 1);
        startAnim();
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public void addBean() {
//        renderBeans.add(new RenderBean(0));
    }

    public void addTime() {
//        if(renderBeans.isEmpty()){
//            renderBeans.add(new RenderBean(0));
//        }
//        renderBeans.get(renderBeans.size()-1).addTime(timeStep);
//        postInvalidate();

    }

    public void setRenderBeans(ArrayDeque<LinkedList<HardMediaData>> renderBeans) {
        this.renderBeans = renderBeans;
        postInvalidate();
    }

    private void startAnim() {
        animator = ValueAnimator.ofInt(0, 2);
        animator.setDuration(500);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                showInt = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        progressPaint.setStrokeWidth(getHeight() - 10);
        flashPaint.setStrokeWidth(getHeight() - 10);

        float startWidth = 0;
        if (renderBeans != null && !renderBeans.isEmpty()) {
            Iterator<LinkedList<HardMediaData>> it = renderBeans.iterator();
            while (it.hasNext()) {
                List<HardMediaData> list = it.next();
                if(list==null||list.isEmpty()) continue;
                long time = list.get(list.size() - 1).info.presentationTimeUs - list.get(0).info
                        .presentationTimeUs;
                float longWidht = time/1000 / (float) totalMilles * getWidth();
                float endWidth = startWidth + longWidht;
                canvas.drawLine(startWidth, getHeight() / 2, endWidth, getHeight() / 2, progressPaint);
                startWidth += longWidht + space;
            }
        }
        if (showInt == 1 || record) {
            canvas.drawLine(startWidth, getHeight() / 2, startWidth + flashWidth, getHeight() / 2,
                    flashPaint);
        }
    }

}
