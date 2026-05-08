# Zúzmaraváros — Hóeltakarító Szimulátor (GUI változat)

A meglévő konzolos prototípus fölé épített Java Swing grafikus felület.
A program egy Pass'N'Play körökre osztott szimulációs játék, amelyet két játékos közösen játszik egy gépen: az egyik takarító (hókotróval tisztítja az utakat), a másik buszvezető (a végállomások között közlekedik).

## Fordítás és futtatás

A projekt gyökerében (`prototipusVegleges/`) található **`run-gui.bat`** fájlra dupla kattintással lefordítja és elindítja a játékot.

Parancssorból:

```
cd prototipusVegleges
javac -d bin -encoding UTF-8 -sourcepath src src\gui\Main.java
java -cp bin gui.Main
```

VS Code-ban a Java Extension Pack-kel a `gui/Main.java` fölött látható "Run" linkre kattintva is indítható.

A meglévő konzolos prototípus változatlanul fut tovább a `motor.SzkeletonProgram` belépési ponton keresztül.

**Követelmény:** Java 11+ JDK.

## Architektúra — kevert MVC

A felület **MVC** mintát követ, **kevert push/pull** kommunikációval a model és a view között:

- **Model** (`motor`, `jarmuvek`, `jatekosok`, `takaritofejek`, `terkep` csomagok) — a meglévő üzleti logika, a játékállapot. Nem tud a GUI-ról.
- **View** (`gui.view` csomag) — a Swing panelek, amik a model állapotát rajzolják.
- **Controller** (`gui.controller` csomag) — a felhasználói inputot model-akciókká fordítja.

A model `GameStateListener` interfészen keresztül **push** üzenetekkel jelzi, ha változott (kiválasztás, lépés, kör vége). A view-k feliratkoznak listenerekként, és push-üzenet hatására `repaint()`-et hívnak, ami a `paintComponent()`-ben **pull** módon olvassa a model aktuális állapotát.

## Csomagstruktúra

```
prototipusVegleges/src/
├── motor/                          (változatlan model + új listener)
│   ├── GameState.java              központi állapot, listener-mechanizmus
│   ├── GameStateListener.java      push-event interfész
│   ├── GameActions.java            akciók (endRound is itt)
│   ├── CommandRouter, CommandHandler, CommandContext  CLI szerelvény
│   ├── Difficulty, EntityType      enumok
│   ├── NamedEntity                 polimorf entitás interfész
│   └── SzkeletonProgram            CLI belépési pont
│
├── jarmuvek/                       (jármű hierarchia)
│   ├── Jarmu                       absztrakt ős, baleseti logika
│   ├── Auto                        NPC, otthon-munkahely BFS-szel
│   ├── Busz                        Pass'N'Play buszos játékos jármű
│   └── Hokotro                     takarító jármű cserélhető fejjel
│
├── jatekosok/                      (játékos hierarchia)
│   ├── Jatekos                     közös ős (vagyon, jármű-park, raktár)
│   ├── TakaritoJatekos             hókotró flotta
│   └── BuszosJatekos               busz flotta
│
├── takaritofejek/                  (Strategy minta)
│   ├── Fej                         absztrakt ős
│   ├── FejTipus, FejFactory        enum + gyári osztály
│   ├── SoproFej                    havat jobbra tolja
│   ├── HanyoFej                    havat eltünteti
│   ├── JegtoroFej                  jeget feltöri
│   ├── SoszoroFej                  10 só → olvasztás + 3 kör védelem
│   ├── SarkanyFej                  10 kerozin → azonnali olvasztás
│   └── ZuzottkoSzoroFej            10 zúzottkő → 3 körre csúszás-csökkentés
│
├── terkep/                         (úthálózat)
│   ├── Ut                          él a gráfban (sávok listája)
│   ├── Sav                         egy sáv: hó, jég, sózás, zúzottkő
│   ├── UtTipus, DepositType        enumok
│
└── gui/                            (új MVC réteg — Stage 1-3)
    ├── Main                        belépési pont
    ├── MainMenu                    Zúzmaraváros indítóképernyő
    ├── GameWindow                  fő játékablak (BorderLayout)
    ├── GameInitializer             hardcoded város felépítése
    ├── GameLayout                  csomópont-pozíciók, rajzolási konstansok
    ├── view/
    │   ├── MapPanel                térkép, sávok, járművek, jelmagyarázat
    │   ├── HudPanel                felső sáv (kör, aktív játékos, vagyon)
    │   ├── ContextPanel            jobb panel (kijelölt entitás + akciók)
    │   └── EventLogPanel           alsó eseménylog
    └── controller/
        ├── MapController           egérkattintás a térképen
        └── ActionController        gombakciók model-hívássá fordítása
```

## A játék főbb mechanikái

A részletes szabályrendszer a `DOK/` mappában lévő `latexdoc.pdf`-ben olvasható. Röviden:

