package app.turp.chat.ui

/**
 * Turkish copy for Turp-owned interface text.
 *
 * Provider/model names, URLs, file names, code, user text, and model output are
 * intentionally not translated. Dynamic rules below only match phrases that
 * Turp itself formats.
 */
internal object TurkishUiCopy {
    private fun exactLookup(text: String): String? =
        exact1(text) ?:
            exact2(text) ?:
            exact3(text) ?:
            exact4(text) ?:
            exact5(text) ?:
            exact6(text) ?:
            exact7(text) ?:
            exact8(text) ?:
            exact9(text) ?:
            exact10(text) ?:
            exact11(text) ?:
            exact12(text) ?:
            exact13(text)

    private fun exact1(text: String): String? = when (text) {
        "Back" -> "Geri"
        "Close" -> "Kapat"
        "Open" -> "Aç"
        "Save" -> "Kaydet"
        "Cancel" -> "İptal"
        "Delete" -> "Sil"
        "Edit" -> "Düzenle"
        "Add" -> "Ekle"
        "Create" -> "Oluştur"
        "Remove" -> "Kaldır"
        "Rename" -> "Yeniden adlandır"
        "Retry" -> "Yeniden dene"
        "Refresh" -> "Yenile"
        "Continue" -> "Devam et"
        "Done" -> "Bitti"
        "Finish" -> "Bitir"
        "Next" -> "İleri"
        "Previous" -> "Önceki"
        "Search" -> "Ara"
        "Clear" -> "Temizle"
        "Select all" -> "Tümünü seç"
        "Select shown" -> "Gösterilenleri seç"
        "Enable" -> "Etkinleştir"
        "Disable" -> "Devre dışı bırak"
        "Enable all" -> "Tümünü etkinleştir"
        "Disable all" -> "Tümünü devre dışı bırak"
        "Enabled" -> "Etkin"
        "Disabled" -> "Devre dışı"
        "On" -> "Açık"
        "Off" -> "Kapalı"
        "Automatic" -> "Otomatik"
        "Default" -> "Varsayılan"
        "All" -> "Tümü"
        "None" -> "Hiçbiri"
        "Unknown" -> "Bilinmiyor"
        "Ready" -> "Hazır"
        "Loading…" -> "Yükleniyor…"
        "Working…" -> "Çalışıyor…"
        "Preparing…" -> "Hazırlanıyor…"
        "Checking…" -> "Kontrol ediliyor…"
        "Refreshing…" -> "Yenileniyor…"
        "Connecting…" -> "Bağlanıyor…"
        "Connected" -> "Bağlı"
        "Disconnected" -> "Bağlantı kesildi"
        "Copied" -> "Kopyalandı"
        "Copy" -> "Kopyala"
        "Download" -> "İndir"
        "Share" -> "Paylaş"
        else -> null
    }

    private fun exact2(text: String): String? = when (text) {
        "Import" -> "İçe aktar"
        "Export" -> "Dışa aktar"
        "Restore" -> "Geri yükle"
        "Install" -> "Yükle"
        "Uninstall" -> "Kaldır"
        "Apply" -> "Uygula"
        "Reset" -> "Sıfırla"
        "Details" -> "Ayrıntılar"
        "More" -> "Daha fazla"
        "Less" -> "Daha az"
        "Advanced" -> "Gelişmiş"
        "Optional" -> "İsteğe bağlı"
        "Required" -> "Gerekli"
        "Selected" -> "Seçili"
        "In use" -> "Kullanımda"
        "Open settings" -> "Ayarları aç"
        "Back to Settings" -> "Ayarlara dön"
        "Back to setup" -> "Kuruluma dön"
        "Back to runtime manager" -> "Çalışma zamanı yöneticisine dön"
        "Welcome" -> "Hoş geldiniz"
        "Model access" -> "Model erişimi"
        "Preparing Turp…" -> "Turp hazırlanıyor…"
        "Start setup" -> "Kuruluma başla"
        "Enter Turp" -> "Turp'a gir"
        "Manage providers" -> "Sağlayıcıları yönet"
        "Connect a provider" -> "Sağlayıcı bağla"
        "Continue without one" -> "Sağlayıcı olmadan devam et"
        "Skip for now" -> "Şimdilik atla"
        "Set up Turp" -> "Turp'u kur"
        "Restore a backup if you have one, then connect the model provider you actually want to use. Everything optional stays out of your way." -> "Varsa bir yedeği geri yükleyin, ardından kullanmak istediğiniz model sağlayıcısını bağlayın. İsteğe bağlı her şey yolunuzdan çekilir."
        "Private by design" -> "Tasarım gereği özel"
        "Chats, credentials, and tool workspaces stay on this device." -> "Sohbetler, kimlik bilgileri ve araç çalışma alanları bu cihazda kalır."
        "Bring your own models" -> "Kendi modellerinizi kullanın"
        "Use a ChatGPT account, API provider, or local server." -> "Bir ChatGPT hesabı, API sağlayıcısı veya yerel sunucu kullanın."
        "Optional tools stay optional" -> "İsteğe bağlı araçlar isteğe bağlı kalır"
        "Python and Linux are managed later from Settings → Local execution." -> "Python ve Linux daha sonra Ayarlar → Yerel çalıştırma bölümünden yönetilir."
        "Starting fresh? Ignore the restore card and tap Continue." -> "Sıfırdan mı başlıyorsunuz? Geri yükleme kartını atlayıp Devam et'e dokunun."
        "Model access connected" -> "Model erişimi bağlandı"
        "Connect a model provider" -> "Model sağlayıcısı bağla"
        "A provider is needed only when you send a message. You may connect one now or continue and do it later." -> "Bir sağlayıcı yalnızca mesaj gönderirken gerekir. Şimdi bağlayabilir veya devam edip daha sonra ekleyebilirsiniz."
        "Credentials were found and this step is complete." -> "Kimlik bilgileri bulundu ve bu adım tamamlandı."
        "The built-in provider catalog is delayed. Setup remains usable and Turp will keep retrying in the background." -> "Yerleşik sağlayıcı kataloğu gecikiyor. Kurulum kullanılabilir kalacak ve Turp arka planda yeniden denemeye devam edecek."
        "Setup" -> "Kurulum"
        "Finish setup" -> "Kurulumu tamamla"
        "Setup & connections" -> "Kurulum ve bağlantılar"
        "App settings and configuration" -> "Uygulama ayarları ve yapılandırma"
        "Appearance, backups, memory, and advanced behavior are grouped for later." -> "Görünüm, yedekler, hafıza ve gelişmiş davranışlar daha sonra düzenlenmek üzere gruplandırılmıştır."
        "New chat" -> "Yeni sohbet"
        else -> null
    }

