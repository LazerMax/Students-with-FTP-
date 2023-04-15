import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;


public class FileLoader{
        private String fileURL;

    //создание URL-адреса
    public void getFileURL() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите логин:");
            String user = scanner.nextLine()+":";
            System.out.println("Введите пароль:");
            String pass = scanner.nextLine()+"@";
            System.out.println("Введите ip-адрес FTP-сервера");
            String host = scanner.nextLine()+"/";
            String ftpUrl = "ftp://";
            System.out.println("Введите путь к файлу:");
            String filePath = scanner.nextLine();

            ftpUrl = String.format(ftpUrl + user + pass + host + filePath);

            this.fileURL = ftpUrl;
        }


        // получение списка студентов из файла на FTP-сервере
            public String downloadFile(){
                String line = "";
                String str = "";

                String ftpURL = "ftp://";
                String host = "192.168.0.40/";
                String user = "root:";
                String pass = "aaaaaa@";
                String filePath = "students.json";

                ftpURL = String.format(ftpURL + user + pass + host + filePath);
                System.out.println(ftpURL);
               // getFileURL();
               // ftpURL = this.fileURL;

                try {
                    URL url = new URL("ftp://root:aaaaaa@192.168.0.40/students.json");
                    URLConnection conn = url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = bufferedReader.readLine()) != null) {
                        str += line;
                    }
                    bufferedReader.close();

                    str = str.replaceAll("[,{}]", "\n");
                    str = str.replaceAll("\\s+"," ");
                    str = str.replaceAll("\"", "");

                } catch (Exception ex) {
                    throw new Error("Не удалось подключится к FTP-серверу");
                }

                return str;
            }

            // изменение файла на FTP-сервере
            public void uploadFile(List<Student> students) throws Exception {
               URL url = new URL("ftp://root:aaaaaa@192.168.0.40/students.json");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

                students.forEach(student -> {
                    try {
                        System.out.println(student.toString());
                        writer.write(student.toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                writer.close();
            }
        }

