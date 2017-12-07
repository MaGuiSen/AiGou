package com.ma.aigou.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ma.aigou.R;
import com.ma.aigou.adapter.ListAdapter;
import com.ma.lib.widget.banner.BannerView;
import com.ma.lib.widget.pullloadview.PullLoadView2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment{
    View currView = null;
    @Bind(R.id.refreshView)
    PullLoadView2 pullLoadView;
    @Bind(R.id.change)
    View change;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        currView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, currView);
        initView();
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pullLoadView.changeShowType(true, 2);
            }
        });
        return currView;
    }

    boolean isSuccess = false;

    View initBanner(){
        View layout_banner = View.inflate(getActivity(),R.layout.layout_home_banner, null);
        BannerView bannerView = (BannerView) layout_banner.findViewById(R.id.banner);
        BannerView.BannerData bannerData = new BannerView.BannerData();
        bannerData.imgUrl = "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=157669227,183289158&fm=173&s=F58045B71440BAE4248186AF0300700E&w=640&h=374&img.JPEG";

        BannerView.BannerData bannerData2 = new BannerView.BannerData();
        bannerData.imgUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2100961456,64396807&fm=173&s=EA997E8588534AD447B8A0A303007042&w=540&h=360&img.JPEG";

        BannerView.BannerData bannerData3 = new BannerView.BannerData();
        bannerData.imgUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2433582460,3457889240&fm=173&s=AF41CE100D92C8D05848194D030070F8&w=572&h=324&img.JPEG";


        List<BannerView.BannerData> dataList = new ArrayList<>();
        dataList.add(bannerData);
        dataList.add(bannerData2);
        dataList.add(bannerData3);
        bannerView.notifyDataChange(dataList);

        return layout_banner;
    }

    void addCategory(List<Map<String, Object>> categoryList, String title, int icon){
        Map<String, Object> category = new HashMap<>();
        category.put("icon", icon);
        category.put("title", title);
        categoryList.add(category);
    }

    View initCategory(){
        List<Map<String, Object>> categoryList = new ArrayList<>();
        addCategory(categoryList, "天猫", R.mipmap.icon_tianmao);
        addCategory(categoryList, "聚划算", R.mipmap.icon_tianmao);
        addCategory(categoryList, "天猫国际", R.mipmap.icon_tianmao);
        addCategory(categoryList, "外卖", R.mipmap.icon_tianmao);
        addCategory(categoryList, "天猫超市", R.mipmap.icon_tianmao);
        addCategory(categoryList, "充值中心", R.mipmap.icon_tianmao);
        addCategory(categoryList, "飞猪旅行", R.mipmap.icon_tianmao);
        addCategory(categoryList, "领金币", R.mipmap.icon_tianmao);
        addCategory(categoryList, "拍卖", R.mipmap.icon_tianmao);
        addCategory(categoryList, "分类", R.mipmap.icon_tianmao);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout layoutLine1 = new LinearLayout(getContext());
        layoutLine1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout layoutLine2 = new LinearLayout(getContext());
        layoutLine2.setOrientation(LinearLayout.HORIZONTAL);

        layout.addView(layoutLine1);
        layout.addView(layoutLine2);
        int numPerLine = 5;

        LinearLayout c_layoutLine = layoutLine1;
        for(int i=0;i<categoryList.size();i++){
            if(i == numPerLine){
                c_layoutLine = layoutLine2;
            }
            Map<String, Object> categoryItem = categoryList.get(i);
            LinearLayout category = (LinearLayout) View.inflate(getActivity(),R.layout.layout_category_item, null);
            category.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

            ImageView iconImg = (ImageView) category.findViewById(R.id.img_icon);
            TextView title = (TextView) category.findViewById(R.id.txt_title);
            iconImg.setImageResource((Integer) categoryItem.get("icon"));
            title.setText((String) categoryItem.get("title"));
            c_layoutLine.addView(category);
            category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        return layout;
    }

    public void initView(){
        ListAdapter adapter = new ListAdapter(getContext());
        adapter.addHeadView(initBanner());
        adapter.addHeadView(initCategory());
        pullLoadView.initView(adapter);
        pullLoadView.setPullLoadListener(new PullLoadView2.PullLoadListener() {
            @Override
            public void loadDataFun(final boolean isLoadMore, final int pageIndex, int pageSize) {
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // 更新列表
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int code = 200;
                                List dataList = new ArrayList();
                                if (pageIndex == 4){
                                    for(int i=0;i<10;i++){
                                        dataList.add(i);
                                    }
                                }else{
                                    for(int i=0;i<20;i++){
                                        dataList.add(i);
                                    }
                                }
                                if(pageIndex == 2){
                                    if(!isSuccess){
                                        code = 100;
                                        isSuccess = true;
                                    }
                                }
                                pullLoadView.endLoad(code == 200, isLoadMore, dataList);
                            }
                        });
                        timer.cancel();
                    }
                }, 3000);
            }
        });
    }
}