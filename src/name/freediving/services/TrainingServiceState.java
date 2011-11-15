package name.freediving.services;

import java.util.Iterator;

import name.freediving.R;
import name.freediving.entry.TrainingPrimitive;
import name.freediving.entry.TrainingProg;
import name.freediving.util.Log;
import name.freediving.util.StringUtils;
import android.content.Context;
import android.net.Uri;

public class TrainingServiceState {
	private boolean isAutoSwitch;
	private int currentCount;
	private TrainingPrimitive currPrimitive;
	private TimeFormat timeFormat;
	private Phase phase;
	private int phaseTimeElapsed;
	private TrainingProg program;
	private boolean isSoundOnPref;
	private boolean isSoundPeriodicOnPref;
	private Uri soundPhaseUri;
	private int soundPhaseId;
	private boolean isSoundPhaseOnPref;
	private State state;
	private int timeElapsed;
	private int phaseTime;
	private boolean isVibroOnPref;
	private long vibroPeriodicalDuration;
	private boolean isVibroPeriodicOnPref;
	private boolean isVibroPhaseOn;
	private long vibroPhaseDuration;
	private boolean isVibroPhaseOnPref;
	private CharSequence whyStopped;
	private Iterator<TrainingPrimitive> progIterator;
	static final int TICK_TIME = 1000;
	private static final String TAG = "TrainingServiceState";

	TrainingServiceState(boolean awaiting, TimeFormat elapsed, Phase nextPhase,
			long vibroPeriodicalDuration, long vibroPhaseDuration,
			CharSequence whyStopped) {
		this.setAutoSwitch(awaiting);
		this.timeFormat = elapsed;
		this.vibroPeriodicalDuration = vibroPeriodicalDuration;
		this.vibroPhaseDuration = vibroPhaseDuration;
		this.whyStopped = whyStopped;
	}


	public int getCurrentCount() {
		return currentCount;
	}

