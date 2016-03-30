package com.szycha.amazingradio.Adapter;

/**
 * Created by poldek on 05.12.14.
 */
public class ItemData {

    private String title;
    private String imageUrl;
    private String descryption;
    private String link;
    private String data;
    private int image;

    public ItemData(String descryption, String title, String imageUrl, String link, String data, int image) {
        this.descryption = descryption;
        this.title = title;
        this.imageUrl = imageUrl;
        this.link = link;
        this.data = data;
        this.image = image;

    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }


    public String getImageUrl ()
    {

        return imageUrl;
    }

    public void setImageUrl (String imageUrl)
    {
        this.imageUrl = imageUrl;
    }


    public String getDescryption () {
        return descryption;
    }

    public void setDescryption (String descryption) {
        this.descryption = descryption;
    }


    public String getLink () {
        return link;
    }

    public void setLink (String link) {
        this.link = link;
    }

    public String getData () {
        return data;
    }

    public void setData (String data) {
        this.data = data;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }
}
