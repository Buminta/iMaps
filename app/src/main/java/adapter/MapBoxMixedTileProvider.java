package adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;


import net.windjs.imaps.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import Encrypt.RC4;
import global.GlobalClass;

/**
 * Created by me866chuan on 8/27/14.
 */
public class MapBoxMixedTileProvider implements TileProvider {

    private final MapBoxOnlineTileProvider provider;
    private final Context context;
    private byte[] rc4_key = "key_windjs".getBytes();
    public MapBoxMixedTileProvider(final Context context) throws Exception {
        provider = new MapBoxOnlineTileProvider();
        this.context = context;
    }


    @Override
    public Tile getTile(final int arg0, final int arg1, final int arg2) {
        if(arg2 <= 19 && arg2 >= 5){
            final URL url = provider.getTileUrl(arg0, arg1, arg2);
            final String finalUrl = (url.toString().substring(9)).replace('/', '_')
                    .replace('.', '_').replace("_png", ".png");

            boolean contained = false;

            RC4 encRC4 = new RC4(rc4_key);

            for (final String file : context.fileList()) {
                if (file.contains(finalUrl)) {
                    contained = true;
                    break;
                }
            }
            try {
                if (contained) {
                    FileInputStream fis = context.openFileInput(finalUrl);
                    return new Tile(256, 256, encRC4.decrypt(byteArrayFromInputStream(fis)));
                } else {
                    Tile t = provider.getTile(arg0, arg1, arg2);
                    if(t.data.length > 0) {
                        FileOutputStream fos = context.openFileOutput(finalUrl,
                                Context.MODE_PRIVATE);
                        fos.write(encRC4.encrypt(t.data));
                        fos.write(t.data);
                        fos.flush();
                        fos.close();
                        return t;
                    }
//                    MapOpenStreetTileProvider tmp = new MapOpenStreetTileProvider();
//                    t = tmp.getTile(arg0, arg1, arg2);
//                    if(t.data.length > 0) return t;
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Resources res = context.getResources();
        Drawable drawable = res.getDrawable(R.drawable.tile_default);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitMapData = stream.toByteArray();
        return new Tile(256, 256, bitMapData);
    }

    private byte[] byteArrayFromInputStream(final FileInputStream is)
            throws IOException {
        final BufferedInputStream bis = new BufferedInputStream(is);
        final ArrayList<Integer> bytes = new ArrayList<Integer>();
        int current = 0;
        while ((current = bis.read()) != -1) {
            bytes.add(Integer.valueOf(current));
        }
        final byte[] bs = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            bs[i] = bytes.get(i).byteValue();
        }
        bis.close();
        is.close();
        return bs;
    }

}