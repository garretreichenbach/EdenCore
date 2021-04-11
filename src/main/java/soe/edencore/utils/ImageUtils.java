package soe.edencore.utils;

import api.utils.textures.StarLoaderTexture;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import soe.edencore.EdenCore;
import soe.edencore.server.bot.EdenBot;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    private final static String emotesPath = DataUtils.getWorldDataPath() + "/images/emotes";
    private final static String avatarsPath = DataUtils.getWorldDataPath() + "/images/avatars";

    private final static ConcurrentHashMap<Long, File> emoteCache = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Long, File> avatarCache = new ConcurrentHashMap<>();

    public static void initialize() {
        loadEmotes();
        loadAvatars();
    }

    private static void loadEmotes() {
        File emotesFolder = new File(emotesPath);
        if(!emotesFolder.exists()) emotesFolder.mkdirs();
        else {
            if(emotesFolder.listFiles() != null && emotesFolder.listFiles().length > 0) {
                for(File emoteFile : emotesFolder.listFiles()) {
                    long emoteId = Long.parseLong(emoteFile.getName().substring(0, emoteFile.getName().lastIndexOf(".") - 1));
                    try {
                        emoteCache.put(emoteId, emoteFile);
                    } catch(Exception exception) {
                        exception.printStackTrace();
                        Emote emote = getEmoteFromId(emoteId);
                        if(emote != null) LogUtils.logMessage(MessageType.WARNING, "Failed to load emote " + emote.getName() + ".");
                        else LogUtils.logMessage(MessageType.ERROR, "Failed to find any emotes with matching ids to " + emoteId + ".");
                    }
                }
            }
        }
    }

    private static void loadAvatars() {
        File avatarsFolder = new File(avatarsPath);
        if(!avatarsFolder.exists()) avatarsFolder.mkdirs();
        else {
            if(avatarsFolder.listFiles() != null && avatarsFolder.listFiles().length > 0) {
                for(File avatarFile : avatarsFolder.listFiles()) {
                    long userId = Long.parseLong(avatarFile.getName().substring(0, avatarFile.getName().lastIndexOf(".") - 1));
                    try {
                        avatarCache.put(userId, avatarFile);
                    } catch(Exception exception) {
                        exception.printStackTrace();
                        User user = null;
                        try {
                            user = getBot().bot.retrieveUserById(userId).complete(true);
                        } catch (RateLimitedException e) {
                            e.printStackTrace();
                            user = getBot().bot.retrieveUserById(userId).complete();
                        }
                        if(user != null) LogUtils.logMessage(MessageType.WARNING, "Failed to load avatar for user " + user.getName() + ".");
                        else LogUtils.logMessage(MessageType.ERROR, "Failed to find any users matching id " + userId + ".");
                    }
                }
            }
        }
    }

    @Nullable
    public static InputStream getImage(String url) {
        InputStream imageStream = imgCache.get(url);
        if(imageStream != null) return imageStream;
        else {
            fetchImage(url);
            return null;
        }
    }

    public static Emote getEmoteFromId(long emoteId) {
        for(Emote emote : getBot().bot.getGuildById(EdenCore.instance.serverId).getEmotes()) {
            if(emote.getIdLong() == emoteId) return emote;
        }
        return null;
    }

    public static File getEmoteImage(Emote emote) {
        if(emoteCache.containsKey(emote.getIdLong())) {
            try {
                return emoteCache.get(emote.getIdLong());
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        } else fetchEmote(emote);
        try {
            return emoteCache.get(emote.getIdLong());
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static File getAvatarImage(long userId) {
        if(userId == getBot().bot.getSelfUser().getIdLong()) return new File(avatarsPath + "/" + userId + ".png");
        if(avatarCache.containsKey(userId)) {
            try {
                return avatarCache.get(userId);
            } catch(Exception exception) {
                exception.printStackTrace();
                User user = null;
                try {
                    user = getBot().bot.retrieveUserById(userId).complete(true);
                } catch (RateLimitedException e) {
                    e.printStackTrace();
                    user = getBot().bot.retrieveUserById(userId).complete();
                }
                if(user != null) LogUtils.logMessage(MessageType.WARNING, "Failed to download avatar for user " + user.getName() + ".");
                else LogUtils.logMessage(MessageType.ERROR, "Failed to find any users matching id " + userId + ".");
            }
        } else fetchAvatar(userId);
        try {
            return avatarCache.get(userId);
        } catch(Exception exception) {
            exception.printStackTrace();
        }
        return new File(avatarsPath + "/" + getBot().bot.getSelfUser().getIdLong() + ".png");
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

    private static void fetchAvatar(long userId) {
        User user = null;
        try {
            user = getBot().bot.retrieveUserById(userId).complete(true);
        } catch(RateLimitedException e) {
            e.printStackTrace();
            user = getBot().bot.retrieveUserById(userId).complete();
        }
        if(user != null) {
            String url = (user.getEffectiveAvatarUrl().equals("https://cdn.discordapp.com/embed/avatars/0.png")) ? "https://i.imgur.com/fCRCVIn.png" : user.getAvatarUrl();
            if(!downloadingImages.contains(url)) {
                new Thread() {
                    @Override
                    public void run() {
                        downloadingImages.add(url);
                        String type = (url.contains("?")) ? url.substring(url.lastIndexOf("."), url.lastIndexOf("?") - 1) : url.substring(url.lastIndexOf("."));
                        StarLoaderTexture.runOnGraphicsThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    avatarCache.put(userId, writeToFile(avatarsPath + "/" + userId + type, new URL(url)));
                                } catch(Exception exception) {
                                    exception.printStackTrace();
                                    LogUtils.logMessage(MessageType.ERROR, "Failed to download avatar image from url " + url + ".");
                                }
                            }
                        });
                        downloadingImages.remove(url);
                    }
                }.start();
            }
        }
    }

    private static void fetchEmote(Emote emote) {
        if(!downloadingImages.contains(emote.getImageUrl())) {
            new Thread() {
                @Override
                public void run() {
                    String url = emote.getImageUrl();
                    if(!downloadingImages.contains(url)) {
                        new Thread() {
                            @Override
                            public void run() {
                                downloadingImages.add(url);
                                StarLoaderTexture.runOnGraphicsThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            emoteCache.put(emote.getIdLong(), writeToFile(emotesPath + "/" + emote.getIdLong() + ".png", new URL(url)));
                                        } catch(Exception exception) {
                                            exception.printStackTrace();
                                            LogUtils.logMessage(MessageType.ERROR, "Failed to download emote image from url " + url + ".");
                                        }
                                    }
                                });
                                downloadingImages.remove(url);
                            }
                        }.start();
                    }
                }
            }.start();
        }
    }

    private static File writeToFile(String filePath, URL url) {
        File newFile = new File(filePath);
        try {
            if(newFile.exists()) newFile.delete();
            newFile.createNewFile();
            ImageIO.write(ImageIO.read(url), filePath.substring(filePath.lastIndexOf(".")), newFile);
        } catch(IOException exception) {
            exception.printStackTrace();
            LogUtils.logMessage(MessageType.ERROR, "Failed to write data to " + filePath + ".");
        }
        return newFile;
    }

    public static InputStream fromURL(String u) {
        InputStream inputStream = null;
        try {
            URL url = new URL(u);
            inputStream = url.openStream();
        } catch(IOException e) {
            e.printStackTrace();
            LogUtils.logMessage(MessageType.ERROR, "Failed to get input stream from url " + u + ".");
        }
        return inputStream;
    }

    private static EdenBot getBot() {
        return EdenCore.instance.botThread.getBot();
    }
}
