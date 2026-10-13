import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDP_Data_Type_2 {

    public static void main(String[] args) throws Exception {
        String studentCode = "B23DCCN589";
        String qCode = "PyPcSp78";
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2207;

        DatagramSocket socket = new DatagramSocket();

        // a. Gửi ;studentCode;qCode
        byte[] out = (";" + studentCode + ";" + qCode).getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(out, out.length, server, port));

        // b. Nhận requestId;n;A1,A2,...,Am
        byte[] buf = new byte[1024];
        DatagramPacket in = new DatagramPacket(buf, buf.length);
        socket.receive(in);
        String msg = new String(in.getData(), 0, in.getLength(), StandardCharsets.UTF_8);

        String[] parts = msg.split(";");
        String requestId = parts[0];
        int n = Integer.parseInt(parts[1].trim());
        String[] a = parts[2].split(",");

        // c. Đánh dấu các số đã có, rồi lấy các số từ 1 đến n chưa có
        boolean[] seen = new boolean[n + 1];
        for (String s : a) {
            int v = Integer.parseInt(s.trim());
            if (v >= 1 && v <= n) {
                seen[v] = true;
            }
        }

        StringBuilder missing = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            if (!seen[i]) {
                if (missing.length() > 0) {
                    missing.append(',');
                }
                missing.append(i);
            }
        }

        String reply = requestId + ";" + missing;
        byte[] res = reply.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(res, res.length, server, port));

        // d. Đóng socket
        socket.close();
    }
}