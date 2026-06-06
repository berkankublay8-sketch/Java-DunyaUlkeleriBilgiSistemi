import java.io.*;
import java.net.*;
import java.util.*;

public class Odev_2321032015 {

    static Scanner giris = new Scanner(System.in);
    static String dosyaAdi = "ulkeler.txt";

    public static void main(String[] args) {
        String secim = "";
        while (!secim.equals("0")) {
            System.out.println("\n+========================================+");
            System.out.println("|    DUNYA ULKELERI BILGI SISTEMI        |");
            System.out.println("+========================================+");
            System.out.println("|  1 - Veri Cek (API)                    |");
            System.out.println("|  2 - Listele                           |");
            System.out.println("|  3 - Guncelle                          |");
            System.out.println("|  4 - Sil                               |");
            System.out.println("|  5 - Sirala                            |");
            System.out.println("|  6 - Ulke Ara                          |");
            System.out.println("|  7 - Ulke Karsilastir                  |");
            System.out.println("|  8 - Nufus Yogunlugu                   |");
            System.out.println("|  9 - Ulke Oyunu                        |");
            System.out.println("| 10 - Istatistikler                     |");
            System.out.println("|  0 - Cikis                             |");
            System.out.println("+========================================+");
            System.out.print("  Seciminiz: ");
            secim = giris.nextLine().trim();

            if      (secim.equals("1"))  veriCek();
            else if (secim.equals("2"))  listeMenu();
            else if (secim.equals("3"))  islemYap("guncelle");
            else if (secim.equals("4"))  islemYap("sil");
            else if (secim.equals("5"))  siralaMenu();
            else if (secim.equals("6"))  ulkeAra();
            else if (secim.equals("7"))  karsilastir();
            else if (secim.equals("8"))  yogunlukHesapla();
            else if (secim.equals("9"))  ulkeOyunu();
            else if (secim.equals("10")) istatistikler();
            else if (secim.equals("0"))  System.out.println("\n  Gorusuruz!");
            else System.out.println("  [!] Gecersiz secim.");
        }
    }

    static void veriCek() {
        baslik("VERI CEK");
        try {
            System.out.println("  API baglaniyor...");
            URL url = new URL("https://restcountries.com/v3.1/all?fields=name,capital,population,area,region");
            HttpURLConnection bag = (HttpURLConnection) url.openConnection();
            bag.setConnectTimeout(5000);
            bag.setReadTimeout(5000);
            BufferedReader br = new BufferedReader(new InputStreamReader(bag.getInputStream(), "UTF-8"));
            String satir, veri = "";
            while ((satir = br.readLine()) != null)
                veri += satir;
            br.close();
            bag.disconnect();

            String[] ulkeler = veri.split("\\{\"name\"");
            FileWriter fw = new FileWriter(dosyaAdi, true);
            int sayac = 0;

            for (int i = 1; i < ulkeler.length; i++) {
                try {
                    String p = ulkeler[i];
                    String ulke    = veriAl(p, "\"common\":\"");
                    String baskent = veriAl(p, "\"capital\":[\"");
                    String nufus   = sayiAl(p, "\"population\":");
                    String alan    = sayiAl(p, "\"area\":");
                    String bolge   = veriAl(p, "\"region\":\"");

                    if (ulke != null && bolge != null) {
                        String bas = baskent == null ? "Yok" : baskent;
                        fw.write(ulke + " | " + bas + " | " + nufus + " | " + alan + " | " + bolge + "\n");
                        sayac++;
                    }
                } catch (Exception e) {}
            }
            fw.close();
            System.out.println("  [OK] " + sayac + " ulke kaydedildi -> " + dosyaAdi);
        } catch (Exception e) {
            System.out.println("  [!] API baglanti hatasi: " + e.getMessage());
        } finally {
            System.out.println("  Baglanti islemi tamamlandi.");
        }
    }