    private fun exact3(text: String): String? = when (text) {
        "Search chats and messages" -> "Sohbetlerde ve mesajlarda ara"
        "All chats" -> "Tüm sohbetler"
        "Archived" -> "Arşivlenmiş"
        "PROJECTS" -> "PROJELER"
        "RECENT CHATS" -> "SON SOHBETLER"
        "ARCHIVED CHATS" -> "ARŞİVLENMİŞ SOHBETLER"
        "PROJECT CHATS" -> "PROJE SOHBETLERİ"
        "Archived chats will appear here." -> "Arşivlenmiş sohbetler burada görünür."
        "No chats in this project yet." -> "Bu projede henüz sohbet yok."
        "No project" -> "Proje yok"
        "Unpin" -> "Sabitlemeyi kaldır"
        "Pin" -> "Sabitle"
        "Move to project" -> "Projeye taşı"
        "Share portable chat" -> "Taşınabilir sohbeti paylaş"
        "Unarchive" -> "Arşivden çıkar"
        "Archive" -> "Arşivle"
        "Delete permanently" -> "Kalıcı olarak sil"
        "Move chat" -> "Sohbeti taşı"
        "Create new project" -> "Yeni proje oluştur"
        "Project" -> "Proje"
        "Rename project" -> "Projeyi yeniden adlandır"
        "Delete project" -> "Projeyi sil"
        "Rename chat" -> "Sohbeti yeniden adlandır"
        "New project" -> "Yeni proje"
        "Chats are kept and moved back to All chats." -> "Sohbetler korunur ve Tüm sohbetler bölümüne geri taşınır."
        "Its complete message history, attachments, and local code workspace records will be removed from Turp." -> "Tüm mesaj geçmişi, ekleri ve yerel kod çalışma alanı kayıtları Turp'dan kaldırılır."
        "Settings" -> "Ayarlar"
        "Providers & models" -> "Sağlayıcılar ve modeller"
        "Add your first API provider" -> "İlk API sağlayıcınızı ekleyin"
        "Backup & transfer" -> "Yedekleme ve aktarım"
        "Cloud backups, local archives, and restore" -> "Bulut yedekleri, yerel arşivler ve geri yükleme"
        "Chat behavior" -> "Sohbet davranışı"
        "New chat defaults" -> "Yeni sohbet varsayılanları"
        "Model, thinking, context, and output limits" -> "Model, düşünme, bağlam ve çıktı sınırları"
        "Response style" -> "Yanıt stili"
        "Emoji use and global answer presentation" -> "Emoji kullanımı ve genel yanıt sunumu"
        "Custom instructions" -> "Özel talimatlar"
        "Reusable tone and workflow profiles" -> "Yeniden kullanılabilir üslup ve iş akışı profilleri"
        "Intelligence" -> "Zekâ"
        "Memory" -> "Hafıza"
        "Cross-chat facts and preferences stored locally" -> "Sohbetler arasında kullanılan, yerel saklanan bilgiler ve tercihler"
        "Background tasks" -> "Arka plan görevleri"
        "Chat naming and context compression models" -> "Sohbet adlandırma ve bağlam sıkıştırma modelleri"
        "Tools & safety" -> "Araçlar ve güvenlik"
        "Search & web" -> "Arama ve web"
        "Native routing, search engines, credentials, and page fetching" -> "Yerel yönlendirme, arama motorları, kimlik bilgileri ve sayfa getirme"
        "Local execution" -> "Yerel çalıştırma"
        else -> null
    }

    private fun exact4(text: String): String? = when (text) {
        "Python, Linux, packages, and approval policy" -> "Python, Linux, paketler ve onay ilkesi"
        "Privacy & safety" -> "Gizlilik ve güvenlik"
        "Generated UI safety and local-data behavior" -> "Oluşturulan arayüz güvenliği ve yerel veri davranışı"
        "Personalization" -> "Kişiselleştirme"
        "Appearance" -> "Görünüm"
        "Theme, palette, launcher icon, and AMOLED black" -> "Tema, palet, başlatıcı simgesi ve AMOLED siyahı"
        "About" -> "Hakkında"
        "About Turp" -> "Turp hakkında"
        "Version, architecture, and privacy model" -> "Sürüm, mimari ve gizlilik modeli"
        "Assistant responses" -> "Asistan yanıtları"
        "Less emoji" -> "Daha az emoji"
        "Avoid decorative emoji and use them only when they add meaning" -> "Dekoratif emojilerden kaçın ve yalnızca anlam kattıklarında kullan"
        "Enabled by default. Technical symbols and emoji requested by the user are not blocked." -> "Varsayılan olarak etkindir. Teknik semboller ve kullanıcının istediği emojiler engellenmez."
        "Background task models" -> "Arka plan görevi modelleri"
        "Choose how Turp names chats and compresses older context." -> "Turp'un sohbetleri nasıl adlandıracağını ve eski bağlamı nasıl sıkıştıracağını seçin."
        "Configure a usable provider to enable model-based automation." -> "Model tabanlı otomasyon için kullanılabilir bir sağlayıcı yapılandırın."
        "Chat naming" -> "Sohbet adlandırma"
        "Model mode considers newer messages whenever a name is regenerated." -> "Model modu, ad her yeniden oluşturulduğunda daha yeni mesajları dikkate alır."
        "Context compression" -> "Bağlam sıkıştırma"
        "Older messages outside the active context window are merged into saved compact context." -> "Etkin bağlam penceresinin dışındaki eski mesajlar, kaydedilmiş sıkıştırılmış bağlamla birleştirilir."
        "Local • no API call" -> "Yerel • API çağrısı yok"
        "Use selected model" -> "Seçili modeli kullan"
        "Choose automation model" -> "Otomasyon modeli seç"
        "Use memory" -> "Hafızayı kullan"
        "Expose selected enabled memories to chats and allow memory tools" -> "Seçili etkin hafızaları sohbetlere sun ve hafıza araçlarına izin ver"
        "Automatic memory" -> "Otomatik hafıza"
        "Allow models to save stable, non-sensitive details; duplicate items are merged" -> "Modellerin kalıcı ve hassas olmayan ayrıntıları kaydetmesine izin ver; yinelenen öğeler birleştirilir"
        "Add memory" -> "Hafıza ekle"
        "Manual memories are available immediately and use the same deduplication rules." -> "Elle eklenen hafızalar hemen kullanılabilir ve aynı yinelenme önleme kurallarını kullanır."
        "Category" -> "Kategori"
        "Save memory" -> "Hafızayı kaydet"
        "Saved memories" -> "Kaydedilmiş hafızalar"
        "Nothing is stored yet." -> "Henüz hiçbir şey kaydedilmedi."
        "Search memories" -> "Hafızalarda ara"
        "All categories" -> "Tüm kategoriler"
        "Delete disabled" -> "Devre dışı olanları sil"
        "No memories saved yet." -> "Henüz kaydedilmiş hafıza yok."
        "No memories match the current filters." -> "Geçerli filtrelerle eşleşen hafıza yok."
        "Manual" -> "Elle"
        "From chat" -> "Sohbetten"
        "Delete memory?" -> "Hafıza silinsin mi?"
        "This permanently removes the selected memory data from Turp." -> "Bu işlem seçili hafıza verisini Turp'dan kalıcı olarak kaldırır."
        "Disabled memories are currently excluded from chats. This cleanup permanently removes them." -> "Devre dışı hafızalar şu anda sohbetlere dahil edilmez. Bu temizlik onları kalıcı olarak kaldırır."
        "Recently updated" -> "Son güncellenen"
        "Recently created" -> "Son oluşturulan"
        "Theme mode" -> "Tema modu"
        "Color scheme" -> "Renk şeması"
        "Follow device" -> "Cihazı takip et"
        else -> null
    }

