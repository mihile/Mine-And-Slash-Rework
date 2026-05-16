$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $PSScriptRoot
$OutputDir = Join-Path $ProjectRoot "output"
$JarFolders = @(
    (Join-Path $ProjectRoot "build\libs"),
    (Join-Path $ProjectRoot "deps\Library-of-Exile-Rework\build\libs"),
    (Join-Path $ProjectRoot "deps\dungeon_realm\build\libs"),
    (Join-Path $ProjectRoot "deps\the_harvest\build\libs")
)

if (!(Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
}

foreach ($folder in $JarFolders) {
    if (!(Test-Path $folder)) {
        throw "build/libs folder not found: $folder"
    }

    $jars = Get-ChildItem $folder -Filter "*.jar" |
        Where-Object {
            $_.Name -notmatch "sources" -and
            $_.Name -notmatch "javadoc" -and
            $_.Name -notmatch "dev" -and
            $_.Name -notmatch "slim"
        } |
        Sort-Object LastWriteTime -Descending

    if ($jars.Count -eq 0) {
        throw "No playable jar found in $folder"
    }

    $jar = $jars[0]
    Copy-Item $jar.FullName $OutputDir -Force

    Write-Host "Copied playable jar:"
    Write-Host $jar.FullName
}

Write-Host "To:"
Write-Host $OutputDir
