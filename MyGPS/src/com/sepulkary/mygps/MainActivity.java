package com.sepulkary.mygps;

import android.app.*;
import android.content.*;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;

public class MainActivity extends Activity implements LocationListener, OnTouchListener {
	static Context context;
	final int RATERELOADED = 2; // Количество запусков приложения перед просьбой прорейтинговать
	int appReloaded = 0; // Количество запусков приложения 
	boolean appRated = false;
	public static final String PREFS_NAME = "MyPrefsFile";

	String masterLoginInput = "";
	String masterPassInput = "";
	String target1LoginInput = "";
	String target1PassInput = "";
	String target2LoginInput = "";
	String target2PassInput = "";
	double target1Latitude = 0.0;
	double target1Longitude = 0.0;
	String target1LastLocationTime = "";
	double target1BatteryLevel = 0.0;
	String target1UserMessage = "";
	String target1ServiceMessage = "";
	double target2Latitude = 0.0;
	double target2Longitude = 0.0;
	String target2LastLocationTime = "";
	double target2BatteryLevel = 0.0;
	String target2UserMessage = "";
	String target2ServiceMessage = "";

	String target1OldServiceMessage = "";
	String target2OldServiceMessage = "";

	final int REQUEST_CODE_HELP = 1;
	final int REQUEST_CODE_USERS = 2;
	final int REQUEST_CODE_PERSON = 3;
	final int REQUEST_CODE_MESSAGE = 4;
	final int REQUEST_CODE_INCOMING_MESSAGE = 5;

	final int MESSAGE_TO_NONE = 0;
	int messageToTargets = MESSAGE_TO_NONE;
	String messageInput = "";

	// Индикатор состояния программы
	final static int STATE_ZERO = 0; // Ни свои данные, ни данные целей не введены
	final static int STATE_OWNER = 1; // Введены свои данные
	final static int STATE_TARGETS = 2; // Введены свои данные и данные хотя бы одной цели
	final static int STATE_RUN_OWNER = 3; // Введены свои данные и пользователь нажал кнопку "Старт"
	final static int STATE_RUN_TARGETS = 4; // Введены свои данные и данные хотя бы одной цели и пользователь нажал кнопку "Старт"
	int appState = STATE_ZERO;

	// Индикатор автоперезагрузки
	boolean autoReloadState = false;

	// Пользователь, по которому центрируется карта
	final static int USER_MASTER = 0;
	final static int USER_TARGET1 = 1;
	final static int USER_TARGET2 = 2;
	int userMapCentered = USER_MASTER;

	// Флаг попытки связи с сервером
	final static int SERVER_UNCONNECT = 0;
	final static int SERVER_CONNECT = 1;
	int currentConnectStatus = SERVER_UNCONNECT;

	private int _xDelta = 0;
	private int _yDelta = 0;

	final static int minTime = 10000; // Минимальное время обновления информации о положении пользователя, мс
	final static int minDistance = 5; // Минимальное смещение пользователя, при котором будут выданы новые координаты, м
	double masterLongitude = 0.0;
	double masterLatitude = 0.0;
	int masterBatteryLevel = 0;

	private RelativeLayout rootLayout; // Верхний уровень разметки
	private RelativeLayout buttonLayout; // Панелька с кнопками
	private MapView mapView; // Вывод карты с местоположением пользователя
	private ImageButton settingsButton; // Кнопка "Настройки"
	private ImageButton start_stopButton; // Кнопка "Старт/Стоп сканирования"
	private ImageButton personButton; // Кнопка "Найти и отобразить человека"
	private ImageButton messageButton; // Кнопка "Отправить сообщение"
	private ImageButton ratingButton; // Кнопка "Прорейтинговать"
	private ImageButton helpButton; // Кнопка "Помощь"
	private ImageButton dragButton; // Кнопка "Перетащить панельку с кнопками"
	private com.google.ads.AdView adView; // Рекламный блок

	boolean userAsked = false;

	private LocationManager locationManager;
	private Location location;
	private String provider;

	static int LOGLEVEL = 2;
	static boolean DEBUG = LOGLEVEL > 1;
	static boolean WARNING = LOGLEVEL > 0;
	static final String TAG = "MainActivity";

