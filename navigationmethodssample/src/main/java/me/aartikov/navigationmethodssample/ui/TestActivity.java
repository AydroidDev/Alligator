package me.aartikov.navigationmethodssample.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.aartikov.alligator.NavigationContext;
import me.aartikov.alligator.NavigationContextBinder;
import me.aartikov.alligator.Navigator;
import me.aartikov.alligator.annotations.RegisterScreen;
import me.aartikov.navigationmethodssample.R;
import me.aartikov.navigationmethodssample.SampleApplication;
import me.aartikov.navigationmethodssample.SampleTransitionAnimationProvider;
import me.aartikov.navigationmethodssample.screens.TestScreen;
import me.aartikov.navigationmethodssample.screens.TestSmallScreen;


@RegisterScreen(TestScreen.class)
public class TestActivity extends AppCompatActivity {
	@BindView(R.id.root_view)
	View mRootView;

	@BindView(R.id.counter_text_view)
	TextView mCounterTextView;

	@BindView(R.id.forward_button)
	Button mForwardButton;

	@BindView(R.id.replace_button)
	Button mReplaceButton;

	@BindView(R.id.reset_button)
	Button mResetButton;

	@BindView(R.id.finish_button)
	Button mFinishButton;

	private Navigator mNavigator = SampleApplication.getNavigator();
	private NavigationContextBinder mNavigationContextBinder = SampleApplication.getNavigationContextBinder();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		ButterKnife.bind(this);

		TestScreen screen = SampleApplication.getScreenResolver().getScreenOrNull(this);
		int counter = screen != null ? screen.getCounter() : 1;
		mCounterTextView.setText(getString(R.string.counter_template, counter));

		mForwardButton.setOnClickListener(view -> mNavigator.goForward(new TestScreen(counter + 1)));
		mReplaceButton.setOnClickListener(view -> mNavigator.replace(new TestScreen(counter)));
		mResetButton.setOnClickListener(view -> mNavigator.reset(new TestScreen(1)));
		mFinishButton.setOnClickListener(view -> mNavigator.finish());

		mRootView.setBackgroundColor(getRandomColor());
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		bindNavigationContext();
		setInitialFragmentIfRequired();
	}

	private void bindNavigationContext() {
		NavigationContext navigationContext = new NavigationContext.Builder(this, SampleApplication.getNavigationFactory())
				.fragmentNavigation(getSupportFragmentManager(), R.id.fragment_container)
				.transitionAnimationProvider(new SampleTransitionAnimationProvider())
				.build();
		mNavigationContextBinder.bind(navigationContext);
	}

	private void setInitialFragmentIfRequired() {
		boolean hasFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null;
		if (!hasFragment && mNavigator.canExecuteCommandImmediately()) {
			mNavigator.reset(new TestSmallScreen(1));
		}
	}

	@Override
	protected void onPause() {
		mNavigationContextBinder.unbind(this);
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		mNavigator.goBack();
	}

	private static int getRandomColor() {
		Random random = new Random();
		return Color.HSVToColor(new float[]{random.nextInt(360), 0.1f, 1.0f});
	}
}
