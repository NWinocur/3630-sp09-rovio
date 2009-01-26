
public interface RovioConstants {

	public static enum RovioState {
		IDLE, 
		DRIVING_HOME,
		DOCKING,
		EXECUTING_PATH,
		RECORDING_PATH
	}
	
	public static enum RovioStateFlag {
		HOME_POSITION (1);
		
		private final int value;
		
		RovioStateFlag(int value) {
			this.value = value;
		}
		
		public RovioStateFlag[] getFlags(int value) {
			int numFlags = 0;
			int a = value;
			while(a > 0) {
				if((a & 1) == 1)
					numFlags++;
				a = a >> 1;
			}
			
			RovioStateFlag[] flags = new RovioStateFlag[numFlags];
			
			for(RovioStateFlag flag : RovioStateFlag.values()) {
				if((flag.value & value) > 0) {
					
				}
			}
			
			return flags;
		}
	}
	
	public static enum CameraResolution {
		_176x144,
		_320x240,
		_352x240,
		_640x480
	}
	
	public static enum VideoCompression {
		LOW,
		MEDIUM,
		HIGH
	}
	
	public static enum DDNSState {
		NO_UPDATE,
		UPDATING,
		UPDATE_SUCCESSFUL,
		UPDATE_FAILED
	}
	
}
