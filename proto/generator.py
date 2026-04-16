import os

os.makedirs("tesztek", exist_ok=True)

# phyton script ami kigenerálja az input meg output fileokat, szóval nem kell egyesével létrehozni őket, elég ide beírni egymás alá
files = {
    # 24. Teszt: Sószóró fej működése - Sikertelen (Kifogyott só)
    "24_soszorofej_nincsso_in.txt": """letrehoz TakaritoJatekos takarito1
kivalaszt takarito1
penz takarito1 1000
utletrehoz Havas_Ut Telephely_1 Csomopont_A 10 normal 1
lerakodas Havas_Ut 0 Ho 5
letrehoz Hokotro kotro_01
fejvasarlas kotro_01 SoszoroFej
fejcsere kotro_01 SoszoroFej
kivalaszt kotro_01
lepes Havas_Ut 0
takarit kotro_01 0
kilepes""",

    "24_soszorofej_nincsso_exp.txt": """Parancssori prototipus indult. A parancsok listajahoz: help
OK: TakaritoJatekos 'takarito1' sikeresen letrehozva.
OK: Kivalasztva: TakaritoJatekos takarito1
OK: 'takarito1' egyenlege beallitva: 1000
OK: Ut letrehozva: Havas_Ut (1 sav).
OK: Lerakodas rogzitve: Havas_Ut sav 0 -> HO
OK: Hokotro 'kotro_01' sikeresen letrehozva.
OK: Fejvasarlas sikeres: SOSZOROFEJ, levonas: 80
OK: Fejcsere sikeres: kotro_01 -> SOSZOROFEJ
OK: Kivalasztva: Hokotro kotro_01
OK: 'kotro_01' a(z) 'Havas_Ut' 0. savjaba lepett.
ERROR: Nincs eleg so a soszorofejhez.
OK: A jatek leall.
OK: Betoltes kesz. Lefuttatott parancsok szama: 12""",

}

for filename, content in files.items():
    path = os.path.join("tesztek", filename)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content.strip() + "\n")

print(f"Sikeresen letrehozva az elso 34 teszt a 'tesztek' mappaban!")