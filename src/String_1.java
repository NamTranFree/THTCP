import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class String_1 {

    public static void main(String[] args) throws Exception {
        String studentCode = "B23DCCN589";
        String qCode = "LJRghk3U";
        InetAddress server = InetAddress.getByName("36.50.135.242");
        int port = 2208;

        DatagramSocket socket = new DatagramSocket();

        // a. Gửi ;studentCode;qCode
        byte[] out = (";" + studentCode + ";" + qCode).getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(out, out.length, server, port));

        // b. Nhận requestId;data
        byte[] buf = new byte[1024];
        DatagramPacket in = new DatagramPacket(buf, buf.length);
        socket.receive(in);
        String msg = new String(in.getData(), 0, in.getLength(), StandardCharsets.UTF_8);

        int sep = msg.indexOf(';');
        String requestId = msg.substring(0, sep);
        String data = msg.substring(sep + 1);

        // c. Chữ đầu của mỗi từ viết hoa, các chữ còn lại viết thường
        StringBuilder sb = new StringBuilder();
        boolean wordStart = true;
        for (char c : data.toCharArray()) {
            if (c == ' ') {
                sb.append(c);
                wordStart = true;
            } else {
                sb.append(wordStart ? Character.toUpperCase(c) : Character.toLowerCase(c));
                wordStart = false;
            }
        }

        String reply = requestId + ";" + sb;
        byte[] res = reply.getBytes(StandardCharsets.UTF_8);
        socket.send(new DatagramPacket(res, res.length, server, port));

        // d. Đóng socket
        socket.close();
    }
}