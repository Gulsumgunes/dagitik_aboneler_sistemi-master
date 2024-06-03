
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server2 {
    private static final int PORT = 5002;
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_ID = 2;

    private static Aboneler aboneler;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(SERVER_HOST));
        System.out.println("Server2 is running on port " + PORT);
        aboneler = new Aboneler(100);
        aboneler.setAboneler(new ArrayList<>()); // Aboneler listesini başlat

        // Server1 ile aynı şekilde PingThread'leri başlat
        new PingThread(SERVER_ID, "localhost", 5001).start();
        new PingThread(SERVER_ID, "localhost", 5003).start();

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket, SERVER_ID).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private int serverId;

        public ClientHandler(Socket socket, int serverId) {
            this.clientSocket = socket;
            this.serverId = serverId;
        }

        public void run() {
            BufferedReader in = null;
            String message = null;
            PrintWriter out = null;

            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                message = in.readLine();

                if (message != null) {
                    String[] parts = message.split(" ");
                    String action = parts[0];

                    switch (action) {
                        case "ABONOL":
                            int aboneNum = Integer.parseInt(parts[1]);
                            String abonolResponse = aboneOl(aboneNum);
                            out.println(abonolResponse);
                            break;
                        case "ABONPTAL":
                            int iptalNum = Integer.parseInt(parts[1]);
                            String iptalResponse = aboneIptal(iptalNum);
                            out.println(iptalResponse);
                            break;
                        case "GIRIS":
                            int girisNum = Integer.parseInt(parts[2]);
                            String girisResponse = girisYap(girisNum);
                            out.println(girisResponse);
                            break;
                        case "CIKIS":
                            int cikisNum = Integer.parseInt(parts[2]);
                            String cikisResponse = cikisYap(cikisNum);
                            out.println(cikisResponse);
                            break;
                        case "SERILESTIRILMIS_NESNE":
                            try {
                                ObjectInputStream objectInputStream = new ObjectInputStream(
                                        clientSocket.getInputStream());
                                Aboneler receivedAboneler = (Aboneler) objectInputStream.readObject();
                                String serilestirilmisResponse = handleSerilestirilmisNesne(receivedAboneler);
                                out.println(serilestirilmisResponse);
                            } catch (ClassNotFoundException | IOException e) {
                                out.println("99 HATA");
                            }
                            break;
                        default:
                            out.println("99 HATA");
                            break;
                    }
                } else {
                    out.println("99 HATA");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Received message on Server" + serverId + "(" + currentThread()
                    + ") from client: " + message);
        }

        private String aboneOl(int aboneNum) {
            if (aboneler.getAboneler().get(aboneNum - 1)) {
                System.out.println("Abone " + aboneNum + " zaten abone durumunda.");
                return "50 HATA";
            }

            if (isAbonolAccepted()) {
                aboneler.updateAboneDurumu(aboneNum, true);
                notifyAbonelerGuncellemesi();
                aboneler.printAbonelerListesi(); // Aboneler listesini detaylı olarak yazdır
                return "55 TAMM";
            } else {
                return "99 HATA";
            }
        }

        private String aboneIptal(int iptalNum) {
            if (!aboneler.getAboneler().get(iptalNum - 1)) {
                System.out.println("Abone " + iptalNum + " zaten abone değil durumunda.");
                return "50 HATA"; // Zaten abone değil durumu
            }

            if (isAboneIptalAccepted()) {
                aboneler.updateAboneDurumu(iptalNum, false);
                notifyAbonelerGuncellemesi();
                aboneler.printAbonelerListesi(); // Aboneler listesini detaylı olarak yazdır
                return "55 TAMM";
            } else {
                return "99 HATA";
            }
        }

        private String girisYap(int girisNum) {
            if (!aboneler.getAboneler().get(girisNum - 1)) {
                System.out.println("Abone " + girisNum + " abone değil durumunda.");
                return "50 HATA"; // Abone değil durumu
            }

            if (isGirisYapAccepted()) {
                aboneler.updateGirisDurumu(girisNum, true);
                notifyAbonelerGuncellemesi();
                aboneler.printAbonelerListesi(); // Aboneler listesini detaylı olarak yazdır
                return "55 TAMM";
            } else {
                return "99 HATA";
            }
        }

        private String cikisYap(int cikisNum) {
            if (cikisNum <= 0 || cikisNum > aboneler.getGirisYapanlarListesi().size()) {
                System.out.println("Geçersiz indeks: " + cikisNum);
                return "50 HATA"; // Geçersiz indeks
            }

            if (!aboneler.getGirisYapanlarListesi().get(cikisNum - 1)) {
                System.out.println("Giriş yapmamış abone " + cikisNum);
                return "50 HATA"; // Giriş yapmamış olma durumu
            }

            if (isCikisYapAccepted()) {
                aboneler.updateGirisDurumu(cikisNum, false);
                notifyAbonelerGuncellemesi();
                aboneler.printAbonelerListesi(); // Aboneler listesini detaylı olarak yazdır
                return "55 TAMM";
            } else {
                return "99 HATA";
            }
        }

        // ...
        private String handleSerilestirilmisNesne(Aboneler receivedAboneler) {
            aboneler.updateAbonelerList(receivedAboneler);
            notifyAbonelerGuncellemesi();
            aboneler.printAbonelerListesi(); // Aboneler listesini detaylı olarak yazdır
            return "55 TAMM";
        }
        // ...

        private boolean isAbonolAccepted() {
            return true;
        }

        private boolean isAboneIptalAccepted() {
            return true;
        }

        private boolean isGirisYapAccepted() {
            return true;
        }

        private boolean isCikisYapAccepted() {
            return true;
        }

        private void notifyAbonelerGuncellemesi() {
            for (int i = 1; i <= 3; i++) {
                if (i != serverId) {
                    try (Socket notifySocket = new Socket("localhost", 5000 + i);
                            ObjectOutputStream outputStream = new ObjectOutputStream(notifySocket.getOutputStream())) {

                        outputStream.writeObject(aboneler);
                        System.out.println();
                        System.out.println("Server" + serverId + " tarafından güncellenmiş aboneler listesi Server" + i
                                + " sunucusuna gönderildi.");

                    } catch (IOException e) {
                        System.out.println("Server" + serverId + " tarafından güncellenmiş aboneler listesi Server" + i
                                + " sunucusuna gönderilemedi.");
                    }
                }
            }
        }
    }

    private static class PingThread extends Thread {
        private String host;
        private int port;

        public PingThread(int serverId, String host, int port) {
            this.host = host;
            this.port = port;
        }

        public void run() {
            try {
                while (true) {
                    try (Socket socket = new Socket(host, port)) {
                        System.out.println("Pinged " + host + " on port " + port);
                    } catch (IOException e) {
                        System.out.println("Ping to " + host + " on port " + port + " failed, retrying...");
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ie) {
                        System.out.println("Ping thread interrupted: " + ie.getMessage());
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
