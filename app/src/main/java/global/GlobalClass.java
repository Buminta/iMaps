package global;

/**
 * Created by me866chuan on 8/24/14.
 */
import android.app.Application;

public class GlobalClass extends Application{

    private String name;
    private String email;
    private int oldFragment = -1;
    private float zoomFloat;
    private String oldFragmentS = "";


    public String getName() {

        return name;
    }

    public void setName(String aName) {

        name = aName;

    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String aEmail) {

        email = aEmail;
    }

    public int getOldFragment() {
        return oldFragment;
    }

    public void setOldFragment(int oldFragment) {
        this.oldFragment = oldFragment;
    }

    public float getZoomFloat() {
        return zoomFloat;
    }

    public void setZoomFloat(float zoomFloat) {
        this.zoomFloat = zoomFloat;
    }

    public String getOldFragmentS() {
        return oldFragmentS;
    }

    public void setOldFragmentS(String oldFragmentS) {
        this.oldFragmentS = oldFragmentS;
    }
}