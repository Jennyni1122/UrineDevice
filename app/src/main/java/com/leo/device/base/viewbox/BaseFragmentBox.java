package com.leo.device.base.viewbox;


import androidx.fragment.app.Fragment;

/**
 * @author Leo90
 */
public abstract class BaseFragmentBox<B> extends BaseActivityBox<B> {
    private Fragment fragment;

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
        setActivity(fragment.getActivity());
    }
}
