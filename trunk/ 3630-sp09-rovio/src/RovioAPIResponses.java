import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public interface RovioAPIResponses {

	public static class RevResponse implements RovioConstants {
		protected final String response;
		protected final String cmd;
		
		public RevResponse(InputStream in) {
			final Scanner scanner = new Scanner(in);
			final Map<String,String> valueMap = new HashMap<String,String>();
			
			String line;
			String[] parts;
			while(scanner.hasNext()) {
				line = scanner.nextLine().trim();
				parts = line.split(" = ");
				valueMap.put(parts[0], parts[1]);
			}
			
			cmd = valueMap.get("Cmd");
			response = valueMap.get("responses");
		}		
	}
	
	public static class MCUReport extends RevResponse {
		private final boolean 	lightOn,
								radarOn,
								barrierDetected;
		
		private final int	leftTicks,
							rightTicks,
							rearTicks;
		
		private final WheelDirection 	leftDirection,
										rightDirection,
										rearDirection;
		
		public MCUReport(InputStream in) {
			super(in);

			final String[] data = new String[response.length() / 2];
			
			for (int i = 0; i < data.length; i += 1) {
				data[i] = response.substring((i * 2), (i * 2) + 1);
			}
			
			leftDirection = WheelDirection.get(Byte.parseByte(data[2], 16));
			leftTicks = Short.parseShort(data[3] + data[4], 16);
			
			rightDirection = WheelDirection.get(Byte.parseByte(data[5], 16));
			rightTicks = Short.parseShort(data[6] + data[7], 16);
			
			rearDirection = WheelDirection.get(Byte.parseByte(data[8], 16));
			rearTicks = Short.parseShort(data[9] + data[10], 16);
			
			// TODO: Turn these into enum values and add getters for the resulting fields.
			final byte headPosition = Byte.parseByte(data[12], 16);
			final byte batteryStatus = Byte.parseByte(data[13], 16);
			
			final byte miscStatus = Byte.parseByte(data[14], 16);
			
			lightOn = (miscStatus & 1) > 0; // bit 0 (2 ^ 0)
			radarOn = (miscStatus & 2) > 0; // bit 1 (2 ^ 1)
			barrierDetected = (miscStatus & 4) > 0; // bit 2 (2 ^ 2)
			
			final int chargerStatus = (miscStatus & (8 + 16 + 32)) >> 3; // bits 3, 4, and 5
		}
		
		public String toString() {
			return 	"Left direction: " + leftDirection + " Ticks: " + leftTicks + "\n" +
					"Right direction: " + rightDirection + " Ticks: " + rightTicks + "\n" +
					"Rear direction: " + rearDirection + " Ticks: " + rearTicks;
		}
		
		public boolean isLightOn() {
			return lightOn;
		}

		public boolean isRadarOn() {
			return radarOn;
		}

		public boolean isBarrierDetected() {
			return barrierDetected;
		}

		public int getLeftTicks() {
			return leftTicks;
		}

		public int getRightTicks() {
			return rightTicks;
		}

		public int getRearTicks() {
			return rearTicks;
		}

		public WheelDirection getLeftDirection() {
			return leftDirection;
		}

		public WheelDirection getRightDirection() {
			return rightDirection;
		}

		public WheelDirection getRearDirection() {
			return rearDirection;
		}
	}
}
