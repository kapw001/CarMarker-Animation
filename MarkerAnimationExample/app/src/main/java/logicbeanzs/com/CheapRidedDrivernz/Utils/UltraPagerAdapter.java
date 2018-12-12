package logicbeanzs.com.CheapRidedDrivernz.Utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.basegooglemap.R;
import com.tmall.ultraviewpager.UltraViewPager;

import org.json.JSONException;
import org.json.JSONObject;

public class UltraPagerAdapter extends PagerAdapter {
    private boolean isMultiScr;
    UltraViewPager mRecyclerView;
    public UltraPagerAdapter(boolean isMultiScr, UltraViewPager mRecyclerView) {
        this.isMultiScr = isMultiScr;
        this.mRecyclerView = mRecyclerView;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        @SuppressLint("InflateParams")
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.layout_child, null);
        //new LinearLayout(container.getContext());
        RecyclerView recyclerView = linearLayout.findViewById(R.id.recyclerView);
        TextView textView =  linearLayout.findViewById(R.id.pager_textview);
//        ParallaxPageTransformer pageTransformer = new ParallaxPageTransformer()
//                .addViewToParallax(new ParallaxPageTransformer.ParallaxTransformInformation(R.id.img_background,
//                        2, 2));
////                .addViewToParallax(new ParallaxTransformInformation(R.id.tutorial_img_phone, -0.65f,
////                        PARALLAX_EFFECT_DEFAULT));
        JSONObject ob = new JSONObject();
        try {
            ob.put("name","Sedan");
            ob.put("img",R.mipmap.car);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (position) {
            case 0:
                textView.setText("Economy");
                CarAdapter adapter = new CarAdapter(container.getContext(), new CarAdapter.interaction() {
                    @Override
                    public void onCarSelect(JSONObject ob, int position, ImageView img) {
                        CustomCarSelect.getInstance().changeState(ob,position,img);
                    }
                });
                adapter.setParams(recyclerView);
                for (int i=0;i<2;i++)
                {
                    if (i==0)
                    {
                        try {
                            ob.put("select",1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.setData(ob.toString());
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(adapter);
                break;
            case 1:
                textView.setText("Personal");
                CarAdapter adapters = new CarAdapter(container.getContext(), new CarAdapter.interaction() {
                    @Override
                    public void onCarSelect(JSONObject ob, int position,ImageView img) {
                        CustomCarSelect.getInstance().changeState(ob,position,img);
                    }
                });
                adapters.setParams(recyclerView);
                for (int i=0;i<2;i++)
                {
                    adapters.setData(ob.toString());
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(adapters);
                break;
            case 2:
                textView.setText("Consumer");
                CarAdapter adapter3 = new CarAdapter(container.getContext(), new CarAdapter.interaction() {
                    @Override
                    public void onCarSelect(JSONObject ob, int position,ImageView img) {
                        CustomCarSelect.getInstance().changeState(ob,position,img);
                    }
                });
                adapter3.setParams(recyclerView);
                for (int i=0;i<3;i++)
                {
                    adapter3.setData(ob.toString());
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(adapter3);
                break;
            case 3:
                textView.setText("Daily");
                CarAdapter adapter4 = new CarAdapter(container.getContext(), new CarAdapter.interaction() {
                    @Override
                    public void onCarSelect(JSONObject ob, int position, ImageView img) {
                        CustomCarSelect.getInstance().changeState(ob,position,img);
                    }
                });
                adapter4.setParams(recyclerView);
                for (int i=0;i<2;i++)
                {
                    adapter4.setData(ob.toString());
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(adapter4);
                break;
        }
        container.addView(linearLayout);
        return linearLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        LinearLayout view = (LinearLayout) object;
        container.removeView(view);
    }
}
