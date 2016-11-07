package com.jizhou_xie.gvt;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jizhou_xie.gvt.logic.MapAdapter;
import com.jizhou_xie.gvt.logic.old.Word;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Texts to display
        Resources resources = getResources();
        String[] purpose_data = {resources.getString(R.string.purpose_zh),resources.getString(R.string.purpose_de),resources.getString(R.string.purpose_gap)};
        // Keys associate with the texts
        Word.WordPurpose[] purposeKeys = {Word.WordPurpose.ZH, Word.WordPurpose.DE, Word.WordPurpose.GaP};

        MapAdapter<Word.WordPurpose> arrayAdapterPrposes = new MapAdapter<Word.WordPurpose>(this,android.R.layout.simple_expandable_list_item_1, purposeKeys, purpose_data);
        ListView list_view_purposes = (ListView)findViewById(R.id.purposes_list);
        list_view_purposes.setAdapter(arrayAdapterPrposes);

        //setup click listener
        list_view_purposes.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MapAdapter<Word.WordPurpose> mapAdaptor = (MapAdapter<Word.WordPurpose>)adapterView.getAdapter();
                Intent intent = new Intent(MainActivity.this, TrainerActivity.class);
                intent.putExtra(ExtraKeys.EXTRA_MAIN_TO_TRAINER_PURPOSE, mapAdaptor.getKey(position));
                startActivity(intent);
            }
        });
    }


}
