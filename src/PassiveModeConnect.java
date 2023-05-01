import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class PassiveModeConnect {
    private static InputStream reader;
    private static OutputStream writer;
    private static Socket socket;
    private static String ip;

    private static int portNumber;


    public static void connectToFTPServer() throws IOException {
        try {
            String server = "127.0.0.1";
            int port = 21;
            String user = "root";
            String password = "aaaaaa";

            socket = new Socket(server, port);

            reader = socket.getInputStream();
            writer = socket.getOutputStream();

            byte[] response = new byte[1024];

            String request = "USER " + user + "\r\n";
            writer.write(request.getBytes());
            reader.read(response);


            request = "PASS " + password + "\r\n";
            writer.write(request.getBytes());
            reader.read(response);

            request = "PASV\r\n";
            writer.write(request.getBytes());
            reader.read(response);


            String responseString = new String(response);
            System.out.println(responseString);
            int startIndex = responseString.indexOf("(");
            int endIndex = responseString.indexOf(")");

            while (startIndex == -1 || endIndex == -1){
                request = "PASV\r\n";
                writer.write(request.getBytes());
                reader.read(response);
                startIndex = responseString.indexOf("(");
                endIndex = responseString.indexOf(")");
            }

               String[] addressParts = responseString.substring(startIndex + 1, endIndex).split(",");
            ip = addressParts[0] + "." + addressParts[1] + "." + addressParts[2] + "." + addressParts[3];
            portNumber = Integer.parseInt(addressParts[4]) * 256 + Integer.parseInt(addressParts[5]);

        } catch (IOException e) {
            System.err.println(e);
        }
    }

        public static String getFileByPassiveMode () throws IOException {
            try {
                connectToFTPServer();
                socket = new Socket(ip, portNumber);
                InputStream dataInputStream = socket.getInputStream();

                String fileName = "students.json";
                String request = "RETR " + fileName + "\r\n";
                writer.write(request.getBytes());

                byte[] response = new byte[1024];

                dataInputStream.read(response);

                String str = new String(response);

                dataInputStream.close();
                socket.close();
                reader.close();
                writer.close();

                str = str.replaceAll("[,{}]", "\n");
                str = str.replaceAll("\\s+"," ");
                str = str.replaceAll("\"", "");

                return str;

            } catch (IOException e) {
                System.err.println(e);
            }
            return null;
        }

     public static void sendFileByPassiveMode (){
        try {
            connectToFTPServer();
            socket = new Socket(ip, portNumber);

            String fileName = "students.json";
            byte[] response = new byte[1024];

           String request = "DELE " + fileName + "\r\n";
            writer.write(request.getBytes());


            File file = new File("students.json");

            OutputStream dataOutputStream = socket.getOutputStream();

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                response = line.getBytes();
                dataOutputStream.write(response);
                dataOutputStream.write("\r\n".getBytes());
            }

             request = "STOR " + fileName + "\r\n";
            writer.write(request.getBytes());

            System.out.println(getFileByPassiveMode());

            scanner.close();
            dataOutputStream.close();
            socket.close();
            reader.close();
            writer.close();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
     }
}

