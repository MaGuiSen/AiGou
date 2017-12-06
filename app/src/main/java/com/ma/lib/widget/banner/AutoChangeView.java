package com.ma.lib.widget.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
/**
 * 自定义，根据外部输入的数字显示点所在位子
 * @author M
 *
 */
public class AutoChangeView extends View {
	private Paint paint = new Paint();
	private int whichChoice = 0;
	int choiceColor = 0xffe6e6e6;
	int noChoiceColor = 0xffffffff;
	int space = 5;
	public AutoChangeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AutoChangeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoChangeView(Context context) {
		super(context);

	}
	/**
	 * 默认显示第一页
	 * 
	 * @param whichChoice <= num-1
	 */
	public void setCurrentChoiced(int whichChoice) {
		this.whichChoice = whichChoice;
		invalidate();
	}

	int[] widthRound;
	int num = 1;
	int r = 10;//默认半径
	/**
	 * 设置有多少个点
	 * @param num
	 */
	public void setNum(int num) {
		this.num = num;
		invalidate();
	}
	int[] colors ;
	public void getLocat(int width, int num) {
		widthRound = new int[num];
		int a = 0;
		r = width/90;
		if(num%2==0){
			for(int i=(1-num);i<=(num-1);i=i+2){
				widthRound[a] = width/2+(r+space)*i;
				a++;
			}
		}else{
			for(int i=(-(num/2)*2);i<=((num/2)*2);i=i+2){
				widthRound[a] = width/2+(r+space)*i;
				a++;
				
			}
		}
		if(whichChoice>=num){
			//防止越界
			whichChoice = num-1;
		}
		colors = new int[num];
		for(int i=0;i<num;i++){
			if( i == whichChoice){
				colors[i] = choiceColor;
				continue;
			}
			colors[i] = noChoiceColor;
		}
	}
	public void setSpace(int space){
		this.space = space;
	}
	public void setNoChoiceColor(int noChoiceColor){
		this.noChoiceColor = noChoiceColor;
	}
	public void setChoiceColor(int choiceColor){
		this.choiceColor = choiceColor;
	}
	/**
	 * 重写这个方法
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 获取焦点改变背景颜色.
		int height = getHeight();// 获取对应高度组件的高度
		int width = getWidth(); // 获取对应组件的宽度
		getLocat(width,num);
		paint.setColor(0xffff0000);
		for(int i=0;i<widthRound.length;i++){
			canvas.save();
			paint.setColor(colors[i]);
			canvas.drawCircle(widthRound[i], height/2, r, paint);
			canvas.restore();
		}
	}
}
