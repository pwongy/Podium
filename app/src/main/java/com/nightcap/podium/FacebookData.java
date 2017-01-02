package com.nightcap.podium;

/**
 * Created by Paul on 19/02/2016.
 */
public class FacebookData {
    String name;
    int audience;
    String topic;
    String path;
    String emojiCode, emojiExtra;
    boolean popularityLow = false;

    public FacebookData(String[] data) {
        setName(data[0]);
        setAudience(Integer.valueOf(data[1]));
        setTopic(data[2]);
        setPath(data[3]);
        setEmojiCode(data[4]);
        setEmojiExtra(data[5]);
    }

    public void setName(String name_from_csv) {
        name = name_from_csv;
    }

    public void setAudience(int audience_from_csv) {
        audience = audience_from_csv;
    }

    public void setTopic(String topic_from_csv) {
        topic = topic_from_csv;
    }

    public void setPath(String path_from_csv) {
        path = path_from_csv;
    }

    public void setEmojiCode(String emoji_from_csv) {
        emojiCode = emoji_from_csv;
    }

    public void setEmojiExtra(String emoji_extra_csv) {
        emojiExtra = emoji_extra_csv;
    }
}
