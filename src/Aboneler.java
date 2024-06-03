
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Aboneler implements Serializable {
    private static final long serialVersionUID = 1L;

    private long lastUpdatedEpochMiliSeconds;
    private List<Boolean> abonelerListesi;
    private List<Boolean> girisYapanlarListesi;

    Aboneler(int aboneSayisi) {
        lastUpdatedEpochMiliSeconds = 0;
        abonelerListesi = new CopyOnWriteArrayList<>();
        girisYapanlarListesi = new CopyOnWriteArrayList<>();
        initializeLists(aboneSayisi);
    }

    private void initializeLists(int aboneSayisi) {
        for (int i = 0; i < aboneSayisi; i++) {
            abonelerListesi.add(false); // Varsayılan olarak abone değiller
            girisYapanlarListesi.add(false); // Varsayılan olarak giriş yapmamışlar
        }

        for (int i = 0; i < Math.min(100, abonelerListesi.size()); i++) {
            abonelerListesi.set(i, false);
        }

        for (int i = 0; i < Math.min(100, girisYapanlarListesi.size()); i++) {
            girisYapanlarListesi.set(i, false);
        }

        // System.out.println(abonelerListesi);
        // System.out.println(girisYapanlarListesi);

    }

    public long getEpochMiliSeconds() {
        return lastUpdatedEpochMiliSeconds;
    }

    public void setEpochMiliSeconds(long lastUpdatedEpochMiliSeconds) {
        this.lastUpdatedEpochMiliSeconds = lastUpdatedEpochMiliSeconds;
    }

    public List<Boolean> getAboneler() {
        return abonelerListesi;
    }

    public void setAboneler(List<Boolean> aboneler) {
        this.abonelerListesi = aboneler;
    }

    public List<Boolean> getGirisYapanlarListesi() {
        return girisYapanlarListesi;
    }

    public void setGirisYapanlarListesi(List<Boolean> girisYapanlarListesi) {
        this.girisYapanlarListesi = girisYapanlarListesi;
    }

    public synchronized void updateAbonelerList(Aboneler receivedAboneler) {
        this.abonelerListesi = receivedAboneler.getAboneler();
        this.girisYapanlarListesi = receivedAboneler.getGirisYapanlarListesi();
        updateLastUpdatedEpoch();
    }

    // Senkronize abone durumu güncelleme
    public synchronized void updateAboneDurumu(int aboneNum, boolean durum) {
        abonelerListesi.set(aboneNum - 1, durum);
        updateLastUpdatedEpoch();
    }

    public synchronized void updateGirisDurumu(int girisNum, boolean durum) {
        girisYapanlarListesi.set(girisNum - 1, durum);
        updateLastUpdatedEpoch();
    }

    // Yeni metod: Aboneler listesini ekrana yazdırma
    public synchronized void printAbonelerListesi() {
        // Senkronize aboneler listesini ekrana yazdırma
        System.out.println("Aboneler Listesi:");
        for (int i = 0; i < abonelerListesi.size(); i++) {
            System.out.println("Abone " + (i + 1) + ": " + abonelerListesi.get(i));
        }
    }

    private synchronized void updateLastUpdatedEpoch() {
        lastUpdatedEpochMiliSeconds = System.currentTimeMillis();
    }

}