    static void listeMenu() {
        baslik("LISTELE");
        System.out.println("  1-Hepsini  2-Bolgeye Gore  3-Nufusa Gore (Min)");
        System.out.print("  Seciminiz: ");
        String s = giris.nextLine().trim();
        String aBolge = "";
        long mNufus = 0;

        if (s.equals("2")) {
            System.out.print("  Bolge (Europe,Asia,Africa...): ");
            aBolge = giris.nextLine().trim();
        }
        if (s.equals("3")) {
            System.out.print("  Min Nufus: ");
            try {
                mNufus = Long.parseLong(giris.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  [!] Gecersiz sayi!");
                return;
            }
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(dosyaAdi));
            String satir;
            int sayac = 0;
            System.out.println();
            tabloBaslik();

            while ((satir = br.readLine()) != null) {
                String[] p = satir.split("\\|");
                if (p.length < 5) continue;

                boolean goster = false;
                if      (s.equals("1")) goster = true;
                else if (s.equals("2") && p[4].trim().equalsIgnoreCase(aBolge)) goster = true;
                else if (s.equals("3")) {
                    try {
                        if (Long.parseLong(p[2].trim()) >= mNufus) goster = true;
                    } catch (Exception ex) {}
                }

                if (goster) { tabloSatir(p); sayac++; }
            }
            br.close();
            tabloAlt(sayac);
        } catch (Exception e) {
            System.out.println("  [!] Dosya okunamadi.");
        }
    }

    static void islemYap(String islemTipi) {
        if (islemTipi.equals("guncelle")) {
            baslik("GUNCELLE");
            System.out.println("  1-Ulke Adina Gore  2-Baskente Gore  3-Bolgeye Gore");
            System.out.print("  Seciminiz: ");
            String k = giris.nextLine().trim();
            if      (k.equals("1")) guncelle("ulke");
            else if (k.equals("2")) guncelle("baskent");
            else if (k.equals("3")) guncelle("bolge");
            else System.out.println("  [!] Gecersiz secim!");
        } else {
            sil();
        }
    }

    static void guncelle(String kriter) {
        System.out.print("  Aranacak deger: ");
        String aranan = giris.nextLine().trim();
        ArrayList<String> liste = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(dosyaAdi));
            String satir;

            while ((satir = br.readLine()) != null) {
                String[] p = satir.split("\\|");
                if (p.length < 5) { liste.add(satir); continue; }

                boolean eslesti = false;
                if      (kriter.equals("ulke")    && p[0].trim().equalsIgnoreCase(aranan)) eslesti = true;
                else if (kriter.equals("baskent") && p[1].trim().equalsIgnoreCase(aranan)) eslesti = true;
                else if (kriter.equals("bolge")   && p[4].trim().equalsIgnoreCase(aranan)) eslesti = true;

                if (eslesti) {
                    System.out.println("  Bulunan: " + satir);
                    System.out.print("  Guncellensin mi? (e/h): ");
                    if (giris.nextLine().trim().equalsIgnoreCase("e")) {
                        System.out.println("  1-Ulke Adi  2-Baskent  3-Nufus  4-Alan  5-Bolge");
                        System.out.print("  Seciminiz: ");
                        String alan = giris.nextLine().trim();
                        System.out.print("  Yeni deger: ");
                        String yeni = giris.nextLine().trim();

                        if      (alan.equals("1")) p[0] = yeni;
                        else if (alan.equals("2")) p[1] = yeni;
                        else if (alan.equals("3")) p[2] = yeni;
                        else if (alan.equals("4")) p[3] = yeni;
                        else if (alan.equals("5")) p[4] = yeni;

                        satir = p[0] + " | " + p[1] + " | " + p[2] + " | " + p[3] + " | " + p[4];
                        System.out.println("  [OK] Kayit guncellendi.");
                    }
                }
                liste.add(satir);
            }
            br.close();