    private fun exact5(text: String): String? = when (text) {
        "Light" -> "Açık"
        "Dark" -> "Koyu"
        "Dynamic" -> "Dinamik"
        "Graphite" -> "Grafit"
        "Ocean" -> "Okyanus"
        "Violet" -> "Menekşe"
        "Sunset" -> "Gün batımı"
        "Choose a restrained built-in palette or Android dynamic colors. Every swatch is rendered from that palette, not the currently selected one." -> "Sade bir yerleşik palet veya Android dinamik renklerini seçin. Her renk örneği mevcut seçimden değil, kendi paletinden oluşturulur."
        "Match launcher icon to palette" -> "Başlatıcı simgesini paletle eşleştir"
        "Keep the classic Turp green icon regardless of the selected palette." -> "Seçili paletten bağımsız olarak klasik yeşil Turp simgesini koru."
        "Android themed icons can recolor Turp's monochrome layer. Dynamic uses the live wallpaper-derived Material You palette when themed icons are off." -> "Android temalı simgeleri Turp'un tek renkli katmanını yeniden renklendirebilir. Temalı simgeler kapalıyken Dinamik, duvar kâğıdından türetilen canlı Material You paletini kullanır."
        "AMOLED black" -> "AMOLED siyahı"
        "AMOLED black only changes dark mode surfaces." -> "AMOLED siyahı yalnızca koyu mod yüzeylerini değiştirir."
        "Interface panels" -> "Arayüz panelleri"
        "Panel shape is a choice. Blur, softness, and tint remain continuous controls." -> "Panel şekli ayrı bir seçimdir. Bulanıklık, yumuşaklık ve renk tonu kesintisiz kontroller olarak kalır."
        "Blur" -> "Bulanıklık"
        "Hidden by tint" -> "Renk tonu tarafından gizleniyor"
        "0% disables blur. Higher values increase the panel-local blur radius." -> "%0 bulanıklığı kapatır. Daha yüksek değerler panel içi bulanıklık yarıçapını artırır."
        "Tint is fully opaque and covers the blurred background. Lower Tint opacity to reveal blur." -> "Renk tonu tamamen opak ve bulanık arka planı kapatıyor. Bulanıklığı görmek için renk tonu opaklığını azaltın."
        "Panel shape" -> "Panel şekli"
        "Rounded" -> "Yuvarlatılmış"
        "Flat" -> "Düz"
        "Edge softness" -> "Kenar yumuşaklığı"
        "Hard" -> "Sert"
        "Softens the boundary where flat panels merge into the page." -> "Düz panellerin sayfayla birleştiği sınırı yumuşatır."
        "Rounded panels use a hard, rounded boundary. Choose Flat to adjust softness." -> "Yuvarlatılmış paneller sert ve yuvarlak bir sınır kullanır. Yumuşaklığı ayarlamak için Düz'ü seçin."
        "Tint opacity" -> "Renk tonu opaklığı"
        "100% is fully opaque and hides background blur." -> "%100 tamamen opaktır ve arka plan bulanıklığını gizler."
        "0% is transparent. 100% is a fully opaque panel tint." -> "%0 saydamdır. %100 tamamen opak panel renk tonudur."
        "Generated content" -> "Oluşturulan içerik"
        "Controls how Turp handles AI-generated interactive UI." -> "Turp'un yapay zekâ tarafından oluşturulan etkileşimli arayüzü nasıl işleyeceğini denetler."
        "Safe generated rendering" -> "Güvenli oluşturulan içerik işleme"
        "Generated widgets are paused and shown as safe fallback content." -> "Oluşturulan widget'lar duraklatılır ve güvenli yedek içerik olarak gösterilir."
        "Generated widgets may render, but Turp still applies its capability checks and crash recovery." -> "Oluşturulan widget'lar işlenebilir; Turp yine de yetenek kontrollerini ve çökme kurtarmasını uygular."
        "Automatic repair attempts" -> "Otomatik onarım denemeleri"
        "Invalid completed widgets, charts, and diagrams are repaired in place up to this limit." -> "Geçersiz tamamlanmış widget, grafik ve diyagramlar bu sınıra kadar yerinde onarılır."
        "Third-party AI and services" -> "Üçüncü taraf yapay zekâ ve hizmetler"
        "Turp is a client, not an AI model host. Responses come from the provider or local server selected by the user." -> "Turp bir istemcidir, yapay zekâ modeli barındırmaz. Yanıtlar kullanıcının seçtiği sağlayıcıdan veya yerel sunucudan gelir."
        "Privacy" -> "Gizlilik"
        "Terms" -> "Koşullar"
        "Data deletion" -> "Veri silme"
        "No Turp account, ads, analytics, or Turp cloud. Chat history and API keys remain on this device; traffic goes to endpoints and web tools you explicitly enable." -> "Turp hesabı, reklam, analiz veya Turp bulutu yoktur. Sohbet geçmişi ve API anahtarları bu cihazda kalır; trafik yalnızca açıkça etkinleştirdiğiniz uç noktalara ve web araçlarına gider."
        "Privacy policy" -> "Gizlilik politikası"
        "Terms & disclaimer" -> "Koşullar ve sorumluluk reddi"
        "Legal" -> "Yasal"
        "Custom instruction profiles" -> "Özel talimat profilleri"
        "New custom profile" -> "Yeni özel profil"
        "No saved prompts yet." -> "Henüz kaydedilmiş istem yok."
        else -> null
    }

