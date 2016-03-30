package com.szycha.amazingradio;

import android.bluetooth.le.ScanRecord;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.szycha.amazingradio.Adapter.RecyclerItemClickListener;
import com.szycha.amazingradio.Adapter.TabAdapterRecycler;
import com.szycha.amazingradio.AppService.StreamService;
import com.szycha.amazingradio.AppService.Utils;

public class ScrollingActivity extends AppCompatActivity implements Animation.AnimationListener {


    static int IMAGE_NUMBER = 1;
    private Animation animZoomIn, fadeIn, fadeOut;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private TabAdapterRecycler adapter;

    private Intent serviceIntent;
    private static boolean isStreaming = false;
    private int positionGrana = -1;
    private String radio_station = null;
    private boolean mBufferBroadcastIsRegistered;
    private Handler handlerInfo;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Grid
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMiasto);
        recyclerView.setHasFixedSize(true);
        //Tabelet
        if (getResources().getBoolean(R.bool.isSmallerTablet)) {

            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggeredGridLayoutManager);

            //Phone
        } else {

            /*TYLKO DLA TELEFONU :
            dwa detektory Landscape /Portret
            Podklatalog values-port oraz values land
            po detekcji przy telefonie zmienia układ
             */
            if (!getResources().getBoolean(R.bool.is_landscape)) {
                LinearLayoutManager llm = new LinearLayoutManager(this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(llm);
            } else {
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(staggeredGridLayoutManager);
            }
        }

        adapter = new TabAdapterRecycler(this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Log.d("playStatus", "" + isStreaming);
                isStreaming = Utils.getDataBooleanFromSP(ScrollingActivity.this, Utils.IS_STREAM);
                Log.i("###", " : " + adapter.getLink(position));
                serviceIntent = new Intent(ScrollingActivity.this, StreamService.class);
                serviceIntent.putExtra("radio_link", adapter.getLink(position));
                serviceIntent.putExtra("nazwa", adapter.getNazwa(position));
                radio_station = adapter.getNazwa(position);

                //Zmiana playa na pause

                if (!isStreaming) {

                    adapter.setImagePLayPause(position, R.drawable.pause);
                    startStreaming();
                    Utils.setDataBooleanToSP(ScrollingActivity.this, Utils.IS_STREAM, true);
                    Utils.setPosition(ScrollingActivity.this, Utils.POSITION_DATA, position);
                    Utils.setNazwa(ScrollingActivity.this, Utils.NAZWA_RADIA, adapter.getNazwa(position));
                } else {

                    if (isStreaming) {
                        //imagePLayPause.setImageResource(R.drawable.play);
                        //Toast.makeText(ScrollingActivity.this, "Stop Streaming..", Toast.LENGTH_SHORT).show();
                        stopStreaming();
                        isStreaming = false;
                        Utils.setDataBooleanToSP(ScrollingActivity.this, Utils.IS_STREAM, false);
                        Utils.setPosition(ScrollingActivity.this, Utils.POSITION_DATA, -1);
                        adapter.setImagePLayPause(position, R.drawable.play);
                        collapsingToolbarLayout.setTitle("");
                    }
                }
            }
        }));

        String radia[] = {"Radio Relax",
                "BBC Radio ONE",
                "BBC Radio 1XTRA",
                "Amazing Radio",
                "BBC Radio 4",};

        String image[] = {"file:///android_asset/relax.jpg",
                "file:///android_asset/bbc_radio_one.jpg",
                "file:///android_asset/bbc_radio_xtra.jpg",
                "file:///android_asset/radio_amazing.jpg",
                "file:///android_asset/bbc_radio_four.png"
        };
        String opis[] = {"Chill plays a wide range of chillout music from around the world",
                "Listen to BBC Radio 1, home of the Official Chart, the Live Lounge and the world's ",
                "BBC 1Xtra is a UK digital radio station from the BBC that plays the best hip hop, R&B",
                "Only radio station dedicated exclusively to playing new and emerging music from independent",
                "Radio 4, Speech based news, current affairs and factual network. Includes detailed programme information, audio clips and listings. Available on FM, DAB"};
        String radio_link[] = {
                "http://media-sov.musicradio.com:80/ChillMP3",
                "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio1_mf_p",
                "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio1xtra_mf_p",
                "http://109.74.195.10:8000",
                "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio4fm_mf_p"

        };
        for (int a = 0; a < radia.length; a++) {
            adapter.addDane(opis[a], radia[a], image[a], radio_link[a], "Data", R.drawable.play);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        //Start animatiom toolbar
        animZoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // listeners for animation effects..
        animZoomIn.setAnimationListener(this);
        fadeOut.setAnimationListener(this);
        fadeIn.setAnimationListener(this);
        collapsingToolbarLayout.startAnimation(animZoomIn);

        //Start
        isStreaming = Utils.getDataBooleanFromSP(this, Utils.IS_STREAM);
        positionGrana = Utils.getPosition(this, Utils.POSITION_DATA);
        collapsingToolbarLayout.setTitle(Utils.getRadioNazwa(this, Utils.NAZWA_RADIA));

        if (positionGrana != -1) {
            adapter.setImagePLayPause(positionGrana, R.drawable.pause);
        }

        handlerInfo = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String info = msg.getData().getString("info");
                if (info != null) {
                    //Toast.makeText(getApplicationContext(), ":" + info.toString(), Toast.LENGTH_SHORT).show();
                    collapsingToolbarLayout.setTitle(info);
                }
            }
        };

        //Jak powraca z notyfikacji to spisuje nazwe radia
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Log.i("dd", "Extra:" + extras.getString("radio"));
        }
    }

        @Override
        protected void onPause () {
            super.onPause();
            if (mBufferBroadcastIsRegistered) {
                unregisterReceiver(broadcastBufferReceiver);
                mBufferBroadcastIsRegistered = false;
            }
        }

        @Override
        protected void onResume () {
            super.onResume();
            if (!mBufferBroadcastIsRegistered) {
                registerReceiver(broadcastBufferReceiver, new IntentFilter(StreamService.BROADCAST_BUFFER));
                mBufferBroadcastIsRegistered = true;
            }
        }

    private void startStreaming() {
        //Toast.makeText(this, "Start Streaming..", Toast.LENGTH_SHORT).show();
        stopStreaming();
        try {
            startService(serviceIntent);
        } catch (Exception e) {}
    }

    private void stopStreaming() {
        try {
            stopService(serviceIntent);
        } catch (Exception e) {}
    }

    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            String bufferValue = bufferIntent.getStringExtra("buffering");
            int bufferIntValue = Integer.parseInt(bufferValue);
            switch (bufferIntValue) {
                case 0:
                    Message m = new Message();
                    Bundle b = new Bundle();
                    b.putString("info", radio_station); // for example
                    m.setData(b);
                    handlerInfo.sendMessage(m);
                    break;
                case 1:
                    collapsingToolbarLayout.setTitle("Buffering ...");
                    //Pobieranie danych beffering
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAnimationEnd(Animation animation) {

        if (animation == animZoomIn) {
            collapsingToolbarLayout.startAnimation(fadeOut);
        } else if (animation == fadeOut) {
            if (IMAGE_NUMBER == 1) {
                IMAGE_NUMBER = 2;
                collapsingToolbarLayout.setBackgroundResource(R.drawable.radio_one);
                collapsingToolbarLayout.startAnimation(fadeIn);
            } else if (IMAGE_NUMBER == 2) {
                IMAGE_NUMBER = 3;
                collapsingToolbarLayout.setBackgroundResource(R.drawable.radio_two);
                collapsingToolbarLayout.startAnimation(fadeIn);
            } else {
                IMAGE_NUMBER = 1;
                collapsingToolbarLayout.setBackgroundResource(R.drawable.radio_three);
                collapsingToolbarLayout.startAnimation(fadeIn);
            }

        } else if (animation == fadeIn) {
            collapsingToolbarLayout.startAnimation(animZoomIn);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }
}
