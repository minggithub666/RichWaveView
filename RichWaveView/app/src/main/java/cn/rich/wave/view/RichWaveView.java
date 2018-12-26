package cn.rich.wave.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import cn.rich.wave.R;

public class RichWaveView extends View {
    private int baseLine = 0;// 基线，用于控制水位上涨的，这里是写死了没动，你可以不断的设置改变。
    private Paint mPaint;

    private int waveHeight = 100;// 波浪的最高度
    private int waveWidth = 0 ;//波长

    private int waveNumber = 5 ;//波浪个数
    private float offset = 0f;//偏移量
    /**
     * 区域起始颜色
     */
    private int mWaveStartColor;
    /**
     * 区域终点颜色
     */
    private int mWaveEndColor;



    public RichWaveView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public RichWaveView(Context context)
    {
        this(context, null);
    }

    /**
     * 获得我自定义的样式属性
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public RichWaveView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RichWaveView, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.RichWaveView_waveWidth:
                    waveWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RichWaveView_waveHeight:
                    waveHeight = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
						16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RichWaveView_waveStartColor:
                    // 默认颜色设置为黑色
                    mWaveStartColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.RichWaveView_waveEndColor:
                    mWaveEndColor = a.getColor(attr, Color.BLACK);
                    break;

            }

        }
        a.recycle();
        initView();


    }

    /**
     * 不断的更新偏移量，并且循环。
     */
    private void updateXControl(){
        //设置一个波长的偏移
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0,waveWidth);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatorValue = (float)animation.getAnimatedValue() ;
                offset = animatorValue;//不断的设置偏移量，并重画
                postInvalidate();
            }
        });
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(getPath(),mPaint);
    }
    //初始化paint，没什么可说的。
    private void initView(){
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        //mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(6);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int viewWidth = getMeasuredWidth();//获取控件宽度
        int viewHeight = getMeasuredHeight();//获取控件高度


        //当用户未设置波长时，设置波长默认为控件宽度
        if(waveWidth == 0)
        {
            waveWidth = viewWidth;
        }
        waveNumber = (viewWidth * 2)/waveWidth + 1;
        baseLine = viewHeight/2;
        Shader shader = new LinearGradient(0, 0, viewWidth, viewHeight, mWaveStartColor,
                mWaveEndColor, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        updateXControl();
    }

    /**
     * 核心代码，计算path
     * @return
     */
    private Path getPath(){

        int itemWidth = waveWidth/2;//半个波长
        Path mPath = new Path();
        mPath.moveTo(-itemWidth * 3, baseLine);//起始坐标


        for (int i = -5; i < waveNumber; i++) {
            int startX = i * itemWidth;
            mPath.quadTo(
                    startX + itemWidth/2 + offset,//控制点的X,（起始点X + itemWidth/2 + offset)
                    getWaveHeigh( i ),//控制点的Y
                    startX + itemWidth + offset,//结束点的X
                    baseLine//结束点的Y
            );
        }

        offset = offset + itemWidth/2;
        mPath.moveTo(-itemWidth * 3 , baseLine);//起始坐标
        //核心的代码就是这里
        for (int i = -5; i < waveNumber; i++) {
            int startX = i * itemWidth;
            mPath.quadTo(
                    startX + itemWidth/2 + offset,//控制点的X,（起始点X + itemWidth/2 + offset)
                    getWaveHeigh( i ),//控制点的Y
                    startX + itemWidth + offset,//结束点的X
                    baseLine//结束点的Y
            );
        }

        offset = offset + itemWidth/2;
        mPath.moveTo(-itemWidth * 3 , baseLine);//起始坐标
        //核心的代码就是这里
        for (int i = -5; i < waveNumber; i++) {
            int startX = i * itemWidth;
            mPath.quadTo(
                    startX + itemWidth/2 + offset,//控制点的X,（起始点X + itemWidth/2 + offset)
                    getWaveHeigh( i ),//控制点的Y
                    startX + itemWidth + offset,//结束点的X
                    baseLine//结束点的Y
            );
        }

        return  mPath;
    }
    //奇数峰值是正的，偶数峰值是负数
    private int getWaveHeigh(int num){
        if(num % 2 == 0){
            return baseLine + waveHeight;
        }
        return baseLine - waveHeight;
    }
}

