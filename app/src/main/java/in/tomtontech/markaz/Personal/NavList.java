package in.tomtontech.markaz.Personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Switch;

import in.tomtontech.markaz.Activity.MainActivity;
import in.tomtontech.markaz.R;

public class NavList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String category;
    String inst_id = "";
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            category = extra.getString("category");
            inst_id = extra.getString("inst_id");
        }
        setContentView(R.layout.activity_nav_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ctx = getApplicationContext();
        switch (category) {
            case "Institution":
                displaySelectedScreen(R.id.nav_inst);
                break;
            case "Events":
                displaySelectedScreen(R.id.nav_eve);
                break;
            case "Photos":
                displaySelectedScreen(R.id.nav_pho);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getFragmentManager().getBackStackEntryCount();
            if (count > 1) {
                getFragmentManager().popBackStack();

            } else {
                super.onBackPressed();
                if (inst_id==null || inst_id.equalsIgnoreCase("")) {
                    Intent intent = new Intent(ctx, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_list, menu);
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
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;
        switch (itemId) {
            case R.id.nav_home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_inst:
                inst_id = "";
                fragment = new InstitutionCategory();
                break;
            case R.id.nav_eve:
                inst_id = "";
                fragment = new EventCategory();
                break;
            case R.id.nav_pho:
                fragment = new PhotoFragment();
                if (inst_id != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("inst_id", inst_id);
                    fragment.setArguments(bundle);
                }
                break;
        }
        if (fragment != null) {

            android.app.FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Log.v("jhafkds", "afjkd");
            getSupportFragmentManager().popBackStack();
            ft.replace(R.id.content_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            manager.popBackStack(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