	static double EPSILON = 0.00000001;

	Timer myTimer = new Timer(); // Создаем таймер

	MyItemizedOverlay myItemizedOverlay = null; // Вывод карты с местоположением пользователя
	MyItemizedOverlay myItemizedOverlay2 = null; // Цель 1
	MyItemizedOverlay myItemizedOverlay3 = null; // Цель 2
	private static int DEFAULT_ZOOM = 14; // Увеличение карты OpenStreetMap по умолчанию
	int currentZoom = DEFAULT_ZOOM;
	private static float DEFAULT_LAT = 52.516667f; // Начальная точка карты OpenStreetMap (отображается при первом запуске программы). Конкретно - Берлин
	private static float DEFAULT_LONG = 13.383333f;
	float initialLat = 0.0f;
	float initialLong = 0.0f;

	boolean isInFront = true;

	double ExtractDouble(String in) {
		double out = 0;

		try {
			out = Double.parseDouble(in);
		} catch (NumberFormatException e) {
		}
		return out;
	}

	private void getBatteryPercentage() {
		BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(this);
				int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				if (currentLevel >= 0 && scale > 0)
					masterBatteryLevel = (currentLevel * 100) / scale;
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}

	//http://stackoverflow.com/questions/3407256/height-of-status-bar-in-android
	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	void interfaceRedraw() {
		switch (appState) {
			case STATE_ZERO:
				settingsButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha_infinite));
				start_stopButton.clearAnimation();
				start_stopButton.setImageResource(R.drawable.ic_action_play_inactive);
				personButton.setImageResource(R.drawable.ic_action_person_inactive);
				messageButton.setImageResource(R.drawable.ic_action_new_email_inactive);
				break;
			case STATE_OWNER:
				settingsButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha_infinite));
				start_stopButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha_infinite));
				start_stopButton.setImageResource(R.drawable.ic_action_play);
				personButton.setImageResource(R.drawable.ic_action_person_inactive);
				messageButton.setImageResource(R.drawable.ic_action_new_email_inactive);
				break;
			case STATE_TARGETS:
				settingsButton.clearAnimation();
				start_stopButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha_infinite));
				start_stopButton.setImageResource(R.drawable.ic_action_play);
				personButton.setImageResource(R.drawable.ic_action_person_inactive);
				messageButton.setImageResource(R.drawable.ic_action_new_email_inactive);
				break;
			case STATE_RUN_OWNER:
				settingsButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha_infinite));
				start_stopButton.clearAnimation();
				start_stopButton.setImageResource(R.drawable.ic_action_pause);
				personButton.setImageResource(R.drawable.ic_action_person_inactive);
				messageButton.setImageResource(R.drawable.ic_action_new_email_inactive);
				break;
			case STATE_RUN_TARGETS:
				settingsButton.clearAnimation();
				start_stopButton.clearAnimation();
				start_stopButton.setImageResource(R.drawable.ic_action_pause);
				personButton.setImageResource(R.drawable.ic_action_person);
				messageButton.setImageResource(R.drawable.ic_action_new_email);
				break;
		}
	}

	void SaveSettings() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		if (appReloaded < RATERELOADED + 1) { // Запишем количество запусков приложения
			appReloaded++;
			editor.putInt("appReloaded", appReloaded);
			editor.putBoolean("appRated", appRated);
		}

		editor.putString("masterLoginInput", masterLoginInput);
		editor.putString("masterPassInput", masterPassInput);
		editor.putString("target1LoginInput", target1LoginInput);
		editor.putString("target1PassInput", target1PassInput);
		editor.putString("target2LoginInput", target2LoginInput);
		editor.putString("target2PassInput", target2PassInput);
		editor.putInt("appState", appState);
		editor.putBoolean("autoReloadState", autoReloadState);
		editor.putInt("currentZoom", currentZoom);
		editor.putFloat("initialLat", (float) masterLatitude);
		editor.putFloat("initialLong", (float) masterLongitude);
		editor.putInt("messageToTargets", messageToTargets);
		editor.putString("messageInput", messageInput);
		editor.putString("target1OldServiceMessage", target1OldServiceMessage);
		editor.putString("target2OldServiceMessage", target2OldServiceMessage);
		editor.putString("target1ServiceMessage", target1ServiceMessage);
		editor.putString("target2ServiceMessage", target2ServiceMessage);

		editor.commit();
	}

	void RestoreSettings() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		appReloaded = settings.getInt("appReloaded", 0);
		appRated = settings.getBoolean("appRated", false);
		masterLoginInput = settings.getString("masterLoginInput", "");
		masterPassInput = settings.getString("masterPassInput", "");
		target1LoginInput = settings.getString("target1LoginInput", "");
		target1PassInput = settings.getString("target1PassInput", "");
		target2LoginInput = settings.getString("target2LoginInput", "");
		target2PassInput = settings.getString("target2PassInput", "");
		appState = settings.getInt("appState", STATE_ZERO);
		autoReloadState = settings.getBoolean("autoReloadState", false);
		initialLat = settings.getFloat("initialLat", DEFAULT_LAT);
		initialLong = settings.getFloat("initialLong", DEFAULT_LONG);
		messageToTargets = settings.getInt("messageToTargets", MESSAGE_TO_NONE);
		messageInput = settings.getString("messageInput", "");
		target1OldServiceMessage = settings.getString("target1OldServiceMessage", "");
		target2OldServiceMessage = settings.getString("target2OldServiceMessage", "");
		target1ServiceMessage = settings.getString("target1ServiceMessage", "");
		target2ServiceMessage = settings.getString("target2ServiceMessage", "");
		currentZoom = settings.getInt("currentZoom", DEFAULT_ZOOM);
	}

	void createInfoNotification(String title, String message, int notifyIcon) {
		Intent notificationIntent = new Intent(context, MainActivity.class);
		Notification.Builder nb = new Notification.Builder(context).setSmallIcon(notifyIcon)
				  .setAutoCancel(true)
				  .setTicker(message)
				  .setContentText(message)
				  .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
				  .setWhen(System.currentTimeMillis())
				  .setContentTitle("New message")
				  .setDefaults(Notification.DEFAULT_ALL);
		Notification n = nb.getNotification();

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		//Random r = new Random();
		//int nid = r.nextInt(2147483647);
		nm.notify(101, n);
	}

	void RootOperation(){
		if (DEBUG) {
			Log.v(TAG, "location = " + location);
			Log.v(TAG, "appState = " + appState);
		}

		//RestoreSettings();

		currentZoom = mapView.getZoomLevel();

		if ((appState == STATE_RUN_OWNER) || (appState == STATE_RUN_TARGETS)) {
			getBatteryPercentage();

			mapView.getOverlays().remove(myItemizedOverlay); //http://stackoverflow.com/questions/5861091/cant-remove-overlay-items
			mapView.getOverlays().remove(myItemizedOverlay2);
			mapView.getOverlays().remove(myItemizedOverlay3);

			Drawable masterMarker = getResources().getDrawable(R.drawable.map_marker_master);
			int markerWidth = masterMarker.getIntrinsicWidth();
			int markerHeight = masterMarker.getIntrinsicHeight();
			masterMarker.setBounds(0, markerHeight, markerWidth, 0);

			ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
			myItemizedOverlay = new MyItemizedOverlay(masterMarker, resourceProxy);
			mapView.getOverlays().add(myItemizedOverlay);

			GeoPoint point = new GeoPoint((int) (masterLatitude * 1000000), (int) (masterLongitude * 1000000)); // Центрируем карту по местоположению пользователя и выводим маркер
			if (userMapCentered == USER_MASTER)
				mapView.getController().animateTo(point);
			myItemizedOverlay.addItem(point, "masterPoint", "masterPoint");

			if (appState == STATE_RUN_TARGETS) {
				if (currentConnectStatus == SERVER_UNCONNECT) {
					currentConnectStatus = SERVER_CONNECT;
					new RequestTask().execute("http://sepulkary.com/all_my_family_gps/connect_to_my_family.php");
				}

				if (DEBUG)
					Log.v(TAG, target1UserMessage + ", " + target1ServiceMessage + ", " + target1OldServiceMessage);

				if ((!target1UserMessage.equals("")) && (!target1ServiceMessage.equals(target1OldServiceMessage))
						  && (!target1ServiceMessage.equals(String.valueOf(MESSAGE_TO_NONE)))) {// Пришло новое сообщение от цели 1
					target1OldServiceMessage = target1ServiceMessage;

					if (DEBUG)
						Log.w(TAG, "New message from target 1");

					Intent intent = new Intent(MainActivity.this, NewIncomingMessageActivity.class);
					intent.putExtra("targetLoginInput", target1LoginInput);
					intent.putExtra("targetUserMessage", target1UserMessage);
					startActivityForResult(intent, REQUEST_CODE_INCOMING_MESSAGE);

					//AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

					//alertDialogBuilder.setTitle(getString(R.string.message_new_from) + " " + target1LoginInput);
					//alertDialogBuilder.setMessage(target1UserMessage).setCancelable(false)
					//		  .setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
					//			  public void onClick(DialogInterface dialog, int id) {
					//			  }
					//		  });
					//AlertDialog alertDialog = alertDialogBuilder.create();
					//alertDialog.show();

					//if (!isInFront)
					//	createInfoNotification(getString(R.string.message_new), getString(R.string.message_new_from) + " "
					//			  + target1LoginInput, R.drawable.ic_action_unread);
				}

				if ((!target2UserMessage.equals("")) && (!target2ServiceMessage.equals(target2OldServiceMessage))
						  && (!target2ServiceMessage.equals(String.valueOf(MESSAGE_TO_NONE)))) {// Пришло новое сообщение от цели 2
					target2OldServiceMessage = target2ServiceMessage;

					if (DEBUG)
						Log.w(TAG, "New message from target 2");

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

					alertDialogBuilder.setTitle(getString(R.string.message_new_from) + " " + target2LoginInput);
					alertDialogBuilder.setMessage(target2UserMessage).setCancelable(false)
							  .setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
								  public void onClick(DialogInterface dialog, int id) {
								  }
							  });
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();

					//if (!isInFront)
					//	createInfoNotification(getString(R.string.message_new), getString(R.string.message_new_from) + " "
					//			  + target2LoginInput, R.drawable.ic_action_unread);
				}

				if ((Math.abs(target1Latitude) > EPSILON) && (Math.abs(target1Longitude) > EPSILON)) {
					Drawable target1Marker = getResources().getDrawable(R.drawable.map_marker_target1);
					int target1MarkerWidth = target1Marker.getIntrinsicWidth();
					int target1MarkerHeight = target1Marker.getIntrinsicHeight();
					target1Marker.setBounds(0, target1MarkerHeight, target1MarkerWidth, 0);

					resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
					myItemizedOverlay2 = new MyItemizedOverlay(target1Marker, resourceProxy);
					mapView.getOverlays().add(myItemizedOverlay2);

					GeoPoint target1Point = new GeoPoint((int) (target1Latitude * 1000000), (int) (target1Longitude * 1000000)); // Центрируем карту по местоположению пользователя и выводим маркер
					if (userMapCentered == USER_TARGET1)
						mapView.getController().animateTo(target1Point);
					myItemizedOverlay2.addItem(target1Point, "target1Point", "target1Point");
				}

				if ((Math.abs(target2Latitude) > EPSILON) && (Math.abs(target2Longitude) > EPSILON)) {
					Drawable target2Marker = getResources().getDrawable(R.drawable.map_marker_target2);
					int target2MarkerWidth = target2Marker.getIntrinsicWidth();
					int target2MarkerHeight = target2Marker.getIntrinsicHeight();
					target2Marker.setBounds(0, target2MarkerHeight, target2MarkerWidth, 0);

					resourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
					myItemizedOverlay3 = new MyItemizedOverlay(target2Marker, resourceProxy);
					mapView.getOverlays().add(myItemizedOverlay3);

					GeoPoint target2Point = new GeoPoint((int) (target2Latitude * 1000000), (int) (target2Longitude * 1000000)); // Центрируем карту по местоположению пользователя и выводим маркер
					if (userMapCentered == USER_TARGET2)
						mapView.getController().animateTo(target2Point);
					myItemizedOverlay3.addItem(target2Point, "target2Point", "target2Point");
				}
			}
		}
		SaveSettings();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (DEBUG)
			Log.w(TAG, "onCreate");

		context = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE); // Приложение без заголовка
		setContentView(R.layout.activity_main); // Выбираем UI

		rootLayout = (RelativeLayout) findViewById(R.id.rootLayout); // Верхний уровень разметки
		buttonLayout = (RelativeLayout) findViewById(R.id.buttonLayout); // Панелька с кнопками
		mapView = (MapView) findViewById(R.id.mapview); // Вывод карты с местоположением пользователя
		settingsButton = (ImageButton) findViewById(R.id.settingsButton); // Кнопка "Настройки"
		start_stopButton = (ImageButton) findViewById(R.id.start_stopButton); // Кнопка "Старт/Стоп сканирования"
		personButton = (ImageButton) findViewById(R.id.personButton); // Кнопка "Найти и отобразить человека"
		messageButton = (ImageButton) findViewById(R.id.messageButton); // Кнопка "Отправить сообщение"
		ratingButton = (ImageButton) findViewById(R.id.ratingButton); // Кнопка "Прорейтинговать"
		helpButton = (ImageButton) findViewById(R.id.helpButton); // Кнопка "Помощь"
		dragButton = (ImageButton) findViewById(R.id.dragButton); // Кнопка "Перетащить панельку с кнопками"
		adView = (com.google.ads.AdView) findViewById(R.id.adView); // Рекламный блок

		adView.setVisibility(View.GONE); // Выключаем рекламу

		RestoreSettings();

		mapView.getController().setZoom(currentZoom);
		mapView.setMultiTouchControls(true);

		myTimer = new Timer();
		final Handler uiHandler = new Handler();
		myTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				uiHandler.post(new Runnable() {
					@Override
					public void run() {
						RootOperation();
					}
				});
			}
		}, 0L, minTime); // 0 миллисекунд до первого запуска

		if ((appState == STATE_RUN_OWNER) || (appState == STATE_RUN_TARGETS)) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);// Подключаем определение местоположения
			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, false);
			provider = LocationManager.GPS_PROVIDER;
			location = locationManager.getLastKnownLocation(provider);
			if (location != null)
				onLocationChanged(location);
			locationManager.requestLocationUpdates(provider, minTime, minDistance, MainActivity.this);
		}

		interfaceRedraw();

		GeoPoint point = new GeoPoint((int) (initialLat * 1000000), (int) (initialLong * 1000000)); // Центрируем карту по начальному местоположению
		mapView.getController().animateTo(point);

		dragButton.setOnTouchListener(this);

		personButton.setOnClickListener(new Button.OnClickListener() { // Обработчик кнопки "Найти и отобразить человека" (можно найти и владельца)
			@Override
			public void onClick(View arg0) {
				if (appState == STATE_RUN_TARGETS) {
					Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
					arg0.startAnimation(animAlpha);

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							Intent intent = new Intent(MainActivity.this, PersonActivity.class);
							startActivityForResult(intent, REQUEST_CODE_PERSON);
						}
					}, 350);
				}
			}
		});

		messageButton.setOnClickListener(new Button.OnClickListener() { // Обработчик кнопки "отправить сообщение"
			@Override
			public void onClick(View arg0) {
				if (appState == STATE_RUN_TARGETS) {
					Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
					arg0.startAnimation(animAlpha);

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							Intent intent = new Intent(MainActivity.this, MessageActivity.class);
							startActivityForResult(intent, REQUEST_CODE_MESSAGE);
						}
					}, 350);
				}
			}
		});

		start_stopButton.setOnClickListener(new Button.OnClickListener() { // Обработчик кнопки "Старт/Стоп сканирования"
			@Override
			public void onClick(View arg0) {
				if ((appState == STATE_OWNER) || (appState == STATE_TARGETS)) {
					Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
					arg0.startAnimation(animAlpha);

					locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);// Подключаем определение местоположения
					Criteria criteria = new Criteria();
					provider = locationManager.getBestProvider(criteria, false);
					provider = LocationManager.GPS_PROVIDER;
					location = locationManager.getLastKnownLocation(provider);
					if (location != null)
						onLocationChanged(location);
					locationManager.requestLocationUpdates(provider, minTime, minDistance, MainActivity.this);

					if (appState == STATE_OWNER)
						appState = STATE_RUN_OWNER;
					else if (appState == STATE_TARGETS)
						appState = STATE_RUN_TARGETS;
				}
				else if ((appState == STATE_RUN_OWNER) || (appState == STATE_RUN_TARGETS)) {
					if (appState == STATE_RUN_OWNER)
						appState = STATE_OWNER;
					else if (appState == STATE_RUN_TARGETS)
						appState = STATE_TARGETS;

					if (location != null) // Отключаем GPS
						locationManager.removeUpdates(MainActivity.this);
				}
				interfaceRedraw();
			}
		});

		settingsButton.setOnClickListener(new Button.OnClickListener() { // Обработчик кнопки "Настройки"
			@Override
			public void onClick(View arg0) {
				Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
				arg0.startAnimation(animAlpha);

				if (location != null) // Отключаем GPS
					locationManager.removeUpdates(MainActivity.this);

				if ((appState == STATE_RUN_OWNER) || (appState == STATE_RUN_TARGETS)) {
					if (appState == STATE_RUN_OWNER)
						appState = STATE_OWNER;
					else if (appState == STATE_RUN_TARGETS)
						appState = STATE_TARGETS;}

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(MainActivity.this, UsersActivity.class);
						intent.putExtra("masterLoginInput", masterLoginInput);
						intent.putExtra("masterPassInput", masterPassInput);
						intent.putExtra("target1LoginInput", target1LoginInput);
						intent.putExtra("target1PassInput", target1PassInput);
						intent.putExtra("target2LoginInput", target2LoginInput);
						intent.putExtra("target2PassInput", target2PassInput);
						intent.putExtra("appState", appState);
						intent.putExtra("autoReloadState", autoReloadState);
						startActivityForResult(intent, REQUEST_CODE_USERS);
					}
				}, 350);
				interfaceRedraw();
			}
		});

		ratingButton.setOnClickListener(new Button.OnClickListener() { // Обработчик кнопки "Прорейтинговать"
			@Override
			public void onClick(View arg0) {
				Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
				arg0.startAnimation(animAlpha);

				Toast toast = Toast.makeText(context, R.string.rate_app, Toast.LENGTH_LONG);
				View view = toast.getView();
				view.setBackgroundResource(R.drawable.child_selector);
				toast.show();

				userAsked = true;

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						Uri uri = Uri.parse("market://details?id=" + getPackageName()); // Go to Android market
						Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
						try {
							startActivity(goToMarket);
						} catch (ActivityNotFoundException e) {
							Toast toast = Toast.makeText(context, R.string.couldnt_rate, Toast.LENGTH_LONG);
							View view = toast.getView();
							view.setBackgroundResource(R.drawable.child_selector);
							toast.show();
						}
					}
				}, 350);
			}
		});

		helpButton.setOnClickListener(new Button.OnClickListener() { // Обработчик кнопки "Помощь"
			@Override
			public void onClick(View arg0) {
				Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
				arg0.startAnimation(animAlpha);

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(MainActivity.this, HelpActivity.class);
						startActivityForResult(intent, REQUEST_CODE_HELP);
					}
				}, 350);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (DEBUG)
			Log.w(TAG, "onResume");

		isInFront = true;

		if ((appReloaded == RATERELOADED) && (!appRated)) {// Просим оценить приложение
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

			appRated = true;
			alertDialogBuilder.setTitle(getString(R.string.rate_app));

			alertDialogBuilder.setMessage(getString(R.string.if_you_like) + getString(R.string.app_name) + getString(R.string.please_rate))
					  .setCancelable(false).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Uri uri = Uri.parse("market://details?id=" + getPackageName());
					Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
					try {
						startActivity(goToMarket);
					} catch (ActivityNotFoundException e) {
						Toast toast = Toast.makeText(context, R.string.couldnt_rate, Toast.LENGTH_LONG);
						View view = toast.getView();
						view.setBackgroundResource(R.drawable.child_selector);
						toast.show();
					}
					dialog.cancel();
				}
			}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}

		// http://stackoverflow.com/questions/17141829/android-read-text-file-from-internet
		// Берем файл с сервера; если он содержит определенную строку, то включаем рекламу, иначе она остается выключенной 
		new Thread() {
			@Override
			public void run() {
				try {
					URL u = new URL(getString(R.string.ads_switcher));
					HttpURLConnection c = (HttpURLConnection) u.openConnection();
					c.setRequestMethod("GET");
					c.connect();
					InputStream in = c.getInputStream();
					final ByteArrayOutputStream bo = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					in.read(buffer);
					bo.write(buffer);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (bo.toString().contains(getString(R.string.ads_target)))
								adView.setVisibility(View.VISIBLE); // Включаем рекламу
							try {
								bo.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (location != null)
			locationManager.removeUpdates(this); // Отключаем GPS

		if (myTimer != null) { // Отключаем таймер
			myTimer.cancel();
			myTimer = null;
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (DEBUG)
			Log.w(TAG, "onPause");

		isInFront = false;

		SaveSettings();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			masterLatitude = location.getLatitude();
			masterLongitude = location.getLongitude();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	// http://stackoverflow.com/questions/9398057/android-move-a-view-on-touch-move-action-move

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final int X = (int) event.getRawX();
		final int Y = (int) event.getRawY();

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) buttonLayout.getLayoutParams();
				_xDelta = X - lParams.leftMargin;
				_yDelta = Y - lParams.topMargin;
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				int w = getWindowManager().getDefaultDisplay().getWidth();
				int h = getWindowManager().getDefaultDisplay().getHeight();
				int vw = buttonLayout.getWidth();
				int vh = buttonLayout.getHeight();
				int adsh = adView.getHeight();

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) buttonLayout.getLayoutParams();
				if (((X - _xDelta) > 0) && ((X - _xDelta) < (w - vw))) {
					layoutParams.leftMargin = X - _xDelta;
				}
				if (((Y - _yDelta) > 0) && ((Y - _yDelta) < (h - vh - adsh - getStatusBarHeight()))) {
					layoutParams.topMargin = Y - _yDelta;
				}
				buttonLayout.setLayoutParams(layoutParams);
				break;
		}
		rootLayout.invalidate();

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		if (requestCode == REQUEST_CODE_USERS) {
			masterLoginInput = data.getStringExtra("masterLoginInput");
			masterPassInput = data.getStringExtra("masterPassInput");
			target1LoginInput = data.getStringExtra("target1LoginInput");
			target1PassInput = data.getStringExtra("target1PassInput");
			target2LoginInput = data.getStringExtra("target2LoginInput");
			target2PassInput = data.getStringExtra("target2PassInput");
			autoReloadState = data.getBooleanExtra("autoReloadState", false);

			if (DEBUG) {
				//Log.v(TAG, "masterLoginInput = " + masterLoginInput);
				//Log.v(TAG, "masterPassInput = " + masterPassInput);
				//Log.v(TAG, "target1LoginInput = " + target1LoginInput);
				//Log.v(TAG, "target1PassInput = " + target1PassInput);
				//Log.v(TAG, "target2LoginInput = " + target2LoginInput);
				//Log.v(TAG, "target2PassInput = " + target2PassInput);
			}

			appState = STATE_ZERO;

			if (!masterLoginInput.equals("") && !masterPassInput.equals("")) {
				if (!target1LoginInput.equals("") && (!target1PassInput.equals("")) || (!target2LoginInput.equals("") && !target2PassInput.equals("")))
					appState = STATE_TARGETS;
				else
					appState = STATE_OWNER;
			}

			interfaceRedraw();
		}

		if (requestCode == REQUEST_CODE_PERSON) {
			userMapCentered = data.getIntExtra("userMapCentered", USER_MASTER);

			if (DEBUG)
				Log.v(TAG, "userMapCentered = " + userMapCentered);
		}

		if ((requestCode == REQUEST_CODE_MESSAGE) && (resultCode == RESULT_OK)) {
			messageInput = data.getStringExtra("messageInput");
			messageToTargets = data.getIntExtra("messageToTargets", MESSAGE_TO_NONE);

			if (DEBUG)
				Log.v(TAG, "messageInput = " + messageInput + "; messageToTargets = " + String.valueOf(messageToTargets));
		}
	}

	class RequestTask extends AsyncTask<String, String, String> {//dajver.blogspot.ru/2013/02/json.html

		@Override
		protected String doInBackground(String... params) {
			try {
				//создаем запрос на сервер
				DefaultHttpClient hc = new DefaultHttpClient();
				ResponseHandler<String> res = new BasicResponseHandler();
				//он у нас будет посылать post запрос
				HttpPost postMethod = new HttpPost(params[0]);
				//будем передавать одиннадцать параметров
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(11);

				nameValuePairs.add(new BasicNameValuePair("masterLoginInput", masterLoginInput));
				nameValuePairs.add(new BasicNameValuePair("masterPassInput", masterPassInput));

				nameValuePairs.add(new BasicNameValuePair("target1LoginInput", target1LoginInput));
				nameValuePairs.add(new BasicNameValuePair("target1PassInput", target1PassInput));

				nameValuePairs.add(new BasicNameValuePair("target2LoginInput", target2LoginInput));
				nameValuePairs.add(new BasicNameValuePair("target2PassInput", target2PassInput));

				nameValuePairs.add(new BasicNameValuePair("masterLatitude", String.valueOf(masterLatitude)));
				nameValuePairs.add(new BasicNameValuePair("masterLongitude", String.valueOf(masterLongitude)));

				nameValuePairs.add(new BasicNameValuePair("masterBatteryLevel", String.valueOf(masterBatteryLevel)));
				nameValuePairs.add(new BasicNameValuePair("masterUserMessage", messageInput));
				nameValuePairs.add(new BasicNameValuePair("masterServiceMessage", String.valueOf(messageToTargets)));

				//собераем их вместе и посылаем на сервер
				postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				//получаем ответ от сервера
				String response = hc.execute(postMethod, res);

				//if (DEBUG)
				//	Log.v(TAG, "Http Client response = " + response);

				JSONObject json = new JSONObject(response);
				target1Latitude = ExtractDouble(json.getString("target1Latitude"));
				target1Longitude = ExtractDouble(json.getString("target1Longitude"));
				target1LastLocationTime = json.getString("target1LastLocationTime");
				target1BatteryLevel = ExtractDouble(json.getString("target1BatteryLevel"));
				target1UserMessage = json.getString("target1UserMessage");
				target1ServiceMessage = json.getString("target1ServiceMessage");
				target2Latitude = ExtractDouble(json.getString("target2Latitude"));
				target2Longitude = ExtractDouble(json.getString("target2Longitude"));
				target2LastLocationTime = json.getString("target2LastLocationTime");
				target2BatteryLevel = ExtractDouble(json.getString("target2BatteryLevel"));
				target2UserMessage = json.getString("target2UserMessage");
				target2ServiceMessage = json.getString("target2ServiceMessage");

				if (DEBUG) {
					//Log.v(TAG, "target1Latitude = " + target1Latitude);
					//Log.v(TAG, "target1Longitude = " + target1Longitude);
					//Log.v(TAG, "target1LastLocationTime = " + target1LastLocationTime);
					Log.v(TAG, "target1UserMessage = " + target1UserMessage);
					Log.v(TAG, "target1ServiceMessage = " + target1ServiceMessage);
					//Log.v(TAG, "target2UserMessage = " + target2UserMessage);
					//Log.v(TAG, "target2ServiceMessage = " + target2ServiceMessage);
				}
			} catch (Exception e) {
				if (DEBUG)
					Log.e(TAG, "Error = " + e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			currentConnectStatus = SERVER_UNCONNECT;
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}
}