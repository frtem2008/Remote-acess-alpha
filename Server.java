//сервер для удалённого выполнения команд админами на компах клиентов
//просто демонстрационная версия
//клиенты уже переписываются на питон
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


public class Server {
    public static int PORT = 8080;//порт сервера
    public static ArrayList<Phone> phones = new ArrayList<>();//все сокеты
    public static ArrayList<Phone> phonesA = new ArrayList<>();//админские сокеты
    public static ArrayList<Phone> phonesC = new ArrayList<>();//клиентские сокеты

    public static ArrayList<String> ips = new ArrayList<>();//все ip адреса
    public static ArrayList<String> ipsA = new ArrayList<>();//ip адреса админов
    public static ArrayList<String> ipsC = new ArrayList<>();//ip адреса клиентов

    public static void main(String[] args) throws IOException {
        server();//запуск сервера
    }


    //добавление с перезаписью для типа String

    public static void addReplace(ArrayList<String> where, String what) {
        if (where.contains(what)) {
            where.add(where.indexOf(what), what);
        } else {
            where.add(what);
        }
    }

    //добавление с перезаписью для типа Phone
    public static void addReplace(ArrayList<Phone> where, Phone what) {
        if (where.contains(what)) {
            where.add(where.indexOf(what), what);
        } else {
            where.add(what);
        }
    }


    public static void server() {
        try (ServerSocket server = new ServerSocket(PORT)) {//запуск сервера
            System.out.println("Server started");
            while (true) {
                Phone phone = new Phone(server);//создание сокета сервера и ожидание присоединения клиентов
                new Thread(() -> {//каждый клиент в отдельном потоке
                    System.out.println("Client connected: ip address is " + phone.getIp());
                    addReplace(phones, phone);//запись о сокетах
                    addReplace(ips, phone.getIp());//запись об ip адресах

                    String data, root, ip, command, args, success;//переменные для вывода и отправки информации

                    while (true) {
                        //формат данных для админа и для клиента
                        //data = A$ip на кого$command$args - админ
                        //data = C$ip мой $command$args$результат - клиент
                        data = phone.readLine();//считывание данных
                        System.out.println("Data: " + data);

                        root = data.split("\\$")[0]; //информация об отправителе(админ/клиент)
                        if (root.trim().equals("A")) {
                            //добавление информации об админе
                            String aip = phone.getIp();
                            addReplace(phonesA, phone);
                            addReplace(ipsA, aip);
                            //получение информации о клиенте
                            ip = data.split("\\$")[1];//ip клиента, исполняющего команду
                            System.out.println("Ip to send: " + ip);
                            command = data.split("\\$")[2];//сама команда
                            System.out.println("Command to send: " + command);
                            args = data.split("\\$")[3];//аргументы команды
                            System.out.println("Args to send: " + args);
                            for (int i = 0; i < ips.size(); i++) {
                                System.out.println(i + " in ips " + ips.get(i));
                            }

                            int index = ips.indexOf(ip);//отправка информации нужному клиенту
                            phones.get(index).writeLine(command + "$" + args);

                        } else if (root.trim().equals("C")) {
                            //добавление информации о клиенте
                            ip = data.split("\\$")[1];//его ip адрес
                            addReplace(ipsC, ip);//запись об ip адресе клиента
                            addReplace(phonesC, phone);//запись о сокете клиента
                            System.out.println("Ip to send: " + ip);

                            command = data.split("\\$")[2];//команда, которая была выполнена
                            System.out.println("Command to send: " + command);

                            args = data.split("\\$")[3];//аргументы команды
                            System.out.println("Args to send: " + args);

                            success = data.split("\\$")[4];//успех выполнения (success/no success)
                            System.out.println("Success to send: " + success);

                            String response = ip + "$" + command + "$" + args + "$" + success;//формирование ответного запроса
                            for (int i = 0; i < phonesA.size(); i++) {
                                System.out.println(phonesA.get(i).getIp());
                            }
                            //отправка данных о клиенте всем админам
                            for (int i = 0; i < phonesA.size(); i++) {
                                phonesA.get(i).writeLine(response);
                            }
                        }

                        try {
                            Thread.sleep(10);//пауза в запросах
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
    }
}


