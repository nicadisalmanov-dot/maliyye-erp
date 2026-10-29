@echo off
echo ========================================
echo   MaliyyeApp - Avtomatik Deploy
echo ========================================

echo.
echo [1/4] .jar fayli yaradilir...
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo XETA: .jar fayli yaradilmadi!
    pause
    exit /b 1
)
echo .jar fayli yaradildi.

echo.
echo [2/4] .jar fayli Google Cloud-a yuklenir...
gcloud compute scp target\MaliyyeApp-0.0.1-SNAPSHOT.jar maliyye-erp-server:/home/nijadi/ --zone=us-central1-b --quiet
if %ERRORLEVEL% NEQ 0 (
    echo XETA: .jar fayli yuklenmedi!
    pause
    exit /b 1
)
echo .jar fayli yuklendi.

echo.
echo [3/4] Kohno tetbiq dayandirilir...
gcloud compute ssh maliyye-erp-server --zone=us-central1-b --quiet --command="sudo pkill -9 -f MaliyyeApp || true"
echo Kohno tetbiq dayandirildi.

echo.
echo [4/4] Yeni tetbiq ise salinir...
gcloud compute ssh maliyye-erp-server --zone=us-central1-b --quiet --command="sudo DB_URL='jdbc:postgresql://ep-wild-field-b57cg85i-pooler.c-7.us-east-2.aws.neon.tech/neondb?sslmode=require' DB_USER='neondb_owner' DB_PASSWORD='npg_gloDd6cbrq7p' nohup java -jar /home/nijadi/MaliyyeApp-0.0.1-SNAPSHOT.jar > /home/nijadi/app.log 2>&1 &"
echo Yeni tetbiq ise salindi.

echo.
echo ========================================
echo   DEPLOY TAMAMLANDI!
echo ========================================
echo   Tetbiq: http://104.154.58.107:8080
echo ========================================
echo.
echo 30 saniye gozleyin, sonra brauzerde yoxlayin.
pause