	void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}

	public TrainingPrimitive getCurrPrimitive() {
		return currPrimitive;
	}

	public TimeFormat getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(TimeFormat elapsed) {
		this.timeFormat = elapsed;
	}

	public Phase getPhase() {
		return phase;
	}

	void setPhase(Phase phase) {
		this.phase = phase;
	}

	public int getPhaseTimeElapsed() {
		return phaseTimeElapsed;
	}

	void setPhaseTimeElapsed(int phaseTimeElapsed) {
		this.phaseTimeElapsed = phaseTimeElapsed;
	}

	public TrainingProg getProgram() {
		return program;
	}

	boolean initProgram(TrainingProg program) {
		Log.d(TAG, "initProgram:" + program);
		this.program = program;
		progIterator = program.getPrimitives().iterator();
		if (progIterator.hasNext()) {
			currPrimitive = progIterator.next();
			Log.d(TAG, "initProgram: currPrimitive=" + currPrimitive);
			currentCount = 0;
			whyStopped = getProgram().getName() + ": Ready to begin!";
			phase = Phase.BREATHINGIN;
			phaseTime = phase.getTimeOfPhase(currPrimitive, 0);
			cachedPhaseTime=StringUtils.formatTime(phaseTime);
			if (phaseTime == 0 && !switchPhase()) {
				Log.d(TAG, "initProgram:" + false);
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean isSoundOn() {
		return isSoundOnPref;
	}

	public void setSoundOn(boolean soundOn) {
		this.isSoundOnPref = soundOn;
	}

	public boolean isSoundPeriodicOn() {
		return isSoundPeriodicOnPref;
	}

	public void setSoundPeriodicOn(boolean soundPeriodicOn) {
		this.isSoundPeriodicOnPref = soundPeriodicOn;
	}

	Uri getSoundPhase() {
		return soundPhaseUri;
	}

	void setSoundPhase(Uri soundPhase) {
		this.soundPhaseUri = soundPhase;
	}

	int getSoundPhase_id() {
		return soundPhaseId;
	}

	void setSoundPhase_id(int soundPhase_id) {
		this.soundPhaseId = soundPhase_id;
	}

	public boolean isSoundPhaseOn() {
		return isSoundPhaseOnPref;
	}

	public void setSoundPhaseOn(boolean soundPhaseOn) {
		this.isSoundPhaseOnPref = soundPhaseOn;
	}

	public State getState() {
		return state;
	}

	void setState(State state) {
		this.state = state;
	}

	public int getTimeElapsed() {
		return timeElapsed;
	}

	void setTimeElapsed(int timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	public int getTimeOfPhase() {
		return phaseTime;
	}

	void setTimeOfPhase(int timeOfPhase) {
		this.phaseTime = timeOfPhase;
	}

	public boolean isVibroOn() {
		return isVibroOnPref;
	}

	public void setVibroOn(boolean vibroOn) {
		this.isVibroOnPref = vibroOn;
	}

	long getVibroPeriodicalDuration() {
		return vibroPeriodicalDuration;
	}

	void setVibroPeriodicalDuration(long vibroPeriodicalDuration) {
		this.vibroPeriodicalDuration = vibroPeriodicalDuration;
	}

	public boolean isVibroPeriodicOn() {
		return isVibroPeriodicOnPref;
	}

	public void setVibroPeriodicOn(boolean vibroPeriodicOn) {
		this.isVibroPeriodicOnPref = vibroPeriodicOn;
	}

	public boolean isVibroPhase() {
		return isVibroPhaseOn;
	}

	public void setVibroPhase(boolean vibroPhase) {
		this.isVibroPhaseOn = vibroPhase;
	}

	long getVibroPhaseDuration() {
		return vibroPhaseDuration;
	}

	void setVibroPhaseDuration(long vibroPhaseDuration) {
		this.vibroPhaseDuration = vibroPhaseDuration;
	}

	public boolean isVibroPhaseOn() {
		return isVibroPhaseOnPref;
	}

	public void setVibroPhaseOn(boolean vibroPhaseOn) {
		this.isVibroPhaseOnPref = vibroPhaseOn;
	}

	public CharSequence getWhyStopped() {
		return whyStopped;
	}

	void setWhyStopped(CharSequence whyStopped) {
		this.whyStopped = whyStopped;
	}

	public boolean isAutoSwitch() {
		return isAutoSwitch;
	}

	public void setAutoSwitch(boolean isAutoSwitch) {
		this.isAutoSwitch = isAutoSwitch;
	}

	// this switches time view : 00:00/00:00 , 00:00 or -00:00
	public enum TimeFormat {
		ELAPSED, ELAPSEDREMAINING, REMAINING;
	}

	public enum Phase {

		BREATHINGIN {
			public String toText(Context context) {
				return context.getString(R.string.breath_in);
			}

			@Override
			public Phase next() {
				return HOLDINGIN;
			}

			@Override
			public int getTimeOfPhase(TrainingPrimitive prim, int count) {
				return prim.getA() + (prim.getB() * count);
			}
		},

		BREATHINGOUT {
			public String toText(Context context) {
				return context.getString(R.string.breath_out);
			}

			@Override
			public Phase next() {
				return HOLDINGOUT;
			}

			@Override
			public int getTimeOfPhase(TrainingPrimitive prim, int count) {
				return prim.getE() + (prim.getF() * count);
			}
		},

		HOLDINGIN {
			public String toText(Context context) {
				return context.getString(R.string.holding);
			}

			@Override
			public Phase next() {
				return BREATHINGOUT;
			}

			@Override
			public int getTimeOfPhase(TrainingPrimitive prim, int count) {
				return prim.getC() + (prim.getD() * count);
			}
		},

		HOLDINGOUT {
			public String toText(Context context) {
				return context.getString(R.string.holding);
			}

			@Override
			public Phase next() {
				return RESTING;
			}

			@Override
			public int getTimeOfPhase(TrainingPrimitive prim, int count) {
				return prim.getG() + prim.getH() * count;
			}
		},

		RESTING {
			public String toText(Context context) {
				return context.getString(R.string.resting);
			}

			@Override
			public Phase next() {
				return BREATHINGIN;
			}

			@Override
			public int getTimeOfPhase(TrainingPrimitive prim, int count) {
				return prim.getRest();
			}
		};
		public abstract String toText(Context context);

		public abstract Phase next();

		public abstract int getTimeOfPhase(TrainingPrimitive prim, int count);

	}

	@Override
	public String toString() {
		return "TrainingServiceState [isAutoSwitch=" + isAutoSwitch
				+ ", currentCount=" + currentCount + ", currPrimitive="
				+ currPrimitive + ", timeFormat=" + timeFormat + ", phase="
				+ phase + ", phaseTimeElapsed=" + phaseTimeElapsed
				+ ", program=" + program + ", isSoundOnPref=" + isSoundOnPref
				+ ", isSoundPeriodicOnPref=" + isSoundPeriodicOnPref
				+ ", soundPhaseUri=" + soundPhaseUri + ", soundPhaseId="
				+ soundPhaseId + ", isSoundPhaseOnPref=" + isSoundPhaseOnPref
				+ ", state=" + state + ", timeElapsed=" + timeElapsed
				+ ", phaseTime=" + phaseTime + ", isVibroOnPref="
				+ isVibroOnPref + ", vibroPeriodicalDuration="
				+ vibroPeriodicalDuration + ", isVibroPeriodicOnPref="
				+ isVibroPeriodicOnPref + ", isVibroPhaseOn=" + isVibroPhaseOn
				+ ", vibroPhaseDuration=" + vibroPhaseDuration
				+ ", isVibroPhaseOnPref=" + isVibroPhaseOnPref
				+ ", whyStopped=" + whyStopped + ", progIterator="
				+ progIterator + ", cachedPhaseTime=" + cachedPhaseTime + "]";
	}

	/**
	 * Looks for the next not empty primitive's part, switches cycle, primitive
	 * 
	 * @return true if switched successfully
	 */
	boolean switchPhase() {
		Log.d(TAG, "switchPhase:");
		Phase ps = this.phase.next();
		phaseTimeElapsed = 0;
		setPhase(ps);
		if (ps == Phase.BREATHINGIN) {
			if (currPrimitive.getCount() > currentCount)
				setCurrentCount(++currentCount);
			else if (getProgIterator().hasNext()) {
				currPrimitive = progIterator.next();
				currentCount = 0;
				if (currPrimitive == null)
					throw new RuntimeException("Prim is null");
			} else {
				Log.d(TAG, "switchPhase:" + "prog is finished");
				return false;
			}
		}
		int time = ps.getTimeOfPhase(currPrimitive, currentCount);
		if (time == 0)
			switchPhase();

		phaseTime = time;
		cachedPhaseTime = StringUtils.formatTime(time);
		return true;
	}

	private String cachedPhaseTime;

	public String getCachedPhaseTime() {
		return cachedPhaseTime;
	}

	/*
	 * States of whole training
	 */
	public enum State {

		PAUSED, RUNNING, STOPPED;
	}

	void tik() {
		phaseTimeElapsed += TICK_TIME;
		timeElapsed += TICK_TIME;
	}

	public boolean hasMoreTime() {
		return phaseTime > phaseTimeElapsed;
	}

	Iterator<TrainingPrimitive> getProgIterator() {
		return progIterator;
	}

	void setProgIterator(Iterator<TrainingPrimitive> progIterator) {
		this.progIterator = progIterator;
	}

	public CharSequence elapsedTimeToText() {
		switch (getTimeFormat()) {
		case ELAPSED:
			return StringUtils.formatTime(getPhaseTimeElapsed());
		case ELAPSEDREMAINING:
			return StringUtils.formatTime(getPhaseTimeElapsed()) + "/"
					+ cachedPhaseTime;
		default:
			return StringUtils.formatTime(getTimeOfPhase()
					- getPhaseTimeElapsed());
		}
	}

}