- **Térkép:** gráf csomópontokkal és többsávos utakkal. Egy `Telephely`, két `Vegallomas_*`, és néhány "sima" csomópont. Utak típusa: normál, híd, alagút (alagútban nem hull a hó).
- **Időjárás:** minden Kör vége gombnyomás után minden sávra +1 hó hullik (kivéve alagút).
- **Hó → jég:** ha egy sávon 5 forgalmi (autó vagy busz) áthalad, a hó 1:1 arányban jéggé tömörödik.
- **Balesetek:** jeges sávon a járművek csúszhatnak. A valószínűség: alap (8/18/32% nehézségtől) + 3% / jégegység, csökkenthető zúzottkővel (-12%). Baleseti kiesés 2 kör. A hókotró immunis.
- **Játék vége:** 5+ baleset (kijárási tilalom), VAGY minden busz mozgásképtelen.
- **Játékosok:** mindketten egy gépet osztanak (Pass'N'Play). Először a takarító lép, aztán a buszos. Mindkettő után az NPC autók a legrövidebb úton mennek otthon és munkahely között.

## A felület működése

A főmenüben (Zúzmaraváros) választhatsz nehézséget és a hókotró kezdő fejét (söprő vagy jégtörő — a többi típust telephelyen vásárolhatsz). Indítás után a játékablak négy panelből áll:

- **Felül (HUD):** kör sorszáma, aktív játékos, mindkét játékos vagyona fityingben, balesetszám, és a "Kész vagyok" gomb.
- **Középen (Térkép):** csomópontok (sárga = telephely, zöld = végállomás, szürke = sima), sávok (fehér = havas, világoskék = jeges, szürke = sózott, barna = zúzottkős, fekete = sima), járművek (kék háromszög = hókotró, piros téglalap = busz, sárga téglalap = autó). Jelmagyarázat a jobb felső sarokban.
- **Jobbra (Kontextus panel):** ha kijelölsz egy járművet, megjelennek az adatai és az akciógombok. Mozgás (szomszédos utak listája gombokként), Várakozás, és hókotrónál a fej-specifikus Takarítás gomb. Ha a hókotró telephelyen áll, ott további 6 telephelyi akció (új hókotró vásárlás, fej vásárlás/csere, só/kerozin/zúzottkő töltés).
- **Alul (Eseménylog):** az utolsó 12 esemény fordított időrendben.

A "Kész vagyok" gomb felirata az aktív játékostól függ — felváltva váltja a játékosokat, és a buszos végén lefuttatja a kör végi szimulációt (NPC autók lépnek, havazik, idő-számlálók ketyegnek, játékvége ellenőrzés).

## Implementált funkciók etaponként

**1. etap (csontváz):** MVC infrastruktúra, push-listener mechanizmus, főmenü, hardcoded ProjLab térkép, alapvető rajzolás, kattintásos kijelölés.

**2. etap (gameplay):** Mozgás-akciók a kontextus panelen, Várakozás, fejtípus-specifikus Takarítás (nyersanyag-ellenőrzéssel), Kör vége folyamat (NPC autó BFS-pathfinding, havazás, time tick, játékvége check), körszámláló.

**3. etap (telephely + Pass'N'Play + csiszolás):** 6 telephelyi akció dialógusokkal, Pass'N'Play formalizálás (aktív játékos kijelzés, gép-átadás modálok, jármű-tulajdonjog ellenőrzés), játékvége dialógus "Új játék"/"Kilépés" gombokkal, eseménylog panel, jelmagyarázat overlay.

## Ami még nincs (jövőbeli iterációkhoz)

- Fájlból betöltött térkép (jelenleg `GameInitializer` hardcode-olja)
- Sáv-szintű mozgás (jelenleg a hókotró ugyanazon a savIndex-en marad lépéskor)
- Sávokon a hó/jég konkrét mennyiségének numerikus kijelzése (csak színkód látszik)
- Animált járműmozgás (jelenleg ugranak)
- Hangeffektek

## Karbantartás

A modell rétegben (`motor`, `jarmuvek`, `jatekosok`, `takaritofejek`, `terkep`) **csak additív** módosítások történtek — minden meglévő CLI-funkcionalitás változatlan, a `motor.SzkeletonProgram` belépési pont továbbra is használható tesztelésre. Új akció bevezetésekor:

1. A modell rétegben implementáld az üzleti logikát (pl. új metódus a `Hokotro`-ba vagy `GameActions`-be).
2. Az `ActionController`-be tegyél egy publikus metódust, ami hívja a modell-akciót, kezeli a hibákat (`warn`), majd `state.fireStateChanged()`-et hív.
3. A `ContextPanel` (vagy `HudPanel`) megfelelő helyén tegyél egy `JButton`-t, ami a controller metódusát hívja.

A push-listener miatt a UI automatikusan frissül.
