package rnr.test.calendardemo;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
        CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL
};

Cursor calendarCursor = getContentResolver().query(uri, projection, null, null, null);
while (calendarCursor.moveToNext()) {

    if (calendarCursor.getInt(1) == 0) continue;
    if (calendarCursor.getInt(3) != CalendarContract.Calendars.CAL_ACCESS_OWNER) continue;

    final long calendarId = calendarCursor.getLong(0);
    String text = calendarCursor.getString(2);
    Button btn = new Button(this);
    btn.setText(text);
    btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Intent calIntent = new Intent(Intent.ACTION_EDIT)
                    .setType("vnd.android.cursor.item/event");
            calIntent.putExtra(CalendarContract.Events.CALENDAR_ID, calendarId);
            startActivityForResult(calIntent, 4567);
        }
    });
    layout.addView(btn);
}
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
}
