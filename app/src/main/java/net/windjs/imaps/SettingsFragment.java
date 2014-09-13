package net.windjs.imaps;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;

import adapter.ApiMapsAdapter;
import model.MyToast;

/**
 * Created by me866chuan on 9/12/14.
 */
public class SettingsFragment extends Fragment{
    private View rootView;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.settings_fragment, container, false);

        TextView view = (TextView) rootView.findViewById(R.id.loginFB);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiMapsAdapter adap = new ApiMapsAdapter();
                String result = null;
                try {
                    result = adap.loginFB();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MyToast t = new MyToast(getActivity().getApplicationContext());
                t.setText(result);
                t.show();
            }
        });

        return rootView;
    }
}
