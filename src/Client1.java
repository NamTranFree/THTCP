import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client1 {
    private static final String SERVER_HOST = "36.50.135.242";
    private static final int SERVER_PORT = 2206;
    private static final int TIMEOUT_MS = 5000;

    private static final String STUDENT_CODE = "B23DCCN589";
    private static final String Q_CODE = "50RTetQL";

    public static void main(String[] args) {

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // ----- Bước a: Gửi studentCode;qCode -----
            String request = STUDENT_CODE + ";" + Q_CODE;
            sendLine(out, request);
            System.out.println("Đã gửi: " + request);

            // ----- Bước b: Nhận chuỗi số nguyên -----
            String numberLine = readLine(in);
            if (numberLine == null || numberLine.isEmpty()) {
                System.out.println("Không nhận được dữ liệu từ server.");
                return;
            }
            System.out.println("Nhận được: " + numberLine);

            // ----- Bước c: Tìm khoảng cách nhỏ nhất -----
            String[] parts = numberLine.split(",");
            int[] numbers = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                numbers[i] = Integer.parseInt(parts[i].trim());
            }

            int[] sorted = Arrays.copyOf(numbers, numbers.length);
            Arrays.sort(sorted);

            int minDistance = Integer.MAX_VALUE;
            int first = 0, second = 0;
            for (int i = 0; i < sorted.length - 1; i++) {
                int distance = sorted[i + 1] - sorted[i];
                if (distance < minDistance) {
                    minDistance = distance;
                    first = sorted[i];
                    second = sorted[i + 1];
                }
            }

            String result = minDistance + "," + first + "," + second;
            System.out.println("Kết quả tính được: " + result);

            // Gửi kết quả lên server
            sendLine(out, result);
            System.out.println("Đã gửi lên server: " + result);

        } catch (IOException e) {
            System.out.println("Lỗi kết nối/giao tiếp: " + e.getMessage());
        }
        // ----- Bước d: try-with-resources tự đóng socket -----
        System.out.println("Đã đóng kết nối. Kết thúc chương trình.");
    }

    /**
     * Gửi một chuỗi qua OutputStream, kết thúc bằng ký tự xuống dòng '\n'.
     */
    private static void sendLine(OutputStream out, String data) throws IOException {
        String line = data + "\n";
        out.write(line.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }

    /**
     * Đọc dữ liệu từ InputStream cho đến khi gặp ký tự '\n' (hoặc hết luồng).
     */
    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        boolean received = false;

        while ((b = in.read()) != -1) {
            received = true;
            if (b == '\n') {
                break;
            }
            if (b == '\r') {
                continue; // bỏ qua ký tự CR nếu có (CRLF)
            }
            buffer.write(b);
        }

        if (!received) {
            return null; // luồng đã đóng, không có dữ liệu
        }
        return buffer.toString(StandardCharsets.UTF_8.name());
    }
}