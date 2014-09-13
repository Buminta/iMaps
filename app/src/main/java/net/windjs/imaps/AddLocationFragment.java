package net.windjs.imaps;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.LinearLayout;

import adapter.ImageAdapter;
import global.GlobalClass;
import model.ExpandableHeightGridView;

/**
 * Created by me866chuan on 9/12/14.
 */
public class AddLocationFragment extends Fragment{
    private LayoutInflater inflater;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.add_location, container, false);

        onEventListener();
        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onEventListener() {
        ImageAdapter adapter = new ImageAdapter(getActivity());
        adapter.setmThumbIds(new Integer[]{
                R.drawable.round,
                R.drawable.round,
                R.drawable.round,
                R.drawable.round,
                R.drawable.round,
                R.drawable.round,
                R.drawable.round,
                R.drawable.round,
                R.drawable.round,
        });

        final ExpandableHeightGridView grid1 = (ExpandableHeightGridView) rootView.findViewById(R.id.gridView1);
        final ExpandableHeightGridView grid2 = (ExpandableHeightGridView) rootView.findViewById(R.id.gridView2);
        final ExpandableHeightGridView grid3 = (ExpandableHeightGridView) rootView.findViewById(R.id.gridView3);
        grid1.setExpanded(true);
        grid2.setExpanded(true);
        grid3.setExpanded(true);

        grid1.setAdapter(adapter);
        grid2.setAdapter(adapter);
        grid3.setAdapter(adapter);


        final Animation in = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.slide_down);
        LinearLayout view = (LinearLayout) rootView.findViewById(R.id.layoutView1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid1.setVisibility(View.VISIBLE);
                grid1.startAnimation(in);
                grid2.setVisibility(View.GONE);
                grid3.setVisibility(View.GONE);
            }
        });

        view = (LinearLayout) rootView.findViewById(R.id.layoutView2);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid1.setVisibility(View.GONE);
                grid2.setVisibility(View.VISIBLE);
                grid2.startAnimation(in);
                grid3.setVisibility(View.GONE);
            }
        });

        view = (LinearLayout) rootView.findViewById(R.id.layoutView3);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid1.setVisibility(View.GONE);
                grid2.setVisibility(View.GONE);
                grid3.setVisibility(View.VISIBLE);
                grid3.startAnimation(in);
            }
        });
    }
}
