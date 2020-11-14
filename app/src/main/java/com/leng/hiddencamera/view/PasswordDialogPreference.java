package com.leng.hiddencamera.view;

import com.leng.hiddencamera.R;
import com.leng.hiddencamera.util.SettingsUtil;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class PasswordDialogPreference extends DialogPreference {
	private EditText mEtPassword;

	public PasswordDialogPreference(final Context context, AttributeSet attrs) {
		super(context, attrs);

		setDialogLayoutResource(R.layout.alert_dialog_text_entry);
	}

	@Override
	protected void onBindDialogView(View view) {
		mEtPassword = (EditText) view.findViewById(R.id.password_edit);
		super.onBindDialogView(view);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			String inputPassword = mEtPassword.getText().toString();
			SettingsUtil.write(this.getContext(),
					SettingsUtil.PREF_KEY_PASSWORD, inputPassword);
		}
	}

}
