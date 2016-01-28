
package com.iss.diagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义图表统计图
 * 
 * @author hubing
 * @version 1.0.0 2016-1-15
 */

public class DiagramView extends View {

    /** 统计图表数据源 */
    private ArrayList<IDiagramData> datas;

    /** 剩余小于1%比例的其他总和 */
    private float mResiduePercent;

    /** 控制显示宽 */
    private int mHeight;

    /** 控制显示高 */
    private int mWidth;

    /** 画笔对象 */
    private Paint mPaint;

    /** 起始角度 */
    private float mStartAngle;

    /** 最外层圆环显示区域 */
    private RectF mOutsideCircleRect;

    /** 圆形图表显示区域 */
    private RectF mDiagramRect;

    /** 颜色块显示区域 */
    private RectF mColorSolidRect;

    /** 文本显示颜色 */
    private int mTextColor = 0xff999999;

    /** 外层圆环宽 */
    private int mOutsideStrokeWidth;

    /** 内层图表圆环宽 */
    private int mDiagramStrokeWidth;

    /** 最外层圆环显示颜色 */
    private int mOutsideCircleColor = 0xfff6f6f6;

    /** 图表间隔颜色 */
    private int mDividerColor = Color.WHITE;

    /** 图表间隔宽 */
    private int mDividerSize = 5;

    /** 其他所有显示的名称 */
    private String mOtherName = "其他";

    /** 最大显示的应用数 */
    private int mMaxShowCount = 5;

    /** 图表的中心点x坐标 */
    private int mDiagramCenterX;

    /** 图表的中心点y坐标 */
    private int mDiagramCenterY;

    public DiagramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化画笔
     * 
     * @author hubing
     */
    private void init() {
        // 创建画笔对象
        mPaint = new Paint();
        // 设置抗锯齿
        mPaint.setAntiAlias(true);
        // 创建最外层圆环显示区域对象
        mOutsideCircleRect = new RectF();
        // 创建图表显示区域对象
        mDiagramRect = new RectF();
        // 创建颜色块显示区域对象
        mColorSolidRect = new RectF();
    }

