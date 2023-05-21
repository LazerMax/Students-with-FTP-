import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class FTPConnect {
    private String host = "127.0.0.1";
    private int port = 21;
    private String username = "root";
    private String password = "aaaaaa";
    private BufferedReader reader;
    private OutputStream writer;
    private Socket socket;

    private Socket dataSocket;
    private String ip;
    private BufferedReader controlReader;
    private PrintWriter controlWriter;
    private String response;
    private  int portNumber;


    private void connectToFTPServer() throws IOException, InterruptedException {

            socket = new Socket(host, port);
            writer = socket.getOutputStream();
            controlReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            controlWriter = new PrintWriter((writer), true);

            response = controlReader.readLine();

            if (!response.startsWith("220")) {
                throw new IOException("FTP server not responding");
            }

            controlWriter.println("USER " + username);
            response = controlReader.readLine();

            controlWriter.println("PASS " + password);

            while (!response.startsWith("230")){
                response = controlReader.readLine();
            }
    }

        private  void enterPassiveMode () throws IOException {
            try {
                connectToFTPServer();

                controlWriter.println("PASV");
                response = controlReader.readLine();

                int openingParenthesis = response.indexOf("(");
                int closingParenthesis = response.indexOf(")", openingParenthesis + 1);
                if (closingParenthesis > 0) {
                    String data = response.substring(openingParenthesis + 1, closingParenthesis);
                    String[] parts = data.split(",");
                    if (parts.length == 6) {
                         ip = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
                         portNumber = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);
                         dataSocket = new Socket(ip, portNumber);
                    }
                }
            } catch (IOException e) {
                System.err.println(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    private void enterActiveMode() throws IOException, InterruptedException {
        connectToFTPServer();

        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        String localAddress = socket.getLocalAddress().getHostAddress().replace('.', ',');
        String portString = String.format("%d,%d", port / 256, port % 256);
        String addressString = String.format("%s", localAddress);
        controlWriter.println(String.format("PORT %s,%s", addressString, portString));
        serverSocket.setSoTimeout(100000);
        dataSocket = serverSocket.accept();
        serverSocket.close();
    }

        private String getFileFromFTP() throws IOException, InterruptedException {

            //enterPassiveMode();
            enterActiveMode();

            InputStream dataInputStream = dataSocket.getInputStream();

            String fileName = "students.json";
            controlWriter.println("RETR " + fileName);

            byte[] response = new byte[1024];

            dataInputStream.read(response);

            String str = new String(response);
            System.out.println(str);

            dataInputStream.close();
            socket.close();
            writer.close();

            str = str.replaceAll("[,{}]", "\n");
            str = str.replaceAll("\\s+"," ");
            str = str.replaceAll("\"", "");

            return str;
        }

      public static void start() throws IOException, InterruptedException {
          FTPConnect passiveModeConnect = new FTPConnect();
          passiveModeConnect.getFileFromFTP();;
      }

     private void sendFileFromFTP(){
        try {
            //enterPassiveMode();
            enterActiveMode();
            dataSocket = new Socket(ip, portNumber);

            String fileName = "students.json";
            byte[] response = new byte[1024];

            controlWriter.println("DELE " + fileName);

            File file = new File(fileName);

            OutputStream dataOutputStream = dataSocket.getOutputStream();

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                response = line.getBytes();
                dataOutputStream.write(response);
                dataOutputStream.write("\r\n".getBytes());
            }

             controlWriter.println("STOR " + fileName);

            scanner.close();
            dataOutputStream.close();
            socket.close();
            writer.close();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
     }
}