            FileWriter fw = new FileWriter(dosyaAdi, false);
            for (int i = 0; i < liste.size(); i++) fw.write(liste.get(i) + "\n");
            fw.close();
        } catch (Exception e) {
            System.out.println("  [!] Hata: " + e.getMessage());
        }
    }

    static void sil() {
        baslik("SIL");
        System.out.print("  Silinecek ulke adi: ");
        String aranan = giris.nextLine().trim();
        ArrayList<String> liste = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(dosyaAdi));
            String satir;

            while ((satir = br.readLine()) != null) {
                String[] p = satir.split("\\|");
                if (p.length > 0 && p[0].trim().equalsIgnoreCase(aranan)) {
                    System.out.println("  Bulunan: " + satir);
                    System.out.print("  Silinsin mi? (e/h): ");
                    if (giris.nextLine().trim().equalsIgnoreCase("e")) {
                        System.out.println("  [OK] Kayit silindi.");
                        continue;
                    }
                }
                liste.add(satir);
            }
            br.close();

            FileWriter fw = new FileWriter(dosyaAdi, false);
            for (int i = 0; i < liste.size(); i++) fw.write(liste.get(i) + "\n");
            fw.close();
        } catch (Exception e) {
            System.out.println("  [!] Hata: " + e.getMessage());
        }
    }

    static void siralaMenu() {
        baslik("SIRALA");
        System.out.println("  1-Nufus Artan  2-Nufus Azalan  3-Ulke Adi (A-Z)");
        System.out.print("  Seciminiz: ");
        String s = giris.nextLine().trim();
        ArrayList<String> liste = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(dosyaAdi));
            String satir;
            while ((satir = br.readLine()) != null) liste.add(satir);
            br.close();

            int n = liste.size();
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    String[] a = liste.get(j).split("\\|");
                    String[] b = liste.get(j + 1).split("\\|");
                    boolean deg = false;

                    try {
                        if      (s.equals("1")) deg = Long.parseLong(a[2].trim()) > Long.parseLong(b[2].trim());
                        else if (s.equals("2")) deg = Long.parseLong(a[2].trim()) < Long.parseLong(b[2].trim());
                        else if (s.equals("3")) deg = a[0].trim().compareToIgnoreCase(b[0].trim()) > 0;
                    } catch (Exception ex) {}

                    if (deg) {
                        String t = liste.get(j);
                        liste.set(j, liste.get(j + 1));
                        liste.set(j + 1, t);
                    }
                }
            }

            System.out.println();
            tabloBaslik();
            for (int i = 0; i < liste.size(); i++) {
                String[] p = liste.get(i).split("\\|");
                if (p.length >= 5) tabloSatir(p);
            }
            tabloAlt(liste.size());
        } catch (Exception e) {
            System.out.println("  [!] Dosya okunamadi.");
        }
    }

    static void kutuGoster(String[] p) {
        try {
            long nufus = Long.parseLong(p[2].trim());
            double alan = Double.parseDouble(p[3].trim());
            double yog = alan > 0 ? nufus / alan : 0;

            System.out.println("\n  +" + cizgi(40) + "+");
            System.out.println("  | " + p[0].trim().toUpperCase());
            System.out.println("  +" + cizgi(40) + "+");
            System.out.println("  | Baskent  : " + p[1].trim());
            System.out.println("  | Bolge    : " + p[4].trim());
            System.out.println("  | Nufus    : " + nufus);
            System.out.println("  | Alan     : " + alan + " km2");
            System.out.println("  | Yogunluk : " + yog + " k/km2");
            System.out.println("  +" + cizgi(40) + "+");
        } catch (NumberFormatException e) {}
    }

    static void ulkeAra() {
        baslik("ULKE ARA");
        System.out.print("  Aranacak ulke adi: ");
        String aranan = giris.nextLine().trim().toLowerCase();

        try {
            BufferedReader br = new BufferedReader(new FileReader(dosyaAdi));
            String satir;
            int sayac = 0;

            while ((satir = br.readLine()) != null) {
                String[] p = satir.split("\\|");
                if (p.length >= 5 && p[0].toLowerCase().indexOf(aranan) != -1) {
                    kutuGoster(p);
                    sayac++;
                }
            }
            br.close();
            System.out.println(sayac == 0 ? "  [!] Ulke bulunamadi!" : "\n  Toplam " + sayac + " sonuc.");
        } catch (Exception e) {
            System.out.println("  [!] Dosya okunamadi.");
        }
    }

    static void karsilastir() {
        baslik("ULKE KARSILASTIR");
        System.out.print("  1. ulke adi: ");
        String ad1 = giris.nextLine().trim().toLowerCase();
        System.out.print("  2. ulke adi: ");
        String ad2 = giris.nextLine().trim().toLowerCase();

        String[] u1 = null, u2 = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(dosyaAdi));
            String satir;

            while ((satir = br.readLine()) != null) {
                String[] p = satir.split("\\|");
                if (p.length < 5) continue;
                if (p[0].toLowerCase().indexOf(ad1) != -1) u1 = p;
                if (p[0].toLowerCase().indexOf(ad2) != -1) u2 = p;
            }
            br.close();
        } catch (Exception e) {
            System.out.println("  [!] Dosya okunamadi.");
            return;
        }

        if (u1 == null || u2 == null) {
            System.out.println("  [!] Ulke bulunamadi!");
            return;
        }

        try {
            long n1 = Long.parseLong(u1[2].trim());
            long n2 = Long.parseLong(u2[2].trim());
            double a1 = Double.parseDouble(u1[3].trim());
            double a2 = Double.parseDouble(u2[3].trim());
            double y1 = a1 > 0 ? n1 / a1 : 0;
            double y2 = a2 > 0 ? n2 / a2 : 0;

            System.out.println("\n  +" + cizgi(50) + "+");
            System.out.println("  | " + u1[0].trim().toUpperCase() + " | " + u2[0].trim().toUpperCase());
            System.out.println("  +" + cizgi(50) + "+");
            System.out.println("  | Baskent   : " + u1[1].trim() + " | Baskent   : " + u2[1].trim());
            System.out.println("  | Bolge     : " + u1[4].trim() + " | Bolge     : " + u2[4].trim());
            System.out.println("  | Nufus     : " + n1 + " | Nufus     : " + n2);
            System.out.println("  | Alan(km2) : " + a1 + " | Alan(km2) : " + a2);
            System.out.println("  | Yogunluk  : " + y1 + " | Yogunluk  : " + y2);
            System.out.println("  +" + cizgi(50) + "+");

            long nufusFark = n1 - n2;
            if (nufusFark < 0) nufusFark = nufusFark * -1;
            long alanFark = (long)(a1 - a2);
            if (alanFark < 0) alanFark = alanFark * -1;

            System.out.println("  Daha kalabalik : " + (n1 > n2 ? u1[0].trim() : u2[0].trim()) + " (" + nufusFark + " kisi)");
            System.out.println("  Daha genis     : " + (a1 > a2 ? u1[0].trim() : u2[0].trim()) + " (" + alanFark + " km2)");
            System.out.println("  Daha yogun     : " + (y1 > y2 ? u1[0].trim() : u2[0].trim()));
        } catch (Exception e) {
            System.out.println("  [!] Veri hatasi!");
        }
    }

    static void yogunlukHesapla() {
        baslik("NUFUS YOGUNLUGU");
        System.out.print("  Ulke adi: ");
        String aranan = giris.nextLine().trim().toLowerCase();

        try {
            BufferedReader br = new BufferedReader(new FileReader(dosyaAdi));
            String satir;
            boolean bulundu = false;

            while ((satir = br.readLine()) != null) {
                String[] p = satir.split("\\|");
                if (p.length >= 5 && p[0].toLowerCase().indexOf(aranan) != -1) {
                    kutuGoster(p);
                    bulundu = true;
                }
            }
            br.close();
            if (!bulundu) System.out.println("  [!] Ulke bulunamadi!");
        } catch (Exception e) {
            System.out.println("  [!] Dosya okunamadi.");
        }
    }

    static void ulkeOyunu() {
        baslik("ULKE OYUNU");
        ArrayList<String> ulkelerListe = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(dosyaAdi));
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] p = satir.split("\\|");
                if (p.length >= 5) ulkelerListe.add(satir);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("  [!] Dosya okunamadi.");
            return;
        }

        if (ulkelerListe.size() == 0) {
            System.out.println("  [!] Veri yok!");
            return;
        }

        Random rnd = new Random();
        String[] seciliUlke = ulkelerListe.get(rnd.nextInt(ulkelerListe.size())).split("\\|");
        String ulkeAdi = seciliUlke[0].trim();
        String baskent = seciliUlke[1].trim();
        String nufus   = seciliUlke[2].trim();
        String alan    = seciliUlke[3].trim();
        String ulkeAdiKucuk = ulkeAdi.toLowerCase();

        System.out.println("  Tahmin et oyununa hosgeldin!");
        System.out.println("  Ulkenin ilk ve son harfi verildi.");
        System.out.println("  Nufus ve Alan bilgisi: " + nufus + " kisi, " + alan + " km2");
        System.out.println("  15 hakkin var!\n");

        char[] tahminEdilen = new char[ulkeAdiKucuk.length()];
        for (int i = 0; i < ulkeAdiKucuk.length(); i++) {
            if (i == 0 || i == ulkeAdiKucuk.length() - 1) {
                tahminEdilen[i] = ulkeAdiKucuk.charAt(i);
            } else if (ulkeAdiKucuk.charAt(i) == ' ') {
                tahminEdilen[i] = ' ';
            } else {
                tahminEdilen[i] = '*';
            }
        }

        int hak = 15;
        boolean bildi = false;
        ArrayList<Character> yanlisHarfler = new ArrayList<Character>();

        while (hak > 0 && !bildi) {
            String gosterim = "";
            for (int i = 0; i < tahminEdilen.length; i++) {
                gosterim = gosterim + tahminEdilen[i];
            }

            System.out.println("  " + gosterim);
            System.out.print("  Harf tahmin et (" + hak + " hak): ");
            String tahmin = giris.nextLine().trim().toLowerCase();

            if (tahmin.length() == 1) {
                char harf = tahmin.charAt(0);

                if (yanlisHarfler.indexOf(harf) != -1) {
                    System.out.println("  Bu harfi zaten tahmin ettin!");
                    continue;
                }

                if (ulkeAdiKucuk.indexOf(harf) != -1) {
                    for (int i = 0; i < ulkeAdiKucuk.length(); i++) {
                        if (ulkeAdiKucuk.charAt(i) == harf) {
                            tahminEdilen[i] = harf;
                        }
                    }
                    System.out.println("  Dogru!");

                    boolean tamam = true;
                    for (int i = 0; i < tahminEdilen.length; i++) {
                        if (tahminEdilen[i] == '*') { tamam = false; break; }
                    }

                    if (tamam) {
                        System.out.println("  Tebrikler! Ulke: " + ulkeAdi);
                        System.out.println("  Baskent: " + baskent);
                        bildi = true;
                    }
                } else {
                    hak--;
                    yanlisHarfler.add(harf);
                    System.out.println("  Yanlis! " + hak + " hakkin kaldi.");
                    if (hak == 0) {
                        System.out.println("  Maalesef bilemedin! Dogru cevap: " + ulkeAdi);
                        System.out.println("  Baskent: " + baskent);
                    }
                }
            } else if (tahmin.equalsIgnoreCase(ulkeAdi)) {
                System.out.println("  Tebrikler! Dogru cevap!");
                System.out.println("  Ulke: " + ulkeAdi + " | Baskent: " + baskent);
                bildi = true;
            } else {
                hak--;
                System.out.println("  Yanlis! " + hak + " hakkin kaldi.");
                if (hak == 0) {
                    System.out.println("  Maalesef bilemedin! Dogru cevap: " + ulkeAdi);
                    System.out.println("  Baskent: " + baskent);
                }
            }
            System.out.println();
        }
    }

    static void istatistikler() {
        baslik("ISTATISTIKLER");

        try {
            BufferedReader br = new BufferedReader(new FileReader(dosyaAdi));
            String satir;

            int toplam = 0;
            long maxNufus = -1, minNufus = 9999999999999L;
            double maxAlan = -1;
            String enKalabalik = "", enIssiz = "", enGenis = "";

            ArrayList<String>  bolgeler = new ArrayList<String>();
            ArrayList<Integer> ulkeSay  = new ArrayList<Integer>();
            ArrayList<Long>    topNufus = new ArrayList<Long>();

            while ((satir = br.readLine()) != null) {
                String[] p = satir.split("\\|");
                if (p.length < 5) continue;
                toplam++;

                try {
                    long nufus   = Long.parseLong(p[2].trim());
                    double alan  = Double.parseDouble(p[3].trim());
                    String bolge = p[4].trim();

                    if (nufus > maxNufus)  { maxNufus = nufus; enKalabalik = p[0].trim(); }
                    if (nufus < minNufus)  { minNufus  = nufus; enIssiz     = p[0].trim(); }
                    if (alan  > maxAlan)   { maxAlan   = alan;  enGenis     = p[0].trim(); }

                    if (bolgeler.indexOf(bolge) != -1) {
                        int idx = bolgeler.indexOf(bolge);
                        ulkeSay.set(idx, ulkeSay.get(idx) + 1);
                        topNufus.set(idx, topNufus.get(idx) + nufus);
                    } else {
                        bolgeler.add(bolge);
                        ulkeSay.add(1);
                        topNufus.add(nufus);
                    }
                } catch (Exception e) {}
            }
            br.close();

            System.out.println("  Toplam ulke     : " + toplam);
            System.out.println("  En Kalabalik    : " + enKalabalik + " (" + maxNufus + ")");
            System.out.println("  En Az Nufuslu   : " + enIssiz     + " (" + minNufus  + ")");
            System.out.println("  En Genis        : " + enGenis     + " (" + (long)maxAlan + " km2)");
            System.out.println();
            System.out.println("  BOLGE               ULKE SAYISI   TOPLAM NUFUS        ORT. NUFUS");
            System.out.println("  " + cizgi(70));

            for (int i = 0; i < bolgeler.size(); i++) {
                long ort = ulkeSay.get(i) > 0 ? topNufus.get(i) / ulkeSay.get(i) : 0;
                System.out.println("  " + bolgeler.get(i) + " | " + ulkeSay.get(i) + " | " + topNufus.get(i) + " | " + ort);
            }
            System.out.println("  " + cizgi(70));

        } catch (Exception e) {
            System.out.println("  [!] Dosya okunamadi.");
        }
    }

    static String veriAl(String veri, String aranacak) {
        int i = veri.indexOf(aranacak);
        if (i == -1) return null;
        int bitis = veri.indexOf("\"", i + aranacak.length());
        return bitis == -1 ? null : veri.substring(i + aranacak.length(), bitis);
    }

    static String sayiAl(String veri, String aranacak) {
        int i = veri.indexOf(aranacak);
        if (i == -1) return "0";
        return veri.substring(i + aranacak.length()).split(",")[0].replace("}", "").trim();
    }

    static void baslik(String b) {
        System.out.println("\n  --- " + b + " ---");
    }

    static void tabloBaslik() {
        System.out.println("  ULKE                   BASKENT            NUFUS         ALAN(km2)      BOLGE");
        System.out.println("  " + cizgi(85));
    }

    static void tabloSatir(String[] p) {
        String ad = p[0].trim().length() > 24 ? p[0].trim().substring(0, 21) + "..." : p[0].trim();
        String bs = p[1].trim().length() > 18 ? p[1].trim().substring(0, 15) + "..." : p[1].trim();
        System.out.println("  " + ad + "     " + bs + "     " + p[2].trim() + "     " + p[3].trim() + "     " + p[4].trim());
    }

    static void tabloAlt(int n) {
        System.out.println("  " + cizgi(85));
        System.out.println("  Toplam: " + n + " kayit.");
    }

    static String cizgi(int n) {
        String c = "";
        for (int i = 0; i < n; i++) c += "-";
        return c;
    }
}
