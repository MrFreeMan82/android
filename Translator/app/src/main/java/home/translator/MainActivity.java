package home.translator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import java.lang.String;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity {

        EditText editTextArabic;
        EditText editTextRoman;
        Button btnTranslate;
        TextView viewResult;

    private String roman_to_arab(String roman)
    {
        int result, last_value, current_value;
        
        result = 0; last_value = 0;
        
        for(int i = roman.length() - 1; i >= 0; i--)
        {
            String s = roman.substring(i, i + 1).toUpperCase();
            switch(s)
            {
                case "C": current_value = 100; break;
                case "D": current_value = 500; break;
                case "I": current_value = 1; break;
                case "L": current_value = 50; break;
                case "M": current_value = 1000; break;
                case "V": current_value = 5; break;
                case "X": current_value = 10; break;
                default : current_value = 0;
            }
            
            if(current_value < last_value) result -= current_value; else result += current_value;
            last_value = current_value;
        }

        return Integer.toString(result);
    }

    private String arab_to_roman(String arab)
    {
        int [] values = {1,4,5,9,10,40,50,90,100,400,500,900,1000};
        String [] romans = {"I","IV","V","IX","X","XL","L","XC","C","CD","D","CM","M"};

        String roman = "";
        int k = 12;
        int arabic = Integer.parseInt(arab);

        if ((arabic > 0) && (arabic <= 3999))
        {
            while (arabic > 0)
            {
                while (values[k] > arabic) k--;
                arabic -= values[k];
                roman += romans[k];
            }
        }
        return roman;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        editTextArabic = (EditText) findViewById(R.id.editTextArabic);
        editTextRoman = (EditText) findViewById(R.id.editTextRoman);
        btnTranslate = (Button) findViewById(R.id.btnTranslate);
        viewResult = (TextView) findViewById(R.id.viewResult);

        editTextArabic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
               if(!editTextRoman.getText().toString().isEmpty()) editTextRoman.setText("");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextRoman.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if(!editTextArabic.getText().toString().isEmpty()) editTextArabic.setText("");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String arab;
                String roman;
                arab = editTextArabic.getText().toString();
                roman = editTextRoman.getText().toString();

                if ((arab.isEmpty()) && (!roman.isEmpty()))
                {
                    arab = roman_to_arab(roman);
                    viewResult.setText(arab);

                }
                else if ((! arab.isEmpty()) && (roman.isEmpty()))
                {
                   roman = arab_to_roman(arab);
                   viewResult.setText(roman);
                }
            }
        });
    }
}
