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

# Modul 2 –  CI/CD & DevOps

## Refleksi

### 1) Code quality issue yang saya perbaiki dan strategi perbaikannya
Saya memperbaiki **7 temuan code quality** yang terdeteksi sebagai *PMD violations*, yaitu: **(1)** `UseUtilityClass` pada `src/main/java/id/ac/ui/cs/advprog/eshop/EshopApplication.java` dan **(2)** `UnnecessaryImport` pada `src/main/java/id/ac/ui/cs/advprog/eshop/controller/ProductController.java`, serta **(3–7)** lima temuan `UnnecessaryModifier` pada `src/main/java/id/ac/ui/cs/advprog/eshop/service/ProductService.java` (method `create`, `findAll`, `findById`, `update`, dan `delete`). Untuk `UseUtilityClass`, PMD menganggap kelas tersebut sebagai *utility class* karena hanya berisi `main()`, padahal konteksnya adalah *entry point* Spring Boot. Karena itu, saya pilih pendekatan yang paling gampang dan minim risiko dengan nambahin *suppression* yang spesifik (`@SuppressWarnings("PMD.UseUtilityClass")`) daripada bikin *private constructor* hanya demi ngilangin violations. Untuk `UnnecessaryImport`, saya ngehapus *wildcard import* dan diganti jadi import eksplisit untuk annotations yang emang dipakai (misalnya `GetMapping`, `PostMapping`, `RequestMapping`, `PathVariable`, dan `ModelAttribute`) sehingga PMD gak lagi nemuin import yang gak terpakai. Sementara itu, untuk lima `UnnecessaryModifier`, saya hapus keyword `public` pada method interface karena method di interface emang *implicitly public* sehingga penghapusan ini ga ngubah perilaku aplikasi dan aman secara runtime.

### 2) Apakah workflow CI/CD saat ini sudah memenuhi definisi CI dan CD?
Menurut saya, implementasi saat ini **sudah memenuhi definisi Continuous Integration (CI)**, tapi **belum memenuhi Continuous Deployment (CD)**. Dari sisi CI, GitHub Actions sudah ngejalanin proses build dan test secara otomatis setiap ada `push` dan `pull_request`, termasuk mengeksekusi `./gradlew test` sehingga setiap perubahan kode langsung diverifikasi lewat pipeline yang konsisten. Selain itu, pipeline juga nambahin pemeriksaan kualitas dan keamanan yang berjalan otomatis, seperti PMD dan Scorecard scanning, yang ngebantu ngejaga kualitas perubahan sebelum digabungin nanti. Namun, dari sisi CD, workflow yang ada belum ngelakuin proses *deployment* atau *release* secara otomatis (misalnya ngebangun artifact/image lalu di-deploy ke server/PaaS, atau nge-publish release). Karena pipeline berhenti pada tahap testing dan *code scanning* tanpa langkah *delivery/deployment*, maka praktik yang berjalan saat ini lebih tepat disebut **CI + continuous code scanning**, bukan **continuous deployment**.