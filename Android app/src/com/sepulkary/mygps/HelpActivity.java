package com.sepulkary.mygps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class HelpActivity extends Activity {
	private Button okHelpButton; // 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_help);

		okHelpButton = (Button) findViewById(R.id.okHelpButton);

		okHelpButton.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(HelpActivity.this, MainActivity.class);
						setResult(RESULT_OK, intent);
						finish();
					}
				});
	}
}
