package com.example.wibi.FriendandUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.wibi.R;
import com.example.wibi.UserInteraction.UserInteractionActivity;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FriendandUserActivity extends AppCompatActivity {

    ImageView btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendand_user);

        btnReturn = findViewById(R.id.btnReturn);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new UserFragment(), "Find Friend");
        viewPagerAdapter.addFragment(new FriendFragment(), "All Friend");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReturn();
            }
        });

    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragmentList;
        private ArrayList<String> titleList;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragmentList = new ArrayList<>();
            this.titleList = new ArrayList<>();
        }


        @NonNull
        @NotNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            titleList.add(title);
            System.out.println(fragment+" ís added and title is: "+title);
        }

        @Nullable
        @org.jetbrains.annotations.Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }

    public void doReturn(){
        startActivity(new Intent(FriendandUserActivity.this, UserInteractionActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       doReturn();
    }
}