Zúzmaraváros — jármű ikonok
===========================

A játék három járműtípushoz PNG ikont tölt be ebből a mappából (ha elérhetők).
Ha valamelyik fájl nincs jelen, a kód a beépített geometrikus alakzatra esik vissza
(narancs háromszög = hókotró, piros téglalap = autó, sárga téglalap = busz).

Várt fájlok:

  hokotro.png   — A hókotró ikonja (a feltöltött narancsos Volvo)
  auto.png      — Az NPC autó ikonja (a feltöltött piros autó)
  busz.png      — A busz ikonja (a feltöltött fehér-sárga busz emoji)

Ajánlott méret: 32×32 vagy 64×64 pixel, négyzetes, átlátszó (alpha) háttérrel.
A kód automatikusan átskáláz 32×32-re, hogy elférjenek a csomópontokon.

Hová helyezd:
  prototipusVegleges/icons/hokotro.png
  prototipusVegleges/icons/auto.png
  prototipusVegleges/icons/busz.png

A betöltés indításkor egyszer történik (gui.view.VehicleIcons osztály).
Ha módosítod a képeket, indítsd újra a játékot a frissítéshez.
