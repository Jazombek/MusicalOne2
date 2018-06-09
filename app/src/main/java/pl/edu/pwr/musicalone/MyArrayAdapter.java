package pl.edu.pwr.musicalone;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;



public class MyArrayAdapter extends ArrayAdapter {

    private ArrayList songs;
    private MainMenu mainActivity;
    private TextView titleTv;
    private TextView artistTv;
    private TextView lengthTv;
    private ConstraintLayout cl;

    public MyArrayAdapter(Context context, ArrayList<Song> songs) {

        super(context, 0, songs);

        this.songs=songs;
        this.mainActivity=(MainMenu)context;
    }


    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int pos) {
        return songs.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Song song = (Song)getItem(position);


        /*if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_layout, parent, false);
        }*/
        LayoutInflater inflater = (LayoutInflater) mainActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.song_layout, null);
        }






        titleTv =  convertView.findViewById(R.id.titleTextView);
        artistTv =  convertView.findViewById(R.id.artistTevtView);
        lengthTv = convertView.findViewById(R.id.timeTextView);
        cl=convertView.findViewById(R.id.songback);
        final ImageButton play = convertView.findViewById(R.id.playImageButton) ;

        play.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mainActivity.setTitle(song.getTitle());
                mainActivity.playSong(song.getMediaPath());
                //
                //mainActivity.startService(new Intent(MyService.ACTPLAY));
                //mainActivity.servicePlay(position);

            }

        });



        titleTv.setText(song.getTitle());
        artistTv.setText(song.getArtist());
        lengthTv.setText(song.getSeconds());
        updateColor();



        return convertView;
    }

    public void updateColor(){
        int color=mainActivity.getColorPrim();
        titleTv.setTextColor(color);
        artistTv.setTextColor(color);
        lengthTv.setTextColor(color);
        int color2=mainActivity.getColorSec();
        cl.setBackgroundColor(color2);

    }




}