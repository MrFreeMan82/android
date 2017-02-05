package home.animals;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Game.Callback{

    private Button trueButton;
    private Button falseButton;
    private Button elseButton;
    private EditText elseText;
    private TextView questionText;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        game = new Game(this);

        trueButton = (Button) findViewById(R.id.trueButton);
        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.ifTrue();
            }
        });
        falseButton = (Button) findViewById(R.id.falseButton);
        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.ifFalse();
            }
        });
        elseButton = (Button) findViewById(R.id.elseButton);
        elseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(elseText.getText().toString().isEmpty()) return;

                game.ifElse(elseText.getText().toString());
                elseText.setText("");
            }
        });
        elseText = (EditText) findViewById(R.id.elseText);
        questionText = (TextView) findViewById(R.id.questionText);

        game.start();
    }

    @Override
    public void onBufChange()
    {
       questionText.setText(game.getQuestion());
    }

    @Override
    public void onContinue()
    {
        elseButton.setEnabled(false);
        elseText.setVisibility(View.INVISIBLE);
        trueButton.setEnabled(true);
        falseButton.setEnabled(true);
    }

    @Override
    public void onYield()
    {
        trueButton.setEnabled(false);
        falseButton.setEnabled(false);
        elseButton.setEnabled(true);
        elseText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEndGame()
    {
        this.finish();
    }
}
