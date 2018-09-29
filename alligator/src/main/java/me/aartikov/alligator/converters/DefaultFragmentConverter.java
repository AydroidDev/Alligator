package me.aartikov.alligator.converters;

import java.io.Serializable;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import me.aartikov.alligator.Screen;

/**
 * Date: 15.09.2018
 * Time: 10:18
 *
 * @author Artur Artikov
 */

/**
 * Creates a fragment of the given class. It also puts a screen to the fragment's arguments if {@code ScreenT} is {@code Serializable} or {@code Parcelable}.
 *
 * @param <ScreenT> screen type
 */

public class DefaultFragmentConverter<ScreenT extends Screen> implements FragmentConverter<ScreenT> {
	private static final String KEY_SCREEN = "me.aartikov.alligator.KEY_SCREEN";

	private Class<ScreenT> mScreenClass;
	private Class<? extends Fragment> mFragmentClass;

	public DefaultFragmentConverter(Class<ScreenT> screenClass, Class<? extends Fragment> fragmentClass) {
		mScreenClass = screenClass;
		mFragmentClass = fragmentClass;
	}

	@Override
	@NonNull
	public Fragment createFragment(@NonNull ScreenT screen) {
		try {
			Fragment fragment = mFragmentClass.newInstance();
			if (screen instanceof Serializable) {
				Bundle arguments = new Bundle();
				arguments.putSerializable(KEY_SCREEN, (Serializable) screen);
				fragment.setArguments(arguments);
			} else if (screen instanceof Parcelable) {
				Bundle arguments = new Bundle();
				arguments.putParcelable(KEY_SCREEN, (Parcelable) screen);
				fragment.setArguments(arguments);
			}
			return fragment;
		} catch (InstantiationException e) {
			throw new RuntimeException("Failed to create a fragment", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to create a fragment", e);
		}
	}

	@Override
	@NonNull
	@SuppressWarnings("unchecked")
	public ScreenT getScreen(@NonNull Fragment fragment) {
		if (fragment.getArguments() == null) {
			throw new IllegalArgumentException("Fragment has no arguments.");
		} else if (Serializable.class.isAssignableFrom(mScreenClass)) {
			return checkNotNull((ScreenT) (fragment.getArguments().getSerializable(KEY_SCREEN)));
		} else if (Parcelable.class.isAssignableFrom(mScreenClass)) {
			return checkNotNull((ScreenT) fragment.getArguments().getParcelable(KEY_SCREEN));
		} else {
			throw new IllegalArgumentException("Screen " + mScreenClass.getSimpleName() + " should be Serializable or Parcelable.");
		}
	}

	private ScreenT checkNotNull(@Nullable ScreenT screen) {
		if (screen == null) {
			throw new IllegalArgumentException("Failed to get screen from arguments of fragment.");
		}
		return screen;
	}
}
