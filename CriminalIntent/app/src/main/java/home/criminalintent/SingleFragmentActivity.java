package home.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Дима on 02.01.2017.
 */

public abstract class SingleFragmentActivity extends FragmentActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentManager fm = super.getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.main);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.main, fragment).commit();

        }
    }
}
