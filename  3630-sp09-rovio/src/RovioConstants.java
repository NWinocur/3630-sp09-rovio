import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

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
		_352x288,
		_320x240,
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
	
	/** Different types for the manual drive command. */
	public static enum DriveType {
		STOP,
		FORWARD,
		BACKWARD,
		STRAIGHT_LEFT,
		STRAIGHT_RIGHT,
		ROTATE_LEFT_BY_SPEED,
		ROTATE_RIGHT_BY_SPEED,
		DIAGONAL_FORWARD_LEFT,
		DIAGONAL_FORWARD_RIGHT,
		DIAGONAL_BACKWARD_LEFT,
		DIAGONAL_BACKWARD_RIGHT,
		HEAD_UP,
		HEAD_DOWN,
		HEAD_MIDDLE,
		RESERVED_0,
		RESERVED_1,
		RESERVED_2,
		ROTATE_LEFT_BY_20_DEGREES,
		ROTATE_RIGHT_BY_20_DEGREES
	}
	
	/** Different response codes from API calls. */
	public static enum ResponseCode {
		SUCCESS(0),
		FAILURE(1),
		ROBOT_BUSY(2),
		FEATURE_NOT_IMPLEMENTED(3),
		UNKNOWN_CGI_ACTION(4),
		NO_NS_SIGNAL(5),
		NO_EMPTY_PATH_AVAILABLE(6),
		FAILED_TO_READ_PATH(7),
		PATH_BASEADDRESS_NOT_INITIALIZED(8),
		PATH_NOT_FOUND(9),
		PATH_NAME_NOT_SPECIFIED(10),
		NOT_RECORDING_PATH(11),
		FLASH_NOT_INITIALIZED(12),
		FAILED_TO_DELETE_PATH(13),
		FAILED_TO_READ_FROM_FLASH(14),
		FAILED_TO_WRITE_TO_FLASH(15),
		FLASH_NOT_READY(16),
		NO_MEMORY_AVAILABLE(17),
		NO_MCU_PORT_AVAILABLE(18),
		NO_NS_PORT_AVAILABLE(19),
		NS_PACKET_CHECKSUM_ERROR(20),
		NS_UART_READ_ERROR(21),
		PARAMETER_OUTOFRANGE(22),
		NO_PARAMETER(23);
		
		private int value;
		private static final Map<Integer, ResponseCode> lookup = new HashMap<Integer, ResponseCode>();

		static {
			for(ResponseCode c : EnumSet.allOf(ResponseCode.class)) {
				lookup.put(c.getValue(), c);
			}
		}
		
		private ResponseCode(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public static ResponseCode get(int c) {
			return lookup.get(c);
		}	
	}
	
	public enum BatteryState {
		NORMAL,
		NEED_CHARGE,
		DEAD
	}

	public enum WheelDirection {
		NOCHANGE(0),
		CLOCKWISE(2),
		COUNTERCLOCKWISE(5);
		
		private int value;
		private static final Map<Integer, WheelDirection> lookup = new HashMap<Integer, WheelDirection>();

		static {
			for(WheelDirection d : EnumSet.allOf(WheelDirection.class)) {
				lookup.put(d.getValue(), d);
			}
		}
		
		private WheelDirection(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public static WheelDirection get(int d) {
			return lookup.get(d);
		}
	}
	
	public enum HeadPosition {
		HIGH,
		MID,
		LOW
	}

}
