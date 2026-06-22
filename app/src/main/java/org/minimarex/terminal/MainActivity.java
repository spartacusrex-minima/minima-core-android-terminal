package org.minimarex.terminal;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;
import org.minimarex.minimaapi.MinimaAPI;
import org.minimarex.minimaapi.MinimaAPIListener;
import org.minimarex.minimaapi.MinimaAPILogger;

public class MainActivity extends AppCompatActivity {

    ViewPager mMainPager;

    TerminalAdapter mTerminalAdapter;

    MinimaAPI mMinimaAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Minima Terminal");
        //tb.getOverflowIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        setSupportActionBar(tb);

        mTerminalAdapter = new TerminalAdapter(this);

        mMainPager = findViewById(R.id.main_pager);
        mMainPager.setAdapter(mTerminalAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mMainPager);

        tabs.getTabAt(0).setText("Terminal");
        //tabs.getTabAt(0).setIcon(R.drawable.ic_network);

        tabs.getTabAt(1).setText("Logs");
        //tabs.getTabAt(1).setIcon(R.drawable.ic_transfer);

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //Refresh View
                mTerminalAdapter.refreshPagerView(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Refresh View
                mTerminalAdapter.refreshPagerView(tab.getPosition());
            }
        });

        //Register the MinimaAPI
        mMinimaAPI = new MinimaAPI(this, new MinimaAPIListener() {
            @Override
            public void response(JSONObject jsonObject) {
                MinimaAPILogger.log(jsonObject.toString());
            }
        });

        //Set this API handler
        mTerminalAdapter.getTerminalView().setMinimaAPI(mMinimaAPI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMinimaAPI.onDestroy();
    }
}

