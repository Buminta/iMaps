package net.windjs.imaps;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import adapter.ImageAdapter;
import global.GlobalClass;
import model.ExpandableHeightGridView;

/**
 * Created by me866chuan on 9/12/14.
 */
public class NoteFragment extends Fragment{
    private LayoutInflater inflater;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.note_fragment, container, false);

        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
