package com.avallon.liveshop.ui.main;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.avallon.liveshop.R;
import com.avallon.liveshop.models.BitmapLruCache;
import com.avallon.liveshop.ui.main.favorites.FavoritesFragment;
import com.avallon.liveshop.ui.main.friends.FriendsFragment;
import com.avallon.liveshop.ui.main.home.HomeFragment;
import com.avallon.liveshop.ui.signin.SignInActivity;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private enum Screen {
        HOME, FAVORITE, FRIENDS
    }

    private HomeFragment homeFragment;
    private FavoritesFragment favoritesFragment;
    private FriendsFragment friendsFragment;
    private TextView mUserNameTextView;
    private TextView mUserEmailTexVview;
    private NetworkImageView mAvatarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false);
        navigationView.addHeaderView(headerView);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

        attachFragment(Screen.HOME);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserNameTextView = (TextView) headerView.findViewById(R.id.user_name_text);
        mUserNameTextView.setText(currentUser.getDisplayName());
        mUserEmailTexVview = (TextView) headerView.findViewById(R.id.user_email_text);
        mUserEmailTexVview.setText(currentUser.getEmail());

        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(this), imageCache);

        mAvatarImageView = (NetworkImageView) headerView.findViewById(R.id.img_avatar);
        mAvatarImageView.setImageUrl(currentUser.getPhotoUrl().toString(), imageLoader);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            attachFragment(Screen.HOME);
        } else if (id == R.id.nav_favorites) {
            attachFragment(Screen.FAVORITE);
        } else if (id == R.id.nav_friends) {
            attachFragment(Screen.FRIENDS);
        } else if (id == R.id.nav_sign_out) {
            displaySignOutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void attachFragment(Screen screen) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        switch (screen) {
            case HOME:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                fragmentTransaction.replace(R.id.fragment_container, homeFragment);

                break;
            case FAVORITE:
                if (favoritesFragment == null) {
                    favoritesFragment = new FavoritesFragment();
                }
                fragmentTransaction.replace(R.id.fragment_container, favoritesFragment);

                break;
            case FRIENDS:
                if (friendsFragment == null) {
                    friendsFragment = new FriendsFragment();
                }
                fragmentTransaction.replace(R.id.fragment_container, friendsFragment);

                break;
        }

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void displaySignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sign_out_title);
        builder.setMessage(R.string.sign_out_message);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.show();
    }
}
