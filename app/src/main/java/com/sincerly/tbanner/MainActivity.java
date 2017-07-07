package com.sincerly.tbanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.sincerly.tbanner.banner.TBanner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  TBanner.OnBannerListener, TBanner.OnBannerPageChangeListener {

	private TBanner banner;
	private TBanner banner2;
	private TBanner banner3;
	private TBanner banner4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		banner= (TBanner) findViewById(R.id.banner1);
		banner2= (TBanner) findViewById(R.id.banner2);
		banner3= (TBanner) findViewById(R.id.banner3);
		banner4= (TBanner) findViewById(R.id.banner4);


		List<ImageView> list=new ArrayList<>();
		List<String> titles=new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			ImageView imageView=new ImageView(MainActivity.this);
			if(i==0){
				imageView.setImageResource(R.mipmap.lunbo1);
			}else if(i==1){
				imageView.setImageResource(R.mipmap.lunbo2);
			}else if(i==2){
				imageView.setImageResource(R.mipmap.lunbo3);
			}else if(i==3){
				imageView.setImageResource(R.mipmap.lunbo4);
			}else if(i==4){
				imageView.setImageResource(R.mipmap.lunbo5);
			}else if(i==5){
				imageView.setImageResource(R.mipmap.lunbo6);
			}
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			list.add(imageView);
			titles.add("数据测试测试时事实是"+i);
		}
		banner.setData(list);
		banner.setTitles(titles);
		banner.setOnBannerClickListener(this);
		banner.start();

		List<ImageView> list2=new ArrayList<>();
		List<String> titles2=new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ImageView imageView=new ImageView(MainActivity.this);
			if(i==0){
				imageView.setImageResource(R.mipmap.lunpo1);
			}else if(i==1){
				imageView.setImageResource(R.mipmap.lunpo2);
			}else if(i==2){
				imageView.setImageResource(R.mipmap.lunpo3);
			}else if(i==3){
				imageView.setImageResource(R.mipmap.lunpo4);
			}
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			list2.add(imageView);
			titles2.add("数据测试测试时事实是"+i);
		}
		banner2.setData(list2);
		banner2.setTitles(titles2);
		banner2.setOnBannerClickListener(this);
		banner2.start();


		List<ImageView> list3=new ArrayList<>();
		List<String> titles3=new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ImageView imageView=new ImageView(MainActivity.this);
			if(i==0){
				imageView.setImageResource(R.mipmap.lunpo1);
			}else if(i==1){
				imageView.setImageResource(R.mipmap.lunpo2);
			}else if(i==2){
				imageView.setImageResource(R.mipmap.lunpo3);
			}else if(i==3){
				imageView.setImageResource(R.mipmap.lunpo4);
			}
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			list3.add(imageView);
			titles3.add("数据测试测试时事实是"+i);
		}

		banner3.setData(list3);
		banner3.setTitles(titles3);
		banner3.setOnBannerClickListener(this);
		banner3.start();

		List<ImageView> list4=new ArrayList<>();
		List<String> titles4=new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ImageView imageView=new ImageView(MainActivity.this);
			if(i==0){
				imageView.setImageResource(R.mipmap.lunpo1);
			}else if(i==1){
				imageView.setImageResource(R.mipmap.lunpo2);
			}else if(i==2){
				imageView.setImageResource(R.mipmap.lunpo3);
			}else if(i==3){
				imageView.setImageResource(R.mipmap.lunpo4);
			}
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			list4.add(imageView);
			titles4.add("数据测试测试时事实是"+i);
		}

		banner4.setData(list4);
		banner4.setTitles(titles4);
		banner4.setOnBannerClickListener(this);
		banner4.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		banner.destory();
		banner2.destory();
		banner3.destory();
		banner4.destory();
	}

	@Override
	public void onBannerClick(int position) {
		Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {


	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
}
