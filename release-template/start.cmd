start "BuchungsstapelKontieren" .\java\bin\javaw.exe ^
--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED ^
--add-opens=javafx.controls/javafx.scene.control=ALL-UNNAMED ^
-cp ".\app\BuchungsstapelKontieren.jar;.\app\libs/*" KontierenApp ^
/secondary /minimized
exit