    private fun exact6(text: String): String? = when (text) {
        "Additional instructions" -> "Ek talimatlar"
        "Override default tone/persona" -> "Varsayılan üslup/personayı geçersiz kıl"
        "Use for new chats" -> "Yeni sohbetlerde kullan"
        "Use Turp default for new chats" -> "Yeni sohbetlerde Turp varsayılanını kullan"
        "Edit custom profile" -> "Özel profili düzenle"
        "Name" -> "Ad"
        "Prepend" -> "Başa ekle"
        "Override" -> "Geçersiz kıl"
        "Instructions" -> "Talimatlar"
        "Availability in new chats" -> "Yeni sohbetlerde kullanılabilirlik"
        "Local execution is opt-in for fresh installs. Existing chats keep their own tool choices." -> "Yerel çalıştırma yeni kurulumlarda isteğe bağlıdır. Mevcut sohbetler kendi araç seçimlerini korur."
        "Tool defaults" -> "Araç varsayılanları"
        "Python" -> "Python"
        "Bundled Python 3.12 · no Linux download required" -> "Dahili Python 3.12 · Linux indirmesi gerekmez"
        "Linux commands" -> "Linux komutları"
        "Requires a separate distribution download" -> "Ayrı bir dağıtım indirmesi gerekir"
        "Runtime manager" -> "Çalışma zamanı yöneticisi"
        "Inspect environments, install packages, run a test, or add/remove the optional Linux distribution." -> "Ortamları inceleyin, paket yükleyin, test çalıştırın veya isteğe bağlı Linux dağıtımını ekleyip kaldırın."
        "Open runtime manager" -> "Çalışma zamanı yöneticisini aç"
        "Package approval" -> "Paket onayı"
        "Choose when Turp may install Python or Linux packages and which sources are trusted." -> "Turp'un Python veya Linux paketlerini ne zaman yükleyebileceğini ve hangi kaynakların güvenilir olduğunu seçin."
        "Ask every time" -> "Her seferinde sor"
        "Show the full plan and wait for you" -> "Tam planı göster ve onayınızı bekle"
        "Trusted list" -> "Güvenilir liste"
        "Auto-approve only package names you list" -> "Yalnızca listelediğiniz paket adlarını otomatik onayla"
        "Approval model" -> "Onay modeli"
        "A separately selected model allows or denies the preflight plan" -> "Ayrı seçilen bir model ön kontrol planına izin verir veya reddeder"
        "Auto-approve" -> "Otomatik onayla"
        "Install every valid preflight plan without asking" -> "Geçerli her ön kontrol planını sormadan yükle"
        "Choose approval model" -> "Onay modeli seç"
        "Trusted pip packages" -> "Güvenilir pip paketleri"
        "Comma, space, or newline separated" -> "Virgül, boşluk veya yeni satırla ayrılmış"
        "Trusted Linux packages (apt/apk)" -> "Güvenilir Linux paketleri (apt/apk)"
        "Advanced package sources" -> "Gelişmiş paket kaynakları"
        "Allow pip direct references and relaxed apt names; command-line options remain blocked" -> "pip doğrudan referanslarına ve esnek apt adlarına izin ver; komut satırı seçenekleri engelli kalır"
        "Allow package changes?" -> "Paket değişikliklerine izin verilsin mi?"
        "Allow package installation?" -> "Paket yüklemeye izin verilsin mi?"
        "Allow and install" -> "İzin ver ve yükle"
        "Before the first install" -> "İlk yüklemeden önce"
        "Commands run as uid 0 inside the selected PRoot distribution." -> "Komutlar seçili PRoot dağıtımında uid 0 olarak çalışır."
        "Back up now" -> "Şimdi yedekle"
        "Developer settings" -> "Geliştirici ayarları"
        "Local diagnostics for measuring Turp's rendering and process performance. No metrics are uploaded or stored in chat history." -> "Turp'un işleme ve süreç performansını ölçen yerel tanılamalar. Hiçbir ölçüm yüklenmez veya sohbet geçmişinde saklanmaz."
        "Enable developer settings" -> "Geliştirici ayarlarını etkinleştir"
        "Tool diagnostics" -> "Araç tanılamaları"
        "Show tool diagnostics" -> "Araç tanılamalarını göster"
        "Off by default. Normal chats show only a concise failure summary and Retry." -> "Varsayılan olarak kapalıdır. Normal sohbetler yalnızca kısa bir hata özeti ve Yeniden dene seçeneğini gösterir."
        "Performance counter" -> "Performans sayacı"
        else -> null
    }

    private fun exact7(text: String): String? = when (text) {
        "Show performance overlay" -> "Performans katmanını göster"
        "Cause profiler" -> "Neden profilleyici"
        "Detailed metrics" -> "Ayrıntılı ölçümler"
        "Panel opacity" -> "Panel opaklığı"
        "Text opacity" -> "Metin opaklığı"
        "Overlay scale" -> "Katman ölçeği"
        "Update interval" -> "Güncelleme aralığı"
        "Overlay position" -> "Katman konumu"
        "Top left" -> "Sol üst"
        "Top right" -> "Sağ üst"
        "Bottom left" -> "Sol alt"
        "Bottom right" -> "Sağ alt"
        "Blur boundary diagnostics" -> "Bulanıklık sınırı tanılamaları"
        "Show blur boundary guides" -> "Bulanıklık sınırı kılavuzlarını göster"
        "Guide thickness" -> "Kılavuz kalınlığı"
        "Allocation / blocking GC pressure" -> "Ayırma / engelleyici GC baskısı"
        "Buffer swap" -> "Arabellek takası"
        "Project" -> "Proje"
        "Created by @omerfaruknehir" -> "@omerfaruknehir tarafından oluşturuldu"
        "Open the creator's GitHub profile" -> "Geliştiricinin GitHub profilini aç"
        "Build source" -> "Derleme kaynağı"
        "No GitHub source was embedded in this build" -> "Bu derlemeye GitHub kaynağı eklenmemiş"
        "Licenses & notices" -> "Lisanslar ve bildirimler"
        "Offline dependency catalog and full license texts" -> "Çevrimdışı bağımlılık kataloğu ve tam lisans metinleri"
        "Report an issue" -> "Sorun bildir"
        "Bugs, regressions, and feature requests" -> "Hatalar, gerilemeler ve özellik istekleri"
        "Updates" -> "Güncellemeler"
        "Check automatically" -> "Otomatik kontrol et"
        "Check the source repository once per day when Turp starts" -> "Turp başlatıldığında kaynak deposunu günde bir kez kontrol et"
        "Check for updates" -> "Güncellemeleri kontrol et"
        "Check again" -> "Tekrar kontrol et"
        "Turp is up to date" -> "Turp güncel"
        "Trup is upper to date!?" -> "Trup daha güncel!?"
        "Update check failed" -> "Güncelleme kontrolü başarısız"
        "Download update" -> "Güncellemeyi indir"
        "Open release page" -> "Sürüm sayfasını aç"
        "Build information" -> "Derleme bilgileri"
        "Version" -> "Sürüm"
        "Build" -> "Derleme"
        "Package" -> "Paket"
        "Source repository" -> "Kaynak deposu"
        "Source commit" -> "Kaynak commit'i"
        "Minimum Android" -> "Minimum Android"
        "Target Android" -> "Hedef Android"
        "Running on" -> "Çalıştığı sürüm"
        "Device ABI" -> "Cihaz ABI'si"
        "Private by design" -> "Tasarım gereği özel"
        "Chats, credentials, and workspaces stay on your device. Turp connects directly to providers you configure and has no application backend, ads, or telemetry." -> "Sohbetler, kimlik bilgileri ve çalışma alanları cihazınızda kalır. Turp yapılandırdığınız sağlayıcılara doğrudan bağlanır; uygulama arka ucu, reklam veya telemetri içermez."
        "This is a debug-signed development build." -> "Bu, hata ayıklama imzalı bir geliştirme derlemesidir."
        else -> null
    }

