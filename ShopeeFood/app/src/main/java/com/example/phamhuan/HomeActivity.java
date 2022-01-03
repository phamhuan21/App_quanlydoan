package com.example.phamhuan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.phamhuan.Fragment.CartFragment;
import com.example.phamhuan.Fragment.HomeFragment;
import com.example.phamhuan.Fragment.OrderFragment;
import com.example.phamhuan.Fragment.ProfileFragment;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;


public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView navigation;
    private ViewPager viewPager;
    private boolean cart = false;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private long exitTime = 0;
    private int pager_number = 4;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AppUpdateChecker appUpdateChecker=new AppUpdateChecker(this);
        appUpdateChecker.checkForUpdate();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle); toggle.syncState();
        RecyclerView recyclerView = findViewById(R.id.sidebarview);
        LinearLayoutManager llManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(llManager);
        recyclerView.setHasFixedSize(true);
        SidebarAdapter sidebarAdapter = new SidebarAdapter(this, drawerLayout);
        recyclerView.setAdapter(sidebarAdapter);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        viewPager.setOffscreenPageLimit(pager_number);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_category:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_video:
                        viewPager.setCurrentItem(2);
                        return true;
                    case R.id.navigation_favorite:
                        viewPager.setCurrentItem(3);
                        return true;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

                if (viewPager.getCurrentItem() == 1) {
                    toolbar.setTitle("Đơn hàng");
                } else if (viewPager.getCurrentItem() == 2) {
                    toolbar.setTitle("Giỏ hàng");
                } else if (viewPager.getCurrentItem() == 3) {
                    toolbar.setTitle("Profile");
                } else {
                    if(Constant.AdminLogin.equals("true")){
                        toolbar.setTitle("--- Admin Dashboard ---");
                    } else {
                        toolbar.setTitle("Shopeefood");
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new OrderFragment();
                case 2:
                    return new CartFragment();
                case 3:
                    return new ProfileFragment();
            }
            return null;
        }
        @Override
        public int getCount() {
            return pager_number;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(Utils.showCart){
            viewPager.setCurrentItem((2), true);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem((0), true);
        } else {
            exitApp();
        }
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Chạm lần nữa để thoát", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}