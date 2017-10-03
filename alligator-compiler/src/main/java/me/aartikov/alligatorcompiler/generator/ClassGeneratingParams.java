package me.aartikov.alligatorcompiler.generator;

/**
 * Date: 12-Jan-16
 * Time: 15:09
 *
 * @author Alexander Blinov
 */
public final class ClassGeneratingParams {
	private String mName;
	private String mBody;

	public ClassGeneratingParams() {
	}

	public void setName(String name) {
		mName = name;
	}

	public void setBody(String body) {
		mBody = body;
	}

	public String getName() {
		return mName;
	}

	public String getBody() {
		return mBody;
	}
}
