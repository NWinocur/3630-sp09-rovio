
public class EncoderTracker implements RovioConstants {
	private final RovioAPI api;
	private final boolean useUpdate;

	private int leftEncoder,
				rightEncoder,
				rearEncoder;
	
	private final Thread taskThread;
	private final EncoderTask task;

	public EncoderTracker(RovioAPI api) {
		this(api, true);
	}

	public EncoderTracker(RovioAPI api, boolean useUpdate) {
		this.api = api;
		this.useUpdate = useUpdate;
		
		task = new EncoderTask();
		taskThread = new Thread(task);
		taskThread.setDaemon(true);
		
		taskThread.start();
	}
	
	public void update() {
		task.update();
	}

	private class EncoderTask implements Runnable {
		private final int time = 200;
		
		public void run() {
			while(true) {
				if(useUpdate) {
					try {
						this.wait();
					} catch(InterruptedException e) {
					}
				}

				final RovioAPIResponses.MCUReport report = api.getMCUReport();
				leftEncoder += report.getLeftTicks() *
						((report.getLeftDirection() == WheelDirection.CLOCKWISE) ? 1 : -1);
				rightEncoder += report.getRightTicks() *
						((report.getRightDirection() == WheelDirection.CLOCKWISE) ? 1 : -1);
				rearEncoder += report.getRearTicks() *
						((report.getRearDirection() == WheelDirection.CLOCKWISE) ? 1 : -1);
				
				try {
					Thread.sleep(time);
				} catch(InterruptedException e) {
				}
			}
		}
		
		public void update() {
			this.notify();
		}
	}

	public int getLeftEncoder() {
		return leftEncoder;
	}

	public int getRightEncoder() {
		return rightEncoder;
	}

	public int getRearEncoder() {
		return rearEncoder;
	}
}
