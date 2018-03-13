package com.smallplanet.labalib;

/* The Labal langauge is very minimalistic. Each command is a single, non numerical character (excluding +/-). 
 * Each command can optionally be followed by a single numerical value, which makes sense only in the context of the command. For example,
 * "<120" would mean animate left 120 units.
 * 
 * x move to x position
 * y move to y position
 * 
 * < move left
 * > move right
 * ^ move up
 * v move down
 * 
 * f alpha fade
 * 
 * s uniform scale
 * 
 * r roll
 * p pitch
 * y yaw
 * 
 * d duration for current pipe
 * 
 * D staggaered duration based on sibling/child index
 * 
 * L loop (absolute) this segment (value is number of times to loop, -1 means loop infinitely)
 * 
 * l loop (relative) this segment (value is number of times to loop, -1 means loop infinitely)
 * 
 * e easing (we allow e# for shorthand or full easeInOutQuad)
 * 
 * | pipe animations (chain)
 *
 * , pipe animations with built in delay
 * 
 * ! invert an action (instead of move left, its move to current position from the right)
 * 
 * [] concurrent Laba animations ( example: [>d2][!fd1] )
 * 
 * * means a choreographed routine; the * is followed by a series of operators which represent the preprogrammed actions
 * 
 */

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.*;

