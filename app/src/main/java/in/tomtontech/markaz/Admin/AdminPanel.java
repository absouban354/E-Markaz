package in.tomtontech.markaz.Admin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import in.tomtontech.markaz.Admin.Fragment.AdminEventFragment;
import in.tomtontech.markaz.Admin.Fragment.AdminInstitutionFragment;
import in.tomtontech.markaz.Admin.Fragment.AdminPhotoFragment;
import in.tomtontech.markaz.Admin.Fragment.BlankFragment;
import in.tomtontech.markaz.R;


public class AdminPanel extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
  DrawerLayout drawer;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_panel);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id=item.getItemId();
    if(id==R.id.nav_institution)
    {
      FragmentManager fm=this.getFragmentManager();
      FragmentTransaction ft=fm.beginTransaction();
      Fragment fragment=new AdminInstitutionFragment();
      ft.replace(R.id.adminPanel_fragment,fragment);
      ft.commit();
      drawer.closeDrawers();
    }
    else if(id==R.id.nav_events)
    {
      FragmentManager fm=this.getFragmentManager();
      FragmentTransaction ft=fm.beginTransaction();
      Fragment fragment=new AdminEventFragment();
      ft.replace(R.id.adminPanel_fragment,fragment);
      ft.commit();
      drawer.closeDrawers();
    }
    else if(id==R.id.nav_photo)
    {
      FragmentManager fm=this.getFragmentManager();
      FragmentTransaction ft=fm.beginTransaction();
      Fragment fragment=new AdminPhotoFragment();
      ft.replace(R.id.adminPanel_fragment,fragment);
      ft.commit();
      drawer.closeDrawers();
    }
    else if(id==R.id.nav_dashBoard)
    {
      FragmentManager fm=this.getFragmentManager();
      FragmentTransaction ft=fm.beginTransaction();
      Fragment fragment=new BlankFragment();
      ft.replace(R.id.adminPanel_fragment,fragment);

      ft.commit();
      drawer.closeDrawers();
    }
    item.setChecked(true);
    return false;
  }
}
