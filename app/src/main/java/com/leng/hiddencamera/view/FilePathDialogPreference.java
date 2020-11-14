package com.leng.hiddencamera.view;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.leng.hiddencamera.R;
import com.leng.hiddencamera.util.SettingsUtil;

import java.io.File;

public class FilePathDialogPreference extends DialogPreference {
	private EditText mEtPassword;

	public FilePathDialogPreference(final Context context, AttributeSet attrs) {
		super(context, attrs);

		setDialogLayoutResource(R.layout.pref_file_path);

		String pathStr = SettingsUtil.read(this.getContext(),
				SettingsUtil.PREF_KEY_FILE_PATH, "MyData");

		setSummary("/sdcard/" + pathStr);
	}

	@Override
	protected void onBindDialogView(View view) {
		mEtPassword = (EditText) view.findViewById(R.id.password_edit);
		String pathStr = SettingsUtil.read(this.getContext(),
				SettingsUtil.PREF_KEY_FILE_PATH, "MyData");
		mEtPassword.setText(pathStr);
		super.onBindDialogView(view);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			String inputPassword = mEtPassword.getText().toString();

			File fileDir = new File("/sdcard/" + inputPassword);

			if (fileDir.mkdirs() || fileDir.isDirectory()) {
				setSummary("/sdcard/" + inputPassword);
				SettingsUtil.write(this.getContext(),
						SettingsUtil.PREF_KEY_FILE_PATH, inputPassword);
			} else {
				Toast.makeText(getContext(), "�ļ�·�����Ϸ�������������", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

}
