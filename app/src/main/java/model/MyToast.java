package model;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.windjs.imaps.R;

import global.GlobalClass;

/**
 * Created by me866chuan on 8/25/14.
 */
public class MyToast extends Toast{
    private View layout;
    private ImageView imageView;
    private TextView text;
    private TextView title;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     */
    public MyToast(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (View) inflater.inflate(R.layout.toast_view, null);
        text = (TextView) layout.findViewById(R.id.like_popup_tv);
        imageView = (ImageView) layout.findViewById(R.id.like_popup_iv);
        title = (TextView) layout.findViewById(R.id.textBar);
        setView(layout);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setStyle(Drawable background){
        layout.setBackground(background);
    }

    public void setImage(int resource){
        imageView.setImageResource(resource);
    }

    public void setText(String txt){
        text.setText(txt);
    }
    public void setTitle(String txt){
        title.setText(txt);
    }
}
