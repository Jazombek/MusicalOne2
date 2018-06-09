package pl.edu.pwr.musicalone;

import android.net.Uri;

public class Song
{
    private Integer songId;
    private String title;
    private String artist;
    private Uri mediaPath;
    private String seconds;
    public Song(Integer id, String title, String artist, Uri mp, String seconds)
    {
        this.songId=id;
        this.title=title;
        this.artist=artist;
        this.mediaPath=mp;
        int tempSec = Integer.parseInt(seconds);
        tempSec=tempSec/1000;
        int tempMin = tempSec/60;
        tempSec=tempSec%60;
        //this.seconds=(Integer.toString(tempMin)+":"+Integer.toString(tempSec));
        this.seconds=String.format("%1$s : %2$2.2s", Integer.toString(tempMin),Integer.toString(tempSec));

    }


    public Integer getSongId() {
        return songId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Uri getMediaPath(){return mediaPath;}

    public String getSeconds(){return seconds;}
}
