package home.push;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Для отправки сообщения нужно в браузере набрать
        // http://dimazdy82.000webhostapp.com/push/?test
    }
}
