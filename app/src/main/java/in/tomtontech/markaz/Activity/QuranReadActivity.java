package in.tomtontech.markaz.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.Fragment.QuranMushafMode;
import in.tomtontech.markaz.Fragment.QuranTranslateFragment;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.BUNDLE_DAWRA_PAGE_NUM;
import static in.tomtontech.markaz.CustomFunction.BUNDLE_QURAN_PAGE_ID;
import static in.tomtontech.markaz.Data.BaseQuranData.PAGE_SURA_START;

public class QuranReadActivity extends AppCompatActivity {
  private static final String LOG_TAG = "quranRead";
  private Context ctx;
  private int pageId;
  private CustomFunction cf;
  private boolean isTranslator = false;
  private TextView tvTitle, tvPage;
  private int NUM_PAGES = 604;
  private int dawra_page = 1;
  private ViewPager viewPager;
  private PagerAdapter mPagerAdapter;
  int[] num;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quran_read);
    ctx = this;
    cf = new CustomFunction(ctx);
    Toolbar toolbar = (Toolbar) findViewById(R.id.quranRead_toolbar);
    setSupportActionBar(toolbar);
    if(getSupportActionBar()!=null)
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    tvTitle = (TextView) findViewById(R.id.quranRead_TitleBar);
    tvPage = (TextView) findViewById(R.id.quranRead_PageNo);
    final int intentPageId = getIntent().getIntExtra(BUNDLE_QURAN_PAGE_ID, 0);
    NUM_PAGES = getIntent().getIntExtra(BUNDLE_DAWRA_PAGE_NUM, 604);
    Log.v(LOG_TAG, "intent page id:" + intentPageId);
    pageId = intentPageId;//SURA_PAGE_START[surahId - 1];
    int surahId = PAGE_SURA_START[pageId - 1];
    String strSurah = ctx.getResources().getStringArray(R.array.sura_names)[surahId - 1];
    tvTitle.setText(strSurah);
    tvPage.setText(getResources().getString(R.string.quranRead_pageNo, pageId));
    viewPager = (ViewPager) findViewById(R.id.quranRead_fragment);
    num = new int[NUM_PAGES];
    for (int i = 0, j = NUM_PAGES - 1; i < num.length; i++, j--) {
      num[i] = j;
    }
    if (NUM_PAGES == 5) {
      pageId = 1;
      Log.v(LOG_TAG, "intent in dawrathul quran:" + intentPageId);
      for (int i = 0, j = intentPageId + 3; i < num.length; i++, j--) {
        num[i] = j;
        Log.v(LOG_TAG, "num value:" + num[i] + " at " + i);
      }
      dawra_page = 4;
      //dawrathul quran else normal quran.
    }
    if (pageId > 0) {
      if (NUM_PAGES == 5) {
        pageId = num[dawra_page]+1;
        Log.v(LOG_TAG,"start page id:"+pageId);
      }
      if (isPageExist(pageId)) {
        isTranslator = false;
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), num, false);
        viewPager.setAdapter(mPagerAdapter);
        if (NUM_PAGES == 5) {
          viewPager.setCurrentItem(dawra_page);
        } else
          viewPager.setCurrentItem(num[pageId - 1]);
      } else {
        isTranslator = true;
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), num, true);
        viewPager.setAdapter(mPagerAdapter);
        if (NUM_PAGES == 5) {
          viewPager.setCurrentItem(dawra_page);
        } else
          viewPager.setCurrentItem(num[pageId - 1]);
      }
      viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {
          if (!isTranslator && !isPageExist(num[position] + 1)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
            alertDialog
                .setMessage("This Page Is Not Available In Mus\'haf Mode.Read In Text Mode ?")
                .setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                  }
                })
                .setPositiveButton("Read", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(ctx, QuranReadActivity.class);
                    intent.putExtra(BUNDLE_QURAN_PAGE_ID, pageId);
                    startActivity(intent);
                    finish();
                  }
                })
                .setTitle("Page Unavailable")
                .show();

          }
          tvPage.setText(getResources().getString(R.string.quranRead_pageNo, num[position] + 1));
          pageId = num[position] + 1;
          if (NUM_PAGES == 5) {
            dawra_page = position;
          }
          int suraId = PAGE_SURA_START[num[position]];
          String strSura = ctx.getResources().getStringArray(R.array.sura_names)[suraId - 1];
          tvTitle.setText(strSura);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
      });
    }
    //changeFragment(pageId,2);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.quran_actionbar_menu, menu);
    MenuItem translate = menu.getItem(0);
    translate.setVisible(true);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.quran_action_translate) {
      if (isTranslator) {
        if (isPageExist(pageId)) {
          isTranslator = false;
          mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), num, false);
          viewPager.setAdapter(mPagerAdapter);
          if (NUM_PAGES == 5) {
            viewPager.setCurrentItem(dawra_page);
          } else
            viewPager.setCurrentItem(num[pageId - 1]);
        } else {
          Toast.makeText(ctx, "Page Image Not Found", Toast.LENGTH_SHORT).show();
        }
      } else {
        isTranslator = true;
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), num, true);
        viewPager.setAdapter(mPagerAdapter);
        if (NUM_PAGES == 5) {
          viewPager.setCurrentItem(dawra_page);
        } else
          viewPager.setCurrentItem(num[pageId - 1]);
      }
    } else if (id == R.id.quran_action_setting) {
      Intent intent = new Intent(ctx, QuranSettingsActivity.class);
      startActivity(intent);
    } else if (id == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    int[] displayText;
    int currentPage;
    boolean isTranslator;

    ScreenSlidePagerAdapter(FragmentManager fm, int[] displayText, boolean isTranslator) {
      super(fm);
      this.displayText = displayText;
      this.isTranslator = isTranslator;
    }

    @Override
    public Fragment getItem(int position) {
      if (isTranslator) {
        QuranTranslateFragment fragment = new QuranTranslateFragment();
        fragment.setText(displayText[position]);
        currentPage = position + 1;
        return fragment;
      } else {
        QuranMushafMode fragment = new QuranMushafMode();
        fragment.setText(displayText[position]);
        currentPage = position + 1;
        return fragment;
      }
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }
  }

  public Boolean isPageExist(int pageId) {
    String pageNo=String.valueOf(pageId);
    if (pageNo.length() == 2)
      pageNo = "0".concat(pageNo);
    else if (pageNo.length() == 1)
      pageNo = "00".concat(pageNo);
    return cf.getChatImages(pageNo, 3) != null;
  }
}
