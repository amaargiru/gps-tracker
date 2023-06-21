package com.sepulkary.mygps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class UsersActivity extends Activity {
	private EditText masterLoginInput;
	private EditText masterPassInput;
	private EditText target1LoginInput;
	private EditText target1PassInput;
	private EditText target2LoginInput;
	private EditText target2PassInput;
	private Button enterUsersButton;
	private CheckBox AutoStartup;

	String mLoginInput = "";
	String mPassInput = "";
	String t1LoginInput = "";
	String t1PassInput = "";
	String t2LoginInput = "";
	String t2PassInput = "";

	boolean autoReloadState = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_users);

		enterUsersButton = (Button) findViewById(R.id.enterUsersButton);
		masterLoginInput = (EditText) findViewById(R.id.masterLoginInput);
		masterPassInput = (EditText) findViewById(R.id.masterPassInput);
		target1LoginInput = (EditText) findViewById(R.id.target1LoginInput);
		target1PassInput = (EditText) findViewById(R.id.target1PassInput);
		target2LoginInput = (EditText) findViewById(R.id.target2LoginInput);
		target2PassInput = (EditText) findViewById(R.id.target2PassInput);
		AutoStartup = (CheckBox) findViewById(R.id.AutoStartup);

		Intent intent = getIntent();
		mLoginInput = (intent.getStringExtra("masterLoginInput"));
		mPassInput = (intent.getStringExtra("masterPassInput"));
		t1LoginInput = (intent.getStringExtra("target1LoginInput"));
		t1PassInput = (intent.getStringExtra("target1PassInput"));
		t2LoginInput = (intent.getStringExtra("target2LoginInput"));
		t2PassInput = (intent.getStringExtra("target2PassInput"));
		autoReloadState = (intent.getBooleanExtra("autoReloadState", false));

		masterLoginInput.setText(mLoginInput);
		masterPassInput.setText(mPassInput);
		target1LoginInput.setText(t1LoginInput);
		target1PassInput.setText(t1PassInput);
		target2LoginInput.setText(t2LoginInput);
		target2PassInput.setText(t2PassInput);

		AutoStartup.setChecked(autoReloadState);

		enterUsersButton.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(UsersActivity.this, MainActivity.class);
						intent.putExtra("masterLoginInput", masterLoginInput.getText().toString());
						intent.putExtra("masterPassInput", masterPassInput.getText().toString());
						intent.putExtra("target1LoginInput", target1LoginInput.getText().toString());
						intent.putExtra("target1PassInput", target1PassInput.getText().toString());
						intent.putExtra("target2LoginInput", target2LoginInput.getText().toString());
						intent.putExtra("target2PassInput", target2PassInput.getText().toString());
						intent.putExtra("autoReloadState", AutoStartup.isChecked());

						setResult(RESULT_OK, intent);
						finish();
					}
				});
	}
}
