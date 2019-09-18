package com.example.ammaryasser.musicstream;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    //    private final File MyDirectory = new File(Environment.getExternalStorageDirectory() + "/Music Stream");
    private EditText url_ET;
    private ImageButton playlist_btn,
            play_btn,
            stop_btn,
            prev_btn,
            next_btn;
    private ProgressBar progress_Bar;
    private ListView listView;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private DatabaseHelper db;

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;

    private static int url_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        if (!MyDirectory.exists())
//            if (!MyDirectory.mkdir())
//                Toast.makeText(this, "Can't create the App Directory!", Toast.LENGTH_SHORT).show();

//        db = new DatabaseHelper(this);

        url_ET = findViewById(R.id.url_ET);
        playlist_btn = findViewById(R.id.playlist_btn);
        progress_Bar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.listview);
        seekBar = findViewById(R.id.seekbar);
        play_btn = findViewById(R.id.play_btn);
        stop_btn = findViewById(R.id.stop_btn);
//        prev_btn = findViewById(R.id.prev_btn);
//        next_btn = findViewById(R.id.next_btn);

        playlistListener();
        listView.setAdapter(arrayAdapter);
        listViewListener();

        playListener();
        stopListener();
//        prevListener();
//        nextListener();
    }

    private void play_song(int id) {
        String songUrl = arrayList.get(id);
        new MusicStream().execute(songUrl);
    }

//    private void release_obj() {
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }

    private void playlistListener() {
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);

        playlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = url_ET.getText().toString();
                if (!url.matches("[ ]*"))
                    arrayList.add(url.replace(" ", ""));
                else
                    Snackbar.make(view, "Please enter valid url", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                arrayAdapter.notifyDataSetChanged();
                url_ET.setText("");
            }
        });
    }

    private void listViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                url_ID = (int) id;
                play_song(url_ID);
            }
        });
    }

    private void playListener() {
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer == null) {
                    if (arrayList.isEmpty())
                        Snackbar.make(view, "Please provide at least one url", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    else {
                        //play
                        Snackbar.make(view, "Preparing Music", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        url_ID = 0;
                        play_song(url_ID);
                    }
                } else if (mediaPlayer.isPlaying()) {
                    //pause
                    mediaPlayer.pause();
                    play_btn.setImageResource(R.drawable.ic_play);
                } else {
                    //resume
                    mediaPlayer.start();
                    play_btn.setImageResource(R.drawable.ic_pause);
                }
            }
        });
    }

    private void stopListener() {
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                release_obj();
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    play_btn.setImageResource(R.drawable.ic_play);
                }
            }
        });
    }

    final Handler handler = new Handler();
    int mediafilelength;

    private void updateSeekBar() {
        seekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediafilelength) * 100));
        if (mediaPlayer.isPlaying()) {
            Runnable updater = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                }
            };
            handler.postDelayed(updater, 1000);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        play_btn.setImageResource(R.drawable.ic_play);
    }

//    private void prevListener() {
//        if (url_ID - 1 >= 0) {
////            release_obj();
//            if (mediaPlayer != null) {
//                mediaPlayer.release();
//                mediaPlayer = null;
//            }
//            play_song(--url_ID);
//        }
//    }

//    private void nextListener() {
//        if (url_ID + 1 < arrayList.size()) {
////            release_obj();
//            if (mediaPlayer != null) {
//                mediaPlayer.release();
//                mediaPlayer = null;
//            }
//            play_song(++url_ID);
//        }
//    }

    class MusicStream extends AsyncTask<String, Integer, MediaPlayer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            play_btn.setEnabled(false);
            stop_btn.setEnabled(false);
            progress_Bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(MediaPlayer mediaPlayer) {
            super.onPostExecute(mediaPlayer);
            play_btn.setEnabled(true);
            play_btn.setImageResource(R.drawable.ic_pause);
            stop_btn.setEnabled(true);
            progress_Bar.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            seekBar.setProgress(values[0]);
        }

        @Override
        protected MediaPlayer doInBackground(String... strings) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediafilelength = mediaPlayer.getDuration();

            } catch (IOException io) {
                io.printStackTrace();
            } catch (IllegalArgumentException ie) {
                ie.printStackTrace();
            }
            return mediaPlayer;
        }

        private void updateSeekbar() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
        }
    }

//    class DownloadSong extends AsyncTask<String, Integer, Integer> {
//        @Override
//        protected Integer doInBackground(String... strings) {
//            try {
//                URL url = new URL(strings[0]);
//                URLConnection con = url.openConnection();
//                con.connect();
//
//                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
//                OutputStream outputStream = new FileOutputStream(extractFileName(strings[0]));
//
//                final int file_length = con.getContentLength();
//                byte data[] = new byte[1024];
//                int total = 0,
//                        count;
//
//                while ((count = inputStream.read(data)) != -1) {
//                    total += count;
//                    publishProgress(total / file_length * 100);
//                    outputStream.write(data, 0, count);
//                }
//
//                outputStream.flush();
//
//                outputStream.close();
//                inputStream.close();
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            setComponentStatus(false);
//            Toast.makeText(MainActivity.this, "Download Started", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        protected void onPostExecute(Integer integer) {
//            super.onPostExecute(integer);
//            setComponentStatus(true);
//            Toast.makeText(MainActivity.this, "Download Finished!\nYou can play now", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//            progress_Bar.setProgress(values[0]);
//        }
//
//    }

//    private String extractFileName(String url) {
//        final String name = url.substring(url.lastIndexOf('/'), url.lastIndexOf('.')),
//                ext = url.substring(url.lastIndexOf('.'), url.length());
//        File file = new File(MyDirectory, name + ext);
//        int n = 1;
//
//        while (file.exists()) file = new File(MyDirectory, name + n++ + ext);
//
//        return songUrl = file.toString();
//    }

//    private void setComponentStatus(boolean b) {
//        playlist_btn.setEnabled(b);
//        play_btn.setEnabled(b);
//        pause_fab.setEnabled(b);
//        stop_btn.setEnabled(b);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
            return true;
        else if (id == R.id.about)
            return true;

        return super.onOptionsItemSelected(item);
    }
}