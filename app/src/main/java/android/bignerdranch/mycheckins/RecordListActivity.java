package android.bignerdranch.mycheckins;

import androidx.fragment.app.Fragment;

public class RecordListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RecordListFragment();
    }
}