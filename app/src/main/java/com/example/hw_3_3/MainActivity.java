package com.example.hw_3_3;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;

import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener  {

    private final String DATA_STREAM = "http://ep128.hostingradio.ru:8030/ep128"; // ссылка на аудио поток
    private static final String DATA_SD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/music.mp3"; // патч на аудио-файл с SD-карты
    private String nameAudio = ""; // название контента

    private MediaPlayer mediaPlayer; // создание поля медиа-плеера
    private AudioManager audioManager; // создание поля аудио-менеджера
    private Toast toast; // создание поля тоста

    private TextView textOut; // поле вывода информации об аудио-файле
    private Switch switchLoop; // поле переключателя повтора воспроизведения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // присваеваем полям соответствующие ID из activity_main
        textOut = findViewById(R.id.textOut);
        switchLoop = findViewById(R.id.switchLoop);

        // получение доступа к аудио-менеджеру
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);


        // создание слушателя переключателя повтора
        switchLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(mediaPlayer != null)
                    mediaPlayer.setLooping(isChecked); // включение / выключение повтора аудио
            }
        });
    }

    // слушатель нажатия радио-кнопок
    public void onClickSource(View view) {

        releaseMediaPlayer(); // метод освобождения используемых проигрывателем ресурсов

        // обработка нажатия кнопок
        try {
            switch (view.getId()) {
                case R.id.btnStream:
                    // код выполнения кнопки btnStream
                    // размещаем тост (контекст, сообщение, длительность сообщения)
                    toast = Toast.makeText(this, "Запущен поток аудио", Toast.LENGTH_SHORT); // инициализация
                    toast.show(); // демонстрация тоста на экране
                    mediaPlayer = new MediaPlayer(); // создание объекта медиа-плеера
                    mediaPlayer.setDataSource(DATA_STREAM); // указание источника аудио
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // задает аудио-поток, который будет использован для проигрывания
                    mediaPlayer.setOnPreparedListener(this); // ассинхронная подготовка плеера к проигрыванию
                    mediaPlayer.prepareAsync(); // ассинхронная подготовка плейера к проигрыванию
//                    mediaPlayer.prepare(); // ассинхронная подготовка плеера к проигрыванию
//                    mediaPlayer.start(); // ассинхронная подготовка плеера к проигрыванию
                    nameAudio = "РАДИО"; // инициализация названия контента
                    textOut.setText(nameAudio + "\n(проигрывание " + mediaPlayer.isPlaying() + ", время " + mediaPlayer.getCurrentPosition()
                            + ",\nповтор " + mediaPlayer.isLooping() + ", громкость " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + ")");

                    break;
                case R.id.btnRAW:
                    // код выполнения кнопки btnRAW
                    toast = Toast.makeText(this, "Запущен аудио-файл с памяти телефона", Toast.LENGTH_SHORT); // инициализация
                    toast.show(); // демонстрация тоста на экране
                    mediaPlayer = MediaPlayer.create(this, R.raw.amongst_the_stars); // создание дорожки с имеющимся аудио-файлом
                    mediaPlayer.start(); // старт данной дорожки
                    nameAudio = "Magnum Opus - Amongst the Stars"; // инициализация названия контента

                    textOut.setText(nameAudio + "\n(проигрывание " + mediaPlayer.isPlaying() + ", время " + mediaPlayer.getCurrentPosition()
                            + ",\nповтор " + mediaPlayer.isLooping() + ", громкость " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + ")");
                    break;
                case R.id.btnSD:
                    // код выполнения кнопки btnSD
                    toast = Toast.makeText(this, "Запущен аудио-файл с SD-карты", Toast.LENGTH_SHORT); // инициализация
                    toast.show(); // демонстрация тоста на экране
                    mediaPlayer = new MediaPlayer(); // создание объекта медиа-плеера
                    mediaPlayer.setDataSource(DATA_SD); // указание источника аудио
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // подключение аудио-менеджера
                    mediaPlayer.prepare(); // ассинхронная подготовка плеера к проигрыванию
                    mediaPlayer.start(); // ассинхронная подготовка плеера к проигрыванию
                    break;
            }
        } catch (IOException e) { // исключение ввода / вывода
            e.printStackTrace();
            toast = Toast.makeText(this, "Источник информации не найден", Toast.LENGTH_SHORT); // инициализация
            toast.show(); // демонстрация тоста на экране
        }

        if (mediaPlayer == null) return;

        mediaPlayer.setLooping(switchLoop.isChecked()); // включение / выключение повтора
        mediaPlayer.setOnCompletionListener(this); // слушатель окончания проигрывания
    }

    // слушатель управления воспроизведением контента
    public void onClick(View view) {
        if (mediaPlayer == null) return;

        switch (view.getId()) {
            case R.id.btnResume:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start(); // метод возобновления проигрывания
                }
                break;
            case R.id.btnPause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause(); // метод паузы
                }
                break;
            case R.id.btnStop:
                mediaPlayer.stop(); // метод остановки
                break;
            case R.id.btnForward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000); // переход к определённой позиции трека
                // mediaPlayer.getCurrentPosition() - метод получения текущей позиции
                break;
            case R.id.btnBack:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000); // переход к определённой позиции трека
                break;
            case R.id.btnQuiet:
                volDown();
                break;
            case R.id.btnLouder:
                volUp();
                break;
        }
        // информативный вывод информации
        textOut.setText(nameAudio + "\n(проигрывание " + mediaPlayer.isPlaying() + ", время " + mediaPlayer.getCurrentPosition()
                + ",\nповтор " + mediaPlayer.isLooping() + ", громкость " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + ")");
    }

    public void volUp() {
        int maxVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(audioManager.STREAM_MUSIC);
        //float volume = (float) currentVolume / maxVolume; //текущая громкость от 0 до 1

        int newVolume = currentVolume + (int) (0.2 * maxVolume);
        if (newVolume > maxVolume) { newVolume = maxVolume;}
        audioManager.setStreamVolume(audioManager.STREAM_MUSIC, newVolume, 0);
        //Обновляем громкость в медиа плеер
        float newVolumeFloat = (float) newVolume / maxVolume;
        mediaPlayer.setVolume(newVolumeFloat, newVolumeFloat);
        System.out.println("volume:" + currentVolume + ";" + maxVolume + ";" + newVolumeFloat);
    }

    public void volDown() {
        int maxVolume = audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC);
        int minVolume = audioManager.getStreamMinVolume(audioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(audioManager.STREAM_MUSIC);
        //float volume = (float) currentVolume / maxVolume; //текущая громкость от 0 до 1

        int newVolume = currentVolume - (int) (0.2 * maxVolume);
        if (newVolume < minVolume) { newVolume = minVolume; }
        audioManager.setStreamVolume(audioManager.STREAM_MUSIC, newVolume, 0);
        //Обновляем громкость в медиа плеер
        float newVolumeFloat = (float) newVolume / maxVolume;
        mediaPlayer.setVolume(newVolumeFloat, newVolumeFloat);
        System.out.println("volume:" + currentVolume + ";" + minVolume + ";" + newVolumeFloat);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        super.onKeyDown(keyCode, event);

        if (keyCode == KeyEvent.KEYCODE_BACK){
            System.out.println("Бала нажата кнопка Back");
            mediaPlayer.stop();
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volUp();
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volDown();
            return true;
        }

        return false;
    }

    // onPrepared - метод слушателя OnPreparedListener (вызывается, когда плеер готов к проигрыванию)
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) { // метод подготовки дополнительного потока
        mediaPlayer.start(); // старт медиа-плейера
        toast = Toast.makeText(this, "Старт медиа-плейера", Toast.LENGTH_SHORT); // инициализация тоста
        toast.show(); // демонстрация тоста на экране
    }

    // onCompletion() - метод слушателя OnCompletionListener (вызывается, когда достигнут конец проигрываемого содержимого)
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) { // метод закрытия дополнительного потока
        toast = Toast.makeText(this, "Отключение медиа-плейера", Toast.LENGTH_SHORT); // инициализация тоста
        toast.show(); // демонстрация тоста на экране
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer(); // метод освобождения используемых проигрывателем ресурсов
    }

    // метод освобождения используемых проигрывателем ресурсов
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}