import com.learningmachine.android.app.LMApplication;
import com.learningmachine.android.app.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Laba {

	public String labaNotation;
	public String initialLabaNotation;
	public Boolean loop;

	private static float timeScale = 1.0f;

    @FunctionalInterface
    interface ValueAction <A, B, R> {
        public R apply (A a, B b);
    }

    @FunctionalInterface
    public interface Callback <R> {
        public R apply ();
    }

    @FunctionalInterface
    interface EasingAction <A, B, C, R> {
        public R apply (A a, B b, C c);
    }

    @FunctionalInterface
    interface InitAction <A, R> {
        public R apply (A a);
    }

    @FunctionalInterface
    interface PerformAction <A, B, C, R> {
        public R apply (A a, B b, C c);
    }

    @FunctionalInterface
    interface DescribeAction <A, B, R> {
        public R apply (A a, B b);
    }

    public static class LabaTimer {

        long startTime;
        long endTime;
        int loopCount;
        View view;
        ValueAction action;
        Callback onComplete;
        float duration;

        private void Update() {
            long currentTime = System.nanoTime();
            float t = (float) (currentTime - startTime) / (float) (endTime - startTime);
            if(endTime == startTime) {
            	t = 1.0f;
			}

            if (t >= 1.0f) {
                action.apply(1.0f, false);

                if (loopCount == -1) {
                    action.apply(0.0f, true);
                    startTime = System.nanoTime();
                    endTime = startTime + (long) (duration * 1000000000.0f);
                    ScheduleNextUpdate();
                    return;
                }
                if (loopCount > 1) {
                    loopCount--;
                    action.apply(0.0f, true);
                    startTime = System.nanoTime();
                    endTime = startTime + (long) (duration * 1000000000.0f);
                    ScheduleNextUpdate();
                    return;
                }

                if (onComplete != null) {
                    onComplete.apply();
                }

                return;
            }

            action.apply(t, false);
            ScheduleNextUpdate();
        }

        private void ScheduleNextUpdate() {
            view.post(new Runnable() {
                public void run() {
                    Update();
                }
            });
        }

        // Simple timer class to replace the one method we used from LeanTween
        public LabaTimer(View v, ValueAction act, float startVal, float endVal, float dura, Callback complete, int loops) {

            view = v;
			loopCount = loops;
			action = act;
			duration = dura;
			onComplete = complete;
            startTime = System.nanoTime();
            endTime = startTime + (long)(duration * 1000000000.0f);

            action.apply(0.0f, true);
            Update();
        }
    }

	public static class LabaAction extends Object {
		public boolean inverse;
		public Float rawValue;
		public Character operatorChar;

		public View target;
		public Float fromValue;
		public Float toValue;
		public PerformAction action;
		public DescribeAction describe;
		public InitAction init;
		public TimeInterpolator easing;
		public String easingName;


		public float userFloat_1;
		public float userFloat_2;

		public LabaAction(char operatorChar, View target, boolean inverse, float rawValue, TimeInterpolator easing, String easingName) {
			this.operatorChar = operatorChar;
			this.target = target;
			this.inverse = inverse;
			this.rawValue = rawValue;
			this.easing = easing;
			this.easingName = easingName;

			this.action = PerformActions.get(operatorChar);
			this.describe = DescribeActions.get(operatorChar);
			this.init = InitActions.get(operatorChar);

			if(this.inverse == false){
				this.fromValue = 0.0f;
				this.toValue = 1.0f;
			}else{
				this.fromValue = 1.0f;
				this.toValue = 0.0f;
			}

			userFloat_1 = 0.0f;
			userFloat_2 = 0.0f;

			if(this.init != null){
                this.init.apply(this);
			}
		}

		public boolean Init() {
			if (init != null) {
				LabaAction tempAction = new LabaAction (operatorChar, target, inverse, rawValue, easing, easingName);
				this.fromValue = tempAction.fromValue;
				this.toValue = tempAction.toValue;
				this.userFloat_1 = tempAction.userFloat_1;
				this.userFloat_2 = tempAction.userFloat_2;
				return true;
			}
			return false;
		}

		public boolean Perform(float v) {
			if (action != null) {
				action.apply (target, fromValue + (toValue - fromValue) * easing.getInterpolation(v), this);
				return true;
			}
			return false;
		}

		public boolean Describe(StringBuilder sb) {
			if (action != null) {
				describe.apply (sb, this);
				return true;
			}
			return false;
		}
	}


	private static Context context;
    public static void setContext(Context c) {
    	context = c;
	}

	public static float px2dp(float px) {
		if (context == null) {
			Log.d("LABA", "laba context is null, automatic conversion from px to dp is not available");
			return px;
		}
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}

	public static float dp2px(float dp) {
		if (context == null) {
			Log.d("LABA", "laba context is null, automatic conversion from px to dp is not available");
			return dp;
		}
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}

    // these are here for convenience; use autocomplete to quickly look up the number of your easing function
    public static final int linear = 0;
    public static final int easeOutQuad = 1;
    public static final int easeInQuad = 2;
    public static final int easeInOutQuad = 3;
    public static final int easeInCubic = 4;
    public static final int easeOutCubic = 5;
    public static final int easeInOutCubic = 6;
    public static final int easeInQuart = 7;
    public static final int easeOutQuart = 8;
    public static final int easeInOutQuart = 9;
    public static final int easeInQuint = 10;
    public static final int easeOutQuint = 11;
    public static final int easeInOutQuint = 12;
    public static final int easeInSine = 13;
    public static final int easeOutSine = 14;
    public static final int easeInOutSine = 15;
    public static final int easeInExpo = 16;
    public static final int easeOutExpo = 17;
    public static final int easeInOutExpo = 18;
    public static final int easeInCirc = 19;
    public static final int easeOutCirc = 20;
    public static final int easeInOutCirc = 21;
    public static final int easeInBounce = 22;
    public static final int easeOutBounce = 23;
    public static final int easeInOutBounce = 24;

    private static TimeInterpolator[] allEasings = null;

    private static String[] allEasingsByName = new String[] {
            "ease linear", "ease out quad", "ease in quad", "ease in/out quad", "ease in cubic", "ease out cubic", "ease in/out cubic", "ease in quart", "ease out quart", "ease in/out quart",
            "ease in quint", "ease out quint", "ease in/out quint", "ease in sine", "eas out sine", "ease in/out sine", "ease in expo", "ease out expo", "ease in out expo", "ease in circ", "ease out circ", "ease in/out circ",
            "ease in bounce", "ease out bounce", "ease in/out bounce"
    };

	public static float LabaDefaultValue = Float.MIN_VALUE;

	private static Map<Character,InitAction> InitActions;
	private static Map<Character,PerformAction> PerformActions;
	private static Map<Character,DescribeAction> DescribeActions;


	private static int kMaxPipes = 40;
	private static int kMaxActions = 40;
	private static float kDefaultDuration = 0.87f;

	private static boolean isOperator(char c) {
        if (c == ',' || c == '|' || c == '!' || c == 'e') {
            return true;
        }
        return InitActions.containsKey (c);
    }

    private static boolean isNumber(char c) {
        return (c == '+' || c == '-' || c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == '.');
    }


	private static LabaAction[][] ParseAnimationString(View view, String animationString) {
		int idx = 0;
		char[] charString = animationString.toCharArray();

		LabaAction[][] combinedActions = new LabaAction[kMaxPipes][kMaxActions];
		int currentPipeIdx = 0;
		int currentActionIdx = 0;
        TimeInterpolator easingAction = allEasings[easeInOutQuad];
		String easingName = "";

		while (idx < charString.length) {

			boolean invertNextOperator = false;
			char action = ' ';

			// find the next operator
			while (idx < charString.length) {
				char c = charString [idx];
				if (isOperator (c)) {
					if (c == '!') {
						invertNextOperator = true;
					} else if (c == '|') {
						currentPipeIdx++;
						currentActionIdx = 0;
                    } else if (c == ',') {
					    if(currentActionIdx != 0) {
                            currentPipeIdx++;
                            currentActionIdx = 0;
                        }
                        combinedActions [currentPipeIdx][currentActionIdx] = new LabaAction ('d', view, false, kDefaultDuration * 0.26f, easingAction, easingName);
                        currentPipeIdx++;
                        currentActionIdx = 0;
                    } else {
						action = c;
						idx++;
						break;
					}
				}
				idx++;
			}

			// skip anything not important
			while (idx < charString.length && !isNumber (charString [idx]) && !isOperator (charString [idx])) {
				idx++;
			}

			float value = LabaDefaultValue;

			// if this is a number read it in
			if (idx < charString.length && isNumber (charString [idx])) {
				
				// read in numerical value (if it exists)
				boolean isNegativeNumber = false;
				if (charString [idx] == '+') {
					idx++;
				} else if (charString [idx] == '-') {
					isNegativeNumber = true;
					idx++;
				}

				value = 0.0f;

                boolean fractionalPart = false;
				float fractionalValue = 10.0f;
				while (idx < charString.length) {
					char c = charString [idx];
					if (isNumber (c)) {
						if (c >= '0' && c <= '9') {
							if (fractionalPart) {
								value = value + (c - '0') / fractionalValue;
								fractionalValue *= 10.0f;
							} else {
								value = value * 10 + (c - '0');
							}
						}
						if (c == '.') {
							fractionalPart = true;
						}
					}
					if (isOperator (c)) {
						break;
					}
					idx++;
				}

				if (isNegativeNumber) {
					value *= -1.0f;
				}
			}


			// execute the action?
			if (action != ' ') {
				if (InitActions.containsKey (action)) {
					//Log.d("LABA", String.format(Locale.US, " [%d,%d]   action: %c   value: %f   inverted: %b", currentPipeIdx, currentActionIdx, action, value, invertNextOperator));
					combinedActions [currentPipeIdx][currentActionIdx] = new LabaAction (action, view, invertNextOperator, value, easingAction, easingName);
					currentActionIdx++;
				} else {
					if (action == 'e') {
						int easingIdx = (int)(value);
						if (easingIdx >= 0 && idx < allEasings.length) {
							easingAction = allEasings [easingIdx];
							easingName = allEasingsByName [easingIdx];
						}
					}
				}
			}

		}

		return combinedActions;
	}





	private static void AnimateOne(View view, String animationString, Callback onComplete, StringBuilder describe) {
		LabaAction[][] actionList = ParseAnimationString (view, animationString);
		PerformAction durationAction1 = PerformActions.get('d');
		PerformAction durationAction2 = PerformActions.get('D');
		PerformAction loopAction1 = PerformActions.get('L');
		PerformAction loopAction2 = PerformActions.get('l');

		int numOfPipes = 0;

		float duration = 0.0f;
		float looping = 1.0f;
		boolean loopingRelative = false;
		for (int i = 0; i < kMaxPipes; i++) {
			if (actionList [i][0] != null) {
				numOfPipes++;

				float durationForPipe = kDefaultDuration;
				for (int j = 0; j < kMaxActions; j++) {
                    if(actionList [i][j] != null) {
                        if (actionList[i][j].action == durationAction1 || actionList[i][j].action == durationAction2) {
                            durationForPipe = actionList[i][j].fromValue;
                        }
                        if (actionList[i][j].action == loopAction1) {
                            looping = actionList[i][j].fromValue;
                        }
                        if (actionList[i][j].action == loopAction2) {
                            loopingRelative = true;
                            looping = actionList[i][j].fromValue;
                        }
                    }
				}
				duration += durationForPipe;
			}
		}

		// having only a single pipe makes things much more efficient, so treat it separately
		if (numOfPipes == 1) {

			if (loopingRelative) {
                new LabaTimer(view, (v,f) -> {
                    float fv = (Float)v;
                    if ((Boolean)f) {
                        for (int j = 0; j < kMaxActions; j++) {
                            if (actionList [0][j] != null && !actionList [0][j].Init ()) {
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < kMaxActions; i++) {
                        if (actionList [0][i] != null && !actionList [0][i].Perform (fv)) {
                            break;
                        }
                    }
                    return null;
                }, 0.0f, 1.0f, duration, onComplete, (int)looping);
			} else {
				for (int j = 0; j < kMaxActions; j++) {
					if (actionList [0][j] != null && !actionList [0][j].Init ()) {
						break;
					}
				}
                new LabaTimer (view, (v,f) -> {
                    float fv = (Float)v;
					for (int i = 0; i < kMaxActions; i++) {
						if (actionList [0][i] != null && !actionList [0][i].Perform (fv)) {
							break;
						}
					}
                    return null;
				}, 0.0f, 1.0f, duration * timeScale, onComplete, (int)looping);
			}
		} else {

			// for multiple pipes, the only mechanism leantween provides for this in onComplete actions
			// unfortunately, this means we need to create an Action for each pipe


			Callback nextAction = null;
			for (int pipeIdx = numOfPipes - 1; pipeIdx >= 0; pipeIdx--) {

				float durationForPipe = kDefaultDuration;
				float loopingForPipe = 1.0f;
				boolean loopingRelativeForPipe = false;
				for (int j = 0; j < kMaxActions; j++) {
				    if(actionList [pipeIdx][j] != null) {
                        if (actionList[pipeIdx][j].action == durationAction1 || actionList[pipeIdx][j].action == durationAction2) {
                            durationForPipe = actionList[pipeIdx][j].fromValue;
                        }
                        if (actionList[pipeIdx][j].action == loopAction1) {
                            loopingForPipe = actionList[pipeIdx][j].fromValue;
                        }
                        if (actionList[pipeIdx][j].action == loopAction2) {
                            loopingRelativeForPipe = true;
                            loopingForPipe = actionList[pipeIdx][j].fromValue;
                        }
                    }
				}

				int idx = pipeIdx;
                Callback localNextAction = nextAction;
				if (localNextAction == null) {
					localNextAction = onComplete;
				}
				if (localNextAction == null) {
					localNextAction = () -> { return null; };
				}


				final boolean loopingRelativeForPipeFinal = loopingRelativeForPipe;
				final float durationForPipeFinal = durationForPipe;
				final float loopingForPipeFinal = loopingForPipe;
				final Callback localNextActionFinal = localNextAction;

				nextAction = () -> {

					if (loopingRelativeForPipeFinal) {
                        new LabaTimer (view, (v,f) -> {
                            float fv = (Float)v;
							if ((Boolean)f) {
								for (int j = 0; j < kMaxActions; j++) {
									if (actionList [idx][j] != null && !actionList [idx][j].Init ()) {
										break;
									}
								}
							}
							for (int j = 0; j < kMaxActions; j++) {
								if (actionList [idx][j] != null && !actionList [idx][j].Perform (fv)) {
									break;
								}
							}
							return null;
						}, 0.0f, 1.0f, durationForPipeFinal, localNextActionFinal, (int)loopingForPipeFinal);
					} else {
						for (int j = 0; j < kMaxActions; j++) {
							if (actionList [idx][j] != null && !actionList [idx][j].Init ()) {
								break;
							}
						}
                        new LabaTimer (view, (v,f) -> {
                            float fv = (Float)v;
							for (int j = 0; j < kMaxActions; j++) {
								if (actionList [idx][j] != null && !actionList [idx][j].Perform (fv)) {
									break;
								}
							}
                            return null;
						}, 0.0f, 1.0f, durationForPipeFinal * timeScale, localNextActionFinal, (int)loopingForPipeFinal);
					}

					return null;
				};
			}

			if (nextAction != null) {
				nextAction.apply ();
			} else {
				if (onComplete != null) {
					onComplete.apply ();
				}
			}

		}
	}

	public static void Animate(View view, String animationString, Callback onComplete) {

		CheckInit();

		if (animationString.contains ("[")) {
			String[] parts = animationString.replace ('[', ' ').split ("]");
			for (String part : parts) {
				if (part.length() > 0) {
					AnimateOne (view, part, onComplete, null);
					onComplete = null;
				}
			}
		} else {
			AnimateOne (view, animationString, onComplete, null);
			onComplete = null;
		}
	}




	private static void DescribeOne(View view, String animationString, StringBuilder sb) {
		LabaAction[][] actionList = ParseAnimationString (view, animationString);
		PerformAction durationAction1 = PerformActions.get('d');
		PerformAction durationAction2 = PerformActions.get('D');
		PerformAction loopingAction1 = PerformActions.get('L');
		PerformAction loopingAction2 = PerformActions.get('l');

		int numOfPipes = 0;

		float duration = 0.0f;
		int looping = 1;
		String loopingRelative = "absolute";
		for (int i = 0; i < kMaxPipes; i++) {
			if (actionList [i][0] != null) {
				numOfPipes++;

				float durationForPipe = kDefaultDuration;
				for (int j = 0; j < kMaxActions; j++) {
				    if(actionList [i][j] != null) {
                        if (actionList[i][j].action == durationAction1 || actionList[i][j].action == durationAction2) {
                            durationForPipe = actionList[i][j].fromValue;
                        }
                        if (actionList[i][j].action == loopingAction1) {
                            looping = (int) (float) actionList[i][j].fromValue;
                        }
                        if (actionList[i][j].action == loopingAction2) {
                            looping = (int) (float) actionList[i][j].fromValue;
                            loopingRelative = "relative";
                        }
                    }
				}
				duration += durationForPipe;
			}
		}

		// having only a single pipe makes things much more efficient, so treat it separately
		if (numOfPipes == 1) {
			int stringLengthBefore = sb.length();

			for (int i = 0; i < kMaxActions; i++) {
				if (!actionList [0][i].Describe (sb)) {
					break;
				}
			}


			if (looping > 1) {
				sb.append (String.format(Locale.US, " %d repeating %d times, ", loopingRelative, looping));
			} else if (looping == -1) {
				sb.append (String.format(Locale.US, " %d repeating forever, ", loopingRelative));
			}

			if (stringLengthBefore != sb.length()) {
				sb.append (String.format(Locale.US, " %s  ", actionList [0][0].easingName));

				sb.setLength(sb.length() - 2);
				if (duration == 0.0f) {
					sb.append (" instantly.");
				} else {
					sb.append (String.format(Locale.US, " over %f seconds.", duration * timeScale));
				}
			} else {
				if (sb.length() > 2) {
                    sb.setLength(sb.length() - 2);
				}
				sb.append (String.format(Locale.US, " wait for %f seconds.", duration * timeScale));
			}

		} else {

			for (int pipeIdx = 0; pipeIdx < numOfPipes; pipeIdx++) {
				int stringLengthBefore = sb.length();

				float durationForPipe = kDefaultDuration;
				int loopingForPipe = 1;
				String loopingRelativeForPipe = "absolute";
				for (int j = 0; j < kMaxActions; j++) {
				    if (actionList [pipeIdx][j] != null) {
                        if (actionList[pipeIdx][j].action == durationAction1 || actionList[pipeIdx][j].action == durationAction2) {
                            durationForPipe = actionList[pipeIdx][j].fromValue;
                        }
                        if (actionList[pipeIdx][j].action == loopingAction1) {
                            loopingForPipe = (int) (float) actionList[pipeIdx][j].fromValue;
                        }
                        if (actionList[pipeIdx][j].action == loopingAction2) {
                            loopingForPipe = (int) (float) actionList[pipeIdx][j].fromValue;
                            loopingRelativeForPipe = "relative";
                        }
                    }
				}

				int idx = pipeIdx;
				for (int j = 0; j < kMaxActions; j++) {
					if (actionList [idx][j] != null && !actionList [idx][j].Init ()) {
						break;
					}
				}

				for (int j = 0; j < kMaxActions; j++) {
					if (actionList [idx][j] != null && !actionList [idx][j].Describe (sb)) {
						break;
					}
				}

				if (loopingForPipe > 1) {
					sb.append (String.format(Locale.US, " %s repeating %d times, ", loopingRelativeForPipe, loopingForPipe));
				} else if (loopingForPipe == -1) {
					sb.append (String.format(Locale.US, " %s repeating forever, ", loopingRelativeForPipe));
				}

				if (stringLengthBefore != sb.length()) {
					sb.append (String.format(Locale.US, " %s  ", actionList [idx][0].easingName));

					sb.setLength(sb.length() - 2);
					if (durationForPipe == 0.0f) {
						sb.append (" instantly.");
					} else {
						sb.append (String.format(Locale.US, " over %f seconds.", durationForPipe * timeScale));
					}
				} else {
					sb.append (String.format(Locale.US, " wait for %f seconds.", durationForPipe * timeScale));
				}

				if (pipeIdx + 1 < numOfPipes) {
					sb.append (" Once complete then  ");
				}
			}
		}
	}

	public static String Describe(View view, String animationString) {

		CheckInit();

		if (animationString == null || animationString.length() == 0) {
			return "do nothing";
		}

		StringBuilder sb = new StringBuilder ();

		if (animationString.contains ("[")) {
			String[] parts = animationString.replace ('[', ' ').split ("]");
			int animNumber = 0;
			sb.append ("Perform a series of animations at the same time.\n");
			for (String part : parts) {
				if (part.length() > 0) {
					sb.append (String.format(Locale.US, "Animation #%d will ", animNumber+1));
					DescribeOne (view, part, sb);
					sb.append ("\n");
					animNumber++;
				}
			}
		} else {
			DescribeOne (view, animationString, sb);
		}
			
		if (sb.length() > 0) {
			// upper case the starting letter
			sb.insert (0, sb.toString ().substring (0, 1).toUpperCase ());
			sb.delete(1,1);
		}

		return sb.toString ();
	}



	private static void CheckInit() {
		if (allEasings == null ){
			allEasings = new TimeInterpolator[] {
					new linear(),
					new easeOutQuad(),
					new easeInQuad(),
					new easeInOutQuad(),
					new easeInCubic(),
					new easeOutCubic(),
					new easeInOutCubic(),
					new easeInQuart(),
					new easeOutQuart(),
					new easeInOutQuart(),
					new easeInQuint(),
                    new easeOutQuint(),
                    new easeInOutQuint(),
                    new easeInSine(),
                    new easeOutSine(),
                    new easeInOutSine(),
                    new easeInExpo(),
                    new easeOutExpo(),
                    new easeInOutExpo(),
                    new easeInCirc(),
                    new easeOutCirc(),
                    new easeInOutCirc(),
			};
		}


		if(InitActions == null) {
			InitActions = new HashMap<Character, InitAction>();
			PerformActions = new HashMap<Character, PerformAction>();
			DescribeActions = new HashMap<Character, DescribeAction>();

			RegisterOperation(
					'L',
					(v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = -1.0f;
                        }
                        newAction.fromValue = newAction.toValue = newAction.rawValue;
                        return newAction;
                    },
                    (rt, v, action) -> { return null; },
                    (sb, action) -> { return null; }
            );

            RegisterOperation(
                    'L',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = -1.0f;
                        }
                        newAction.fromValue = newAction.toValue = newAction.rawValue;
                        return newAction;
                    },
                    (rt, v, action) -> { return null; },
                    (sb, action) -> { return null; }
            );

            RegisterOperation(
                    'l',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = -1.0f;
                        }
                        newAction.fromValue = newAction.toValue = newAction.rawValue;
                        return newAction;
                    },
                    (rt, v, action) -> { return null; },
                    (sb, action) -> { return null; }
            );

            RegisterOperation(
                    'd',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = kDefaultDuration;
                        }
                        newAction.fromValue = newAction.toValue = newAction.rawValue;
                        return newAction;
                    },
                    (rt, v, action) -> { return null; },
                    (sb, action) -> { return null; }
            );

            RegisterOperation(
                    'D',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = kDefaultDuration;
                        }
                        // TODO: Figure out how we want to handle child index
                        //newAction.fromValue = newAction.toValue = newAction.rawValue * newAction.target.GetSiblingIndex();
                        return newAction;
                    },
                    (rt, v, action) -> { return null; },
                    (sb, action) -> { return null; }
            );

            RegisterOperation(
                    'x',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = 0.0f;
                        }

                        newAction.rawValue = dp2px(newAction.rawValue );

                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getTranslationX();
                            newAction.toValue = newAction.rawValue;
                        }else{
                            newAction.fromValue = newAction.rawValue;
                            newAction.toValue = newAction.target.getTranslationX();
                        }
                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setTranslationX((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "move to {0} x pos, ", action.rawValue));
                        } else {
                            sb.append(String.format(Locale.US, "move from {0} x pos, ", action.rawValue));
                        }
                        return null;
                    }
            );

            RegisterOperation(
                    'y',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = 0.0f;
                        }

						newAction.rawValue = dp2px(newAction.rawValue );

                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getTranslationY();
                            newAction.toValue = newAction.rawValue;
                        }else{
                            newAction.fromValue = newAction.rawValue;
                            newAction.toValue = newAction.target.getTranslationY();
                        }
                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setTranslationY((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "move to %f y pos, ", action.rawValue));
                        } else {
                            sb.append(String.format(Locale.US, "move from %f y pos, ", action.rawValue));
                        }
                        return null;
                    }
            );

            RegisterOperation(
                    '<',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.target.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            newAction.rawValue = (float)newAction.target.getMeasuredWidth();
                        }
						newAction.rawValue = dp2px(newAction.rawValue );

                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getTranslationX();
                            newAction.toValue = newAction.target.getTranslationX() - newAction.rawValue;
                        }else{
                            newAction.fromValue = newAction.target.getTranslationX() + newAction.rawValue;
                            newAction.toValue = newAction.target.getTranslationX();
                        }
                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setTranslationX((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "move left %f units, ", action.rawValue));
                        } else {
                            sb.append(String.format(Locale.US, "move in from left %f units, ", action.rawValue));
                        }
                        return null;
                    }
            );


            RegisterOperation(
                    '>',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.target.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            newAction.rawValue = (float)newAction.target.getMeasuredWidth();
                        }
						newAction.rawValue = dp2px(newAction.rawValue );

                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getTranslationX();
                            newAction.toValue = newAction.target.getTranslationX() + newAction.rawValue;
                        }else{
                            newAction.fromValue = newAction.target.getTranslationX() - newAction.rawValue;
                            newAction.toValue = newAction.target.getTranslationX();
                        }
                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setTranslationX((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse) {
                            sb.append(String.format(Locale.US, "move right %f units, ", action.rawValue));
                        } else {
                            sb.append(String.format(Locale.US, "move in from right %f units, ", action.rawValue));
                        }
                        return null;
                    }
            );

            RegisterOperation(
                    '^',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.target.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            newAction.rawValue = (float)newAction.target.getMeasuredHeight();
                        }
						newAction.rawValue = dp2px(newAction.rawValue );

                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getTranslationY();
                            newAction.toValue = newAction.target.getTranslationY() - newAction.rawValue;
                        }else{
                            newAction.fromValue = newAction.target.getTranslationY() + newAction.rawValue;
                            newAction.toValue = newAction.target.getTranslationY();
                        }
                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setTranslationY((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "move up %f units, ", action.rawValue));
                        } else {
                            sb.append(String.format(Locale.US, "move in from above %f units, ", action.rawValue));
                        }
                        return null;
                    }
            );

            RegisterOperation(
                    'v',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.target.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            newAction.rawValue = (float)newAction.target.getMeasuredHeight();
                        }
						newAction.rawValue = dp2px(newAction.rawValue );

                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getTranslationY();
                            newAction.toValue = newAction.target.getTranslationY() + newAction.rawValue;
                        }else{
                            newAction.fromValue = newAction.target.getTranslationY() - newAction.rawValue;
                            newAction.toValue = newAction.target.getTranslationY();
                        }

                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setTranslationY((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "move down %f units, ", action.rawValue));
                        } else {
                            sb.append(String.format(Locale.US, "move in from below %f units, ", action.rawValue));
                        }
                        return null;
                    }
            );

            /*
            RegisterOperation(
                    'z',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.target.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            newAction.rawValue = (newAction.target.getMeasuredHeight() + newAction.target.getMeasuredWidth()) * 0.5f;
                        }
                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getTranslationZ();
                            newAction.toValue = newAction.target.getTranslationZ() - newAction.rawValue;
                        }else{
                            newAction.fromValue = newAction.target.getTranslationZ() + newAction.rawValue;
                            newAction.toValue = newAction.target.getTranslationZ();
                        }
                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setTranslationZ((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "move along z axis %f units, ", action.rawValue));
                        } else {
                            sb.append(String.format(Locale.US, "move in from z axis %f units, ", action.rawValue));
                        }
                        return null;
                    }
            );*/


            RegisterOperation(
                    's',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = 1.0f;
                        }
                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getScaleX();
                            newAction.toValue = newAction.rawValue;
                        }else{
                            newAction.fromValue = (newAction.rawValue > 0.5f ? 0.0f : 1.0f);
                            newAction.toValue = newAction.rawValue;
                        }
                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setScaleX((float)v);
                        view.setScaleY((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "scale to %d%%, ", (int)(action.rawValue * 100.0f)));
                        } else {
                            sb.append(String.format(Locale.US, "scale in from %d%%, ", (int)(action.rawValue * 100.0f)));
                        }
                        return null;
                    }
            );

            RegisterOperation(
                    'r',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = 0.0f;
                        }
                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getRotation();
                            newAction.toValue = newAction.target.getRotation() - newAction.rawValue;
                        }else{
                            newAction.fromValue = newAction.target.getRotation() + newAction.rawValue;
                            newAction.toValue = newAction.target.getRotation();
                        }
                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setRotation((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "rotate around z by %f°, ", action.rawValue));
                        } else {
                            sb.append(String.format(Locale.US, "rotate in from around z by %f°, ", action.rawValue));
                        }
                        return null;
                    }
            );

            RegisterOperation(
                    'p',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = 0.0f;
                        }
                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getRotationX();
                            newAction.toValue = newAction.target.getRotationX() - newAction.rawValue;
                        }else{
                            newAction.fromValue = newAction.target.getRotationX() + newAction.rawValue;
                            newAction.toValue = newAction.target.getRotationX();
                        }

                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setRotationX((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "rotate around x by %f°, ", action.rawValue));
                        } else {
                            sb.append(String.format(Locale.US, "rotate in from around x by %f°, ", action.rawValue));
                        }
                        return null;
                    }
            );

            RegisterOperation(
                    'y',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = 0.0f;
                        }
                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getRotationY();
                            newAction.toValue = newAction.target.getRotationY() - newAction.rawValue;
                        }else{
                            newAction.fromValue = newAction.target.getRotationY() + newAction.rawValue;
                            newAction.toValue = newAction.target.getRotationY();
                        }
                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setRotationY((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "rotate around y by %f°, ", action.rawValue));
                        } else {
                            sb.append(String.format(Locale.US, "rotate in from around y by %f°, ", action.rawValue));
                        }
                        return null;
                    }
            );

            RegisterOperation(
                    'f',
                    (v) -> {
                        LabaAction newAction = (LabaAction)v;
                        if (newAction.rawValue == LabaDefaultValue) {
                            newAction.rawValue = 1.0f;
                        }
                        if(!newAction.inverse){
                            newAction.fromValue = newAction.target.getAlpha();
                            newAction.toValue = newAction.rawValue;
                        }else{
                            newAction.fromValue = (newAction.rawValue > 0.5f ? 0.0f : 1.0f);
                            newAction.toValue = newAction.rawValue;
                        }
                        return newAction;
                    },
                    (rt, v, action) -> {
                        View view = (View)rt;
                        view.setAlpha((float)v);
                        return null;
                    },
                    (s, a) -> {
                        LabaAction action = (LabaAction)a;
                        StringBuilder sb = (StringBuilder)s;
                        if(!action.inverse ) {
                            sb.append(String.format(Locale.US, "fade to %d%%, ", (int)(action.rawValue * 100.0f)));
                        } else {
                            sb.append(String.format(Locale.US, "fade from %d%% to %d%%, ", (int)(action.fromValue * 100.0f),(int)(action.toValue * 100.0f)));
                        }
                        return null;
                    }
            );

		}


	}


	static public void RegisterOperation(char charOperator, InitAction init, PerformAction perform, DescribeAction describe){
		CheckInit();
		InitActions.put((Character)charOperator, init);
		PerformActions.put((Character)charOperator, perform);
		DescribeActions.put((Character)charOperator, describe);
	}



    private static class linear implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            return start + (end - start) * val;
        }
    }

    private static class easeInQuad implements TimeInterpolator {
	    float start = 0.0f;
	    float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return end * val * val + start;
        }
    }

    private static class easeOutQuad implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return -end * val * (val - 2) + start;
        }
    }

    private static class easeInOutQuad implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            val /= .5f;
            end -= start;
            if (val < 1) return end / 2 * val * val + start;
            val--;
            return -end / 2 * (val * (val - 2) - 1) + start;
        }
    }

    private static class easeInCubic implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return end * val * val * val + start;
        }
    }

    private static class easeOutCubic implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            val--;
            end -= start;
            return end * (val * val * val + 1) + start;
        }
    }

    private static class easeInOutCubic implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            val /= .5f;
            end -= start;
            if (val < 1) return end / 2 * val * val * val + start;
            val -= 2;
            return end / 2 * (val * val * val + 2) + start;
        }
    }


    private static class easeInQuart implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return end * val * val * val * val + start;
        }
    }

    private static class easeOutQuart implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            val--;
            end -= start;
            return -end * (val * val * val * val - 1) + start;
        }
    }

    private static class easeInOutQuart implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            val /= .5f;
            end -= start;
            if (val < 1) return end / 2 * val * val * val * val + start;
            val -= 2;
            return -end / 2 * (val * val * val * val - 2) + start;
        }
    }


    private static class easeInQuint implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return end * val * val * val * val * val + start;
        }
    }

    private static class easeOutQuint implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            val--;
            end -= start;
            return end * (val * val * val * val * val + 1) + start;
        }
    }

    private static class easeInOutQuint implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            val /= .5f;
            end -= start;
            if (val < 1) return end / 2 * val * val * val * val * val + start;
            val -= 2;
            return end / 2 * (val * val * val * val * val + 2) + start;
        }
    }



    private static class easeInSine implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return -end * (float)Math.cos(val / 1 * (Math.PI / 2)) + end + start;
        }
    }

    private static class easeOutSine implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return end * (float)Math.sin(val / 1 * (Math.PI / 2)) + start;
        }
    }

    private static class easeInOutSine implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return -end / 2 * ((float)Math.cos(Math.PI * val / 1) - 1) + start;
        }
    }



    private static class easeInExpo implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return end * (float)Math.pow(2, 10 * (val / 1 - 1)) + start;
        }
    }

    private static class easeOutExpo implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return end * (-(float)Math.pow(2, -10 * val / 1) + 1) + start;
        }
    }

    private static class easeInOutExpo implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            val /= .5f;
            end -= start;
            if (val < 1) return end / 2 * (float)Math.pow(2, 10 * (val - 1)) + start;
            val--;
            return end / 2 * (-(float)Math.pow(2, -10 * val) + 2) + start;
        }
    }




    private static class easeInCirc implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            end -= start;
            return -end * ((float)Math.sqrt(1 - val * val) - 1) + start;
        }
    }

    private static class easeOutCirc implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            val--;
            end -= start;
            return end * (float)Math.sqrt(1 - val * val) + start;
        }
    }

    private static class easeInOutCirc implements TimeInterpolator {
        float start = 0.0f;
        float end = 1.0f;
        public float getInterpolation(float val) {
            val /= .5f;
            end -= start;
            if (val < 1) return -end / 2 * ((float)Math.sqrt(1 - val * val) - 1) + start;
            val -= 2;
            return end / 2 * ((float)Math.sqrt(1 - val * val) + 1) + start;
        }
    }

}