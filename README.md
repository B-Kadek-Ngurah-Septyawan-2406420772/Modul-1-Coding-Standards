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

# Modul 3 – Maintainability & OO Principles

## Refleksi

### 1) Prinsip SOLID yg saya aplikasiin
- **SRP**
  - `ProductController.java` yg awalnya isi dua controller (product dan car) dlm satu file. Saya pisahin dgn buat file baru **`CarController.java`** dan mindahin endpoint car ke sana.  
  Hasilnya: `ProductController` fokus ngurus alur product aja, sedangkan `CarController` fokus ngurus alur car aja.
  - Saya juga misahin urusan “cara nentuin ID produk” dari service utama. Jadi `ProductServiceImpl` ga lagi nulis aturan ID sendiri, melainkan nyerahin ke **`ProductIdAssigner`** dan strategi ID.  
    Hasilnya: service lebih fokus ke proses utama (buat/update data), bukan detail penentuan ID.

- **OCP**
  - Saya nyiapin “tempat khusus” utk nambah aturan ID dgn bikin interface **`ProductIdGenerationStrategy`**, lalu nambahin strategi bawaan kayak **`MissingProductIdGenerationStrategy`** dan **`ExistingProductIdGenerationStrategy`**.
  - Karena ini, `ProductServiceImpl` cukup manggil `productIdAssigner.assign(product)`. Kalo suatu saat ada aturan ID baru, saya cukup nambahin kelas strategi baru, tanpa ngubah `ProductServiceImpl`.
  - Utk ngebuktiin ini, saya nambahin/nyesuaiin test di **`ProductServiceImplTest`** (misalnya skenario `testCreateProductCanUseAdditionalIdStrategyWithoutChangingServiceCode`) biar keliatan kalo aturan baru bisa ditambahin tanpa ngubah kode inti service.

- **LSP**
  - Sebelumnya `CarController` dibuat sebagai inheritance dari `ProductController` (`extends`), padahal controller car bukan “jenis” product controller dan behavior-nya ga bisa saling ngegantiin.
  - Saya ngehapus inheritance itu dan jadiin **`CarController.java`** berdiri sendiri dan pake `CarService`.  
    Hasilnya: struktur jadi jauh lebih masuk akal dan ngurangin resiko bug saat `ProductController` berubah.

- **ISP**
  - Saya mecah layanan product jadi dua bagian:
    - **`ProductQueryService`** utk kebutuhan baca data (mis. `findAll`, `findById`)
    - **`ProductMutationService`** utk kebutuhan ubah data (mis. `create`, `update`, `delete`)
  - Dengan ini, `ProductController` bisa make bagian yg sesuai (baca vs ubah) tanpa harus bergantung pada semua fungsi sekaligus.
  - Utk ngejaga compatibility-nya, **`ProductService`** tetep ada sebagai gabungan dari keduanya, jadi kode lain yang masih pake `ProductService` ga langsung rusak.

- **DIP**
  - Saya bikin service biar service ga langsung bergantung pada repository yg spesifik, tapi pada interface yg lebih umum, yaitu:
    - Product: **`ProductReadRepository`** dan **`ProductWriteRepository`**
    - Car: **`CarReadRepository`** dan **`CarWriteRepository`**
  - Lalu repository yg real tinggal ngikutin kontraknya itu:
    - **`ProductRepository`** nge-implement `ProductReadRepository` + `ProductWriteRepository`
    - **`CarRepository`** nge-implement  `CarReadRepository` + `CarWriteRepository`
  - Saya juga ganti cara masang dependency jadi lewat constructor biar lebih jelas dan testing-nya gampang, termasuk pada **`ProductServiceImpl`** dan **`CarServiceImpl`**.
  - Perubahan cara komponen saling terhubung ini juga tetep aman karena test controller kaya **`ProductControllerTest`** masih lolos setelah dependensinya dirapiin.

---

### 2) Keuntungan nerapin prinsip SOLID
- **Lebih rapi dan lebih mudah di-maintain (SRP):** Karena `CarController.java` dipisah dari `ProductController.java`, perubahan fitur car ga ngeganggu fitur product, dan sebaliknya.
- **Lebih mudah nambah aturan baru (OCP):** Karena udah ada `ProductIdAssigner` + `ProductIdGenerationStrategy` (termasuk `Missing...` dan `Existing...`), nambah aturan ID baru cukup nambah kelas baru aja, tanpa perlu ngebongkar `ProductServiceImpl`. Ini jauh lebih aman utk ngehindarin resiko bug/regression.
- **Struktur kode lebih masuk akal dan minim side-effect (LSP):** Ngehapus `extends` yg ga tepat bikin perubahan di `ProductController` ga “nyeret” `CarController` tanpa sengaja.
- **Bagian-bagian kode jadi lebih “pas” kebutuhannya (ISP):** Dengan `ProductQueryService` dan `ProductMutationService`, bagian yg cuma butuh baca data ga ikut-ikutan tergantung pada fungsi ubah/hapus.
- **Testing jadi lebih gampang dan lebih fleksibel kalo nanti ganti cara nyimpen (DIP):** Karena service bergantung pada interface (`ProductRead/WriteRepository` dan `CarRead/WriteRepository`) dan make constructor, unit test bakalan lebih gampang. Kalo nanti storage-nya berubah (misalkan dari in-memory ke database), kita bisa nambah/nuker implementasi repository tanpa ngubah main logic di service.

---

### 3) Kerugian kalo ga nerapin prinsip SOLID
- **Perubahan kecil mudah “ngerembet” (kalo ga nerapin SRP):** Kalo car dan product masih digabung di `ProductController.java`, perubahan fitur car bisa ngeganggu alur product.
- **Kode utama cepet “gendut” dan penuh branching (kalo ga nerapin OCP):** Kalo aturan ID tetap ditulis langsung di `ProductServiceImpl`, setiap aturan baru bakalan terus-terusan nambah kondisi baru di method yang sama. Lama kelamaan bakalan sulit dibaca dan rawan muncul bug.
- **Desain inheritance bisa sesat (kalo ga nerapin LSP):** `CarController extends ProductController` bisa bikin perubahan di product ikut ngaruh ke car secara ga terduga.
- **Ketergantungan jadi terlalu besar (kalo ga nerapin ISP):** Kalo semua fungsi dicampur dlm satu interface yg besar, bagian yg cuma butuh baca data tetep “terpaksa” bergantung pada fungsi ubah/hapus.
- **Testing bakalan lebih susah dan susah di-develop (tanpa DIP):** Kalo service bergantung langsung pada repository tertentu dan pemasangan dependency-nya ga jelas, testing bakalan jadi jauh lebih ribet, dan pergantian implementasi penyimpanan bisa maksa perubahan di banyak tempat.