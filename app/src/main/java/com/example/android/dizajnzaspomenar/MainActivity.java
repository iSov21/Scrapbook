package com.example.android.dizajnzaspomenar;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.dizajnzaspomenar.R.drawable.c;
import static com.example.android.dizajnzaspomenar.R.id.fab_new_answer;
import static com.example.android.dizajnzaspomenar.R.id.fab_new_question;
import static com.example.android.dizajnzaspomenar.R.id.pager_title_strip;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    public int position;
    public static int current_position;
    public static int BROJ_PITANJA;

    private ViewPager vpPager;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    public static final List<String> TitleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_question = (FloatingActionButton) findViewById(R.id.fab_new_question);
        fab_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_question();
            }
        });

        FloatingActionButton fab_answer = (FloatingActionButton) findViewById(R.id.fab_new_answer);
        fab_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_answer();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        popuniBazu();

        //provjera();

       /* ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager); */

       /* SpannableString s = new SpannableString("Spomenar");
        s.setSpan(new TypefaceSpan(this, "Sacramento-Regular.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);*/
        dohvatiPitanja();

        vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current_position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        DBAdapter db = new DBAdapter(this);

        db.open();
        Cursor c = db.getAllQuestions();
        if (c.moveToFirst()) {
            do {
                adapter.addFragment(new ListContentFragment(), c.getString(1));
            } while (c.moveToNext());
        }
        db.close();

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position_inner) {
                Globals g = Globals.getInstance();
                g.setQuestionId(position_inner);
                position = position_inner;

                //provjera(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public class DetailOnPageListener extends ViewPager.SimpleOnPageChangeListener{

        private int currentPage;

        @Override
        public void onPageSelected( int position ) { currentPage = position; }

        public final int getCurrentPage() { return currentPage; };
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



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, com.example.android.dizajnzaspomenar.AllUsersActivity.class);
            this.startActivity(intent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, com.example.android.dizajnzaspomenar.Settings.class);
            this.startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Style a {@link Spannable} with a custom {@link Typeface}.
     *
     * @author Tristan Waddington
     */
    public class TypefaceSpan extends MetricAffectingSpan {
        /** An <code>LruCache</code> for previously loaded typefaces. */
        private LruCache<String, Typeface> sTypefaceCache =
                new LruCache<String, Typeface>(12);

        private Typeface mTypeface;

        /**
         * Load the {@link Typeface} and apply to a {@link Spannable}.
         */
        public TypefaceSpan(Context context, String typefaceName) {
            mTypeface = sTypefaceCache.get(typefaceName);

            if (mTypeface == null) {
                mTypeface = Typeface.createFromAsset(context.getApplicationContext()
                        .getAssets(), String.format("fonts/%s", typefaceName));

                // Cache the loaded Typeface
                sTypefaceCache.put(typefaceName, mTypeface);
            }
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            p.setTypeface(mTypeface);

            // Note: This flag is required for proper typeface rendering
            p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            tp.setTypeface(mTypeface);

            // Note: This flag is required for proper typeface rendering
            tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
    }

    public void popuniBazu()
    {
        DBAdapter db = new DBAdapter(this);
        db.open();
        if( db.getAllContacts().getCount() == 0 ) {
            popuniPitanja();
            popuniKorisnike();
            popuniOdgovore();
        }
        db.close();
    }

    public void popuniPitanja() //možda da bude bool da znamo jel uspjesno ili ne?
    {
        DBAdapter db = new DBAdapter(this);
        //---add a question---
        db.open();
        long id;
        id = db.insertQuestion("Kako se zoveš?");
        id = db.insertQuestion("Gdje živiš?");
        id = db.insertQuestion("Koliko imaš godina?");
        id = db.insertQuestion("Boja očiju?");
        id = db.insertQuestion("Najbolji prijatelj?");
        id = db.insertQuestion("Imaš li simpatiju?");
        id = db.insertQuestion("Najdraže jelo?");
        db.close();
    }

    public void popuniKorisnike()
    {
        DBAdapter db = new DBAdapter(this);

        db.open();
        long id;
        id = db.insertContact("Ana", "ana@ana.hr", "pass", 0);
        id = db.insertContact("Pero", "pero@pero.hr", "pass", 0);
        id = db.insertContact("admin", "admin@admin.hr", "admin", 1);
        db.close();
    }

    public void popuniOdgovore() //možda da bude bool da znamo jel uspjesno ili ne?
    {
        DBAdapter db = new DBAdapter(this);

        //---add a question---
        db.open();
        long id;
        id = db.insertAnswer(1,"Ana", 1, "Ana");
        id = db.insertAnswer(1, "Ana", 2, "Zagreb" );
        id = db.insertAnswer(1, "Ana", 3, "15");
        id = db.insertAnswer(1, "Ana", 4, "Plava");
        id = db.insertAnswer(1, "Ana", 5, "Petar");
        id = db.insertAnswer(1, "Ana", 6, "Da");
        id = db.insertAnswer(1, "Ana", 7, "Pizza");

        id = db.insertAnswer(2, "Pero", 1, "Petar");
        id = db.insertAnswer(2, "Pero", 2, "Zadar");
        id = db.insertAnswer(2, "Pero", 3, "10");

        for (int i=4; i<=7; i++)
            id = db.insertAnswer(2, "Pero", i, "---");

        for (int i=1; i<=7; i++)
            id = db.insertAnswer(3, "admin", i, "---");

        db.close();
    }

    public void obrisiPitanja()
    {
        DBAdapter db = new DBAdapter(this);

        db.open();
        db.deleteQuestionsTable();
        db.deleteAnswersTable();
        db.deleteUsersTable();
        db.close();
    }

    public void provjera(int position)
    {DBAdapter db = new DBAdapter(this);

        db.open();
        Cursor c = db.getAllContacts();
        if (c.moveToFirst()){
            do {
                //Toast.makeText(this, c.getString(0), Toast.LENGTH_LONG).show();
                Toast.makeText(this, String.valueOf(position), Toast.LENGTH_LONG).show();
            } while (c.moveToNext());
        }
        db.close();}

    public void new_answer()
    {
        final int id_pitanja = current_position + 1;
        AlertDialog.Builder newAnswer = new AlertDialog.Builder(MainActivity.this);

        newAnswer.setTitle("Novi odgovor");
        newAnswer.setMessage("Odgovor na pitanje " + id_pitanja);

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        newAnswer.setView(input);

        newAnswer.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                DBAdapter db = new DBAdapter(getApplicationContext());
                db.open();

                if ( db.isAnswered(2, id_pitanja) ) {//id_usera, id_pitanja
                    Snackbar.make(findViewById(android.R.id.content),
                            "Već ste odgovorili na ovo pitanje!",
                            Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                //---update answer to question ---
                else{
                    int id2;
                    id2 = db.updateAnswer(2, id_pitanja, input.getText().toString());
                    if (id2 > 0) {
                        Toast.makeText(getApplicationContext(), "Updejtano " + id2 + " redaka!", Toast.LENGTH_SHORT).show();
                        //vpPager.setAdapter(adapterViewPager);
                        vpPager.setCurrentItem(current_position);
                    }
                    db.close();
                }
            }
        });
        newAnswer.setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                //Toast.makeText(getApplicationContext(), "Odustali ste od odgovora.", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog dialog = newAnswer.create();
        dialog.show();
    }

    public void new_question()
    {
        AlertDialog.Builder newAnswer = new AlertDialog.Builder(MainActivity.this);
        newAnswer.setTitle("Novo pitanje");
        newAnswer.setMessage("Unesi novo pitanje: ");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        input.setLayoutParams(lp);
        newAnswer.setView(input);

        newAnswer.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               /* Globals g = Globals.getInstance();
                g.logout();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish(); */
                DBAdapter db = new DBAdapter(getApplicationContext());
                db.open();
                /*if ( !db.Admin(g.getId) )
                {
                     Toast.makeText(getApplicationContext(), "Nemate ovlasti dodati pitanje!", Toast.LENGTH_SHORT).show();
                }

                else{*/
                //---add a question---

                long id3;
                id3 = db.insertQuestion( input.getText().toString());
                if ( id3 > 0 ) {
                    Toast.makeText(getApplicationContext(), "Novo pitanje je uspješno dodano!", Toast.LENGTH_SHORT).show();
                    osvjezi();

                }
                db.close();
            }
        });
        newAnswer.setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
               // Toast.makeText(getApplicationContext(), "Odustali ste od novog pitanja.", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = newAnswer.create();
        dialog.show();
    }
    //------------------------------------------------------------------------------

    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            NUM_ITEMS = BROJ_PITANJA;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            return PageFragment.newInstance( position, TitleList.get(position) );
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            //return "Page " + position;
            //current_position = position;
            return TitleList.get(position);
        }

        public int getItemPosition(Object item) {
            return POSITION_NONE;
        }

    }

    public void dohvatiPitanja()
    {
        int broj_pitanja = 0;
        DBAdapter db = new DBAdapter(this);

        db.open();
        Cursor c = db.getAllQuestions();
        if (c.moveToFirst()) {
            do {
                TitleList.add(c.getString(1));
                ++broj_pitanja;
            } while (c.moveToNext());
        }
        db.close();
        BROJ_PITANJA = broj_pitanja;
    }

    public void osvjezi()
    {
        dohvatiPitanja();
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current_position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}


