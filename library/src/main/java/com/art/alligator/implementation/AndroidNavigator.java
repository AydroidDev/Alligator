package com.art.alligator.implementation;

import java.util.LinkedList;
import java.util.Queue;

import com.art.alligator.Command;
import com.art.alligator.CommandExecutionException;
import com.art.alligator.NavigationContext;
import com.art.alligator.NavigationContextBinder;
import com.art.alligator.NavigationFactory;
import com.art.alligator.Navigator;
import com.art.alligator.Screen;
import com.art.alligator.TransitionAnimation;
import com.art.alligator.implementation.commands.BackCommand;
import com.art.alligator.implementation.commands.BackToCommand;
import com.art.alligator.implementation.commands.FinishCommand;
import com.art.alligator.implementation.commands.ForwardCommand;
import com.art.alligator.implementation.commands.ReplaceCommand;
import com.art.alligator.implementation.commands.ResetCommand;
import com.art.alligator.implementation.commands.SwitchToCommand;

/**
 * Date: 29.12.2016
 * Time: 10:13
 *
 * @author Artur Artikov
 */
public class AndroidNavigator implements NavigationContextBinder, Navigator {
	private NavigationFactory mNavigationFactory;
	private NavigationContext mNavigationContext;
	private Queue<Command> mCommandQueue = new LinkedList<>();
	private boolean mCanExecuteCommands;
	private boolean mIsExecutingCommands;

	public AndroidNavigator(NavigationFactory navigationFactory) {
		mNavigationFactory = navigationFactory;
	}

	@Override
	public void bind(NavigationContext navigationContext) {
		mNavigationContext = navigationContext;
		mCanExecuteCommands = true;
		executeQueuedCommands();
	}

	@Override
	public void unbind() {
		mNavigationContext = null;
		mCanExecuteCommands = false;
	}

	@Override
	public void goForward(Screen screen) {
		goForward(screen, null);
	}

	@Override
	public void goForward(Screen screen, TransitionAnimation animation) {
		executeCommand(new ForwardCommand(screen, animation));
	}

	@Override
	public void goBack() {
		goBack(null);
	}

	@Override
	public void goBack(TransitionAnimation animation) {
		executeCommand(new BackCommand(animation));
	}

	@Override
	public void goBackTo(Class<? extends Screen> screenClass) {
		goBackTo(screenClass, null);
	}

	@Override
	public void goBackTo(Class<? extends Screen> screenClass, TransitionAnimation animation) {
		executeCommand(new BackToCommand(screenClass, animation));
	}

	@Override
	public void replace(Screen screen) {
		replace(screen, null);
	}

	@Override
	public void replace(Screen screen, TransitionAnimation animation) {
		executeCommand(new ReplaceCommand(screen, animation));
	}

	@Override
	public void reset(Screen screen) {
		reset(screen, null);
	}

	@Override
	public void reset(Screen screen, TransitionAnimation animation) {
		executeCommand(new ResetCommand(screen, animation));
	}

	@Override
	public void finish() {
		finish(null);
	}

	@Override
	public void finish(TransitionAnimation animation) {
		executeCommand(new FinishCommand(animation));
	}

	@Override
	public void switchTo(String screenName) {
		executeCommand(new SwitchToCommand(screenName));
	}

	protected void executeCommand(Command command) {
		mCommandQueue.add(command);
		executeQueuedCommands();
	}

	private void executeQueuedCommands() {
		if(mIsExecutingCommands) {
			return;
		}

		mIsExecutingCommands = true;
		while (mCanExecuteCommands && !mCommandQueue.isEmpty()) {
			Command command = mCommandQueue.remove();
			try {
				mCanExecuteCommands = command.execute(mNavigationContext, mNavigationFactory);
				onCommandExecuted(command);
			} catch (CommandExecutionException e) {
				mCommandQueue.clear();
				mIsExecutingCommands = false;
				onError(command, e);
			} catch (Exception e) {
				mCommandQueue.clear();
				mIsExecutingCommands = false;
				throw e;
			}
		}
		mIsExecutingCommands = false;
	}


	protected void onCommandExecuted(Command command) {
		if(mNavigationContext.getNavigationListener() != null) {
			mNavigationContext.getNavigationListener().onCommandExecuted(command);
		}
	}

	protected void onError(Command command, CommandExecutionException e) {
		throw new RuntimeException("Failed to execute navigation command " + command.getClass().getSimpleName() + ". " + e.getMessage(), e);
	}
}
