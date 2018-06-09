package pl.edu.pwr.musicalone;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;


public class MainMenu extends AppCompatActivity {

    public  static MediaPlayer mainPlayer;
    private MyArrayAdapter adapter;
    private ArrayList<Song> arrL;
    private ImageButton pause;
    private TextView title;
    private ImageButton forward;
    private ImageButton backward;
    private ConstraintLayout constraintLayout;
    private ListView lv;
    private Boolean random = true;
    private Boolean play = true;
    private Random rand = new Random();
    private String playMode = "None";
    private int currPos,prevPos=0,temp, colorSec = Color.WHITE, colorPrim=Color.BLACK;
    private MyService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    public SeekBar seekBar;
    public Handler mSeekbarUpdateHandler=new Handler();
    public Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(mainPlayer.getCurrentPosition());
            mSeekbarUpdateHandler.postDelayed(this, 50);
        }
    };

    void fillArrL() {
        arrL = new ArrayList<>();


        MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();

        int resourceID;
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            Log.i("Raw Asset: ", field.getName());
            try {
                resourceID = field.getInt(field);
                Uri mediaPath = Uri.parse("android.resource://" + this.getPackageName() + "/" + resourceID);
                metaRetriver.setDataSource(this, mediaPath);

                String artist = metaRetriver
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String title = metaRetriver
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String time = metaRetriver
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                arrL.add(new Song(resourceID, title, artist, mediaPath, time));


            } catch (IllegalAccessException e) {
                //e.printStackTrace();
            }
        }
    }



    public int getColorPrim(){
        return colorPrim;
    }

    public int getColorSec(){
       return colorSec;
    }

    public void swapColors(){
        if (colorPrim==Color.BLACK){
            colorPrim=Color.WHITE;
            colorSec=Color.BLACK;
        }
        else{
            colorSec=Color.WHITE;
            colorPrim=Color.BLACK;
        }
    }

    public void setPlayMode(String mode){
        playMode=mode;
    }

    private void stopPlaying() {
        if (mainPlayer != null) {
            mainPlayer.stop();
            mainPlayer.release();
            mainPlayer = null;
        }
    }

    private void updateSettings(){
        MyArrayAdapter adapter=new MyArrayAdapter(this,arrL);
        lv.setAdapter(adapter);
        title.setTextColor(colorPrim);
        constraintLayout.setBackgroundColor(colorSec);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        android.support.v7.widget.Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        ab.setBackgroundDrawable(getResources().getDrawable(R.color.verdigris));

        title = findViewById(R.id.title);
        pause = findViewById(R.id.pause);
        forward = findViewById(R.id.forward);
        backward = findViewById(R.id.back);
        seekBar = findViewById(R.id.progressBar);
        constraintLayout=findViewById(R.id.constraintL);


        lv = findViewById(R.id.list);

        fillArrL();


        adapter = new MyArrayAdapter(this, arrL);
        lv.setAdapter(adapter);


        if(savedInstanceState!=null)
        {
            if(mainPlayer==null){
                temp=savedInstanceState.getInt("song");
                mainPlayer = MediaPlayer.create(this, arrL.get(temp).getSongId());
                mainPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {


                        nextSong();
                    }
                });
                mainPlayer.seekTo(savedInstanceState.getInt("keyPos"));
            }

            temp=savedInstanceState.getInt("song");
            seekBar.setMax(mainPlayer.getDuration());
            title.setText(arrL.get(temp).getTitle());
            //mainPlayer.seekTo(savedInstanceState.getInt("keyPos"));



            if(savedInstanceState.getBoolean("isPlaying")){
                mainPlayer.start();
            }
            else {
                mainPlayer.stop();
            }

            if(savedInstanceState.getBoolean("dark")){
                swapColors();
            }
        }else {
            if(mainPlayer==null){
                temp=0;
                mainPlayer = MediaPlayer.create(this, arrL.get(0).getSongId());
                seekBar.setMax(mainPlayer.getDuration());
                title.setText(arrL.get(0).getTitle());
                mainPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {


                        nextSong();
                    }
                });
            }
            if(!mainPlayer.isPlaying())
                mainPlayer.start();
        }

