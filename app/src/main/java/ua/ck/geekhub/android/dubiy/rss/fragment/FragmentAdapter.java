package ua.ck.geekhub.android.dubiy.rss.fragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import ua.ck.geekhub.android.dubiy.rss.adapter.ArrayAdapterItem;
import ua.ck.geekhub.android.dubiy.rss.entity.ObjectItem;
import ua.ck.geekhub.android.dubiy.rss.R;

/**
 * Created by Gary on 20.10.2014.
 */
public class FragmentAdapter extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private char[] chars;
    private Random random = new Random();
    private ListView listView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StringBuilder tmp = new StringBuilder();
        for (char c = '0'; c <= '9'; c++) {
            tmp.append(c);
        }
        for (char c = 'a'; c <= 'z'; c++) {
            tmp.append(c);
        }
        chars = tmp.toString().toCharArray();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_adapter, null);
        listView = (ListView)v.findViewById(R.id.listView);

        Button btn_gen_list = (Button) v.findViewById(R.id.btn_gen_list);
        btn_gen_list.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int list_length = 20 + random.nextInt(10);
                char[] tmp;
//                String[] listItems = new String[list_length];
                ObjectItem[] ObjectItemData = new ObjectItem[list_length];

                for (int i = 0; i < list_length; i++) {
                    int item_length = 10 + random.nextInt(10);
                    tmp = new char[item_length];
                    for (int j = 0; j < item_length; j++) {
                        tmp[j] = chars[random.nextInt(chars.length)];
                    }
                    tmp[0] = Character.toUpperCase(tmp[0]);
                    ObjectItemData[i] = new ObjectItem(9000 + i, new String(tmp));

                }

                ArrayAdapterItem adapter = new ArrayAdapterItem(getActivity(), R.layout.list_item, ObjectItemData);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new ListItemClickListener());
            }
        });

        return v;
    }

    private class ListItemClickListener implements ListView.OnItemClickListener {
        public ListItemClickListener() {

        }

        @Override
        public void onItemClick(AdapterView adapterView, View view, int i, long l) {
            Context context = view.getContext();
            TextView textViewItem = (TextView)view.findViewById(R.id.textViewItem);
            Toast.makeText(getActivity().getApplicationContext(), "Clicked \"" + listView.getItemAtPosition(i).toString() + "\" item. ID: " + textViewItem.getTag().toString(), Toast.LENGTH_LONG).show();
        }
    }

}
