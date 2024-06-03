
import java.io.*;

import java.net.Socket;

public class Client {
    private static final String SERVER1_HOST = "localhost";
    private static final int SERVER1_PORT = 5001;
    private static final String SERVER2_HOST = "localhost";
    private static final int SERVER2_PORT = 5002;
    private static final String SERVER3_HOST = "localhost";
    private static final int SERVER3_PORT = 5003;

    public static void main(String[] args) throws IOException {
        Aboneler aboneler = new Aboneler(20);

        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONOL 1", aboneler);
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "ABONPTAL 2", aboneler);
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "GIRIS ISTMC 33", aboneler);
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "CIKIS ISTMC 99", aboneler);
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONOL 1", aboneler);
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "ABONPTAL 2", aboneler);
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "GIRIS ISTMC 33", aboneler);
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "CIKIS ISTMC 99", aboneler);
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONOL 1", aboneler);
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "ABONPTAL 2", aboneler);
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "GIRIS ISTMC 33", aboneler);
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "CIKIS ISTMC 99", aboneler);
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "ABONOL 1", aboneler);
        sendAndReceiveMessage(SERVER2_HOST, SERVER2_PORT, "ABONPTAL 2", aboneler);
        sendAndReceiveMessage(SERVER3_HOST, SERVER3_PORT, "GIRIS ISTMC 33", aboneler);
        sendAndReceiveMessage(SERVER1_HOST, SERVER1_PORT, "CIKIS ISTMC 99", aboneler);

    }

    private static void sendSerializedObject(Socket socket, Aboneler aboneler) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        // Güncellenmiş Aboneler nesnesini gönder
        objectOutputStream.writeObject(aboneler);
        objectOutputStream.flush(); // ObjectOutputStream'ı sıfırla ve tamponu temizle
    }

    private static void receiveResponse(BufferedReader in) throws IOException {
        // Sunucudan cevabı oku ve uygun işlemleri yap
        String response = in.readLine();
        System.out.println("Response from server: " + response);

        // Diğer işlemleri gerçekleştirin...
    }

    private static void sendAndReceiveMessage(String host, int port, String message, Aboneler aboneler) {
        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Sunucuya mesajı gönder
            out.println(message);

            // Aboneler nesnesini gönder
            sendSerializedObject(socket, aboneler);

            // Sunucudan cevabı al
            receiveResponse(in);

            // Girilen işleme göre aboneler listesini güncelle
            if (message.startsWith("ABONOL")) {
                int aboneNum = Integer.parseInt(message.split(" ")[1]);
                aboneler.updateAboneDurumu(aboneNum, true);
            } else if (message.startsWith("ABONPTAL")) {
                int iptalNum = Integer.parseInt(message.split(" ")[1]);
                aboneler.updateAboneDurumu(iptalNum, false);
            } else if (message.startsWith("GIRIS")) {
                int girisNum = Integer.parseInt(message.split(" ")[2]);
                aboneler.updateGirisDurumu(girisNum, true);
            } else if (message.startsWith("CIKIS")) {
                int cikisNum = Integer.parseInt(message.split(" ")[2]);
                aboneler.updateGirisDurumu(cikisNum, false);
            }

            aboneler.printAbonelerListesi();

        } catch (IOException e) {
            System.out.println("Error connecting to server on port " + port + ": " + e.getMessage());
        }
    }

}
