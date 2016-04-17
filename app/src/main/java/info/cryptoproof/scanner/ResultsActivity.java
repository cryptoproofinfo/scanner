package info.cryptoproof.scanner;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by derrend on 05/11/15.
 */
public class ResultsActivity extends ListActivity {

    ListView l;
    Response response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        l = getListView();
        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        Gson gson = new Gson();
        response = gson.fromJson(json, Response.class);

        l.setAdapter(new cAdapter(this, response));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
            case 0:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(R.string.blockchain + response.compressed_address));
                startActivity(intent);
                break;
            case 1:
                Intent intent_url = new Intent(Intent.ACTION_VIEW, Uri.parse(response.associated_url));
                startActivity(intent_url);
                break;
        }
    }
}

class Response {
    Boolean result;
    String compressed_address;
    String associated_url;
    Boolean verified_status;
    Integer hits;
}

class SingleRow {
    String large;
    String small;
    SingleRow(String large, String small) {
        this.large = large;
        this.small = small;
    }
}

class cAdapter extends BaseAdapter {

    ArrayList<SingleRow> list;
    Context context;
    cAdapter(Context c, Response response) {
        context = c;
        list = new ArrayList<>();
        Resources res = c.getResources();
        String[] largeArray = res.getStringArray(R.array.titles);
        String[] smallArray = {response.compressed_address, response.associated_url,
                (response.verified_status) ? "VERIFIED":"NOT VERIFIED", response.hits.toString()};

        for (int i = 0; i < 4; i++) {
            list.add(new SingleRow(largeArray[i], smallArray[i]));
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.single_row,parent,false);
        TextView title = (TextView) row.findViewById(R.id.title);
        TextView value = (TextView) row.findViewById(R.id.value);

        SingleRow temp = list.get(position);
        title.setText(temp.large);
        value.setText(temp.small);

        if (temp.small.equals("VERIFIED")) {
            value.setTextColor(Color.GREEN);
        } else if (temp.small.equals("NOT VERIFIED")) {
            value.setTextColor(Color.RED);
        }

        return row;
    }
}
