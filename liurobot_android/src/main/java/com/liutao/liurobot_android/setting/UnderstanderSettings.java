package com.liutao.liurobot_android.setting;

import com.liutao.liurobot_android.R;
import com.liutao.liurobot_android.util.SettingTextWatcher;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.Window;


/**
 * 语义理解设置界面
 */
public class UnderstanderSettings extends PreferenceActivity implements OnPreferenceChangeListener {
	private static final String TAG = UnderstanderSettings.class.getSimpleName();
	
	public static final String PREFER_NAME = "com.iflytek.setting";
	private EditTextPreference mVadbosPreference;
	private EditTextPreference mVadeosPreference;
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(PREFER_NAME);
		addPreferencesFromResource(R.xml.understand_setting);
		
		mVadbosPreference = (EditTextPreference)findPreference("understander_vadbos_preference");
		mVadbosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(UnderstanderSettings.this,mVadbosPreference,0,10000));
		
		mVadeosPreference = (EditTextPreference)findPreference("understander_vadeos_preference");
		mVadeosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(UnderstanderSettings.this,mVadeosPreference,0,10000));
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}
	
	@Override
	protected void onResume() {
		// �?放统�? 移动数据统计分析
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// �?放统�? 移动数据统计分析
		super.onPause();
	}
}

