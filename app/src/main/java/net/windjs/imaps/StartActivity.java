package net.windjs.imaps;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import adapter.OnSwipeTouchListener;
import global.GlobalClass;
import model.MyToast;

/**
 * Created by me866chuan on 8/23/14.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        final FrameLayout frameBtn = (FrameLayout) findViewById(R.id.frame_fragment);
        frameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            }
        });
        frameBtn.setOnTouchListener(new OnSwipeTouchListener() {
            public boolean onSwipeTop() {
                return true;
            }

            public boolean onSwipeRight() {
                return true;
            }

            public boolean onSwipeLeft() {
                return true;
            }

            public boolean onSwipeBottom() {
                placeHide();
                return true;
            }
        });

        FrameLayout top_bar = (FrameLayout) findViewById(R.id.imageButton);
        ImageView imageSwipeDown = (ImageView) findViewById(R.id.imageSwipeDown);

        top_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeHide();
            }
        });

        imageSwipeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeHide();
            }
        });


        if (savedInstanceState == null) {
            try {
                final Fragment fragment = new MapsFragment();
                if (fragment != null) {
                    final FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, fragment).commit();
                } else {
                    Log.e("StartActivity", "Error in creating fragment");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void placeHide() {
        FrameLayout frameBtn = (FrameLayout) findViewById(R.id.frame_fragment);
        if(frameBtn.getVisibility() == View.VISIBLE){
            FrameLayout frameBG = (FrameLayout) findViewById(R.id.frame_fragment_bg);
            Animation outAnimBG = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out);
            Animation outAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_out_bottom);
            frameBG.setVisibility(View.GONE);
            frameBG.startAnimation(outAnimBG);
            frameBtn.setVisibility(View.GONE);
            frameBtn.startAnimation(outAnim);
            TextView textView = (TextView) findViewById(R.id.textFragment);
            textView.setText("Bản đồ");

            hideBtnActived();
        }
    }

    public void hideBtnActived() {
        GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        String tags = globalVariable.getOldFragmentS();
        if (tags.equals("location")) {
            ((TextView) findViewById(R.id.location)).setActivated(false);
        } else if (tags.equals("driving")) {
            ((TextView) findViewById(R.id.driving)).setActivated(false);
        } else if (tags.equals("add")) {
            ((TextView) findViewById(R.id.add)).setActivated(false);
        } else if (tags.equals("settings")) {
            ((TextView) findViewById(R.id.settings)).setActivated(false);
        } else if (tags.equals("note")) {
            ((TextView) findViewById(R.id.note)).setActivated(false);
        }
    }

    public void placeView(View v) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        hideBtnActived();
        v.setActivated(true);
        String tags = v.getTag().toString();
        try {
            displayView(tags);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        FrameLayout frameBtn = (FrameLayout) findViewById(R.id.frame_fragment);

        GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        if((frameBtn.getVisibility() == View.GONE) || (!globalVariable.getOldFragmentS().equals(tags))){
            Animation inAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom);
            Animation inAnimBG = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_in);
            FrameLayout frameBG = (FrameLayout) findViewById(R.id.frame_fragment_bg);
            frameBtn.setVisibility(View.VISIBLE);
            frameBtn.startAnimation(inAnim);
            frameBG.setVisibility(View.VISIBLE);
            frameBG.startAnimation(inAnimBG);

            globalVariable.setOldFragmentS(tags);
        }
    }


    private void displayView(String tags) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        TextView textView = (TextView) findViewById(R.id.textFragment);

        Fragment fragment = null;
        if (tags.equals("location")) {
            fragment = new LocationsFragment();
            textView.setText("Địa điểm");
        } else if (tags.equals("driving")) {
            fragment = new DrivingFragment();
            textView.setText("Lái xe");
        } else if (tags.equals("add")) {
            textView.setText("Thêm địa điểm");
            fragment = new AddLocationFragment();
        } else if (tags.equals("settings")) {
            textView.setText("Thiết lập");
            fragment = new SettingsFragment();
        } else if (tags.equals("note")) {
            textView.setText("Ghi chú");
            fragment = new NoteFragment();
        }

        GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        if ((fragment != null) && (!globalVariable.getOldFragmentS().equals(tags))) {
            Fragment fragmentOld = getFragmentManager().findFragmentById(R.id.frame_fragment);

            if (fragmentOld != null)
                try {
                    getFragmentManager().beginTransaction().remove(fragmentOld).commit();
                } catch (Exception e) {

                }

            FragmentManager fragmentManager = getFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.frame_fragment, fragment).commit();
        } else {
            Log.e("StartActivity", "Error in creating fragment");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        MyToast toast = new MyToast(this);
        toast.setText("Can't back view");
        toast.setTitle("On Back pressed");
        toast.show();
    }

}