import os

# ez a script a tesztek mappából megcsinálja latex formátumban a beadandót
test_metadata = {
    "01_fejcsere_sikeres": ("Hókotró fejének cseréje - Sikeres", "A Takarító játékos a telephelyen tartózkodva lecseréli a hókotrója jelenlegi fejét egy másikra, amely elérhető a raktárban.", "A fejcsere metódusainak (TakaritoManager, Hokotro) helyes lefutását és a régi, valamint új Fej objektumkapcsolatok frissülését teszteli."),
    "02_fejcsere_nincstelep": ("Hókotró fejének cseréje - Sikertelen (Nincs telephelyen)", "A játékos a város egy normál útszakaszán próbál meg fejet cserélni a hókotróján.", "Annak ellenőrzése, hogy a rendszer elutasítja a cserét, mivel a művelet kizárólag a Telephely csomóponton hajtható végre."),
    "03_hokotrovasarlas_sikeres": ("Új hókotró vásárlása - Sikeres", "A Takarító menedzser a telephelyen állva új hókotrót vásárol, és rendelkezik a vásárláshoz szükséges elegendő pénzzel.", "A vásárlási feltételek ellenőrzése, a pénzlevonás, és a TakaritoManager járműlistájának bővülésének validálása."),
    "04_hokotrovasarlas_nincspenz": ("Új hókotró vásárlása - Sikertelen (Nincs elég pénz)", "A menedzser a telephelyen próbál hókotrót vásárolni, de a kasszában nincs meg a fedezet.", "Validálja, hogy a vagyon nem megy mínuszba, és a flotta nem bővül járművel fedezet hiányában."),
    "05_hokotrovasarlas_nincstelep": ("Új hókotró vásárlása - Sikertelen (Nincs telephelyen)", "A menedzser egy útszakaszon tartózkodik, és elegendő pénz birtokában próbál hókotrót vásárolni.", "Ellenőrzi, hogy a helyszíni feltétel hiányában a vásárlás meghiúsul."),
    "06_fejvasarlas_sikeres": ("Új fej vásárlása - Sikeres", "A menedzser egy speciális takarítófejet vásárol a telephelyen, elegendő pénzből.", "A fejvásárlás menetének, a készletkezelésnek (a fej a raktárba kerül) és a pénzkövetésnek az ellenőrzése."),
    "07_fejvasarlas_nincspenz": ("Új fej vásárlása - Sikertelen (Nincs elég pénz)", "A menedzser a telephelyen próbál meg fejet vásárolni, de nincs rá elég pénze.", "A tranzakció sikertelen lefutásának ellenőrzése, a raktárkészlet változatlanságának validálása."),
    "08_fejvasarlas_nincstelep": ("Új fej vásárlása - Sikertelen (Nincs telephelyen)", "A menedzser nem telephelyen állva próbál takarítófejet venni a raktárába.", "A tranzakció elutasításának ellenőrzése a helyszín miatt."),
    "09_sotoltes_sikeres": ("Fogyóeszköz töltése - Só (Sikeres)", "A játékos a telephelyen sót tölt a hókotróra szerelt sószóró fejbe.", "A sótöltés logikájának, a készletváltozásnak és a SoszoroFej belső állapotfrissítésének ellenőrzése."),
    "10_sotoltes_nincstelep": ("Fogyóeszköz töltése - Só (Sikertelen, nincs telephelyen)", "A játékos útközben, a várost járva próbál sót tölteni a sószóró fejbe.", "Az akció blokkolásának ellenőrzése helyszíni megkötés miatt."),
    "11_kerozintoltes_sikeres": ("Fogyóeszköz töltése - Biokerozin (Sikeres)", "A játékos a telephelyen biokerozint tölt a sárkány fejbe.", "A kerozin feltöltésének és a SarkanyFej kapacitásnövekedésének validálása."),
    "12_kerozintoltes_nincstelep": ("Fogyóeszköz töltése - Biokerozin (Sikertelen, nincs telephelyen)", "A játékos munka közben próbál biokerozint vételezni a sárkány fejbe.", "A művelet meghiúsulásának ellenőrzése."),
    "13_zuzalektoltes_sikeres": ("Fogyóeszköz töltése - Zúzottkő (Sikeres)", "A játékos a telephelyen zúzottkövet tölt a zúzottkőszóró fejbe.", "A ZuzottkoszoroFej belső készletének sikeres feltöltésének tesztelése."),
    "14_zuzalektoltes_nincstelep": ("Fogyóeszköz töltése - Zúzottkő (Sikertelen, nincs telephelyen)", "A játékos útszakaszon próbálja feltölteni a zúzottkőszóró fejét.", "A művelet meghiúsulásának validálása."),
    "15_busz_vegallomas": ("Busz mozgása - Végállomás elérése (Sikeres kör)", "A Buszsofőr egy buszt egy olyan csomópontra léptet, amely a busz útvonalának végállomása.", "A Busz és a BuszsoforManager tesztelése: ellenőrzi a körtelesítés regisztrációját és a bevétel jóváírását."),
    "16_busz_koztes_megallo": ("Busz mozgása - Köztes csomópont (Nincs körtelesítés)", "A buszsofőr egy buszt mozgat, de a cél egy normál útszakasz vagy köztes csomópont.", "Validálja, hogy köztes lépés esetén a rendszer nem regisztrál teljesített kört és nem ad pénzjutalmat."),
    "17_jarmu_elakadas_hoban": ("Jármű elakadása magas hóban (Sikertelen haladás)", "Egy jármű egy olyan sávra lépne, ahol a hó vastagsága mozgást akadályozó szintű.", "A normál Jarmu osztályok lépésének blokkolásának tesztelése a Sav magas csapadékszintje miatt."),
    "18_hokotro_immunitas": ("Hókotró mozgatása (Sikeres haladás extrém időben is)", "A Takarító játékos hókotróját egy vastag hóréteggel vagy jégpáncéllal borított sávra irányítja.", "A Hokotro környezeti hatásokkal szembeni immunitásának ellenőrzése: a jármű akadálytalanul halad tovább."),
    "19_jarmu_megcsuszas": ("Jármű megcsúszása jeges úton (Baleset)", "Egy normál jármű jégpáncélos sávon halad át, ahol a baleseti ráta alapján megcsúszás következik be.", "A balesetkalkuláció és az ütközési logika tesztelése: a jármű baleseti állapotba kerülésének validálása."),
    "20_auto_athaladas_tomorules": ("Autó áthaladása és jéggé tömörülés", "Egy önműködő NPC autó áthalad egy havas sávon.", "A Sav.autoAthalad() metódus tesztelése: validálja, hogy a hó a jármű súlya alatt Jegpancella tömörül."),
    "21_sarkanyfej_olvasztas": ("Sárkány fej működése - Sikeres olvasztás", "A sárkány fejjel felszerelt hókotró áthalad egy havas/jeges sávon, és van elég biokerozinja.", "Az azonnali hó- és jégolvasztó hatás, valamint a kerozinkészlet csökkenésének ellenőrzése."),
    "22_sarkanyfej_nincskerozin": ("Sárkány fej működése - Sikertelen (Kifogyott kerozin)", "A sárkány fej áthalad a csapadékos sávon, de az üzemanyagtartálya üres.", "Ellenőrzi, hogy a fej kerozin hiányában nem olvasztja el a csapadékot, a sáv állapota változatlan marad."),
    "23_soszorofej_sikeres": ("Sószóró fej működése - Sikeres szórás", "A sószóró fej áthalad a sávon, van elég sója, így kiszórja azt.", "A só kihelyezésének, a meglévő hó olvasztásának és az útburkolat ideiglenes tisztántartásának validálása."),
    "24_soszorofej_nincsso": ("Sószóró fej működése - Sikertelen (Kifogyott só)", "A sószóró fej áthalad az úton, de nincs benne só.", "Ellenőrzi, hogy a sáv állapota változatlan marad és nem jön létre sózott réteg."),
    "25_zuzottkoszorofej_sikeres": ("Zúzottkőszóró fej működése - Sikeres szórás", "A hókotró zúzottkőszóró fejjel halad a jeges úton, és van készlete, amit kiszór.", "Az új ZuzottkoszoroFej tesztelése: a fogyóeszköz csökken, és az adott sávon csökken a baleseti ráta."),
    "26_zuzottkoszorofej_nincsko": ("Zúzottkőszóró fej működése - Sikertelen (Kifogyott kő)", "A zúzottkőszóró fej halad, de üres a tartálya.", "Validálja, hogy nem történik kőszórás, és a sáv csúszásveszélye nem csökken."),
    "27_soprofej_takaritas": ("Söprő fej működése - Normál takarítás", "A söprő fejjel haladó hókotró a csapadékot a menetirány szerinti jobbra lévő sávba tolja át.", "A SoproFej és a Sav szomszédsági relációinak validálása: az aktuális sáv megtisztul, a szomszédoson nő a csapadék."),
    "28_jegtorofej_takaritas": ("Jégtörő fej működése - Jégzúzás", "A jégtörő fejjel felszerelt jármű szilárd jégpáncéllal borított sávon halad át.", "Annak ellenőrzése, hogy a JegtoroFej a szilárd jeget feltört jéggé alakítja, de fizikailag az úton hagyja."),
    "29_hanyofej_takaritas": ("Hányó fej működése - Teljes tisztítás", "A nagyteljesítményű hányó fej áthalad egy csapadékos sávon.", "Validálja, hogy a HanyoFej az úttól messzire repíti a havat, így az aktuális sáv teljesen mentesül a csapadéktól."),
    "30_havazas_uton_hidon": ("Havazás szimulációja - Úton és hídon", "Az Uthalozat globális havazást szimulál, amely érinti a nyílt útszakaszokat és a hidakat.", "A folyamatos hógyarapodás ellenőrzése ezeken a UtTipus elemeken."),
    "31_havazas_alagutban": ("Havazás szimulációja - Alagútban", "A globális havazás lefutása során ellenőrizzük az alagút típusú csomópontokat.", "Validálja, hogy az alagút védett a csapadéktól, így ott nem nő a hóréteg vastagsága."),
    "32_sotoltes_vedelem_havazas": ("Leszórt só hatása (Olvadási idő működése)", "Körök telnek el egy korábban már sózott útszakaszon.", "A Sav állapotgépének tesztelése: a só megakadályozza a hó megmaradását."),
    "33_gameover_tilalom": ("Játék vége - Kijárási tilalom (Vereség)", "A folyamatos játék során a jeges utakon történő balesetek száma elér egy kritikus határértéket.", "A vereséget kiváltó esemény (balesetszámláló) helyes detektálásának tesztelése."),
    "34_gameover_buszok": ("Játék vége - Buszok elakadtak (Vereség)", "A nagy hó miatt a város összes busza mozgásképtelenné (elakadttá) válik.", "A játék végét hajtó alternatív logika ellenőrzése: a rendszer észleli a teljes forgalmi blokádot.")
}

