package ua.ck.geekhub.android.dubiy.rss.fragment;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import ua.ck.geekhub.android.dubiy.rss.R;

/**
 * Created by Gary on 20.10.2014.
 */
public class FragmentAnim extends Fragment {
    final private String LOG_TAG = this.getClass().getSimpleName();

    private Animation animFadeIn;
    private Animation animFadeOut;
    private Animation animZoomIn;
    private Animation animZoomOut;
    private ImageView imageView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "FragmentAnim onAttach");
//        this.getActivity().toString();

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_out);
        animZoomIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.zoom_in);
        animZoomOut = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.zoom_out);
        Log.d(LOG_TAG, "FragmentAnim onCreate");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        Log.d(LOG_TAG, "fragmentAnim onCreateView");
        View v = inflater.inflate(R.layout.fragment_anim, null);
        imageView = (ImageView)v.findViewById(R.id.imageView);
        Button btn_anim_fade_in = (Button)v.findViewById(R.id.btn_anim_fade_in);
        btn_anim_fade_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.startAnimation(animFadeIn);
            }
        });
        Button btn_anim_fade_out = (Button)v.findViewById(R.id.btn_anim_fade_out);
        btn_anim_fade_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.startAnimation(animFadeOut);
            }
        });
        Button btn_anim_zoom_out = (Button)v.findViewById(R.id.btn_anim_zoom_out);
        btn_anim_zoom_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.startAnimation(animZoomOut);
            }
        });
        Button btn_anim_zoom_in = (Button)v.findViewById(R.id.btn_anim_zoom_in);
        btn_anim_zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.startAnimation(animZoomIn);
            }
        });


        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "fragmentAnim onActivityCreated");

    }

    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "fragmentAnim onStart");
    }

    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "fragmentAnim onResume");
//        TextView tv1 = (TextView) getView().findViewById(R.id.textView2);
//        tv1.setText(this.getActivity().toString());
//        tv1.setText("ololo");
    }

    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "fragmentAnim onPause");
    }

    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "fragmentAnim onStop");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG, "fragmentAnim onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "fragmentAnim onDestroy");
    }

    public void onDetach() {
        super.onDetach();
        Log.d(LOG_TAG, "fragmentAnim onDetach");
    }







}
