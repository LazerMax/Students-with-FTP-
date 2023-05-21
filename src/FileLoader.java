import java.io.*;
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


            // изменение или создание локального файла
            public static void changeFile(List<Student> students) throws Exception {

            File file = new File("students.json");
            FileWriter writer = new FileWriter(file);

                students.forEach(student -> {
                    try {
                        writer.write(student.toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                writer.close();
            }
        }

