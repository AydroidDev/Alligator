package me.aartikov.alligator.converters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import me.aartikov.alligator.Screen;

/**
 * Date: 21.10.2017
 * Time: 12:56
 *
 * @author Artur Artikov
 */

/**
 * Converts a screen to a fragment and vice versa.
 *
 * @param <ScreenT> screen type
 */
public interface FragmentConverter<ScreenT extends Screen> {

	@NonNull
	Fragment createFragment(@NonNull ScreenT screen);

	@NonNull
	ScreenT getScreen(@NonNull Fragment fragment);
}
