package team16.filtershare;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ShareFilterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SFData> sfDataset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        sfDataset = new ArrayList<>();
        adapter = new SFAdapter(sfDataset);
        recyclerView.setAdapter(adapter);

        GlobalVariables sfApp = ((GlobalVariables)getApplicationContext());
        String picturepath = sfApp.get_picture_path();
        Bitmap bitimg = BitmapFactory.decodeFile(picturepath);
        sfDataset.add(new SFData(bitimg, "Test Filter #1", 22803, new String[]{"dark", "bright", "daily"}, "Jon Snow, 2 days ago"));
        sfDataset.add(new SFData(bitimg, "Test Filter #2", 22803, new String[] {"dark", "bright", "daily"},"Jon Snow, 2 days ago"));
        sfDataset.add(new SFData(bitimg, "Test Filter #3", 22803, new String[] {"dark", "bright", "daily"},"Jon Snow, 2 days ago"));
        sfDataset.add(new SFData(bitimg, "Test Filter #4", 22803, new String[] {"dark", "bright", "daily"},"Jon Snow, 2 days ago"));
        sfDataset.add(new SFData(bitimg, "Test Filter #5", 22803, new String[] {"dark", "bright", "daily"},"Jon Snow, 2 days ago"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_add) {
            Intent intent = new Intent(ShareFilterActivity.this, FilterMakingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}