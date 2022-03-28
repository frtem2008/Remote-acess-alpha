//клиент админа и клиента
import java.util.Scanner;

public class Client {
    public static Phone phone;//сокет
    public static Scanner s = new Scanner(System.in);//считывание команд админа
    public static boolean A;//админ или клиент?

    public static void main(String[] args) {
        A = s.nextBoolean();//считывание информации о компьютере(в будующем - 2 разных файла)
        s.nextLine();
        if (A) {
            System.out.println("Logged as administrator");
        } else {
            System.out.println("Logged as client");
        }
        connect();//подключение к серверу
    }

    public static void readRequest() {
        new Thread(() -> {
            while (true) {
                System.out.println(phone.readLine());
                //TODO выполнение команд
                //выполнение команд
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    public static void connect() {
        phone = new Phone("127.0.0.1", Server.PORT);//адрес сервера и порт
        System.out.println("Connected to server: ip address is " + phone.getIp());
        //отправка и получение информации
        //непрерывное чтение данных в отдельном потоке
        readRequest();
        while (true) {
            String request;
            //считывание команды админов
            //и результата(в будующем будет отправлятся автоматически) клиентов
            request = s.nextLine();
            //отправка в зависимости от типа клиента
            if (A) {
                phone.writeLine("A$" + request);
            } else {
                phone.writeLine("C$" + request);
            }
        }
    }

}
