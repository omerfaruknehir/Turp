# Turp 0.25.1

## Android sağlayıcı düzeltmesi

- Sağlayıcı uç nokta şablonu doğrulamasındaki geçersiz düzenli ifade nedeniyle Android'de oluşan istek hatalarını düzeltir.
- Bu tür yalnızca çalışma zamanında görülen hataların yayımdan önce yakalanması için Android düzenli ifade motorunda uç nokta genişletmesini gerçekten çalıştıran enstrümantasyon kapsamı ekler.
- Turp arka plandayken veya uygulama penceresi odakta değilken akış ve tamamlanma titreşimlerini durdurur; yanıt üretimi arka planda normal şekilde devam eder.
