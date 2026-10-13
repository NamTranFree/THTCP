import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDP_Data_Type_1 {

    public static void main(String[] args) throws IOException {
        String studentCode = "B23DCCN589";
        String qCode = "VMTZmoUz";
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2207;

        DatagramSocket socket = new DatagramSocket();

        // a. Gửi ;studentCode;qCode
        byte[] out = (";" + studentCode + ";" + qCode).getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(out, out.length, server, port));

        // b. Nhận requestId;a1,a2,...,a50
        byte[] buf = new byte[1024];
        DatagramPacket in = new DatagramPacket(buf, buf.length);
        socket.receive(in);
        String msg = new String(in.getData(), 0, in.getLength(), StandardCharsets.UTF_8);

        int sep = msg.indexOf(';');
        String requestId = msg.substring(0, sep);
        String[] parts = msg.substring(sep + 1).split(",");

        // c. Tìm giá trị lớn nhất và nhỏ nhất
        int max = Integer.parseInt(parts[0].trim());
        int min = max;
        for (String p : parts) {
            int v = Integer.parseInt(p.trim());
            if (v > max) max = v;
            if (v < min) min = v;
        }

        String reply = requestId + ";" + max + "," + min;
        byte[] res = reply.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(res, res.length, server, port));

        // d. Đóng socket
        socket.close();
    }
}