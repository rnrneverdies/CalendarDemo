package rnr.test.calendardemo;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        String[] projection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.VISIBLE,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.DELETED
        };

        Cursor calendarCursor = getContentResolver().query(uri, projection, null, null, null);
        while (calendarCursor.moveToNext()) {
            Log.d(">>>>", "Calendar: " + calendarCursor.getString(2) + " Deleted:" + calendarCursor.getLong(4));
            //if (calendarCursor.getInt(1) == 0) continue;
            if (calendarCursor.getInt(3) != CalendarContract.Calendars.CAL_ACCESS_OWNER) continue;

            final long calendarId = calendarCursor.getLong(0);
            String text = calendarCursor.getString(2);
            Button btn = new Button(this);
            btn.setText("Open Calendar - " + text);
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pushCalendarVisibility();
                    makeAllCalendarsInvisibleExcept(calendarId);
                    Intent calIntent = new Intent();
                    ComponentName cn = new ComponentName("com.android.calendar", "com.android.calendar.LaunchActivity");
                    calIntent.setComponent(cn);
                    startActivityForResult(calIntent, 4567);
                }
            });
            layout.addView(btn);

            btn = new Button(this);
            btn.setText("Create Event - " + text);
            btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pushCalendarVisibility();
                    makeAllCalendarsInvisibleExcept(calendarId);

                    final Intent calIntent = new Intent(Intent.ACTION_EDIT)
                            .setType("vnd.android.cursor.item/event")
                            .putExtra(CalendarContract.Events.CALENDAR_ID, calendarId);

                    startActivityForResult(calIntent, 4567);
                }
            });
            layout.addView(btn);
        }
    }

    // visible calendars cache
    Long[] visibleCalendarsIds;

    private void  pushCalendarVisibility() {
        String[] fields = new String[] { CalendarContract.Calendars._ID };
        String where = CalendarContract.Calendars.VISIBLE + " = 1";
        Cursor calendarCursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, fields, null, null, null);
        visibleCalendarsIds = new Long[calendarCursor.getCount()];
        int i=0;
        while (calendarCursor.moveToNext()) {
            visibleCalendarsIds[i++] = calendarCursor.getLong(0);
        }
    }

    private void restoreCalendarVisibility() {
        ContentValues updateValues = new ContentValues();
        updateValues.put(CalendarContract.Calendars.VISIBLE, 1);
        String where = CalendarContract.Calendars._ID + " IN (" + TextUtils.join(",", visibleCalendarsIds) + ")";
        getContentResolver().update(CalendarContract.Calendars.CONTENT_URI,
                updateValues, where, null);
    }

    private void makeAllCalendarsInvisibleExcept(long calendarId) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(CalendarContract.Calendars.VISIBLE, 0);
        String where = CalendarContract.Calendars._ID + " = " + calendarId;

        // make all invisible
        getContentResolver().update(CalendarContract.Calendars.CONTENT_URI,
                updateValues, null, null);

        updateValues.clear();
        updateValues.put(CalendarContract.Calendars.VISIBLE, 1);

        // make calendarId visible
        getContentResolver().update(CalendarContract.Calendars.CONTENT_URI,
                updateValues, where, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 4567) {
            restoreCalendarVisibility();
        }
    }
}
