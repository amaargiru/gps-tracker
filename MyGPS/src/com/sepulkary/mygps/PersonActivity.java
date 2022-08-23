package com.sepulkary.mygps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;

public class PersonActivity extends Activity {
	private RadioButton radioButtonMaster;
	private RadioButton radioButtonTarget1;
	private RadioButton radioButtonTarget2;
	private Button enterPersonButton;

	final static int USER_MASTER = 0;
	final static int USER_TARGET1 = 1;
	final static int USER_TARGET2 = 2;
	int userMapCentered = USER_MASTER;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userMapCentered = USER_MASTER;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_person);

		radioButtonMaster = (RadioButton) findViewById(R.id.radioButtonMaster);
		radioButtonTarget1 = (RadioButton) findViewById(R.id.radioButtonTarget1);
		radioButtonTarget2 = (RadioButton) findViewById(R.id.radioButtonTarget2);
		enterPersonButton = (Button) findViewById(R.id.okPersonButton); // ������ "��"

		enterPersonButton.setOnClickListener(new Button.OnClickListener() { // ���������� ������ "�����/������������������"
					@Override
					public void onClick(View arg0) {
						if (radioButtonMaster.isChecked())
							userMapCentered = USER_MASTER;
						else if (radioButtonTarget1.isChecked())
							userMapCentered = USER_TARGET1;
						else if (radioButtonTarget2.isChecked())
							userMapCentered = USER_TARGET2;

						Intent intent = new Intent(PersonActivity.this, MainActivity.class);
						intent.putExtra("userMapCentered", userMapCentered);
						setResult(RESULT_OK, intent);
						finish();
					}
				});
	}
}