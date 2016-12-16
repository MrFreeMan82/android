package home.criminalintent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.main);
        
        if(fragment == null){
            fragment = new CrimeFragment();
            fm.beginTransaction().add(R.id.main, fragment).commit();

        }
    }
}