//lol zostawiłeś laptop
        //i co z tego?



        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mainPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        pause.setOnClickListener(pauseListener());
        forward.setOnClickListener(forwardListener());
        backward.setOnClickListener(backwardListener());
        updateSettings();




}
/*
public void servicePlay(int pos){
    musicSrv.setSong(pos);
    musicSrv.playSong();
}
    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MyService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }*/

    private void nextSong(){

        if(!playMode.equals("None")){

            if(playMode.equals("Random")){
                while(temp==currPos){
                    temp=rand.nextInt(arrL.size());
                }


            }
            else{
                temp=(currPos+1)%arrL.size();
            }
            prevPos=currPos;
            currPos=temp;
            setTitle(arrL.get(temp).getTitle());
            playSong(arrL.get(temp).getMediaPath());

        }

    }

    public void playSong(Uri uri) {
        mainPlayer.reset();
        try {
            mainPlayer.setDataSource(this,uri);
            mainPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainPlayer.start();
        seekBar.setMax(mainPlayer.getDuration());
        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
    }


    View.OnClickListener forwardListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainPlayer.getCurrentPosition()+10000<=mainPlayer.getDuration())
                    mainPlayer.seekTo(mainPlayer.getCurrentPosition()+10000);
                else
                    nextSong();
            }
        };

    }
    View.OnClickListener backwardListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainPlayer.getCurrentPosition()-10000>=0)
                    mainPlayer.seekTo(mainPlayer.getCurrentPosition()-10000);
                else
                    if(currPos!=prevPos){
                        currPos=prevPos;
                        setTitle(arrL.get(currPos).getTitle());
                        playSong(arrL.get(currPos).getMediaPath());
                    }
            }
        };

    }
    View.OnClickListener pauseListener()
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainPlayer.isPlaying())
                {
                    mainPlayer.pause();
                    pause.setImageDrawable(getResources().getDrawable(R.drawable.play));
                    mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);

                }
                else
                {
                    mainPlayer.start();
                    pause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);

                }
            }
        };

    }

    public void setTitle(String t){
        title.setText(t);
    }

    @Override
    protected void onDestroy()
    {

        //stopPlaying();
        //stopService(playIntent);
        //musicSrv=null;
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putInt("keyPos", mainPlayer.getCurrentPosition());
        outState.putBoolean("isPlaying",mainPlayer.isPlaying());
        outState.putBoolean("isLooping",mainPlayer.isLooping());
        outState.putBoolean("random",random);
        outState.putString("mode", playMode);
        outState.putInt("song",currPos);
        outState.putBoolean("dark",colorPrim==Color.WHITE);

        //outState.putInt("background",constraintLayout.getSolidColor());
        //outState.putInt("keyPos", mainPlayer.getAudioSessionId());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:

                Intent i2= new Intent(MainMenu.this,Settings.class);
                i2.putExtra("isDark",colorPrim==Color.WHITE);
                i2.putExtra("mode",playMode);
                startActivityForResult(i2,0);

                break;
            case R.id.author:


                Intent i=new Intent(MainMenu.this,Author.class);
                i.putExtra("color", colorPrim);
                i.putExtra("color2",colorSec);
                startActivity(i);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(this,Boolean.toString(data.getBooleanExtra("dark2",false)),Toast.LENGTH_SHORT).show();
        //Boolean asdf = data.getBooleanExtra("dark2", false);
        //if(asdf)swapColors();
        constraintLayout=findViewById(R.id.constraintL);
        if (requestCode == 0) {
            if(resultCode == Activity.RESULT_OK){




                if(data.getBooleanExtra("dark",false)){
                    colorPrim=Color.WHITE;
                    colorSec=Color.BLACK;
                }

                else{
                    colorPrim=Color.BLACK;
                    colorSec=Color.WHITE;
                }

                updateSettings();
                playMode=data.getStringExtra("loop");
                switch (playMode){
                    case("None"):
                        play = false;
                        random=false;
                        break;
                    case ("In order"):
                        play=true;
                        random=false;
                        break;
                    case("Random"):
                        play=true;
                        random=true;
                }
            }

        }
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(arrL);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

}
