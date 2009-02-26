
/**
 * All the valid return constants for the Rovio API.
 * @author Sean Hussey
 */
public interface RovioReturnConstants {

	public static final int 
		/** CGI command successful. */		
		SUCCESS = 0, 
		
		/** CGI command general failure. */
		FAILURE = 1,

		/** Robot is executing autonomous function. */
		ROBOT_BUSY = 2,
		
		/** CGI command not implemented. */
		FEATURE_NOT_IMPLEMENTED = 3,
		
		/** CGI nav command: unknown action requested. */
		UNKNOWN_CGI_ACTION = 4,
		
		/** No NS signal available. */
		NO_NS_SIGNAL = 5,
		
		/** Path memory is full. */
		NO_EMPTY_PATH_AVAILABLE = 6,
		
		/** Failed to read FLASH. */
		FAILED_TO_READ_PATH = 7,
		
		/** FLASH error. */
		PATH_BASEADDRESS_NOT_INITIALIZED = 8,
		
		/** No path with such name. */
		PATH_NOT_FOUND = 9,
		
		/** Path name parameter is missing. */
		PATH_NAME_NOT_SPECIFIED = 10,
		
		/** Save path command received while not in recording mode. */
		NOT_RECORDING_PATH = 11,
		
		/** Flash subsystem failure. */
		FLASH_NOT_INITIALIZED = 12,
		
		/** Flash operation failed. */
		FAILED_TO_DELETE_PATH = 13,
		
		/** Flash operation failed. */
		FAILED_TO_READ_FROM_FLASH = 14,
		
		/** Flash operation failed. */
		FAILED_TO_WRITE_TO_FLASH = 15,
		
		/** Flash failed. */
		FLASH_NOT_READY = 16,
		
		/** NA. */
		NO_MEMORY_AVAILABLE = 17,
		
		/** NA. */
		NO_MCU_PORT_AVAILABLE = 18,
		
		/** NA. */
		NO_NS_PORT_AVAILABLE = 19,
		
		/** NA. */
		NS_PACKET_CHECKSUM_ERROR = 20,
		
		/** NA. */
		NS_UART_READ_ERROR = 21,
		
		/** One or more parameters are out of expected range. */
		PARAMETER_OUTOFRANGE = 22,
		
		/** One or more parameters are missing. */
		NO_PARAMETER = 23;
}