    private fun exact8(text: String): String? = when (text) {
        "Developer options" -> "Geliştirici seçenekleri"
        "Developer options · enabled" -> "Geliştirici seçenekleri · etkin"
        "Composer defaults" -> "Yazma alanı varsayılanları"
        "Starting state for the controls beside the message box." -> "Mesaj kutusunun yanındaki kontrollerin başlangıç durumu."
        "Tools and modes" -> "Araçlar ve modlar"
        "Web search" -> "Web araması"
        "Deep Research" -> "Derin Araştırma"
        "Deep Research plans, searches iteratively, verifies sources, and produces a cited report. Enabling it also enables web search." -> "Derin Araştırma plan yapar, yinelemeli arama yapar, kaynakları doğrular ve kaynaklı bir rapor üretir. Etkinleştirmek web aramasını da açar."
        "Token counting" -> "Token sayımı"
        "Hybrid token counting" -> "Hibrit token sayımı"
        "Context & output" -> "Bağlam ve çıktı"
        "A pair is one request plus its answer. Working history has its own budget inside the total context ceiling." -> "Bir çift, bir istek ve yanıtıdır. Çalışma geçmişinin toplam bağlam üst sınırı içinde ayrı bir bütçesi vardır."
        "Last message pairs" -> "Son mesaj çiftleri"
        "Context token ceiling" -> "Bağlam token üst sınırı"
        "Working history token budget" -> "Çalışma geçmişi token bütçesi"
        "Maximum output tokens" -> "Maksimum çıktı token'ı"
        "Working display" -> "Çalışma görünümü"
        "Turp core prompt" -> "Turp çekirdek istemi"
        "Thinking" -> "Düşünme"
        "Not supported by this model" -> "Bu model tarafından desteklenmiyor"
        "Thinking effort" -> "Düşünme düzeyi"
        "Available levels follow the selected model. Some models cannot fully disable reasoning." -> "Kullanılabilir düzeyler seçili modele bağlıdır. Bazı modeller akıl yürütmeyi tamamen kapatamaz."
        "Minimal" -> "Minimum"
        "Low" -> "Düşük"
        "Medium" -> "Orta"
        "High" -> "Yüksek"
        "Extra high" -> "Çok yüksek"
        "Max" -> "Maksimum"
        "Expanded" -> "Genişletilmiş"
        "While working" -> "Çalışırken"
        "Collapsed" -> "Daraltılmış"
        "Model" -> "Model"
        "One searchable catalog is used everywhere in Turp." -> "Turp'un her yerinde tek bir aranabilir katalog kullanılır."
        "Choose a model" -> "Model seç"
        "No provider selected" -> "Sağlayıcı seçilmedi"
        "Add a usable provider in the Providers tab." -> "Sağlayıcılar sekmesinden kullanılabilir bir sağlayıcı ekleyin."
        "Providers" -> "Sağlayıcılar"
        "Choose a provider, then manage its connection and models." -> "Bir sağlayıcı seçin, ardından bağlantısını ve modellerini yönetin."
        "No providers yet" -> "Henüz sağlayıcı yok"
        "Add a ChatGPT account or configure an API-compatible provider." -> "Bir ChatGPT hesabı ekleyin veya API uyumlu bir sağlayıcı yapılandırın."
        "Add ChatGPT" -> "ChatGPT ekle"
        "Add API" -> "API ekle"
        "ChatGPT OAuth • Connected" -> "ChatGPT OAuth • Bağlı"
        "ChatGPT OAuth • Signing in" -> "ChatGPT OAuth • Oturum açılıyor"
        "ChatGPT OAuth • Needs attention" -> "ChatGPT OAuth • İşlem gerekiyor"
        "ChatGPT OAuth • Disconnected" -> "ChatGPT OAuth • Bağlantı kesildi"
        "Keyless endpoint" -> "Anahtarsız uç nokta"
        "API key saved securely" -> "API anahtarı güvenli şekilde kaydedildi"
        else -> null
    }

    private fun exact9(text: String): String? = when (text) {
        "API key missing" -> "API anahtarı eksik"
        "Refresh models" -> "Modelleri yenile"
        "Edit connection" -> "Bağlantıyı düzenle"
        "Remove provider" -> "Sağlayıcıyı kaldır"
        "Add ChatGPT provider" -> "ChatGPT sağlayıcısı ekle"
        "Provider name" -> "Sağlayıcı adı"
        "Rename ChatGPT provider" -> "ChatGPT sağlayıcısını yeniden adlandır"
        "Use your ChatGPT plan without an API key" -> "API anahtarı olmadan ChatGPT planınızı kullanın"
        "Complete sign-in in your browser…" -> "Tarayıcınızda oturum açmayı tamamlayın…"
        "Sign in again" -> "Tekrar oturum aç"
        "Sign in with ChatGPT" -> "ChatGPT ile oturum aç"
        "Disconnect" -> "Bağlantıyı kes"
        "Usage & limits" -> "Kullanım ve sınırlar"
        "Current account quota windows" -> "Mevcut hesap kota aralıkları"
        "Refresh usage" -> "Kullanımı yenile"
        "Session" -> "Oturum"
        "Weekly" -> "Haftalık"
        "Additional credits available" -> "Ek krediler kullanılabilir"
        "A ChatGPT usage limit has been reached." -> "Bir ChatGPT kullanım sınırına ulaşıldı."
        "Require API key" -> "API anahtarı gerektir"
        "Disable only for a trusted local or keyless endpoint" -> "Yalnızca güvenilir yerel veya anahtarsız bir uç nokta için kapatın"
        "Advanced headers" -> "Gelişmiş başlıklar"
        "Usually unnecessary" -> "Genellikle gerekli değildir"
        "Custom headers JSON" -> "Özel başlıklar JSON"
        "Save connection" -> "Bağlantıyı kaydet"
        "Add provider" -> "Sağlayıcı ekle"
        "Custom provider" -> "Özel sağlayıcı"
        "API base URL" -> "API temel URL'si"
        "Base URL" -> "Temel URL"
        "API key" -> "API anahtarı"
        "API key (optional)" -> "API anahtarı (isteğe bağlı)"
        "API key is required" -> "API anahtarı gerekli"
        "Connect & fetch models" -> "Bağlan ve modelleri getir"
        "Fetch models again" -> "Modelleri yeniden getir"
        "Models from provider" -> "Sağlayıcıdaki modeller"
        "Clear" -> "Temizle"
        "Provider has no model list? Enter manually" -> "Sağlayıcının model listesi yok mu? Elle girin"
        "Hide manual model entry" -> "Elle model girişini gizle"
        "Bundled suggestions" -> "Dahili öneriler"
        "API model ID" -> "API model kimliği"
        "Model display name" -> "Model görünen adı"
        "Manual model will also be included" -> "Elle girilen model de dahil edilecek"
        "Only the selected provider models will be saved." -> "Yalnızca seçili sağlayıcı modelleri kaydedilecek."
        "Models" -> "Modeller"
        "Search models" -> "Modellerde ara"
        "No matching models." -> "Eşleşen model yok."
        "Add model" -> "Model ekle"
        else -> null
    }

