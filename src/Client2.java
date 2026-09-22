import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client2 {
	private static final String SERVER_HOST = "36.50.135.242";
	private static final int SERVER_PORT = 2207;
	private static final int TIMEOUT_MILLIS = 5000;
	private static final String STUDENT_CODE = "B23DCCN589";
	private static final String QUESTION_CODE = "IYssjj3W";

	public static void main(String[] args) {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT), TIMEOUT_MILLIS);
			socket.setSoTimeout(TIMEOUT_MILLIS);

			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());

			output.writeUTF(STUDENT_CODE + ";" + QUESTION_CODE);
			output.flush();

			int firstNumber = input.readInt();
			int secondNumber = input.readInt();

			output.writeInt(firstNumber + secondNumber);
			output.writeInt(firstNumber * secondNumber);
			output.flush();
		} catch (IOException exception) {
			System.err.println("TCP communication failed: " + exception.getMessage());
		}
	}
}
