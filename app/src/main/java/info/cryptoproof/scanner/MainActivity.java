package info.cryptoproof.scanner;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {



    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

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
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        Intent intent = new Intent(getApplicationContext(), DecoderActivity.class);
        startActivityForResult(intent, 1);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1&&resultCode==RESULT_OK&&data!=null){
            StringBuilder urlString = new StringBuilder(getResources().getString(R.string.cryptoproof));
            String scanned = data.getStringExtra("scanned");
//            EditText tv = (EditText) findViewById(R.id.editText);
            urlString.append(scanned);
            TestAsyncTask testAsyncTask = new TestAsyncTask(MainActivity.this, urlString.toString());
            testAsyncTask.execute();
            try {
                String result = testAsyncTask.get();
                checkData(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkUrl(View view) {
        StringBuilder urlString = new StringBuilder(getResources().getString(R.string.cryptoproof));
        EditText tv = (EditText) findViewById(R.id.editText);
        urlString.append(tv.getText().toString());
        TestAsyncTask testAsyncTask = new TestAsyncTask(MainActivity.this, urlString.toString());
        testAsyncTask.execute();
//        String data = null;
        try {
            data = testAsyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        checkData(data);
    }

    public class TestAsyncTask extends AsyncTask<Void, Void, String> {
        private Context mContext;
        private String mUrl;

        public TestAsyncTask(Context context, String url) {
            mContext = context;
            mUrl = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            data = getJSON(mUrl);
            return data;
        }

        private String getJSON(String url) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.connect();
                int status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        return sb.toString();
                    case 404:
                        return "invalid";
                }
            } catch (Exception ex) {
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                    }
                }
            }
            return "null";
        }
    }

    private void checkData (String text) {
        if (data.equals("null")) {
            Toast.makeText(getApplicationContext(), "No response from cryptoproof.info\nAre you connected to the internet?", Toast.LENGTH_LONG).show();

        } else if (data.contains("invalid")) {
            Toast.makeText(getApplicationContext(), "Invalid Bitcoin Address", Toast.LENGTH_LONG).show();

        } else if (data.contains("\"result\": false")) {
            Toast.makeText(getApplicationContext(), "Bitcoin Address Not Found", Toast.LENGTH_LONG).show();

        } else if (data.contains("\"result\": true")) {
            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("json", data);
            startActivity(intent);
        }

    }
}