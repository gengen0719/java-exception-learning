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
`http://localhost:8080/exception-learning` にアクセスすると  
`@GetMapping("/exception-learning")` というアノテーションがついたメソッドが呼び出されます。  
そこにBreakPointを置いてデバッグしていきましょう。  
```
	@GetMapping("/exception-learning")
	public String index(Model model) {
		String userName = "太郎"; //本来はユーザー認証によってuser情報が渡ってくる
		MurderPlayLogLoader loader = new MurderPlayLogLoader();
		return loader.load(userName,model);
	}
```
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

### エンジニアが原因調査できるようにログを残す
catchしたExceptionの内容をどこにも残さず握りつぶしているので、何も分かりません。  
ひとまず以下のようにしてConsoleにExceptionの内容を出力してみましょう  
```
} catch (Exception e) {
	e.printStackTrace();
}
```
これで少なくともログファイルにはエラーの原因が出力されるようになりました。  
エラーの内容からデータベースとの通信がうまくいっていないようなので、データベースの状態を確認してみましょう。  

### ユーザーに問題を知らせる
ユーザーに問題を知らせるためには画面に情報を表示する必要があります。  
今回のサンプルアプリケーションの画面表示の仕組みを見てみましょう。  
もう一度 `@GetMapping("/exception-learning")` というアノテーションがついたメソッドから見ていきます。  
```
	@GetMapping("/exception-learning")
	public String index(Model model) {
		String userName = "太郎"; //本来はユーザー認証によってuser情報が渡ってくる
		MurderPlayLogLoader loader = new MurderPlayLogLoader();
		return loader.load(userName,model);
	}
```
実際に処理を行っているのは `MurderPlayLogLoader` というクラスです。
```
	/**
	 * ユーザーのマーダーミステリーのプレイ記録を読み込んでパラメーターの {@link Model}に詰めます。
	 * @param userName
	 * @param model
	 * @return modelに対応するtemplateを返します
	 */
	public String load(String userName,Model model) {
		MurderPlayLogDao dao = new MurderPlayLogDao();
		List<MurderPlayLog> murderPlayLogList= dao.load(userName);
		model.addAttribute("playLogList", murderPlayLogList);
		model.addAttribute("userName",userName);
		return "playlog";
	}
```
ここで返り値として返している文字列がテンプレートとして利用するhtmlファイルの名前になります。  
`playlog.html` を見てみましょう。  
```
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Test Application ExceptionLearning</title>
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css"
 integrity="sha384-9aIt2nRpC12Uk9gS9baDl411NQApFmC26EwAOH8WgZl5MYYxFfc+NcPb1dKGj7Sk" crossorigin="anonymous">
<link rel="stylesheet" href="/css/index.css">

</head>
<body>
<main>
<h2 th:text="${userName} + 'さんのマーダーミステリープレイ記録'" ></h2>

<div class="card_container">
	<div class="card" style="width:300px;" th:each="playLog : ${playLogList}" >
		<img class="card-img-top" th:src="'/img/' + ${playLog.gameId} + '.jpg'" alt="Card image">
		<div class="card-body">
			<h4 class="card-title">
				<span th:text=${playLog.gameName} />
			</h4>
			<p class="card-text">
				<span>プレイした日:</span>
          		<span th:text=${playLog.playDate} />
			</p>
        </div>
      </div>
</div>


</main>

</body>
</html>
```
ここで `${playLogList}` のように記載されている部分が変数となり  
`MurderPlayLogLoader` で `addAttribute` した値がはめ込まれて表示されます。
```
		model.addAttribute("playLogList", murderPlayLogList);
		model.addAttribute("userName",userName);
```
エラーが発生したことをユーザーに知らせる方法は色々なものがありますが、今回はエラーページを表示してみましょう。  
実はすでにControllerにエラーページを表示する仕組みを用意しています。  
```
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String commonExceptionHandler(Exception e, Model model) {
		ErrorPageLoader errorPageLoader = new ErrorPageLoader();
		return errorPageLoader.load(e, model);
    }
```
Controllerの中でException(例外)が発生すると `@ExceptionHandler(Exception.class)` というアノテーションがついたメソッドが呼び出されます。  
`ErrorPageLoader` ではエラー情報を出力し `error.html` を表示しています。
```
	public String load(Exception e,Model model) {
    	model.addAttribute("message", e.getMessage());
    	e.printStackTrace();
        return "error";
	}
```

**error.html**  

```
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Test Application ExceptionLearning</title>
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css"
 integrity="sha384-9aIt2nRpC12Uk9gS9baDl411NQApFmC26EwAOH8WgZl5MYYxFfc+NcPb1dKGj7Sk" crossorigin="anonymous">
<link rel="stylesheet" href="/css/index.css">

</head>
<body>
	<main>
		<h1 class="text-danger">予期せぬエラーが発生しました。復旧までしばらくお待ちください。</h1>
		<p th:text="${message}" />
	</main>

</body>
</html>
```
`MurderPlayLogDao` で発生した例外をControllerまで伝搬させてこの仕組みを使いましょう。  

### 後片付けをする
`MurderPlayLogDao` はデータベースにアクセスするクラスのため、データベースリソースを使用します。  
```
Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb","root", "root");// 本来はリソースファイルに記載される
		
PreparedStatement ps = con.prepareStatement("SELECT * FROM MURDER_PLAY_LOG WHERE USER_NAME = ?");
ps.setString(1, userName);
ResultSet rs = ps.executeQuery();
```
これらは使用した後には必ずcloseしてあげる必要があります。
```
DbUtils.closeQuietly(con, ps, rs);
```
しかし、例外は発生するとメソッド内の処理を中断するため、closeが実行されない可能性があります。  
途中で処理が中断されてもclose処理が行われるようにfinally節にclose処理を記載しましょう。  

### メソッドの役割と返り値を厳密に定義して実装する
`MurderPlayLogDao` のインターフェースについて考えてみましょう
もともとは
```
public List<MurderPlayLog> load(String userName)
```
となっていました。  
これは `userName` という文字列を受け取って `MurderPlayLog` という構造体のListを返すというインターフェースです。  
それが先ほどの修正で  
```
public List<MurderPlayLog> load(String userName) throws ClassNotFoundException, SQLException 
```
 `userName` という文字列を受け取って `MurderPlayLog` という構造体のListを返す  
 **ただしClassNotFoundExceptionとSQLExceptionという例外を返す可能性がある** というインターフェースに変わりました。  
この **例外を返す可能性がある** とすることで呼び出し元のメソッドは例外処理を行うことができます。  
例外も返り値の一つとして厳密に定義することが大事です。  

## プレイ記録がないユーザーが画面を開いた時は例外か？
`WebPageController` の `userName` を別の名前に変更します。  
```
String userName = "次郎"; //本来はユーザー認証によってuserNameが渡ってくる
```
データベースには次郎さんのプレイ記録がないため、カードが表示されなくなります。  
このままの実装で良いでしょうか？  
良くない場合、どのように実装すれば良いでしょうか？  
メソッドの役割と返り値の定義、HTTPレスポンスコードの扱いを踏まえて実装してみてください。  
