package home.criminalintent;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Дима on 02.01.2017.
 */

public class CrimeListActivity extends SingleFragmentActivity
            implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks
{
    @Override
    protected Fragment createFragment()
    {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime)
    {
        if(findViewById(R.id.detail_fragment) == null)
        {
            Intent intent = CrimePageActivity.newIntent(this, crime.getID());
            startActivity(intent);
        }else{
            Fragment newDetail = CrimeFragment.newInstance(crime.getID());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment, newDetail)
                    .commit();
        }

    }

    @Override
    public void onCrimeUpdated(Crime crime)
    {
      //  Log.d("CrimeListActivity", "onCrimeUpdated", new Exception());
        CrimeListFragment listFragment =
                (CrimeListFragment) getSupportFragmentManager().
                                findFragmentById(R.id.main);
        listFragment.updateUI(CrimeListFragment.Mode.NONE);
    }
}
