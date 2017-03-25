package com.art.alligator;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.art.alligator.implementation.DefaultAnimationProvider;
import com.art.alligator.implementation.DefaultNavigationErrorListener;

/**
 * Date: 29.12.2016
 * Time: 10:13
 *
 * @author Artur Artikov
 */
public class NavigationContext {
	private AppCompatActivity mActivity;
	private FragmentManager mFragmentManager;
	private int mContainerId;
	private ScreenSwitcher mScreenSwitcher;
	private AnimationProvider mAnimationProvider;
	private NavigationCommandListener mNavigationCommandListener;
	private NavigationErrorListener mNavigationErrorListener;

	public NavigationContext(AppCompatActivity activity) {
		this(new Builder(activity));
	}

	public NavigationContext(AppCompatActivity activity, int containerId) {
		this(new Builder(activity).containerId(containerId));
	}

	private NavigationContext(Builder builder) {
		mActivity = builder.mActivity;
		mFragmentManager = builder.mFragmentManager != null ? builder.mFragmentManager : builder.mActivity.getSupportFragmentManager();
		mContainerId = builder.mContainerId;
		mScreenSwitcher = builder.mScreenSwitcher;
		mAnimationProvider = builder.mAnimationProvider != null ? builder.mAnimationProvider : new DefaultAnimationProvider();
		mNavigationCommandListener = builder.mNavigationCommandListener;
		mNavigationErrorListener = builder.mNavigationErrorListener != null ? builder.mNavigationErrorListener : new DefaultNavigationErrorListener();
	}

	public AppCompatActivity getActivity() {
		return mActivity;
	}

	public FragmentManager getFragmentManager() {
		return mFragmentManager;
	}

	public int getContainerId() {
		return mContainerId;
	}

	public boolean hasContainerId() {
		return mContainerId > 0;
	}

	public ScreenSwitcher getScreenSwitcher() {
		return mScreenSwitcher;
	}

	public AnimationProvider getAnimationProvider() {
		return mAnimationProvider;
	}

	public NavigationCommandListener getNavigationCommandListener() {
		return mNavigationCommandListener;
	}

	public NavigationErrorListener getNavigationErrorListener() {
		return mNavigationErrorListener;
	}

	public static class Builder {
		private AppCompatActivity mActivity;
		private FragmentManager mFragmentManager;
		private int mContainerId;
		private ScreenSwitcher mScreenSwitcher;
		private AnimationProvider mAnimationProvider;
		private NavigationCommandListener mNavigationCommandListener;
		private NavigationErrorListener mNavigationErrorListener;

		public Builder(AppCompatActivity activity) {
			mActivity = activity;
		}

		public Builder containerId(int containerId) {
			mContainerId = containerId;
			return this;
		}

		public Builder fragmentManager(FragmentManager fragmentManager) {
			mFragmentManager = fragmentManager;
			return this;
		}

		public Builder screenSwitcher(ScreenSwitcher screenSwitcher) {
			mScreenSwitcher = screenSwitcher;
			return this;
		}

		public Builder animationProvider(AnimationProvider animationProvider) {
			mAnimationProvider = animationProvider;
			return this;
		}

		public Builder navigationListener(NavigationCommandListener navigationCommandListener) {
			mNavigationCommandListener = navigationCommandListener;
			return this;
		}

		public Builder navigationErrorListener(NavigationErrorListener navigationErrorListener) {
			mNavigationErrorListener = navigationErrorListener;
			return this;
		}

		public NavigationContext build() {
			return new NavigationContext(this);
		}
	}
}
