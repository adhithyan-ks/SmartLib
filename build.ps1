$jdk = Get-ChildItem -Path 'C:\Program Files\Java' -Filter jdk* -Directory | Select-Object -First 1 -ExpandProperty FullName
if ($jdk) {
    $env:JAVA_HOME = $jdk
} else {
    Write-Host "JDK not found!"
}
.\mvnw.cmd clean compile
