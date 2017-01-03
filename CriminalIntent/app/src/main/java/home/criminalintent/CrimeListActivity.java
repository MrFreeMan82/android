package home.criminalintent;


import android.support.v4.app.Fragment;

/**
 * Created by Дима on 02.01.2017.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment()
    {
        return new CrimeListFragment();
    }
}
