import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class LTM_JAVA {

    public static void main(String[] args) throws IOException {
        String studentCode = "B23DCCN589";
        String qCode = "CU9qJDcY";
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2208;

        DatagramSocket socket = new DatagramSocket();

        // a. Gửi ;studentCode;qCode
        byte[] out = (";" + studentCode + ";" + qCode).getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(out, out.length, server, port));

        // b. Nhận requestId; data
        byte[] buf = new byte[1024];
        DatagramPacket in = new DatagramPacket(buf, buf.length);
        socket.receive(in);
        String msg = new String(in.getData(), 0, in.getLength(), StandardCharsets.UTF_8);

        int sep = msg.indexOf(';');
        String requestId = msg.substring(0, sep);
        String data = msg.substring(sep + 1);

        // c. Tìm ký tự xuất hiện nhiều nhất và vị trí của nó (đếm từ 1)
        int[] count = new int[65536];
        for (char c : data.toCharArray()) {
            count[c]++;
        }
        char best = data.charAt(0);
        for (char c : data.toCharArray()) {
            if (count[c] > count[best]) {
                best = c;
            }
        }
        StringBuilder positions = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == best) {
                positions.append(i + 1).append(',');
            }
        }

        String reply = requestId + ";" + best + ":" + positions;
        byte[] res = reply.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(res, res.length, server, port));

        // d. Đóng socket
        socket.close();
    }
}