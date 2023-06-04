import java.io.File;
import java.util.Scanner;

public class Main {


   private static Scanner scanner = new Scanner(System.in);
   private static String host, userName, password;
   private static boolean activeMode = true;
   private static FTPConnect ftpConnect;
   private static StringBuilder menu = new StringBuilder();

    //получение данных для подключения к FTP-серверу от пользователя
    private static void getFTPHostAndPort(){
        System.out.println("Введите ip-адрес FTP-сервера");
        host = "127.0.0.1";
        System.out.println("Введите логин для входа на FTP-сервер:");
        userName = "root";
        System.out.println("Введите пароль для входа на FTp-сервер");
        password = "aaaaaa";
    }

    //выбор режима подключения к FTP-серверу
    private static void choiceFTPMode(){

        menu.append(System.lineSeparator())
                .append("Выберите режим подключения:")
                .append(System.lineSeparator())
                .append("1. Активный режим")
                .append(System.lineSeparator())
                .append("2. Пассивный режим")
                .append(System.lineSeparator());

        System.out.println(menu);

        int choice = 0;

        while (choice != 1 && choice != 2){
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e){
                System.out.println("Вы ввели не число! \n");
            }
            if(choice != 1 && choice != 2){
                System.out.println("Введите число 1 или 2");
            }
        }

        switch (choice){
            case 1: activeMode = true;
                break;
            case 2: activeMode = false;
        }
    }

    //выбор действия после подключения
    private static void choosingAnAction(boolean activeMode) throws Exception {

        File file = new File("Students.json");
        int choice = 0;

        while (!(file.exists())){
            menu.append(System.lineSeparator())
                    .append("Файл Students.json не скачан")
                    .append(System.lineSeparator())
                    .append("\nВыберете цифру:")
                    .append("\n1. Получить файл списка студентов с FTP-сервера")
                    .append(System.lineSeparator());

            System.out.println(menu);

            while (choice != 1){
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e){
                    System.out.println("Вы ввели не число! \n");
                }
                if(choice != 1){
                    System.out.println("Введите число 1");
                }
            }
            ftpConnect.getFileFromFTP(activeMode);
            if(!(file.exists())){
                System.out.println("Файла нет ни на компьютере, ни на FTP-сервере.");
                return;
            }
        }


        menu.append(System.lineSeparator())
                .append("Выберете цифру:")
                .append(System.lineSeparator())
                .append("1. Получить файл списка студентов с FTP-сервера")
                .append(System.lineSeparator())
                .append("2. Отправить файл списка студентов на FTP-сервер")
                .append(System.lineSeparator())
                .append("3. Получить список студентов по имени")
                .append(System.lineSeparator())
                .append("4. Получить информацию о студенте по id")
                .append(System.lineSeparator())
                .append("5. Добавить студента")
                .append(System.lineSeparator())
                .append("6. Удалить студента по id")
                .append(System.lineSeparator())
                .append("7. Завершение работы")
                .append(System.lineSeparator());

        System.out.println(menu);

        Students students = new Students();
        students.getArrayOfStudents();


        choice = 0;

        while (choice < 1 || choice > 7){
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e){
                System.out.println("Вы ввели не число! \n");
            }
            if(choice < 1 || choice > 7){
                System.out.println("Введите число от 1 до 7");
            }
        }


        switch (choice){
            case 1: ftpConnect.getFileFromFTP(activeMode);
                break;
            case 2: ftpConnect.sendFileFromFTP(activeMode);
                break;
            case 3: students.getListByName();
                break;
            case 4: students.getStudentById();
                break;
            case 5: students.addStudent();
                break;
            case 6: students.removeStudent();
                break;
            case 7: return;
        }

            choosingAnAction(activeMode);
    }

    public static void main(String[] args) throws Exception {

        getFTPHostAndPort();

        ftpConnect = new FTPConnect(host, userName, password);

        choiceFTPMode();

        choosingAnAction(activeMode);
    }
}

