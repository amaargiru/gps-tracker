**Мой старый проект примерно 2014 года, сейчас вряд ли представляет практическую ценность. Программа была представлена в Google Play, люди ей довольно активно пользовались. Я сделал заготовку для статьи на "Хабре", но полноценный материал так и не выкристаллизовался. Ниже - та самая заготовка.**

Уважаемые пользователи, пожалуйста, не судите строго: пока это только бета-версия, программа еще только тестируется. Поэтому, если у вас возникли вопросы и пожелания, пожалуйста, не торопитесь ставить плохой рейтинг, а лучше напишите свои претензии на me@sepulkary.com, обратная связь очень важна для нас и мы обязательно попытаемся устранить все замеченные вами недочеты.

Что касается самого приложения, то название говорит само за себя, это Бесплатный GPS Локатор, который позволит вам отслеживать до двух других телефонов или планшетов с установленной аналогичной программой.

Есть два важных отличия от аналогичных программ:
1) не нужна регистрация, не нужен ваш email, не нужен ваш номер телефона. Вам просто нужно установить программу на свой телефон и на телефон ребенка, выбрать любые логин и пароль для себя и для ребенка, и все - начинайте наблюдать;  
2) никакого использования GPS-данных пользователя для рекламы или маркетинга. Сбор данных о местоположении пользователя не ведется, данные с сервера периодически стираются.

Вот так выглядит процедура наблюдения, например, за двумя вашими детьми:  
1) установите на своем телефоне (планшете) и на телефонах детей программу "Бесплатный GPS Локатор";  
2) придумайте пару логин/пароль для себя и своего ребенка (например, вы будете PapaNikitin с паролем ghinKk5690asx, для дочки используйте LenaNikitina с паролем ghHHJJNBB71, а для сына PavelNikitin с паролем 67890oulyF);  
3) в программе, установленной на вашем телефоне, введите логины и пароли как на скриншоте;  
4) в программах, установленных на телефонах детей, введите их логины и пароли в верхние поля ("Мой логин" и "Мой пароль"), а свои логины и пароли введите в оставшиеся поля ("Логин цели", "Пароль цели").

И все - каждые 10 секунд вы будете получать с сервера обновленную информацию о местоположении детей, а дети смогут видеть вас и друг друга. Причем, круг "приглядывающих" не ограничен, только вы определяете список лиц, которым передаете пароль, так что наблюдать за ребенком могут одновременно папа, мама, дедушка и обе бабушки :)

Для Android'а написано немало программ, которые, будучи установлены на двух разных аппаратах, позволяют пользователям видеть GPS-координаты друг друга. Но мне что-то не удалось найти оптимальный вариант — то обмен координатами ведется через SMS, то время обновления велико, то требуют регистрации (я понимаю, что сейчас время посадочных страниц и социального маркетинга, но уже достало немного), то начинают предлагать платный контент... Одним словом, все подталкивает к тому, чтобы самому написать Android приложение с бриджем и бортпроводницами, способное хорошо и быстро выполнять нехитрую задачу — выводить на карту маркеры, соответствующие местонахождению телефонов, прикрученных к чему-нибудь подвижному и что было бы жаль потерять (в моем случае это ребенок и автомобиль).

Формально, хотелось бы следующего:  
• быстрый обмен информацией, положение маркера должно обновляться часто, скажем, раз в 20 секунд. Если ребенок во время пикника на природе перегрыз веревку и стал уходить в глубину заповедника, то скорость обновления имеет критическое значение;  
• обмен информацией через сервер. Никаких SMS и прочих обходных путей, просто закидываешь свои координаты на сервер, одновременно получая координаты отслеживаемых аппаратов;  
• можно впятидесятером наблюдать за одним; так, например, все сотрудники отдела могут видеть, где шляется их начальник;  
• можно наблюдать за любым, кто поделился с тобой своей парой логин/пароль. Если Изабелла из Бразилии и Педро из Магадана переслали друг другу по емэйлу волшебные Isabella1996/1234 и PetrNikolaevichMag/ghbdtnbpnehmvs, то они сразу могут беспрепятственно наблюдать за перемещениями друг друга;  
• встроенные сообщения. Конечно, мессенджеров и так миллион, но иногда удобно переписываться, наблюдая за перемещениями своего собеседника;  
• возможность автоматической перезагрузки приложения после отключения питания.  

<img src="http://habrastorage.org/getpro/habr/post_images/a74/728/52b/a7472852bfb0fcd3d72fcc48b8cd16e5.png" align="right"/><b>Карта и маркеры</b>

Для отображения карты <a href="http://www.openstreetmap.org/">OpenStreetMap</a> нам понадобятся <i>osmdroid-android-4.2.jar</i> и <i>slf4j-android-1.6.1-RC1.jar</i> (присутствуют в архиве программы). Код, показывающий карту и маркеры на ней, выглядит примерно так:

### Интерфейс приложения

Внешне приложение представляет одну большую карту OpenStreetMap с небольшой панелькой служебных кнопок (на скриншоте — в левом верхнем углу): "Настройки", "Старт/Стоп", "Найти человека", "Оставить отзыв о приложении", "Помощь". Если зацепиться за изображение руки с вытянутым указательным пальцем, то можно перетащить панельку с кнопками но другое, более удобное место. Если вы еще не ввели логины и пароли, иконка "Настройки" будет мигать, а иконки "Старт/Стоп" и "Найти человека" будут неактивны. В общем, интерфейс должен выглядеть примерно как на видео ниже (сама карта не отображается).

<video align="right">http://www.youtube.com/watch?v=uHWwoZAAB20</video>  
Программно интерфейс реализован в <i>MainActivity</i> (основное окно), <i>UsersActivity</i> (введение логинов и паролей), <i>PersonActivity </i>(поиск цели на карте) и <i>HelpActivity</i> (помощь).  

Обработчик кнопки "Настройки" в <i>MainActivity</i> вызывает <i>UsersActivity</i>, передавая ей ранее введенные логины и пароли (если они были), чтобы не заставлять пользователя заново набирать ранее введенную информацию:  
```java
settingsButton.setOnClickListener(new Button.OnClickListener() { // Обработчик кнопки "Настройки"
	@Override
	public void onClick(View arg0) {
		Animation animAlpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
		arg0.startAnimation(animAlpha); // Мигнем кнопкой

		if (location != null) // Отключаем GPS
			locationManager.removeUpdates(MainActivity.this);

		if ((appState == STATE_RUN_OWNER) || (appState == STATE_RUN_TARGETS)) {// Изменим состояние программы
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
```

Код *UsersActivity* просто отдает введенные данные:

```java
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
```

После введения логинов и паролей можно нажать "Старт", запуская GPS-приемник и обмен данными с сервером:

```java
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
```
