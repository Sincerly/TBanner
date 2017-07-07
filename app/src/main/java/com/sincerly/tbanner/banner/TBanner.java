package com.sincerly.tbanner.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.sincerly.tbanner.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


 /*<!--app:defaultImage   默认加载图片
		 app:autoPlay       自动加载
		 app:direction      指示器方向
		 app:delayTime      延迟时间
		 app:scaleType      图片显示方式
		 app:setScrollerTime  滑动速度
		 app:animation      动画
		 app:indicatorHeight    指示器高度
		 app:indicatorType      指示器类型
		 app:indicatorTextColor 指示器文字颜色
		 app:indicatorTextSize      指示器文字大小
		 app:titleBackgroup 指示器背景色
		 app:repeatMode     滚动模式
		 app:repeatCount    滚动次数
		 app:bannerType     类型-->*/

/**
 * Created by Sincerly on 2017/6/30.
 * {@link #initBannerLayout(Context context, int resouceId)}                      初始化轮播容器
 * {@link #setScaleType(ScaleType)}												   主视图图片缩放类型
 * {@link #setData(List<ImageView>)}                                              主视图
 * {@link #setTitles(List)}                                                       标题
 * {@link #setIndicatorDirection(int) }                                           指示器方向
 * {@link #setIndicatorList(List<ImageView>)}                                     指示器视图
 * {@link #setDelayTime(int)}													   设置延迟时间
 * {@link #start()}																   开始滚动
 * {@link #createIndicators()}                                                    创建指示器
 * {@link #setOnBannerPageChangeListener(OnBannerPageChangeListener)}             ViewPager滑动事件
 * {@link #setOnBannerClickListener(OnBannerListener)}                            创建指示器
 *
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_autoPlay                    自动播放
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_time                        自动播放时间
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_bannerLayout				   自定义banner视图
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_scaleType                   图片缩放类型
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_indicatorHeight             指示器高度
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_indicatorWidth              指示器宽度
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_titleBackGround			   底部title背景
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_indicatorNormalDrawable	   指示器默认样式
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_indicatorSelectedDrawable   指示器选中样式
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_repeatMode                  滚动模式
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_scrollDirection             滚动方向
 * @attr ref com.sincerly.tbanner.R.styleable.TBanner_repeatCount                 滚动次数
 */

public class TBanner<T> extends FrameLayout {
	private OnBannerListener mBannerClickListener;
	private OnBannerPageChangeListener mBannerPageChangeListener;

	private int mDelayTime;
	private int mDirection;
	private int mRepeatCount = -1;
	private int mRepeatMode = RESTART;
	private ScaleType mScaleType;//视图缩放类型(ImageView)
	private int mIndicatorHeight;//指示器高度
	private int mIndicatorWidth;//指示器宽度
	private int mTitleBackGround;
	private int mIndicatorMargin = 16;
	private boolean isAutoPlay;//自动播放
	private int resouceId;//Banner视图Id
	private int indicatorSelectedResouceId;
	private int indicatorNormalResouceId;

	private List<ImageView> mIndicatorLists = new ArrayList<>();//指示器
	private List<String> mTitles = new ArrayList<>();//Banner 底部标题
	private List<T> mViews = new ArrayList<>();//Banner视图
	private int count = 0;//视图个数
	private Context mContext;
	private LinearLayout mContainer;
	private LinearLayout indicatorContainer;
	private TextView mTitleTextView;
	private BannerViewPager mViewPager;
	private MyAdapter adapter;
	private int currentItem = 0;//ViewPager选中的下标
	private int lastPosition = 0;

	public static final int RESTART = 1;
	public static final int REVERSE = 2;
	private int stepCount = 1;//总共要走的步数
	private int currentStep = 0;

