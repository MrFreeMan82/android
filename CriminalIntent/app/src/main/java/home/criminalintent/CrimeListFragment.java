package home.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Дима on 02.01.2017.
 */

public class CrimeListFragment extends Fragment
{
    private int position = 0;
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private static boolean mSubTitleVisible;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    public enum Mode {NONE, DELETE}
    private List<Crime> selectedItems;
    private Callbacks mCallbacks;

    public interface Callbacks
    {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }


    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSelectCheckBox;

        public CrimeHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSelectCheckBox = (CheckBox)
                    itemView.findViewById(R.id.list_item_selected);

            mSelectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if(isChecked)
                        selectedItems.add(mCrime);
                    else
                        selectedItems.remove(mCrime);
                }
            });
        }

        public void bindCrime(Crime crime)
        {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSelectCheckBox.setChecked(false);
        }

        @Override
        public void onClick(View v) {
           /* Toast.makeText(getActivity(),
                    mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT)
                    .show();*/

            position = super.getAdapterPosition();
            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>
    {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes)
        {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }


        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes)
        {
            mCrimes = crimes;
        }
    }

    public void updateUI(Mode mode)
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter.setCrimes(crimes);
            switch(mode)
            {
                case DELETE:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    mAdapter.notifyItemChanged(position);
            }
        }
        updateSubtitle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        selectedItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(savedInstanceState != null)
        {
            mSubTitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI(Mode.NONE);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateUI(Mode.NONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subTitleItem = menu.findItem(R.id.menu_item_show_subtitle);

        if(mSubTitleVisible)
            subTitleItem.setTitle(R.string.hide_subtitle);
        else
            subTitleItem.setTitle(R.string.show_subtitle);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_item_new_crime:
                    Crime crime = new Crime();
                    CrimeLab.get(getActivity()).addCrime(crime);
                    updateUI(Mode.NONE);
                    mCallbacks.onCrimeSelected(crime);
                    return true;

            case R.id.menu_item_delete_crime:
                for (Crime c:selectedItems)
                {
                    CrimeLab.get(getActivity()).deleteCrime(c);
                }
                updateUI(Mode.DELETE);
                return true;

            case R.id.menu_item_show_subtitle:
                    mSubTitleVisible = !mSubTitleVisible;
                    getActivity().invalidateOptionsMenu();
                    updateSubtitle();
                    return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle()
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources().
                getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount); //getString(R.string.subtitle_format, crimeCount);

        if(!mSubTitleVisible) subtitle = null;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubTitleVisible);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = null;
    }
}