    private fun exact10(text: String): String? = when (text) {
        "Edit model" -> "Modeli düzenle"
        "Only the essentials are shown. Pricing is optional." -> "Yalnızca temel alanlar gösterilir. Fiyatlandırma isteğe bağlıdır."
        "Display name" -> "Görünen ad"
        "Context tokens" -> "Bağlam token'ları"
        "Max output" -> "Maksimum çıktı"
        "Request type" -> "İstek türü"
        "Chat" -> "Sohbet"
        "Image generation" -> "Görsel oluşturma"
        "Controls whether this custom endpoint uses chat/completions or images/generations." -> "Bu özel uç noktanın chat/completions mı yoksa images/generations mı kullanacağını belirler."
        "Advanced compatibility" -> "Gelişmiş uyumluluk"
        "Tools" -> "Araçlar"
        "Vision" -> "Görüntü"
        "Files" -> "Dosyalar"
        "Pricing" -> "Fiyatlandırma"
        "Pricing configured" -> "Fiyatlandırma yapılandırıldı"
        "Configured in USD per million tokens" -> "Milyon token başına USD olarak yapılandırıldı"
        "Optional · cost will show as unavailable" -> "İsteğe bağlı · maliyet kullanılamıyor olarak gösterilir"
        "Cached input" -> "Önbelleğe alınmış girdi"
        "Input" -> "Girdi"
        "Output" -> "Çıktı"
        "Routing" -> "Yönlendirme"
        "Fallback search engine" -> "Yedek arama motoru"
        "Saved" -> "Kaydedildi"
        "Save key" -> "Anahtarı kaydet"
        "SearXNG endpoint" -> "SearXNG uç noktası"
        "Public HTTPS base URL" -> "Herkese açık HTTPS temel URL'si"
        "The instance must enable JSON search output." -> "Sunucu JSON arama çıktısını etkinleştirmiş olmalıdır."
        "Tool behavior" -> "Araç davranışı"
        "Maximum search results" -> "Maksimum arama sonucu"
        "3–20 results per search call" -> "Arama çağrısı başına 3–20 sonuç"
        "Allow page fetching" -> "Sayfa getirmeye izin ver"
        "Expose web_fetch so the model can read public HTTPS pages after searching." -> "Modelin aramadan sonra herkese açık HTTPS sayfalarını okuyabilmesi için web_fetch aracını sun."
        "Search chats" -> "Sohbetlerde ara"
        "Search messages" -> "Mesajlarda ara"
        "Search chats and messages…" -> "Sohbetlerde ve mesajlarda ara…"
        "No results" -> "Sonuç yok"
        "No matching chats or messages." -> "Eşleşen sohbet veya mesaj yok."
        "Results" -> "Sonuçlar"
        "All providers" -> "Tüm sağlayıcılar"
        "Sources" -> "Kaynaklar"
        "Referenced file" -> "Başvurulan dosya"
        "External link" -> "Harici bağlantı"
        "A file referenced by this answer." -> "Bu yanıtta başvurulan bir dosya."
        "A source used to support the surrounding claim." -> "İlgili ifadeyi desteklemek için kullanılan bir kaynak."
        "An external page linked from this answer." -> "Bu yanıttan bağlantı verilen harici bir sayfa."
        "Generate image" -> "Görsel oluştur"
        "Edit image" -> "Görseli düzenle"
        else -> null
    }

    private fun exact11(text: String): String? = when (text) {
        "Image editing" -> "Görsel düzenleme"
        "Add image" -> "Görsel ekle"
        "Add another" -> "Bir tane daha ekle"
        "Add reference image" -> "Referans görsel ekle"
        "Add to chat" -> "Sohbete ekle"
        "Remove image" -> "Görseli kaldır"
        "Reference images" -> "Referans görseller"
        "Attachments are unavailable in image generation mode" -> "Görsel oluşturma modunda ekler kullanılamaz"
        "Add an image, then describe the edit…" -> "Bir görsel ekleyin, ardından düzenlemeyi açıklayın…"
        "Describe the image you want…" -> "İstediğiniz görseli açıklayın…"
        "Generating image…" -> "Görsel oluşturuluyor…"
        "Editing image…" -> "Görsel düzenleniyor…"
        "Image request" -> "Görsel isteği"
        "Pending image requests" -> "Bekleyen görsel istekleri"
        "No image models available" -> "Kullanılabilir görsel modeli yok"
        "Choose image model" -> "Görsel modeli seç"
        "Image model" -> "Görsel modeli"
        "Add at least one reference image." -> "En az bir referans görsel ekleyin."
        "Download image" -> "Görseli indir"
        "Share image" -> "Görseli paylaş"
        "Ask AI to retry" -> "Yapay zekâdan yeniden denemesini iste"
        "Automatic repair unavailable" -> "Otomatik onarım kullanılamıyor"
        "Repairing…" -> "Onarılıyor…"
        "Working" -> "Çalışma"
        "Tool call" -> "Araç çağrısı"
        "Tool result" -> "Araç sonucu"
        "Tool failed" -> "Araç başarısız"
        "Run again" -> "Tekrar çalıştır"
        "Stop" -> "Durdur"
        "Stopped" -> "Durduruldu"
        "Queued" -> "Sırada"
        "Running" -> "Çalışıyor"
        "Completed" -> "Tamamlandı"
        "Failed" -> "Başarısız"
        "Timed out" -> "Zaman aşımına uğradı"
        "Approval required" -> "Onay gerekli"
        "Allow" -> "İzin ver"
        "Deny" -> "Reddet"
        "Backup" -> "Yedek"
        "Backups" -> "Yedekler"
        "Local backup" -> "Yerel yedek"
        "Cloud backup" -> "Bulut yedeği"
        "Restore backup" -> "Yedeği geri yükle"
        "Import backup" -> "Yedeği içe aktar"
        "Export backup" -> "Yedeği dışa aktar"
        "Backup saved" -> "Yedek kaydedildi"
        "Backup failed" -> "Yedekleme başarısız"
        "Backup downloaded. Opening preview…" -> "Yedek indirildi. Önizleme açılıyor…"
        else -> null
    }

