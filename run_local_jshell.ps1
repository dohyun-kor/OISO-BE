$envFile = Join-Path $PSScriptRoot 'oiso\.env'
Get-Content $envFile | Where-Object { $_ -match '^\s*[^#][^=]*=' } | ForEach-Object {
    $parts = $_.Split('=', 2)
    [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim().Trim('"'), 'Process')
}
& 'C:\Users\dnen1\.jdks\ms-17.0.19\bin\jshell.exe' '-J-Dfile.encoding=UTF-8' --class-path 'C:\Users\dnen1\.gradle\caches\modules-2\files-2.1\org.postgresql\postgresql\42.7.11\4c21cdd1b3938f400703716d37c4e8ca4d332808\postgresql-42.7.11.jar' $args[0]
