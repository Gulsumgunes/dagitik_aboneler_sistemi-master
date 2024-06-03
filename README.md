Java programlama diliyle Taşıma (Transport) Katmanı gönderim fonksiyonlarını kullanarak dağıtık bir abonelik sistemi.

Bu abonelik sistemi soket üzerinde SMTP, HTTP vb. bir protokol yerine ödev kapsamında ortaya atılan ASUP (Abonelik Servisi Uyelik Protokolü) isminde ilkel bir protokol ile temel sözdizimi aşağıda paylaşılan sırada gerçekleşmektedir.

Yük dağılımı, hata tolerans vb. taleplerde başvurulan dağıtık mimaride her bir sunucu, aboneleri ve abonelerin sistemde çevrimiçi/çevrimdışı olduğu bilgileri tutmaktadır. İstemci bir sunucudan abone olup; bir başka sunucudan sisteme giriş yapabilmektedir.

Sunucular listenin diğer sunucularda da güncellenmesi sonrası (güncel listeyi alan diğer 2 sunucudan “55 TAMM” mesajı gelmesinden sonra) hizmet verdiği istemciye yanıt dönmektedir.

Her sunucu concurrent (eşzamanlı) istemci erişimi sırasında bünyesindeki listelere erişimi thread-safe sunar.

İstemci tarafından sunucuya/sunuculara atılacak istekler ve sunucu yanıtları:

İSTEK: “ABONOL 1” 		//1 numaralı istemcinin abone olma isteği
 	YANIT : “55 TAMM”  veya   “50 HATA” (Örnek hata sebebi: Zaten abone olma durumu)
Yapılan istek doğrultusunda sunucu aboneler listesinin 1. indisli elemanını true yapar. Güncel aboneler nesnesini diğer sunuculara gönderir.

İSTEK: “ABONPTAL 2” 	//2 numaralı istemcinin aboneliğini iptal isteği
 	YANIT : “55 TAMM”  veya  “50 HATA” (Örnek hata sebebi: Zaten abone değil durumu)
 	Yapılan istek doğrultusunda sunucu aboneler listesinin 2. indisli elemanını false yapar. Güncel aboneler nesnesini diğer sunuculara gönderir.

İSTEK: “GIRIS ISTMC 33”	//33 numaralı istemcinin sisteme giriş isteği
 	YANIT : “55 TAMM”  veya   “50 HATA” (Örnek hata sebebi: Abone değil durumu)
 	Yapılan istek doğrultusunda sunucu giriş yapanlar listesinin 33. indisli elemanını true yapar. Güncel aboneler nesnesini diğer sunuculara gönderir.


İSTEK: “CIKIS ISTMC 99”	//99 numaralı istemcinin sistemden çıkış isteği
 	YANIT : “55 TAMM”  veya   “50 HATA” (Örnek hata sebebi: Giriş yapmamış olma durumu)
Yapılan istek doğrultusunda sunucu giriş yapanlar listesinin 99. indisli elemanını false yapar. Güncel aboneler nesnesini diğer sunuculara gönderir.

Sunucudan sunucuya serileştirilmiş nesne (Aboneler.class) gönderimi ve sunucu
yanıtları:  (Şekil.1’ de A,B ve C no’ lu istekler):
İSTEK: SERİLEŞTİRİLMİŞ_NESNE
 	YANIT : “55 TAMM”  veya   “99 HATA” (Örnek hata sebebi: Yanlış nesne gönderimi, Geçmiş tarihli liste gönderimi.)