	private Handler hander = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
		}
	};

	public TBanner(@NonNull Context context) {
		this(context, null);
	}

	public TBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TBanner(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		this.mContext = context;
		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.TBanner);
		isAutoPlay = typeArray.getBoolean(R.styleable.TBanner_autoPlay, true);
		mDelayTime = typeArray.getInt(R.styleable.TBanner_time, 3000);
		resouceId = typeArray.getResourceId(R.styleable.TBanner_bannerLayout, R.layout.view_layout_banner);
		mIndicatorHeight = typeArray.getDimensionPixelSize(R.styleable.TBanner_indicatorHeight, 20);
		mIndicatorWidth = typeArray.getDimensionPixelSize(R.styleable.TBanner_indicatorWidth, 20);
		mTitleBackGround = typeArray.getColor(R.styleable.TBanner_titleBackGround, Color.parseColor("#40FFFFFF"));
		indicatorNormalResouceId = typeArray.getResourceId(R.styleable.TBanner_indicatorNormalDrawable, R.drawable.oval_blue);
		indicatorSelectedResouceId = typeArray.getResourceId(R.styleable.TBanner_indicatorSelectedDrawable, R.drawable.oval_white);
		mDirection = typeArray.getInt(R.styleable.TBanner_direction, Direction.RIGHT);
		mRepeatCount = typeArray.getInt(R.styleable.TBanner_repeatCount, -1);
		mRepeatMode = typeArray.getInt(R.styleable.TBanner_repeatMode, RESTART);

		int scalType = typeArray.getInt(R.styleable.TBanner_scaleType, -1);//视图缩放类型(ImageView)
		if (scalType > 0) {
			setScaleType(scaleTypeArray[scalType]);
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initBannerLayout(mContext, resouceId);
	}

	/**
	 * 初始化Banner布局
	 *
	 * @param resouceId
	 */
	private void initBannerLayout(Context context, int resouceId) {
		View view = LayoutInflater.from(context).inflate(resouceId, null);
		mViewPager = (BannerViewPager) view.findViewById(R.id.viewPager);
		mContainer = (LinearLayout) view.findViewById(R.id.container);
		indicatorContainer = (LinearLayout) view.findViewById(R.id.indicatorContainer);
		mTitleTextView = (TextView) view.findViewById(R.id.mTitleTextView);
		mContainer.setBackgroundColor(mTitleBackGround);
		addView(view);
		initBannerScroller();
	}

	private void initBannerScroller() {
		try {
			Field field = ViewPager.class.getField("mScroller");
			field.setAccessible(true);
			MyScroller scroller = new MyScroller(mViewPager.getContext());
			scroller.setDuration((short) 100);
			field.set(mViewPager, scroller);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置标题
	 *
	 * @param titles
	 * @return
	 */
	public TBanner setTitles(List<String> titles) {
		this.mTitles = titles;
		return this;
	}

	/**
	 * 设置资源
	 *
	 * @param datas
	 * @return
	 */
	public TBanner setData(List<T> datas) {
		this.mViews = datas;
		count = mViews.size();//页数
		if (mRepeatCount > 0) {
			stepCount = mRepeatCount * count;
		}
		return this;
	}

	/**
	 * 开始
	 */
	public void start() {
		initIndicators();
		initViewpager();
	}

	/**
	 * 初始化指示器
	 */
	private void initIndicators() {
		createIndicators();
	}

	private void initViewpager() {
		currentItem = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % count);
		if (adapter == null) {
			adapter = new MyAdapter();
		}
		mViewPager.setAdapter(new MyAdapter());
		mViewPager.setOnPageChangeListener(listener);
		mViewPager.setCurrentItem(currentItem);
		if (isAutoPlay) {//是否是自动播放
			startPlay();
		}
	}

	/**
	 * 创建指示器
	 */
	private void createIndicators() {
		mIndicatorLists.clear();
		indicatorContainer.removeAllViews();
		for (int i = 0; i < count; i++) {
			ImageView imageView = new ImageView(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
			params.rightMargin = mIndicatorMargin;
			imageView.setLayoutParams(params);
			if (i == 0) {
				imageView.setImageResource(indicatorSelectedResouceId);
			} else {
				imageView.setImageResource(indicatorNormalResouceId);
			}
			mIndicatorLists.add(imageView);
			indicatorContainer.addView(imageView);
		}
	}

	/**
	 * 设置自定义指示器
	 */
	private void setIndicatorList(List<ImageView> indicators) {
		mIndicatorLists.clear();
		mIndicatorLists.addAll(indicators);
		count = mIndicatorLists.size();//页数
	}

	/**
	 * 图片填充类型
	 *
	 * @param scaleType
	 */
	public void setScaleType(ScaleType scaleType) {
		this.mScaleType = scaleType;
	}

	public void setRepeatCount(int count) {

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (currentStep <= stepCount) {
					isAutoPlay = false;
					stopPlay();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (currentStep <= stepCount) {
					isAutoPlay = true;
					startPlay();
				}
				break;
			default:
				break;
		}
		return super.dispatchTouchEvent(event);
	}

	private static final ScaleType[] scaleTypeArray = {
			ScaleType.MATRIX,
			ScaleType.FIT_XY,
			ScaleType.FIT_START,
			ScaleType.FIT_CENTER,
			ScaleType.FIT_END,
			ScaleType.CENTER,
			ScaleType.CENTER_CROP,
			ScaleType.CENTER_INSIDE
	};

	private static final MODE[] styleArray = {
			MODE.NONE,
			MODE.TITLE,
			MODE.NUMBER,
			MODE.GUI
	};

	public enum MODE {
		NONE,//正常模式   只显示指示器
		TITLE,//显示title ,指示器
		NUMBER,//右下角数字
		GUI,//启动页
	}

	public enum ScaleType {
		MATRIX(0),
		FIT_XY(1),
		FIT_START(2),
		FIT_CENTER(3),
		FIT_END(4),
		CENTER(5),
		CENTER_CROP(6),
		CENTER_INSIDE(7);

		ScaleType(int t) {
			type = t;
		}

		final int type;
	}

	public void startPlay() {
		hander.removeCallbacks(runnable);
		hander.postDelayed(runnable, mDelayTime);
	}

	public void stopPlay() {
		hander.removeCallbacks(runnable);
	}

	/**
	 * 轮播线程
	 */
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (count > 1 && isAutoPlay) {
				int currentItem = mViewPager.getCurrentItem() + 1;
				if (mDirection == Direction.RIGHT) {//从左往右
				} else if (mDirection == Direction.LEFT) {//从右往左
					currentItem -= 2;
				}
				mViewPager.setCurrentItem(currentItem);
				if (currentStep <= stepCount) {
					hander.postDelayed(runnable, mDelayTime);
				}
			}
		}
	};

	/**
	 * 设置指示器方向
	 */
	public TBanner setIndicatorDirection(int direction) {
		this.mDirection = direction;
		return this;
	}

	/**
	 * 设置延迟时间
	 *
	 * @param delayTime
	 */
	public TBanner setDelayTime(int delayTime) {
		this.mDelayTime = delayTime;
		return this;
	}

	public void destory() {
		hander.removeCallbacks(runnable);
	}

	/**
	 * 设置监听
	 *
	 * @param listener
	 * @return
	 */
	public TBanner setOnBannerClickListener(OnBannerListener listener) {
		this.mBannerClickListener = listener;
		return this;
	}

	public TBanner setOnBannerPageChangeListener(OnBannerPageChangeListener listener) {
		this.mBannerPageChangeListener = listener;
		return this;
	}

	private class OnClickListener implements View.OnClickListener {
		private int position = 0;

		public OnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (mBannerClickListener != null) {
				mBannerClickListener.onBannerClick(position);
			}
		}
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = (View) mViews.get(position % count);
			container.addView(v);
			v.setOnClickListener(new OnClickListener(position % count));
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) mViews.get(position % count));
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}

	private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			int realPosition = position % count;
			if (mBannerPageChangeListener != null) {
				mBannerPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageSelected(int position) {
			if (mRepeatCount > 0) {
				currentStep++;
			}
			int realPosition = position % count;
			if (mBannerPageChangeListener != null) {
				mBannerPageChangeListener.onPageSelected(realPosition);
			}
			//更改指示器选中样式
			mIndicatorLists.get(lastPosition).setImageResource(indicatorNormalResouceId);
			mIndicatorLists.get(realPosition).setImageResource(indicatorSelectedResouceId);
			lastPosition = realPosition;
			mTitleTextView.setText(mTitles.get(realPosition));
			mViewPager.setCurrentItem(position);
		}

		/**
		 当页面滚动状态变化的时候回调这个方法   1-2-0
		 静止->滑动
		 滑动-->静止
		 静止-->拖拽
		 */
		@Override
		public void onPageScrollStateChanged(int state) {
			if (mBannerPageChangeListener != null) {
				mBannerPageChangeListener.onPageScrollStateChanged(state);
			}
//			currentItem = mViewPager.getCurrentItem()%count;
//			// dragging指压 setting滑动 idle滑动停止 指压松开后不论是否翻页setting均触发
//			switch (state) {
//				case ViewPager.SCROLL_STATE_DRAGGING://dragging指压  1
//					Log.e("tag","Page onPageScrollStateChanged 指压 1 currentItem"+currentItem);
//					break;
//				case ViewPager.SCROLL_STATE_SETTLING://静止  2
//					Log.e("tag","Page onPageScrollStateChanged 滑动静止 2 currentItem"+currentItem);
//					if (currentItem == 0&&!isAutoPlay) {
//						mViewPager.setCurrentItem(count-1, false);
//					} else if (currentItem == count -1&&!isAutoPlay) {
//						mViewPager.setCurrentItem(0, false);
//					}
//					break;
//				case ViewPager.SCROLL_STATE_IDLE://滑动停止   0
//					Log.e("tag","Page onPageScrollStateChanged 滑动停止 0 currentItem"+currentItem);
//					if (currentItem == 0&&!isAutoPlay) {
//						mViewPager.setCurrentItem((count-1),false);
//					}else if (currentItem == (count-1)&&!isAutoPlay) {
//						mViewPager.setCurrentItem(0,false);
//					}
//					isAutoPlay=true;
//					startPlay();
//					break;
//			}
		}
	};

	private class Direction {
		/**
		 * 轮播从右往左
		 */
		public static final int LEFT = 0;
		/**
		 * 轮播从下往上
		 */
		public static final int TOP = 1;
		/**
		 * 轮播从左往右
		 */
		public static final int RIGHT = 2;
		/**
		 * 轮播从上往下
		 */
		public static final int BOTTOM = 3;
	}

	/**
	 * 轮播接口
	 */
	public interface OnBannerListener {
		void onBannerClick(int position);
	}

	public interface OnBannerPageChangeListener {
		void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

		void onPageSelected(int position);

		void onPageScrollStateChanged(int state);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.e("tag","OnSizeChanged");
	}
}