latex_content = "\\section{A tesztek részletes tervei, leírásuk a teszt nyelvén}\n"
latex_content += "\\comment{A tesztek részletes tervei alatt meg kell adni azokat a bemeneti adatsorozatokat...}\n\n"

for prefix, (title, desc, func) in test_metadata.items():
    in_file = os.path.join("tesztek", f"{prefix}_in.txt")
    exp_file = os.path.join("tesztek", f"{prefix}_exp.txt")
    
    if os.path.exists(in_file) and os.path.exists(exp_file):
        with open(in_file, 'r', encoding='utf-8') as f_in:
            input_text = f_in.read().strip()
        with open(exp_file, 'r', encoding='utf-8') as f_exp:
            output_text = f_exp.read().strip()
            
        latex_content += f"\\subsection{{{title}}}\n"
        latex_content += f"\\begin{{test-case-description}}\n    {desc}\n\\end{{test-case-description}}\n"
        latex_content += f"\\begin{{test-case-function}}\n    {func}\n\\end{{test-case-function}}\n"
        latex_content += f"\\begin{{test-case-input}}\n\\begin{{verbatim}}\n{input_text}\n\\end{{verbatim}}\n\\end{{test-case-input}}\n"
        latex_content += f"\\begin{{test-case-output}}\n\\begin{{verbatim}}\n{output_text}\n\\end{{verbatim}}\n\\end{{test-case-output}}\n\n"

with open("tesztek_doc.tex", "w", encoding="utf-8") as f_out:
    f_out.write(latex_content)

print("A 'tesztek_doc.tex' fájl sikeresen legenerálva! Ezt egy az egyben bemásolhatod a LaTeX fájlodba.")