# Turp 0.25.0

## Sağlayıcılar, görüntüleme ve iyileştirmeler

- Bir bağlantının iletişim protokolünü sağlayıcı profilinden ayırır; böylece OpenRouter, OpenCode ve desteklenen diğer servisler farklı proxy, ayna, ağ geçidi veya kendi barındırdığınız temel URL'ler arkasında da sağlayıcıya özel davranışlarını korur.
- Model katalogları, sohbet, Responses, Anthropic Messages, Gemini akışı, görseller, hesap/kullanım API'leri ve OpenCode sunucu uçları için sağlayıcı başına düzenlenebilir uç nokta geçersiz kılmaları ekler; hazır ayarlar artık alan adına kilitli kimlikler değil, düzenlenebilir başlangıç değerleridir.
- V2 model keşfi ve üretimiyle birinci sınıf OpenCode V2 sunucu desteği ekler; doğrudan OpenCode Go ve Zen ağ geçidi bağlantılarını ayrı profiller olarak korur.
- OpenRouter entegrasyonunu hesap/anahtar limitleri ve kullanım bilgileri, katalog meta verileri, reasoning desteği, görsel model keşfi ve token başına USD fiyatlarının milyon token başına doğru dönüştürülmesiyle genişletir.
- Turp'un nötr yüzey temasını iyileştirir, Arbor'u ayrı bir yeşil kimlik olarak korur ve başlatıcı görsellerini ayırırken palet simgesinin yalnızca açık uygulama/yeniden başlatma işlemiyle uygulanması davranışını korur.
- Neredeyse tam yükseklikteki Material model seçici sayfasında yerel kaydırma ve hareketleri koruyarak kararlılığı artırır.
- Gerektiğinde yatay kaydırılan içerik dahil zengin Markdown sunumunu geliştirir ve işlenen mesajlar için geliştirici kaynak görünümünü ekler.
- Yapılandırılabilir protokoller, sağlayıcı profilleri, uç nokta çözümleme, OpenCode V2, OpenRouter, temalar, başlatıcı davranışı, model seçici ve sürüm meta verileri için veritabanı/geçiş ve regresyon kapsamını genişletir.
