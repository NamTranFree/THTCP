import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client1 {
    private static final int SERVER_PORT = 2206;
    private static final int TIMEOUT_MILLIS = 5000;
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_STUDENT_CODE = "B16DCCN999";
    private static final String DEFAULT_QUESTION_CODE = "FF49DC02";

    public static void main(String[] args) {
        String studentCode = args.length > 0 ? args[0] : DEFAULT_STUDENT_CODE;
        String questionCode = args.length > 1 ? args[1] : DEFAULT_QUESTION_CODE;
        String host = args.length > 2 ? args[2] : DEFAULT_HOST;

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, SERVER_PORT), TIMEOUT_MILLIS);
            socket.setSoTimeout(TIMEOUT_MILLIS);

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            writeLine(output, studentCode + ";" + questionCode);
            String response = readLine(input);
            if (response == null || response.isBlank()) {
                throw new IOException("Server did not send any numbers");
            }

            String[] values = response.trim().split(",");
            if (values.length < 2) {
                throw new IOException("Server sent fewer than two numbers");
            }

            int first = Integer.parseInt(values[0].trim());
            int second = Integer.parseInt(values[1].trim());
            int minimumDistance = Math.abs(first - second);

            for (int index = 0; index < values.length; index++) {
                int current = Integer.parseInt(values[index].trim());
                for (int nextIndex = index + 1; nextIndex < values.length; nextIndex++) {
                    int next = Integer.parseInt(values[nextIndex].trim());
                    int distance = Math.abs(current - next);
                    if (distance < minimumDistance) {
                        minimumDistance = distance;
                        first = Math.min(current, next);
                        second = Math.max(current, next);
                    }
                }
            }

            writeLine(output, minimumDistance + "," + first + "," + second);
        } catch (IOException | NumberFormatException exception) {
            System.err.println("TCP communication failed: " + exception.getMessage());
        }
    }

    private static void writeLine(OutputStream output, String value) throws IOException {
        output.write((value + "\n").getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    private static String readLine(InputStream input) throws IOException {
        StringBuilder value = new StringBuilder();
        int character;
        while ((character = input.read()) != -1) {
            if (character == '\n') {
                return value.toString().stripTrailing();
            }
            if (character != '\r') {
                value.append((char) character);
            }
        }
        return value.length() == 0 ? null : value.toString();
    }
}
