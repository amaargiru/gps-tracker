package com.sepulkary.mygps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Random;

public class MessageActivity extends Activity {
	static Context context;

	private EditText messageInput;
	private Button sendButton;
	private Button cancelButton;
	private CheckBox checkTarget1;
	private CheckBox checkTarget2;

	final int MESSAGE_TO_NONE = 0;
	final int MESSAGE_TO_FIRST = 1;
	final int MESSAGE_TO_SECOND = 2;
	final int MESSAGE_TO_BOTH = 3;
	int messageToTargets = MESSAGE_TO_NONE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_message);

		sendButton = (Button) findViewById(R.id.sendButton);
		cancelButton = (Button) findViewById(R.id.cancelButton);
		messageInput = (EditText) findViewById(R.id.messageInput);
		checkTarget1 = (CheckBox) findViewById(R.id.checkTarget1);
		checkTarget2 = (CheckBox) findViewById(R.id.checkTarget2);

		sendButton.setOnClickListener(new Button.OnClickListener() { // ���������� ������ "��������� ���������"
					@Override
					public void onClick(View arg0) {
						if (!checkTarget1.isChecked() && !checkTarget2.isChecked()) {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

							alertDialogBuilder.setMessage(getString(R.string.message_select)).setCancelable(false)
									.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
										}
									});
							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
						} else if (messageInput.getText().toString().equals("")) {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

							alertDialogBuilder.setMessage(getString(R.string.message_notmessage)).setCancelable(false)
									.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
										}
									});
							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
						} else {
							Intent intent = new Intent(MessageActivity.this, MainActivity.class);
							intent.putExtra("messageInput", messageInput.getText().toString());

							if (checkTarget1.isChecked())
								messageToTargets += MESSAGE_TO_FIRST;
							if (checkTarget2.isChecked())
								messageToTargets += MESSAGE_TO_SECOND;
							if (checkTarget2.isChecked() && checkTarget2.isChecked())
								messageToTargets += MESSAGE_TO_BOTH;

							Random r = new Random();
							int id = r.nextInt(Integer.MAX_VALUE / 10);
							intent.putExtra("messageToTargets", 10 * id + messageToTargets); // В младшем десятичном разряде всегда будут адреса целей, в остальных - уникальный ID сообщения

							setResult(RESULT_OK, intent);
							finish();
						}
					}
				});

		cancelButton.setOnClickListener(new Button.OnClickListener() { // ���������� ������ "������"
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(MessageActivity.this, MainActivity.class);
						setResult(RESULT_CANCELED, intent);
						finish();
					}
				});
	}
}