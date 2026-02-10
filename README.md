# Modul 1 – Coding Standards

## Refleksi 1

Setelah mengimplementasikan fitur edit dan delete, saya mengecek kembali kode saya berdasarkan standar penulisan kode dari modul ini.

### Prinsip clean code yang diterapkan

- **Separation of concerns:**  
  Saya memisahkan tanggung jawab ke dalam:
    - `ProductController` (web layer)
    - `ProductService` (business logic)
    - `ProductRepository` (data access)  
      Ini membuat code menjadi lebih readable, testable, dan maintainable.

- **Single responsibility & small methods:**  
  Setiap endpoint hanya mengerjakan satu tugas (memuat form, memperbarui, menghapus) dan meneruskan request ke `service/repository`.

- **Routing dan penamaan method yang konsisten:**  
  Semua endpoint menggunakan base path `/product` dan menggunakan nama method yang jelas seperti `findById`, `update`, dan `delete`.

### Praktik secure coding yang diterapkan

- **Perenderan output yang aman:**  
  Daftar produk menggunakan Thymeleaf `th:text`, yang by default melakukan HTML escaping sehingga mengurangi risiko XSS.

- **Menghindari GET yang bersifat destruktif:**  
  Penghapusan dilakukan melalui `POST /product/delete/{productId}`. Kemudian, mapping GET hanya melakukan redirect dan tidak menghapus sehingga mencegah penghapusan tidak sengaja saat URL sedang di-visit.

- **Identifier dibuat di server side:**  
  `productId` dibuat di server side (UUID) untuk mengurangi ketergantungan pada input dari client.

### Kesalahan/perbaikan yang perlu dilakukan

- **Validasi belum ada:**  
  Saya seharusnya menambahkan annotation `jakarta.validation` (misalnya `@NotBlank` untuk nama, `@Min(0)` untuk kuantitas) dan menggunakan `@Valid` + `BindingResult` pada fitur create/edit agar input yang invalid tidak ter-save secara tidak sengaja.

- **Perbedaan huruf besar/kecil pada nama template:**  
  Controller me-return `createProduct/productList/editProduct`, tapi nama file template adalah `CreateProduct.html/ProductList.html/EditProduct.html`. Ini bisa berjalan di Windows, tapi bisa bermasalah di sistem yang case-sensitive (Linux). Saya perlu menyamakan nama view dan nama file.

- **Konsistensi dependensi UI:**  
  Versi Bootstrap CSS/JS dan atribut `integrity` perlu diperbaiki dan dibuat konsisten untuk menghindari masalah tampilan UI.

- **Desain endpoint delete:**  
  Untuk REST/keamanan yang lebih ketat, saya sebaiknya fully menghapus mapping `GET /product/delete/{productId}` dan meng-handle “not allowed method” dengan error page yang sesuai, bukan hanya redirect saja.

## Refleksi 2

1. Setelah menulis unit test, saya menjadi lebih pede karena fitur edit dan delete sekarang sudah diverifikasi secara otomatis sehingga regression akan lebih gampang terdeteksi. Tidak ada angka yang benar untuk jumlah unit test per kelas. Aturan yang baik adalah menulis tes secukupnya untuk mencakup perilaku penting dan edge case dari unit tersebut. Saya mencoba memastikan tes sudah “cukup” dengan mengecek: (a) main path dan failure path untuk setiap public method sudah di-test, (b) boundary/invalid inputs sudah di-test, dan (c) tes failed karena alasan yang tepat ketika saya secara sengaja merusak implementasinya. Code coverage membantu dengan menunjukkan line/branches mana yang di-execute oleh tes, tapi itu sebenarnya hanya sebuah indikator. Bahkan dengan 100% coverage pun, bug masih bisa ada (misalnya assertion salah, edge case terlewat, requirement keliru, concurrency issues, integration problems, atau path yang ter-cover tanpa verifikasi yang benar). Jadi, coverage itu perlu tapi gak cukup. Unit test yang baik juga harus memastikan hasil yang benar dan menyertakan negative scenario.

2. Jika saya membuat functional test suite lain dengan meng-copy kode setup yang sama (annotations yang sama, port/baseUrl logic, dan helper navigasi yang berulang), kodenya bakal menjadi kurang bersih karena meningkatkan duplication dan maintenance cost. Problem utama clean code-nya adalah: setup logic yang diduplikasi di banyak kelas tes (melanggar prinsip DRY), pengulangan “magic strings” untuk URL dan element locators, serta cohesion yang rendah (setiap test class meng-implement kode yang sama alih-alih fokus pada tujuan testing). Duplikasi ini bisa menurunkan code quality seiring berjalannya waktu karena setiap perubahan (misalnya base URL logic, page path, atau id HTML) harus di-update di banyak tempat. Perbaikannya: ekstrak common setup ke dalam kelas dasar abstrak (misalnya `BaseFunctionalTest` dengan `serverPort`, `testBaseUrl`, `baseUrl`, dan `setupTest()`), atau buat helper/page object kecil yang dapat dipakai ulang (misalnya `CreateProductPage` dengan method `open()`, `fillName()`, `fillQuantity()`, `submit()`, dan `ProductListPage` dengan `open()` serta `getRowCount()` / `containsProduct()`). Ini membuat tes tetap fokus pada apa yang ingin diverifikasi, memusatkan locator dan URL, mengurangi duplikasi, dan membuat functional test akan lebih mudah di-maintain di masa depan.
