package ru.romanov.schedule.src;

import java.util.ArrayList;
import java.util.Calendar;

import ru.romanov.schedule.R;
import ru.romanov.schedule.adapters.SubjectAdapter;
import ru.romanov.schedule.utils.CalendarManager;
import ru.romanov.schedule.utils.StringConstants;
import ru.romanov.schedule.utils.Subject;
import ru.romanov.schedule.utils.UpdateService;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity {

	TextView lastSyncTV;
    private static final Uri EVENT_URI = CalendarContract.Events.CONTENT_URI;
    private static final String ACCOUNT_NAME = "buyvich@gmail.com";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("Activity", "MainTabActivity created");
		setContentView(R.layout.main_tab_layout);
		lastSyncTV = (TextView) findViewById(R.id.maintab_last_sync);
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab
		
//		final Intent scheduleIntent = new Intent(this, ScheduleListActivity.class);
//		final Intent updateIntent = new Intent(this, UpdateListActivity.class);
//		
//		ActionBar actionBar = getActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//		
//		Tab scheduleTab = actionBar.newTab();
//		scheduleTab.setText(R.string.schedule);
//		scheduleTab.setTabListener(new TabListener() {
//			
//			@Override
//			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
//				
//			}
//			
//			@Override
//			public void onTabSelected(Tab tab, FragmentTransaction ft) {
//				startActivity(scheduleIntent); 
//			}
//			
//			@Override
//			public void onTabReselected(Tab tab, FragmentTransaction ft) {	
//			}
//		});
//		actionBar.addTab(scheduleTab);
//		
//		Tab updatesTab = actionBar.newTab();
//		updatesTab.setText(R.string.updates);
//		updatesTab.setTabListener(new TabListener() {
//			
//			@Override
//			public void onTabUnselected(Tab tab, FragmentTransaction ft) {	
//				
//			}
//			
//			@Override
//			public void onTabSelected(Tab tab, FragmentTransaction ft) {
//				startActivity(updateIntent); 
//			}
//			
//			@Override
//			public void onTabReselected(Tab tab, FragmentTransaction ft) {	
//			}
//		});
//		actionBar.addTab(updatesTab);
		
		
		Intent uintent = new Intent(this, UpdateService.class);
		Log.i("Service", "UpdateService starting");
		startService(uintent);

		// TODO: get some inspiration from here:
		// http://www.vogella.com/tutorials/AndroidServices/article.html#service_backgroundprocessing
		// 8. Exercise: Define and consume local service

		// This does not suffice
//		PendingIntent pintent = PendingIntent.getService(this, 0, uintent, 0);
//		Calendar cal = Calendar.getInstance();
//		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//		// Start every 30 seconds
//		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30*1000, pintent);
		

		
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, ScheduleListActivity.class);

		// Initialize a TabSpec for each tab and add it toresId the TabHost
		spec = tabHost.newTabSpec("scedule").setIndicator(getString(R.string.schedule))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, UpdateListActivity.class);
		spec = tabHost.newTabSpec("updates").setIndicator(getString(R.string.updates))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
		
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		String lastSync = getSharedPreferences(
				StringConstants.SCHEDULE_SHARED_PREFERENCES, MODE_PRIVATE)
				.getString(StringConstants.SHARED_LAST_SYNC_DATE, "-");
		lastSyncTV.setText(lastSync);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_check_updates:
            this.testCalendar();
			break;
		case R.id.menu_exit:
			AlertDialog alert = getExitAlertDialog();
			alert.show();
			//new ExitDialogFragment(question).show(getSupportFragmentManager(), tag)
			break;
		case R.id.menu_info:
			intent = new Intent(this, UserInfoDialogActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_settings:
			intent = new Intent(this, MenuSettingsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

		return true;
	}

    private void testCalendar() {
        SubjectAdapter subjectAdapter = new SubjectAdapter(this);
        CalendarManager calendarManager = new CalendarManager(this);
        ArrayList<Subject> subjects = subjectAdapter.getAll();
        for (Subject subject : subjects) {
            calendarManager.addEvent(subject.getSubject()
            , subject.getPlace()
            , subject.getDt_start()
            , subject.getDt_end()
            , subject.getT_start()
            , subject.getT_end()
            , subject.getPeriod());
        }


    }

    /**Builds the Uri for events (as a Sync Adapter)*/
    public static Uri buildEventUri() {
        return EVENT_URI
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
    }

	private AlertDialog getExitAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.sure_to_exit))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.dialog_yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SharedPreferences pref = getSharedPreferences(
										StringConstants.SCHEDULE_SHARED_PREFERENCES,
										MODE_PRIVATE);
								SharedPreferences.Editor editor = pref.edit();
								editor.remove(StringConstants.TOKEN);
								editor.commit();
								SharedPreferences schedule = getSharedPreferences(
										StringConstants.MY_SCHEDULE,
										MODE_PRIVATE);
								editor = schedule.edit();
								for (String key : pref.getAll().keySet()) {
									editor.remove(key);
								}
								editor.commit();
								Intent intent = new Intent(MainTabActivity.this, IScheduleActivity.class);
								startActivity(intent);
								MainTabActivity.this.finish();
							}
						})
				.setNegativeButton(getString(R.string.dialog_no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		return builder.create();
	}

}


class ExitDialogFragment extends android.support.v4.app.DialogFragment
{
	String question;
	
	public ExitDialogFragment(String question)
	{
		this.question = question;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.app_name);
		builder.setMessage(this.question);
		builder.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		});
		
		builder.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		return super.onCreateDialog(savedInstanceState);
	}

}
