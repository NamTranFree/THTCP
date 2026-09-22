import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static final int SERVER_PORT = 2208;
    private static final int TIMEOUT_MS = 5000;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: java Client <studentCode> <questionCode> <serverHost>");
            return;
        }

        String studentCode = args[0];
        String questionCode = args[1];
        String serverHost = args[2];

        try (Socket socket = new Socket(serverHost, SERVER_PORT)) {
            socket.setSoTimeout(TIMEOUT_MS);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));

            String request = studentCode + ";" + questionCode;
            out.write(request);
            out.newLine();
            out.flush();

            String domainLine = in.readLine();
            if (domainLine == null) {
                return;
            }

            String[] domains = domainLine.split(",");
            List<String> eduDomains = new ArrayList<>();
            for (String d : domains) {
                String domain = d.trim();
                if (domain.endsWith(".edu")) {
                    eduDomains.add(domain);
                }
            }

            String eduResult = String.join(", ", eduDomains);
            out.write(eduResult);
            out.newLine();
            out.flush();

        } catch (java.net.SocketTimeoutException e) {
            System.err.println("Server không phản hồi trong 5 giây.");
        } catch (IOException e) {
            System.err.println("Lỗi kết nối/giao tiếp: " + e.getMessage());
        }
    }
}