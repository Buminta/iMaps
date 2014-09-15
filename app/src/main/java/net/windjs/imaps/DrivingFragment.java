package net.windjs.imaps;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;

import java.util.Random;

import adapter.SpeedBoard;
import global.GlobalClass;
import model.MyToast;

/**
 * Created by me866chuan on 9/12/14.
 */
public class DrivingFragment extends Fragment {
    private LayoutInflater inflater;
    private View rootView;
    private SpeedBoard speedometer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.driving_fragment, container, false);

        speedometer = (SpeedBoard) rootView.findViewById(R.id.speedometer);

        // Add label converter
        speedometer.setLabelConverter(new SpeedBoard.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        // configure value range and ticks
        speedometer.setMaxSpeed(300);
        speedometer.setMajorTickStep(30);
        speedometer.setMinorTicks(4);
        speedometer.setMaxMinor(10);

        GlobalClass globalClass = (GlobalClass) getActivity().getApplicationContext();
        globalClass.setDrivingBoard(true);

        return rootView;

    }

    @Override
    public void onDestroyView() {
        GlobalClass globalClass = (GlobalClass) getActivity().getApplicationContext();
        globalClass.setDrivingBoard(false);
        super.onDestroyView();
    }

    public void setSpeed(double speed){
        MyToast t = new MyToast(getActivity().getApplicationContext());
        t.setText(String.valueOf(speed));
        t.show();
//        speedometer.setSpeed(speed);
        speedometer.setSpeed((new Random()).nextInt(300));
    }
}