    /**
     * 设置显示应用的名称的颜色
     * 
     * @param mTextColor
     * @author hubing
     */
    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    /**
     * 设置间隔颜色
     * 
     * @param mDividerColor
     * @author hubing
     */
    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
    }

    /**
     * 设置图表间隔大小
     * 
     * @param mDividerSize
     * @author hubing
     */
    public void setDividerSize(int size) {
        this.mDividerSize = size;
    }

    /**
     * 获取除前五之外的显示名称
     * 
     * @return
     * @author hubing
     */
    public String getOtherName() {
        return mOtherName;
    }

    /**
     * 设置除前五之外的显示名称
     * 
     * @param name
     * @author hubing
     */
    public void setOtherName(String name) {
        this.mOtherName = name;
    }

    /**
     * 设置图表数据源
     * 
     * @param datas
     * @author hubing
     */
    public void setDiagramData(ArrayList<IDiagramData> datas) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        this.datas = datas;
        // 降序排序
        Collections.sort(datas, new DiagramComparatorImpl());
        // 计算各自百分比
        calculatePercent();
        this.invalidate();
    }

    /**
     * 计算各自百分比
     * 
     * @author hubing
     */
    private void calculatePercent() {
        float total = 0;
        // 计算总值
        for (IDiagramData data : datas) {
            total += data.getPercentAttrValue();
        }
        // 计算各自所占百分比
        for (IDiagramData data : datas) {
            data.setPercent(data.getPercentAttrValue() / total);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 取控件宽高
        this.mHeight = getMeasuredHeight();
        this.mWidth = getMeasuredWidth();

        // 计算内层图表圆环宽
        int radius = mHeight / 3;
        mDiagramStrokeWidth = radius * 2 / 3;
        mDiagramCenterX = mWidth / 10 + radius;
        mDiagramCenterY = mHeight / 2;

        // 计算图表显示区域
        float l = mDiagramCenterX - radius + mDiagramStrokeWidth / 2;
        float t = mDiagramCenterY - radius + mDiagramStrokeWidth / 2;
        float r = mDiagramCenterX + radius - mDiagramStrokeWidth / 2;
        float b = mDiagramCenterY + radius - mDiagramStrokeWidth / 2;

        mDiagramRect.set(l, t, r, b);

        // 计算外层圆环宽
        float outRadius = radius * 1.1F;
        mOutsideStrokeWidth = mHeight / 70;

        // 计算外层圆环显示区域
        l = mDiagramCenterX - outRadius + mOutsideStrokeWidth / 2;
        t = mDiagramCenterY - outRadius + mOutsideStrokeWidth / 2;
        r = mDiagramCenterX + outRadius - mOutsideStrokeWidth / 2;
        b = mDiagramCenterY + outRadius - mOutsideStrokeWidth / 2;

        mOutsideCircleRect.set(l, t, r, b);
        
        // 计算图表间隔宽
        mDividerSize = mHeight / 90;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 判断是否需要绘制界面
        if (datas == null) {
            return;
        }

        // 重新设置初始值
        mStartAngle = -90;
        mResiduePercent = 1.0f;

        // 设置画笔文字大小
        mPaint.setTextSize(mWidth / 25);

        drawOutsideCircle(canvas);

        for (int i = 0; i < datas.size(); i++) {
            IDiagramData data = datas.get(i);

            // 画前5颜色块和对应名称
            if (i < mMaxShowCount) {
                drawSector(canvas, data.getPercent(), data.getPercentColor());
                // 画文字
                mPaint.setStyle(Style.FILL);
                drawColorAndName(canvas, data.getPercentColor(), data.getShowName(), i);
            } else {
                // 画剩余所有数据，并显示成其他
                drawSector(canvas, mResiduePercent, data.getPercentColor());
                // 画文字
                mPaint.setStyle(Style.FILL);
                drawColorAndName(canvas, data.getPercentColor(), mOtherName, i);
                break;
            }
        }

        // 重新设置初始值
        mStartAngle = -90;
        mResiduePercent = 1.0f;
        mPaint.setColor(mDividerColor);

        // 设置弧形宽度
        mPaint.setStrokeWidth(mDividerSize);
        mPaint.setStyle(Style.FILL);
        for (int i = 0; i < datas.size(); i++) {
            if (i < mMaxShowCount + 1) {
                IDiagramData data = datas.get(i);
                drawDivider(canvas, data.getPercent());
            }
        }
    }

    /**
     * 画图表间隔
     * 
     * @param canvas
     * @param percent
     * @author hubing
     */
    private void drawDivider(Canvas canvas, float percent) {
        // 计算-90度处的直线起始位置
        int radius = mHeight / 3;
        float stopX = mDiagramCenterX;
        float stopY = mDiagramCenterY - radius;

        // 保存当前画面状态
        canvas.save();
        canvas.rotate(mStartAngle + 90, mDiagramCenterX, mDiagramCenterY);
        canvas.drawLine(mDiagramCenterX, mDiagramCenterY - (radius - mDiagramStrokeWidth), stopX, stopY, mPaint);
        // 恢复到旋转之前状态
        canvas.restore();

        // 重新设置角度
        mStartAngle += percent * 360;
        // 重新计算剩余百分比
        mResiduePercent = mResiduePercent - percent;
    }

    /**
     * 画外部圆环颜色
     * 
     * @param canvas
     * @author hubing
     */
    private void drawOutsideCircle(Canvas canvas) {
        // 设置弧形宽度
        mPaint.setStrokeWidth(mOutsideStrokeWidth);
        mPaint.setStyle(Style.STROKE);
        // 设置圆环区域颜色
        mPaint.setColor(mOutsideCircleColor);
        canvas.drawArc(mOutsideCircleRect, 0, 360, false, mPaint);
    }

    /**
     * 画小颜色块和对应显示名称
     * 
     * @param canvas 画布
     * @param color 小颜色块要绘制的颜色值
     * @param showName 显示名称
     * @param position 显示位置
     * @author hubing
     */
    private void drawColorAndName(Canvas canvas, int color, String showName, int position) {
        // 设置画笔颜色
        mPaint.setColor(color);

        // 计算颜色块显示区域
        float l = mDiagramRect.right + mDiagramStrokeWidth / 2 + mWidth / 10;
        float t = mDiagramRect.top - mDiagramStrokeWidth / 2 + position * mWidth / 30 * 2;
        float r = l + mWidth / 30;
        float b = t + mWidth / 30;

        mColorSolidRect.set(l, t, r, b);
        // 画颜色块
        canvas.drawArc(mColorSolidRect, 0, 360, false, mPaint);

        // 绘制对应显示名称
        mPaint.setColor(mTextColor);
        canvas.drawText(showName, r + mWidth / 30, b, mPaint);
    }

    /**
     * 画弧形颜色块
     * 
     * @param canvas 画布
     * @param percent 百分比值
     * @param color 绘制的颜色值
     * @author hubing
     */
    private void drawSector(Canvas canvas, float percent, int color) {
        // 设置弧形宽度
        mPaint.setStrokeWidth(mDiagramStrokeWidth);
        mPaint.setStyle(Style.STROKE);
        // 设置弧形区域颜色
        mPaint.setColor(color);
        // 计算弧形显示角度
        float sweepAngle = percent * 360;
        canvas.drawArc(mDiagramRect, mStartAngle, sweepAngle, false, mPaint);

        // 重新设置角度
        mStartAngle += sweepAngle;
        // 重新计算剩余百分比
        mResiduePercent = mResiduePercent - percent;
    }

    /**
     * 图表数据接口，要用图表显示的实体数据必需实现此接口
     * 
     * @author hubing
     * @version [1.0.0.0, 2016-1-15]
     */
    public interface IDiagramData {

        /**
         * 取百分比值(用以计算展示比例的值)
         * 
         * @return
         * @author hubing
         */
        float getPercentAttrValue();

        /**
         * 获取要显示的统计名称
         * 
         * @return
         * @author hubing
         */
        String getShowName();

        /**
         * 设置计算后要显示的百分比值
         * 
         * @param percent
         * @author hubing
         */
        void setPercent(float percent);

        /**
         * 取要显示的百分比值
         * 
         * @author hubing
         */
        float getPercent();

        /**
         * 取当前对象展示的百分比颜色值
         * 
         * @return
         * @author hubing
         */
        int getPercentColor();

    }

    /**
     * 图表对象排序实现类(降序)
     * 
     * @author hubing
     * @version [1.0.0.0, 2016-1-15]
     */
    static class DiagramComparatorImpl implements Comparator<IDiagramData> {

        @Override
        public int compare(IDiagramData lhs, IDiagramData rhs) {
            float lhsPercent = lhs.getPercentAttrValue();
            float rhsPercent = rhs.getPercentAttrValue();
            return (int) (rhsPercent - lhsPercent);
        }

    }

}
