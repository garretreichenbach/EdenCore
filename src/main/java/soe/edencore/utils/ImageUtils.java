package soe.edencore.utils;

import api.utils.textures.StarLoaderTexture;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ImageUtils
 * <Description>
 *
 * @author TheDerpGamer
 * @since 04/08/2021
 */
public class ImageUtils {

    private final static ConcurrentHashMap<String, InputStream> imgCache = new ConcurrentHashMap<>();
    private final static ConcurrentLinkedQueue<String> downloadingImages = new ConcurrentLinkedQueue<>();

    @Nullable
    public static InputStream getImage(String url) {
        InputStream imageStream = imgCache.get(url);
        if(imageStream != null) return imageStream;
        else {
            fetchImage(url);
            return null;
        }
    }

    private static void fetchImage(final String url) {
        if(!downloadingImages.contains(url)) {
            new Thread() {
                @Override
                public void run() {
                    downloadingImages.add(url);
                    final InputStream imageStream = fromURL(url);
                    StarLoaderTexture.runOnGraphicsThread(new Runnable() {
                        @Override
                        public void run() {
                            imgCache.put(url, imageStream);
                        }
                    });
                    downloadingImages.remove(url);
                }
            }.start();
        }
    }

    private static InputStream fromURL(String u) {
        try {
            URL url = new URL(u);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "NING/1.0");
            return urlConnection.getInputStream();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
