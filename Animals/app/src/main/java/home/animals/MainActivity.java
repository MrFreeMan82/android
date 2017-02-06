package home.animals;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Game.Callback{

    private static final String VIEW_STATE = "VIEW_STATE";
    private Button trueButton;
    private Button falseButton;
    private Button elseButton;
    private EditText elseText;
    private TextView questionText;
    private InputMethodManager imm;
    private boolean keyboardVisible = false;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

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
        elseText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(keyboardVisible) {
                    toggleKeyboard();
                    return true;
                }
                return false;
            }
        });
        questionText = (TextView) findViewById(R.id.questionText);

        if(savedInstanceState != null){
            int state = savedInstanceState.getInt(VIEW_STATE);
            // проверка если бит n выставлен в 1 то разрешаем иначе запрещаем доступ к кнопке.
            trueButton.setEnabled((state & (1<<3)) != 0);
            falseButton.setEnabled((state & (1<<2)) != 0);
            elseButton.setEnabled((state & (1<<1)) != 0);

            if((state & 1) == 0){
                elseText.setVisibility(View.INVISIBLE);
            } else {
                elseText.setVisibility(View.VISIBLE);
            }
        }
        game = Game.get(this);
        game.start();
    }

    private void toggleKeyboard()
    {
        keyboardVisible = !keyboardVisible;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onQuestionChange()
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
        if(keyboardVisible) toggleKeyboard();
    }

    @Override
    public void onConcede()
    {
        trueButton.setEnabled(false);
        falseButton.setEnabled(false);
        elseButton.setEnabled(true);
        elseText.setVisibility(View.VISIBLE);
        toggleKeyboard();
    }

    @Override
    public void onEndGame()
    {
        if(keyboardVisible) toggleKeyboard();
        this.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState)
    {
        super.onSaveInstanceState(saveInstanceState);
        int state = 0;
        if(trueButton.isEnabled())  state |= (1<<3);
        if(falseButton.isEnabled()) state |= (1<<2);
        if(elseButton.isEnabled()) state |= (1<<1);
        if(elseText.getVisibility() == View.VISIBLE) state |= 1;
        saveInstanceState.putInt(VIEW_STATE, state);
    }

    //To-Do: Сделать сохранение при повороте телефона.
}
