$jdk = Get-ChildItem -Path 'C:\Program Files\Java' -Filter jdk* -Directory | Select-Object -First 1 -ExpandProperty FullName
if ($jdk) {
    $env:JAVA_HOME = $jdk
}
.\mvnw.cmd compile exec:java "-Dexec.mainClass=com.college.library.Migration"
