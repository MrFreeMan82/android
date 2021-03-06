package home.criminalintent;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Дима on 02.01.2017.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId()
    {
        return R.layout.main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());

        FragmentManager fm = super.getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.main);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.main, fragment).commit();

        }
    }
}
