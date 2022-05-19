# java-exception-learning
Javaの例外処理の学習のためのプロジェクトです

# 前書き
アプリケーションの実装において例外処理は基礎的な内容ですが非常に重要です。  
しかし実際にユーザーが利用するようなアプリケーションを作ったことがない人にとってはあまりなじみが無いかもしれません。  
私も学生の頃からプログラミングの勉強はしていましたが、例外処理の重要性を実感したのはエンジニアとして働き始めてからでした。  
  
今回はそんな例外処理のJavaでの基本的な実装方法と重要性について学習していただけるようなコンテンツを用意してみました。  
ぜひ実際の運用を想像しながらやってみてください。  

# サンプルアプリケーションの構成
ウェブページにアクセスするとデータベースの `MURDER_PLAY_LOG` テーブルのデータに応じてカードを表示する  
アプリケーションと呼ぶには少し簡単すぎるテストプログラムです。  
  
SpringBootとDockerのMYSQLサーバーで動作します。  
（図を入れる）  
ただ実は内部の実装に重大な問題があります。  
  
実際に動かしてみましょう。  
  
# 準備
## Dockerの準備
mysqlのdocker imageのpull
```
docker pull mysql
```
docker imageの起動
```
docker run --name testdb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=testdb -P --expose=3306 -p 3306:3306 -d mysql:latest
```
テーブルの作成
```
CREATE TABLE MURDER_PLAY_LOG(
 USER_NAME VARCHAR(255),
 GAME_ID VARCHAR(255),
 GAME_NAME VARCHAR(255),
 PLAYED INT,
 PLAY_DATE DATE
)
```
データの投入
```
INSERT INTO MURDER_PLAY_LOG VALUES ("太郎","blue-moon","何度だって青い月に火を灯した",1,"2022-05-04");
INSERT INTO MURDER_PLAY_LOG VALUES ("太郎","mad-mountain","狂気山脈 陰謀の分水嶺",0,null);
INSERT INTO MURDER_PLAY_LOG VALUES ("太郎","kikoku-kan","鬼哭館の殺人事件",0,null);
```

## アプリケーションサーバーの準備
PCの適当な場所にこのリポジトリをcloneします
```
git clone https://github.com/gengen0719/java-exception-learning.git
```
Eclipseにプロジェクトをimportします
```
File > import > Gradle > Existing Gradle project select build.gradle and right click > refresh gradle project
```
ExceptionLearningApplication.java を選択して右クリックし、アプリケーションを実行します
```
Run As > Java Application 
```
以下のURLにアクセスし、画面を開きます
```
http://localhost:8080/exception-learning
```
  
   
![image](https://user-images.githubusercontent.com/74813858/169373559-3594a89d-20ce-48ca-8cc3-e9ce56be1693.png)

# 演習
## データベースが止まってたら…？
さて、現在とくに何の問題もなく動作しているこのアプリケーションですが  
もし突然データベースが止まってしまったらどうなるでしょうか……？  
データベースが止まるとかそんなことあるの？と思われる方もいるかもしれませんが、 **普通にあります**   
ハードウェアの故障、高負荷によるパフォーマンスの低下、ネットワークのトラブルなど原因となる可能性はたくさんありますが、  
アプリケーションとしてソフトウェアを提供するということは、そういった場合も想定してプログラムを書く必要があります。   
実際にDockerを停止してどうなるか見てみましょう  
  
画面を張る  
  
