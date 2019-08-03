package me.aartikov.alligator.commands;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import me.aartikov.alligator.DestinationType;
import me.aartikov.alligator.NavigationContext;
import me.aartikov.alligator.Screen;
import me.aartikov.alligator.ScreenResult;
import me.aartikov.alligator.TransitionType;
import me.aartikov.alligator.animations.AnimationData;
import me.aartikov.alligator.animations.TransitionAnimation;
import me.aartikov.alligator.destinations.ActivityDestination;
import me.aartikov.alligator.destinations.DialogFragmentDestination;
import me.aartikov.alligator.destinations.FragmentDestination;
import me.aartikov.alligator.exceptions.MissingFragmentStackException;
import me.aartikov.alligator.exceptions.NavigationException;
import me.aartikov.alligator.exceptions.NotSupportedOperationException;
import me.aartikov.alligator.exceptions.ScreenNotFoundException;
import me.aartikov.alligator.exceptions.ScreenRegistrationException;
import me.aartikov.alligator.helpers.FragmentStack;
import me.aartikov.alligator.navigationfactories.NavigationFactory;

/**
 * Date: 11.02.2017
 * Time: 11:14
 *
 * @author Artur Artikov
 */

/**
 * Command implementation for {@code goBackTo} method of {@link me.aartikov.alligator.AndroidNavigator}.
 */
public class BackToCommand extends BaseCommand {
	private Class<? extends Screen> mScreenClass;
	@Nullable
	private ScreenResult mScreenResult;
	@Nullable
	private AnimationData mAnimationData;

	public BackToCommand(@NonNull Class<? extends Screen> screenClass, @Nullable ScreenResult screenResult, @Nullable AnimationData animationData) {
		super(screenClass);
		mScreenClass = screenClass;
		mScreenResult = screenResult;
		mAnimationData = animationData;
	}

	@Override
	public boolean execute(@NonNull ActivityDestination destination, @NonNull NavigationContext navigationContext, @NonNull NavigationFactory navigationFactory) throws NavigationException {
		Activity activity = navigationContext.getActivity();
		Intent intent = destination.createEmptyIntent(activity, mScreenClass);
		if (intent == null) {
			throw new ScreenRegistrationException("Can't create intent for a screen " + mScreenClass.getSimpleName());
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		if (mScreenResult != null) {
			navigationContext.getScreenResultHelper().setResultToIntent(intent, activity, mScreenResult, navigationFactory);
		}

		Class<? extends Screen> screenClassFrom = navigationFactory.getScreenClass(activity);
		Class<? extends Screen> screenClassTo = mScreenClass;
		TransitionAnimation animation = TransitionAnimation.DEFAULT;
		if (screenClassFrom != null) {
			animation = navigationContext.getTransitionAnimationProvider().getAnimation(TransitionType.BACK, DestinationType.ACTIVITY, screenClassFrom, screenClassTo, mAnimationData);
		}

		navigationContext.getActivityHelper().start(intent, animation);
		navigationContext.getTransitionListener().onScreenTransition(TransitionType.BACK, DestinationType.ACTIVITY, screenClassFrom, screenClassTo);
		return false;
	}

	@Override
	public boolean execute(@NonNull FragmentDestination destination, @NonNull NavigationContext navigationContext, @NonNull NavigationFactory navigationFactory) throws NavigationException {
		if (navigationContext.getFragmentStack() == null) {
			throw new MissingFragmentStackException("ContainerId is not set.");
		}

		FragmentStack fragmentStack = navigationContext.getFragmentStack();
		List<Fragment> fragments = fragmentStack.getFragments();
		Fragment requiredFragment = null;
		boolean toPrevious = false;
		for (int i = fragments.size() - 1; i >= 0; i--) {
			if (mScreenClass == navigationFactory.getScreenClass(fragments.get(i))) {
				requiredFragment = fragments.get(i);
				toPrevious = i == fragments.size() - 2;
				break;
			}
		}

		if (requiredFragment == null) {
			throw new ScreenNotFoundException(mScreenClass);
		}

		Fragment currentFragment = fragments.get(fragments.size() - 1);
		Class<? extends Screen> screenClassFrom = navigationFactory.getScreenClass(currentFragment);
		Class<? extends Screen> screenClassTo = mScreenClass;
		TransitionAnimation animation = TransitionAnimation.DEFAULT;
		if (screenClassFrom != null) {
			animation = navigationContext.getTransitionAnimationProvider().getAnimation(TransitionType.BACK, DestinationType.FRAGMENT, screenClassFrom, screenClassTo, mAnimationData);
		}

		fragmentStack.popUntil(requiredFragment, animation);
		navigationContext.getTransitionListener().onScreenTransition(TransitionType.BACK, DestinationType.FRAGMENT, screenClassFrom, screenClassTo);
		if (mScreenResult != null || toPrevious) {
			navigationContext.getScreenResultHelper().callScreenResultListener(currentFragment, mScreenResult, navigationContext.getScreenResultListener(), navigationFactory);
		}
		return true;
	}

	@Override
	public boolean execute(@NonNull DialogFragmentDestination destination, @NonNull NavigationContext navigationContext, @NonNull NavigationFactory navigationFactory) throws NavigationException {
		throw new NotSupportedOperationException("BackTo command is not supported for dialog fragments.");
	}
}
