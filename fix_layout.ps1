#!/usr/bin/env pwsh
$layoutPath = "app\src\main\res\layout\activity_main.xml"
$lines = @(Get-Content $layoutPath)
$inKeypad = $false

for ($i = 0; $i -lt $lines.Count; $i++) {
    if ($lines[$i] -match 'android:id="@\+id/main_keypad"') {
        $inKeypad = $true
    }
    elseif ($inKeypad -and $lines[$i] -match '</GridLayout>') {
        $inKeypad = $false
    }

    if ($inKeypad) {
        # Replace all height values with 56dp
        $lines[$i] = $lines[$i] -replace 'android:layout_height="wrap_content"', 'android:layout_height="56dp"'
        $lines[$i] = $lines[$i] -replace 'android:layout_height="38dp"', 'android:layout_height="56dp"'
        $lines[$i] = $lines[$i] -replace 'android:layout_height="36dp"', 'android:layout_height="56dp"'
        $lines[$i] = $lines[$i] -replace 'android:layout_height="32dp"', 'android:layout_height="56dp"'
        $lines[$i] = $lines[$i] -replace 'android:layout_height="44dp"', 'android:layout_height="56dp"'
    }
}

$lines | Set-Content $layoutPath -Encoding UTF8
Write-Host "Updated all keypad button heights to 56dp"

