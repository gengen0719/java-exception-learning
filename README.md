# java-exception-learning
Javaの例外処理の学習のためのプロジェクトです

# 前書き
アプリケーションの実装において例外処理は基礎的な内容ですが非常に重要です。  
しかし実際にユーザーが利用するようなアプリケーションを作ったことがない人にとってはあまりなじみがないかもしれません。  
私も学生の頃からプログラミングの勉強はしていましたが、例外処理の重要性を実感したのはエンジニアとして働き始めてからでした。  
  
今回はそんな例外処理のJavaでの基本的な実装方法と重要性について学習していただけるようなコンテンツを用意してみました。  
ぜひ実際の運用を想像しながらやってみてください。  

# サンプルアプリケーションの構成
ウェブページにアクセスするとデータベースの `MURDER_PLAY_LOG` テーブルのデータに応じてカードを表示する  
アプリケーションと呼ぶには少し簡単すぎるテストプログラムです。  
```
【余談】最近マーダーミステリーにはまっています  
ゲームの特性上、一つのゲームを一生に一度しかプレイできないため、記録を付けられるものが欲しいなと思っています。  
```
SpringBootとDockerのMYSQLサーバーで動作します。  
（図を入れる）  
ただ実は内部の実装に問題があります。  
  
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
INSERT INTO MURDER_PLAY_LOG VALUES ("太郎","mad-mountain","狂気山脈 陰謀の分水嶺",1,"2021-10-25");
INSERT INTO MURDER_PLAY_LOG VALUES ("太郎","kikoku-kan","鬼哭館の殺人事件",1,"2022-01-09");
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
 **画面イメージ**  
  
![image](https://user-images.githubusercontent.com/74813858/169373559-3594a89d-20ce-48ca-8cc3-e9ce56be1693.png)
  
# 演習
## アプリケーションに悪戯をしてみます
さて、現在とくに何の問題もなく動作しているこのアプリケーションですが  
そこにちょっと悪戯をしてみます。  
何をするのかは秘密です。  
実際にアプリケーションを運用するに当たっては予想外の事態が発生しても  
最低限こういう動作をしなければいけないという原則があります。  
それについて考えることからスタートしてみましょう。  
  
**悪戯をされた後の画面**
画面を張る  
  
画面自体は表示されるが、今まで表示されていたゲームのカードが表示されなくなってしまいました。  
  
### ユーザーとしてアプリケーションを利用する時
まずはユーザーとしてこのアプリケーションを利用していることを想像してみてください。  
突然この画面が表示されたらどう思いますか？何をすればよいのかわかるでしょうか？  
  
### エンジニアとしてアプリケーションを運用する時
次にエンジニアとしてこのアプリケーションを運用していることを想像してみてください。  
なにも出ていないこの画面がユーザーから送られてきてトラブルの解消ができるでしょうか？  
またアプリケーションはトラブルが発生した場合に備えてログを出力していますが、そこにも何も出ていません。  
（普通はファイルに出力されますが今回はEclipseのConsoleに出ている出力がログ出力と考えてください）  
  
またHTTPステータスも200で返ってきているので運用監視ツールでも何も検知できません。    
こうなるとエンジニアは泣きながらソースコードを追っていくしかありません。  
通常ユーザーが利用している環境はデバッグをすることもできません。    
今回のアプリケーションはとても簡単なものなので問題の箇所を見つけることはできると思いますが、  
実際に運用されるアプリケーションは数十万行、数百万行というようなコードの中から問題の箇所を探さないといけないかもしれないです。  
  
こういった予期しない問題の発生に備えて例外処理を実装する必要があります。  
  
## 実装の問題点
実際に実装のどこが問題なのかを探してみましょう。  
今回は幸いにもeclipseで動かしているのでデバッグをすることができます。  
Controllerのエンドポイントから順番に処理を見ていきましょう。  
Daoの中に何やら怪しいコードが見つかりました。  
```
} catch (Exception e) {

}
```
このコードは何が問題でしょうか？  
できるだけたくさん問題点を挙げてみてください。  
  
## 例外処理のポイント
- ユーザーに問題を知らせる（例外処理に限らず、ユーザーの操作に対して適切なレスポンスを返す）
- エンジニアが原因調査できるようにログを残す
- 後片付けをする
- メソッドの役割と返り値を厳密に定義して実装する（これも例外処理に限らず）

## コードの修正
例外処理のポイントを踏まえてコードを修正してみましょう  
まず `エンジニアが原因調査できるようにログを残す` というところから。  
catchしたExceptionの内容をどこにも残さず握りつぶしているので、何も分かりません。  
ひとまず以下のようにしてConsoleにExceptionの内容を出力してみましょう  
```
} catch (Exception e) {
	e.printStackTrace();
}
```
これで少なくともログファイルにはエラーの原因が出力されるようになりました。  
エラーの内容からデータベースとの通信がうまくいっていないようなので、データベースの状態を確認してみましょう。  

## プレイ記録がないユーザーが画面を開いた時は例外か？
`WebPageController` の `userName` を別の名前に変更します。  
```
String userName = "次郎"; //本来はユーザー認証によってuserNameが渡ってくる
```
データベースには次郎さんのプレイ記録がないため、カードが表示されなくなります。  
このままの実装で良いでしょうか？  
良くない場合、どのように実装すれば良いでしょうか？  
メソッドの役割と返り値の定義、HTTPレスポンスコードの扱いを踏まえて実装してみてください。  
