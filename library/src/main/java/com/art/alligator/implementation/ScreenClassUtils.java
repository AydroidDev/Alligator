package com.art.alligator.implementation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.art.alligator.NavigationFactory;
import com.art.alligator.Screen;
import com.art.alligator.ViewType;

/**
 * Date: 19.03.2017
 * Time: 9:51
 *
 * @author Artur Artikov
 */

public class ScreenClassUtils {
	private static final String KEY_SCREEN_CLASS_NAME = "com.art.alligator.implementation.ScreenClassUtils.KEY_SCREEN_CLASS_NAME";
	private static final String KEY_PREVIOUS_SCREEN_CLASS_NAME = "com.art.alligator.implementation.ScreenClassUtils.KEY_PREVIOUS_SCREEN_CLASS_NAME";

	private ScreenClassUtils() {
	}

	public static void putScreenClass(Intent intent, Class<? extends Screen> screenClass) {
		intent.putExtra(KEY_SCREEN_CLASS_NAME, screenClass.getName());
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Screen> getScreenClass(Activity activity, NavigationFactory navigationFactory) {
		String className = activity.getIntent().getStringExtra(KEY_SCREEN_CLASS_NAME);
		Class<? extends Screen> screenClass = getClassByName(className);

		if(screenClass == null) {   // screenClass is null. May be activity is a home screen. Try to find it in NavigationFactory.
			for(Class<? extends Screen> sc: navigationFactory.getScreenClasses()) {
				if(navigationFactory.getViewType(sc) == ViewType.ACTIVITY && navigationFactory.getActivityClass(sc) == activity.getClass()) {
					screenClass = sc;
					break;
				}
			}
		}
		return screenClass != null ? screenClass : Screen.class;
	}

	public static void putScreenClass(Fragment fragment, Class<? extends Screen> screenClass) {
		Bundle arguments = fragment.getArguments();
		if(arguments == null) {
			arguments = new Bundle();
			fragment.setArguments(arguments);
		}
		arguments.putSerializable(KEY_SCREEN_CLASS_NAME, screenClass.getName());
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Screen> getScreenClass(Fragment fragment) {
		Class<? extends Screen> screenClass = null;
		if (fragment.getArguments() != null) {
			String className = fragment.getArguments().getString(KEY_SCREEN_CLASS_NAME);
			screenClass = (Class<? extends Screen>) getClassByName(className);
		}
		return screenClass != null ? screenClass : Screen.class;
	}

	public static void putPreviousScreenClass(Intent intent, Class<? extends Screen> screenClass) {
		intent.putExtra(KEY_PREVIOUS_SCREEN_CLASS_NAME, screenClass.getName());
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends Screen> getPreviousScreenClass(Activity activity) {
		String className = activity.getIntent().getStringExtra(KEY_PREVIOUS_SCREEN_CLASS_NAME);
		Class<? extends Screen> screenClass = getClassByName(className);
		return screenClass != null ? screenClass : Screen.class;
	}

	private static Class getClassByName(String className) {
		if(className == null || className.isEmpty()) {
			return null;
		}

		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
