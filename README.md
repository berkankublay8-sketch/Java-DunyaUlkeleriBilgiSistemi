# 🌍 Dünya Ülkeleri Bilgi Sistemi

> Programlama Dilleri I — Dönem Sonu Projesi  
> Süleyman Demirel Üniversitesi | 2024-2025

---

## 📌 Proje Hakkında

Rest Countries API'sinden gerçek zamanlı veri çeken, bu veriyi yerel dosyaya kaydeden ve kullanıcı dostu bir konsol arayüzü üzerinden yöneten Java konsol uygulamasıdır.

---

## 🚀 Özellikler

| # | Özellik | Açıklama |
|---|---------|----------|
| 1 | Veri Çek | API'den tüm dünya ülkelerini çeker, dosyaya kaydeder |
| 2 | Listele | Hepsi, bölgeye göre veya minimum nüfusa göre filtrele |
| 3 | Güncelle | Ülke adı, başkent veya bölgeye göre arama yaparak güncelle |
| 4 | Sil | Ülke kaydını sil |
| 5 | Sırala | Nüfus (artan/azalan) veya alfabetik sıralama (Bubble Sort) |
| 6 | Ülke Ara | Parçalı arama ile ülke detaylarını görüntüle |
| 7 | Ülke Karşılaştır | İki ülkeyi nüfus, alan ve yoğunluk bazında karşılaştır |
| 8 | Nüfus Yoğunluğu | Kişi/km² hesapla |
| 9 | Ülke Oyunu | Rastgele ülke tahmin oyunu (15 hak) |
| 10 | İstatistikler | Bölge bazlı ülke sayısı, toplam ve ortalama nüfus |

---

## 🛠️ Kullanılan Teknolojiler

- **Java** — Ana programlama dili
- **Rest Countries API** — Veri kaynağı
- **HttpURLConnection** — API bağlantısı
- **BufferedReader / FileWriter** — Dosya işlemleri
- **ArrayList** — Dinamik veri yönetimi
- **Bubble Sort** — Sıralama algoritması
- **Random** — Ülke oyununda rastgele seçim

---

## 📁 Dosya Yapısı

```
📦 Dunya-Ulkeleri-Bilgi-Sistemi
 ┣ 📄 Odev_2321032015.java   ← Ana program
 ┗ 📄 ulkeler.txt            ← API'den çekilen veriler (otomatik oluşur)
```

---

## ▶️ Nasıl Çalıştırılır

```bash
# Derle
javac Odev_2321032015.java

# Çalıştır
java Odev_2321032015
```

Program açılınca önce **1 - Veri Çek** seçeneğini kullan, ardından diğer özellikleri kullanabilirsin.

---

## 📊 Veri Formatı

Her ülke `ulkeler.txt` dosyasına şu formatta kaydedilir:

```
Turkey | Ankara | 85000000 | 783562.0 | Asia
```

---

## 👤 Geliştirici

**Berkan Kublay**  
Süleyman Demirel Üniversitesi — Bilgisayar Mühendisliği
