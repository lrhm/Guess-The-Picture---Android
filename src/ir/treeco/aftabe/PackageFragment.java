package ir.treeco.aftabe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import ir.treeco.aftabe.packages.Package;

/**
 * Created by hossein on 8/31/14.
 */
public class PackageFragment extends Fragment {
    private Package mPackage;

    public static PackageFragment newInstance(Package mPackage) {
        PackageFragment packageFragment = new PackageFragment();
        packageFragment.mPackage = mPackage;
        return packageFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_package, container, false);
        ViewPager viewPager = (ViewPager) layout.findViewById(R.id.levels_view_pager);
        viewPager.setAdapter(new LevelsViewPagerAdapter(mPackage));
        return layout;
    }
}