    private fun exact12(text: String): String? = when (text) {
        "Backup restored. Setup was paused; finish provider access later from Settings." -> "Yedek geri yüklendi. Kurulum duraklatıldı; sağlayıcı erişimini daha sonra Ayarlar'dan tamamlayın."
        "Choose folder" -> "Klasör seç"
        "Change folder" -> "Klasörü değiştir"
        "Select folder" -> "Klasör seç"
        "Google Drive" -> "Google Drive"
        "OneDrive" -> "OneDrive"
        "Dropbox" -> "Dropbox"
        "Nextcloud" -> "Nextcloud"
        "Amazon S3, MinIO, Backblaze B2, or another compatible bucket" -> "Amazon S3, MinIO, Backblaze B2 veya başka bir uyumlu bucket"
        "Account connected" -> "Hesap bağlandı"
        "Connect account" -> "Hesap bağla"
        "Disconnect account" -> "Hesap bağlantısını kes"
        "Sign in" -> "Oturum aç"
        "Sign out" -> "Oturumu kapat"
        "Restore from cloud" -> "Buluttan geri yükle"
        "Restore from file" -> "Dosyadan geri yükle"
        "Export to file" -> "Dosyaya dışa aktar"
        "Archive password" -> "Arşiv parolası"
        "Access key ID" -> "Erişim anahtarı kimliği"
        "Secret access key" -> "Gizli erişim anahtarı"
        "Bucket" -> "Bucket"
        "Endpoint" -> "Uç nokta"
        "Region" -> "Bölge"
        "Android grants Turp persistent access only to the folder you select. Create or choose a dedicated Turp folder; no account-wide permission is requested." -> "Android, Turp'a yalnızca seçtiğiniz klasöre kalıcı erişim verir. Turp için özel bir klasör oluşturun veya seçin; hesap genelinde izin istenmez."
        "Runtime" -> "Çalışma zamanı"
        "Runtime status" -> "Çalışma zamanı durumu"
        "Python environment" -> "Python ortamı"
        "Linux environment" -> "Linux ortamı"
        "Linux distribution" -> "Linux dağıtımı"
        "Install Linux" -> "Linux yükle"
        "Remove Linux" -> "Linux'u kaldır"
        "Reinstall" -> "Yeniden yükle"
        "Packages" -> "Paketler"
        "Installed packages" -> "Yüklü paketler"
        "Install package" -> "Paket yükle"
        "Remove package" -> "Paketi kaldır"
        "Run test" -> "Test çalıştır"
        "Terminal" -> "Terminal"
        "Open terminal" -> "Terminali aç"
        "Root terminal" -> "Root terminali"
        "Command" -> "Komut"
        "Run command" -> "Komutu çalıştır"
        "Clear terminal" -> "Terminali temizle"
        "Copy output" -> "Çıktıyı kopyala"
        "Bundled runtime · loading this chat's environment…" -> "Dahili çalışma zamanı · bu sohbetin ortamı yükleniyor…"
        "1–600 seconds. Pure Python is interrupted at the deadline; a blocking native extension may return later." -> "1–600 saniye. Saf Python süre sonunda durdurulur; engelleyici bir yerel eklenti daha sonra dönebilir."
        "Licenses" -> "Lisanslar"
        "Notices" -> "Bildirimler"
        else -> null
    }

    private fun exact13(text: String): String? = when (text) {
        "Open-source licenses" -> "Açık kaynak lisansları"
        "Search licenses" -> "Lisanslarda ara"
        "License text" -> "Lisans metni"
        "Dependency" -> "Bağımlılık"
        "Dependencies" -> "Bağımlılıklar"
        "Changed files" -> "Değişen dosyalar"
        "Cost unavailable" -> "Maliyet kullanılamıyor"
        "Thinking always on" -> "Düşünme her zaman açık"
        "Tools" -> "Araçlar"
        "Vision" -> "Görüntü"
        "Image generation" -> "Görsel oluşturma"
        "Free" -> "Ücretsiz"
        "Favorites" -> "Favoriler"
        "Recent" -> "Son kullanılanlar"
        "Add favorite" -> "Favorilere ekle"
        "Remove favorite" -> "Favorilerden kaldır"
        "Usage" -> "Kullanım"
        "Cost" -> "Maliyet"
        "Input tokens" -> "Girdi token'ları"
        "Output tokens" -> "Çıktı token'ları"
        "Total tokens" -> "Toplam token"
        "Context" -> "Bağlam"
        "Output limit" -> "Çıktı sınırı"
        else -> null
    }

