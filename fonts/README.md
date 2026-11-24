# Fonts

Font CJK diblokir lisensi (Noto CJK Terms). Strategi: **subset + fallback pack**.

- M0 (saat M2): tanpa font CJK dulu — hanya Latin + emoji.
- M6+: font pack (Latin + CJK + Siyam) via Model Manager-style import, atau target distribusi tanpa font CJK.

Struktur Nanti: `fonts/<pack>/<lang>.ttf` + validasi sha256.
