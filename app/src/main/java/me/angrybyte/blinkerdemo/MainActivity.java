package me.angrybyte.blinkerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.angrybyte.blinkerview.BlinkerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.textView).setOnClickListener(v -> {
            final BlinkerView blinker = findViewById(R.id.blinkerView_manual);
            if (blinker.isBlinkingOn()) {
                blinker.stopBlinking();
            } else {
                blinker.startBlinking();
            }
        });
    }

}