    fun translate(text: String): String {
        exactLookup(text)?.let { return it }

        Regex("""Step (\d+) of (\d+)""").matchEntire(text)?.let {
            return "${it.groupValues[2]} adımın ${it.groupValues[1]}. adımı"
        }
        Regex("""Continue from step (\d+) of 3""").matchEntire(text)?.let {
            return "3 adımın ${it.groupValues[1]}. adımından devam et"
        }
        Regex("""(\d+) providers? configured""").matchEntire(text)?.let {
            return "${it.groupValues[1]} sağlayıcı yapılandırıldı"
        }
        Regex("""(\d+) configured providers?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} yapılandırılmış sağlayıcı"
        }
        Regex("""(\d+) selected""").matchEntire(text)?.let {
            return "${it.groupValues[1]} seçildi"
        }
        Regex("""(\d+) of (\d+) selected""").matchEntire(text)?.let {
            return "${it.groupValues[2]} öğenin ${it.groupValues[1]} tanesi seçili"
        }
        Regex("""Source (\d+)""").matchEntire(text)?.let {
            return "Kaynak ${it.groupValues[1]}"
        }
        Regex("""(\d+)% left""").matchEntire(text)?.let {
            return "%${it.groupValues[1]} kaldı"
        }
        Regex("""(\d+)% used""").matchEntire(text)?.let {
            return "%${it.groupValues[1]} kullanıldı"
        }
        Regex("""(\d+) results?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} sonuç"
        }
        Regex("""(\d+) steps?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} adım"
        }
        Regex("""(\d+) queued""").matchEntire(text)?.let {
            return "${it.groupValues[1]} sırada"
        }
        Regex("""(\d+) starred""").matchEntire(text)?.let {
            return "${it.groupValues[1]} favori"
        }
        Regex("""Delete (\d+) memories\?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} hafıza silinsin mi?"
        }
        Regex("""Delete (\d+) disabled memor(?:y|ies)\?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} devre dışı hafıza silinsin mi?"
        }
        Regex("""(\d+) saved items?; (\d+) enabled\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} kayıtlı öğe; ${it.groupValues[2]} etkin."
        }
        Regex("""(\d+)-hour limit""").matchEntire(text)?.let {
            return "${it.groupValues[1]} saatlik sınır"
        }
        Regex("""(\d+)-day limit""").matchEntire(text)?.let {
            return "${it.groupValues[1]} günlük sınır"
        }
        Regex("""(\d+)K context""").matchEntire(text)?.let {
            return "${it.groupValues[1]}K bağlam"
        }
        Regex("""(\d+)K output""").matchEntire(text)?.let {
            return "${it.groupValues[1]}K çıktı"
        }
        Regex("""Delete “(.+)”\?""").matchEntire(text)?.let {
            return "“${it.groupValues[1]}” silinsin mi?"
        }
        Regex("""Delete project “(.+)”\?""").matchEntire(text)?.let {
            return "“${it.groupValues[1]}” projesi silinsin mi?"
        }
        Regex("""Search (\d+) models""").matchEntire(text)?.let {
            return "${it.groupValues[1]} modelde ara"
        }
        Regex("""Showing (\d+) of (\d+)\. Search or filter to narrow the catalog\.""").matchEntire(text)?.let {
            return "${it.groupValues[2]} modelin ${it.groupValues[1]} tanesi gösteriliyor. Kataloğu daraltmak için arayın veya filtreleyin."
        }
        Regex("""(\d+) available for (.+) · metadata refreshes with the catalog""").matchEntire(text)?.let {
            return "${it.groupValues[2]} için ${it.groupValues[1]} model kullanılabilir · meta veriler katalogla yenilenir"
        }
        Regex("""Updated (\d+) models""").matchEntire(text)?.let {
            return "${it.groupValues[1]} model güncellendi"
        }
        Regex("""Automatically updated metadata for (\d+) models""").matchEntire(text)?.let {
            return "${it.groupValues[1]} modelin meta verileri otomatik güncellendi"
        }
        Regex("""Latest release: (.+) · checked (.+)""").matchEntire(text)?.let {
            return "En son sürüm: ${it.groupValues[1]} · kontrol: ${it.groupValues[2]}"
        }
        Regex("""Turp (.+) is available""").matchEntire(text)?.let {
            return "Turp ${it.groupValues[1]} kullanılabilir"
        }
        Regex("""Credits balance: (.+)""").matchEntire(text)?.let {
            return "Kredi bakiyesi: ${it.groupValues[1]}"
        }
        Regex("""Credits: (.+)""").matchEntire(text)?.let {
            return "Krediler: ${it.groupValues[1]}"
        }
        Regex("""Limit reached: (.+)""").matchEntire(text)?.let {
            return "Sınıra ulaşıldı: ${it.groupValues[1]}"
        }
        Regex("""(.+) plan • reported by ChatGPT""").matchEntire(text)?.let {
            return "${it.groupValues[1]} planı • ChatGPT tarafından bildirildi"
        }
        Regex("""(.+) installed""").matchEntire(text)?.let {
            return "${it.groupValues[1]} yüklü"
        }
        Regex("""(.+) install failed""").matchEntire(text)?.let {
            return "${it.groupValues[1]} yüklemesi başarısız"
        }
        Regex("""(.+) requires at least one reference image\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} en az bir referans görsel gerektiriyor."
        }
        Regex("""(.+) accepts at most (\d+) reference images\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} en fazla ${it.groupValues[2]} referans görsel kabul ediyor."
        }
        Regex("""(.+) accepts up to (\d+) reference images\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} en fazla ${it.groupValues[2]} referans görsel kabul ediyor."
        }
        Regex("""(.+) does not accept reference images\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} referans görselleri kabul etmiyor."
        }
        Regex("""Add at least one reference image for (.+)\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} için en az bir referans görsel ekleyin."
        }
        Regex("""(.+) connection""").matchEntire(text)?.let {
            return "${it.groupValues[1]} bağlantısı"
        }
        Regex("""(.+) models""").matchEntire(text)?.let {
            return "${it.groupValues[1]} modelleri"
        }
        Regex("""(.+) ready""").matchEntire(text)?.let {
            return "${it.groupValues[1]} hazır"
        }
        Regex("""Turp detected (.+)\. Continue or open the provider manager to make changes\.""").matchEntire(text)?.let {
            return "Turp ${it.groupValues[1]} algıladı. Devam edin veya değişiklik yapmak için sağlayıcı yöneticisini açın."
        }
        Regex("""(.+) is running in the background • (\d+)s deadline""").matchEntire(text)?.let {
            return "${it.groupValues[1]} arka planda çalışıyor • ${it.groupValues[2]} sn süre sınırı"
        }
        Regex("""(.+) paused for crash recovery""").matchEntire(text)?.let {
            return "${it.groupValues[1]} çökme kurtarması için duraklatıldı"
        }
        Regex("""(.+) included""").matchEntire(text)?.let {
            return "${it.groupValues[1]} dahil"
        }
        Regex("""(.+) excluded""").matchEntire(text)?.let {
            return "${it.groupValues[1]} hariç"
        }

        if (text.startsWith("Managed by Turp · revision ")) {
            return text.replaceFirst("Managed by Turp · revision ", "Turp tarafından yönetiliyor · revizyon ")
        }
        if (text.startsWith("Preset: ")) return text.replaceFirst("Preset: ", "Ön ayar: ")
        if (text.startsWith("Protocol: ")) return text.replaceFirst("Protocol: ", "Protokol: ")
        if (text.startsWith("Backup saved to ")) return text.replaceFirst("Backup saved to ", "Yedek şuraya kaydedildi: ")
        if (text.startsWith("Checking ") && text.endsWith("…")) {
            return "Kontrol ediliyor: ${text.removePrefix("Checking ").removeSuffix("…")}…"
        }
        if (text.startsWith("Source: ")) return text.replaceFirst("Source: ", "Kaynak: ")
        if (text.startsWith("Remove ") && text.endsWith("?")) {
            return "${text.removePrefix("Remove ").removeSuffix("?")} kaldırılsın mı?"
        }
        return text
    }
}
