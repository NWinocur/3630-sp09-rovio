
public class MCUTracker implements RovioConstants {
	private final RovioAPI api;
	private final boolean useUpdate;

	private int leftEncoder,
				rightEncoder,
				rearEncoder;
	
	private boolean previousBarrierState = false;
	private BarrierCallback barrierCallback = null;
	
	private final Thread taskThread;
	private final MCUTask task;
	
	private final Object notifyToken = new Object();

	public MCUTracker(RovioAPI api) {
		this(api, true);
	}

	public MCUTracker(RovioAPI api, boolean useUpdate) {
		this.api = api;
		this.useUpdate = useUpdate;
		
		task = new MCUTask();
		taskThread = new Thread(task);
		taskThread.setDaemon(true);
		
		taskThread.start();
	}
	
	public static interface BarrierCallback {
		
		public void stateChanged(boolean barrierDetected);
	}
	
	public void update() {
		task.update();
	}

	private class MCUTask implements Runnable {
//		private final int time = 200;
		
		public void run() {
			while(true) {
				if(useUpdate) {
					synchronized(notifyToken) {
						try {
							notifyToken.wait();
						} catch(InterruptedException e) {
						}
					}
				}

				final RovioAPIResponses.MCUReport report = api.getMCUReport();
				System.out.println(report);
				leftEncoder += report.getLeftTicks() *
						((report.getLeftDirection() == WheelDirection.CLOCKWISE) ? 1 : -1);
				rightEncoder += report.getRightTicks() *
						((report.getRightDirection() == WheelDirection.CLOCKWISE) ? 1 : -1);
				rearEncoder += report.getRearTicks() *
						((report.getRearDirection() == WheelDirection.CLOCKWISE) ? 1 : -1);
				
				if(report.isBarrierDetected() != previousBarrierState) {
					previousBarrierState = report.isBarrierDetected();
					if(barrierCallback != null) {
						barrierCallback.stateChanged(report.isBarrierDetected());
					}
				}
				
/*				try {
					Thread.sleep(time);
				} catch(InterruptedException e) {
				}*/
			}
		}
		
		public void update() {
			synchronized(notifyToken) {
				notifyToken.notify();
			}
		}
	}
	
	public void setBarrierCallback(BarrierCallback callback) {
		this.barrierCallback = callback;
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
