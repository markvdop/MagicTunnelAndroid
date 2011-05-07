package net.magictunnel;

import net.magictunnel.core.Commands;
import net.magictunnel.core.Installer;
import net.magictunnel.core.Tunnel;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class SystemComponentChecklist extends PreferenceActivity {
	private boolean m_hasRoot = false;
	private boolean m_hasTun = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.checklist);
		
		if (Installer.iodineInstalled()) {
			Intent intent = new Intent().setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
		
		populateScreen();
	}
	
	private void populateScreen() {
		PreferenceScreen screen = getPreferenceScreen();
		
		PreferenceCategory catChecklist = new PreferenceCategory(this);
		catChecklist.setTitle(R.string.checklist_category);
		
		CheckBoxPreference prefRootAccess = createCustomCheckBox(R.string.checklist_root);
		m_hasRoot = Commands.checkRoot();
		prefRootAccess.setChecked(m_hasRoot);
		
		
		CheckBoxPreference prefTun = createCustomCheckBox(R.string.checklist_tun);
		m_hasTun = Tunnel.checkTap();
		prefTun.setChecked(m_hasTun);

		
		PreferenceCategory catAction = new PreferenceCategory(this);
		catAction.setTitle(R.string.checklist_category_action);
		
		screen.addPreference(catChecklist);
		screen.addPreference(prefRootAccess);
		screen.addPreference(prefTun);
		screen.addPreference(catAction);
		
		if (m_hasRoot && m_hasTun) {
			addProceedButton();
		}else {
			addHelpButton();
		}
	}
	
	CheckBoxPreference createCustomCheckBox(int id) {
		CheckBoxPreference checkbox = new CheckBoxPreference(this);
		checkbox.setTitle(id);
		//checkbox.setWidgetLayoutResource(R.layout.checkbox);
		checkbox.setEnabled(false);
		
		return checkbox;
	}
	
	/**
	 * Displays install button if all dependencies are satisfied
	 */
	private void addProceedButton() {
		PreferenceScreen screen = getPreferenceScreen();
		
		Preference prefProceed = new Preference(this);
		prefProceed.setTitle(R.string.checklist_proceed);
		prefProceed.setSummary(R.string.checklist_proceed_subtitle);
		
		prefProceed.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent().setClass(preference.getContext(), 
						MainActivity.class);
				
				Installer installer = new Installer(preference.getContext());
				
				if (!installer.installIodine()) {
					Utils.showErrorMessage(preference.getContext(), "Could not install the client");
					return false;
				}
				
				Toast t = Toast.makeText(preference.getContext(), R.string.checklist_install_ok, Toast.LENGTH_LONG);
				t.show();

				preference.getContext().startActivity(intent);
				finish();
				return false;
			}
		});
		
		screen.addPreference(prefProceed);		
	}
	
	/**
	 * Display a help button if some dependencies are missing
	 */
	private void addHelpButton() {
		PreferenceScreen screen = getPreferenceScreen();
		
		Preference prefHelp = new Preference(this);
		prefHelp.setTitle(R.string.checklist_nok);
		prefHelp.setSummary(R.string.checklist_nok_subtitle);
		
		prefHelp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.magictunnel.net/"));

				try {
				 preference.getContext().startActivity(intent);
				} catch (ActivityNotFoundException ex) {
				 // do something about the exception, or not ...
				}
	
				return false;
			}
		});
		
		screen.addPreference(prefHelp);
	}

}
