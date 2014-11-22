package ua.ck.geekhub.android.dubiy.rss.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import ua.ck.geekhub.android.dubiy.rss.R;

public class ThreadTest extends Activity {

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {

            switch (view.getId()) {
                case R.id.btn_thread: {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //tv1.setText("new Thread"); // exception
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv1.setText("new Thread (runOnUiThread)");
                                }
                            });

                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    btnThread.setText("Hello");
                                }
                            });

                            view.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btnThread.setText("Thread");
                                }
                            }, 1000);

//                            new ImageDownloader(img1).execute("https://pp.vk.me/c617319/v617319674/1dd20/G_jmyHLyot4.jpg");
                        }
                    }).start();
                } break;
                case R.id.btn_image: {
                    tv1.setText("UI Thread");
                    new ImageDownloader(img1).execute("https://pp.vk.me/c617319/v617319674/1dd27/zExE0Kh6t3U.jpg");
                } break;
                case R.id.btn_json: {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DefaultHttpClient client = new DefaultHttpClient();
                            HttpGet httpGet = new HttpGet("http://garik.pp.ua/prj/JSON_android/simple.json");
                            try {
                                HttpResponse execute = client.execute(httpGet);
                                final String responce = EntityUtils.toString(execute.getEntity());
                                view.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv1.setText(responce);
                                    }
                                });

                                JSONObject jsonObject = new JSONObject(responce);
                                final String name = jsonObject.getString("name");
                                view.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv1.setText(name);
                                    }
                                }, 2000);


                                JSONArray jsonArray = jsonObject.getJSONArray("listMessages");
                                String result = "";
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Integer idMessage = jsonArray.getJSONObject(i).getInt("id_message");
                                    result += idMessage + " ";
                                }

                                final String finalResult = result;
                                view.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv1.setText("idMessage: " + finalResult);
                                    }
                                }, 4000);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } break;
            }


//            Toast.makeText(view.getContext(), "Clicked", Toast.LENGTH_LONG).show();

        }
    };

    Button btnImage, btnJson, btnThread;
    ImageView img1;
    TextView tv1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_test);
        btnImage = (Button)findViewById(R.id.btn_image);
        btnJson = (Button)findViewById(R.id.btn_json);
        btnThread = (Button)findViewById(R.id.btn_thread);
        btnImage.setOnClickListener(onClickListener);
        btnJson.setOnClickListener(onClickListener);
        btnThread.setOnClickListener(onClickListener);
        btnJson.setOnClickListener(onClickListener);
        img1 = (ImageView) findViewById(R.id.imageView);
        tv1 = (TextView)findViewById(R.id.textView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.thread_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public ImageDownloader(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
