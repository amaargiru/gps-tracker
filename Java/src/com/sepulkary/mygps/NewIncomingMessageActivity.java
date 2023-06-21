package com.sepulkary.mygps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class NewIncomingMessageActivity extends Activity {
	private TextView messageTitle;
	private TextView messageBody;
	private Button okReadButton;

	String targetLoginInput = "";
	String targetUserMessage = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_new_incoming_message);

		messageTitle = (TextView) findViewById(R.id.messageTitle);
		messageBody = (TextView) findViewById(R.id.messageBody);
		okReadButton = (Button) findViewById(R.id.okReadButton);

		Intent intent = getIntent();
		targetLoginInput = (intent.getStringExtra("targetLoginInput"));
		targetUserMessage = (intent.getStringExtra("targetUserMessage"));

		messageTitle.setText(getString(R.string.message_new_from) + " " + targetLoginInput + ":");
		messageBody.setText(targetUserMessage);

		okReadButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(NewIncomingMessageActivity.this, MainActivity.class);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
}
