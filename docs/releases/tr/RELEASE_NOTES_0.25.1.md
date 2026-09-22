# Turp 0.25.1

## Android sağlayıcı düzeltmesi

- Sağlayıcı uç nokta şablonu doğrulamasındaki geçersiz düzenli ifade nedeniyle Android'de oluşan istek hatalarını düzeltir.
- Bu tür yalnızca çalışma zamanında görülen hataların yayımdan önce yakalanması için Android düzenli ifade motorunda uç nokta genişletmesini gerçekten çalıştıran enstrümantasyon kapsamı ekler.
- Turp arka plandayken veya uygulama penceresi odakta değilken akış ve tamamlanma titreşimlerini durdurur; yanıt üretimi arka planda normal şekilde devam eder.
- Sudo modunda açıkça adı verilen fakat normalde bulunmayan işlevleri, araç çağrısını destekleyen modeller için sentetik sağlayıcı-yerel işlev tanımları olarak dinamik biçimde sunar. Model gerçek bir yerel araç çağrısı döndürebilir; Turp çağrıyı korur ve çalıştırmak yerine yapılandırılmış bir uygulanmadı hatası döndürür.
- Geliştirici “Kaynak” görünümünü sağlayıcı/model/durum meta verileri, sağlayıcıdan dönen reasoning, araç izleri, istek anlık görüntüsü, hatalar ve gerçek OkHttp isteğinden yakalanan isteğe bağlı gizli-bilgileri maskelenmiş son HTTP isteğiyle genişletir.
- Turp'un çekirdek/çalışma zamanı/araç/araştırma/hafıza/dosya/yürütme/üretilen içerik/Sudo/devam/sağlayıcı koruması/yardımcı model bileşenlerini kapsayan Geliştirici sistem istemi düzenleyicisini ekler. Somut istem metni doğrudan düzenlenebilir; bileşen bazında etkinleştirme/sıfırlama, tümünü sıfırlama, dinamik bileşenlerin son oluşturulan yerleşik metnini ve son sıralı sistem bağlamını görüntüleme desteklenir.
- Yüklü derleme GitHub'daki en son sürümden yeniyse “Trup daha güncel!?” sürprizini gösterir.
