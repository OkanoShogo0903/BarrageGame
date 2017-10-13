package sample;

import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PacDebug extends Application{
	final boolean hand_mode = true;
	final String file_name = "C:\\AutoPlog.txt";

	Random rnd = new Random( Instant.now().getNano());

	final int DOWN	= 0;
	final int UP	= 1;

	final int WIDTH = 900;
	final int HEIGHT = 600;
	int ClientWidth = WIDTH;
	int ClientHeight = HEIGHT;

	//	A4...210mm×297mm
	final int A4_WIDTH = 210;
	final int A4_HEIGHT = 297;

	double origin_x = WIDTH-100;
	double origin_y = HEIGHT/2;
	final double A4_center_x = origin_x - 400;
	final double A4_center_y = origin_y;

	int mouse_x;
	int mouse_y;
	int count = 0;

	int color_type = 0;

	int cx = 0;
	int cy = 0;
	int dx = 2;
	int dy = 0;

//	ArrayList<Line> line = new ArrayList<Line>();
	final Canvas canvas = new Canvas(WIDTH,HEIGHT);
	streamTest stream = new streamTest();
	Group root = new Group();
	Point start_point = new Point();
	Line pre_line = new Line();
	Hitohude now_line = new Hitohude(Color.BLUE);
//	AnchorPane center = new AnchorPane();

//    Label label = new Label("Click anywhere.");
	@Override
	public void start(Stage stage) throws Exception {

//		addMouseListener(this);
//	    Button      btn     = new Button( "ColorType1");
//	    btn.setMaxSize(100, 20);
//	    root.getChildren().add( btn );
//		root.getChildren().add(label);
        Scene   scene   = new Scene(root);
		pre_line.setStroke(null);
		root.getChildren().add(pre_line);
		pre_line.setStrokeWidth(1);

		// パレット選択枠
		Image select_img = new Image(Paths.get("etc/select.png").toUri().toString());
		ImageView select = new ImageView(select_img);
		select.setFitHeight(65);
		select.setFitWidth(65);
		select.relocate( 225 -2.5 , 160 -2.5);
		root.getChildren().add(select);


		// 背景
//	    Image img = new Image( Paths.get("etc/back.png").toUri().toString());
//	    Background bg = new Background(new BackgroundImage(img, null, null, null, null));
//	    center.setBackground(bg);

        /* アクションイベント、キーイベントの使い方を確認 */
        // ボタンに押下処理を追加する
//        EventHandler<KeyEvent>      btnActionFilter = ( event ) -> {
//        	System.out.println( "button push!" ); event.consume();
//    	};
//        btn.addEventHandler( KeyEvent.ANY , btnActionFilter );

        // シーンにマウスクリック処理用のイベント・ハンドラを設定
		now_line.setRandColor();
        EventHandler<MouseEvent>   sceneHandler     = ( event ) -> {
	    	mouse_x = -(int)event.getSceneY() + (int)origin_y;
	    	mouse_y = -(int)event.getSceneX() + (int)origin_x;

	    	// A4内をクリックした時の動作
	    	if(
    			hand_mode == true &&
    			(int)event.getSceneX() < A4_center_x + A4_WIDTH/2 &&
				(int)event.getSceneX() > A4_center_x - A4_WIDTH/2 &&
				(int)event.getSceneY() < A4_center_y + A4_HEIGHT/2 &&
				(int)event.getSceneY() > A4_center_y - A4_HEIGHT/2
			){
	        	if(event.isPrimaryButtonDown()==true){
			    	start_point.x = (int)event.getSceneX();
			    	start_point.y = (int)event.getSceneY();

			    	// マウスから出る線の描写
			    	pre_line.setStroke(null); // null いれて描写してから色を付ける
					PreLineDraw(
							event.getSceneX(),event.getSceneY(),
							(double)start_point.x,(double)start_point.y);
	        		pre_line.setStroke(now_line.getColor());

			    	System.out.println(mouse_x);
		        	System.out.println(mouse_y);
					now_line.Move(mouse_x,mouse_y);

		//	        label.setText( Integer.toString(count) );
	        	}
	        	else if(event.isSecondaryButtonDown()==true){
	        		Escape();
	        	}
        	}

        	// パレットのクリックによる動作
			if(
					(int)event.getSceneX() < 225 + 60&&
					(int)event.getSceneX() > 225
			){
				if(
						(int)event.getSceneY() < 160 + 60&&
						(int)event.getSceneY() > 160
				){
					color_type = 0;
					select.relocate( 225 -2.5 , 160 -2.5);
					now_line.setRandColor();
			    	System.out.println("---0---");
				}
				else if(
						(int)event.getSceneY() < 160 + 70 + 60&&
						(int)event.getSceneY() > 160 + 70
				){
					color_type = 1;
					select.relocate( 225 -2.5 , 160 + 70 -2.5);
					now_line.setRandColor();
			    	System.out.println("---1---");
				}
				else if(
						(int)event.getSceneY() < 160 + 140 + 60&&
						(int)event.getSceneY() > 160 + 140
				){
					color_type = 2;
					select.relocate( 225 -2.5 , 160 + 140 -2.5);
					now_line.setRandColor();
			    	System.out.println("---2---");
				}
			}

    	};
        scene.addEventHandler( MouseEvent.MOUSE_PRESSED , sceneHandler );

        // シーンにマウス動作(移動)時のイベントを追加する
        EventHandler<MouseEvent>      sceneMouseFilter  = ( event ) -> {
			if(hand_mode == true || now_line.first_call == true){
				PreLineDraw(
						event.getSceneX(),event.getSceneY(),
						(double)start_point.x,(double)start_point.y);
			}
        };
        scene.addEventFilter( MouseEvent.MOUSE_MOVED , sceneMouseFilter );

		stage.setTitle("PacDebug");
		stage.setWidth(WIDTH);
		stage.setHeight(HEIGHT);
		stage.initStyle(StageStyle.UTILITY); // クローズボックスのみの構成
		stage.centerOnScreen(); // 画面中央に配置
		stage.setResizable(false); //画面サイズ変更不可能

		canvas.setOnKeyPressed(event -> onKeyPressed(event));
		canvas.setFocusTraversable(true);

		root.getChildren().add(canvas);

		stage.setScene(scene);
		stage.show();

		ClientHeight = (int) stage.getScene().getHeight();
		ClientWidth = (int) stage.getScene().getWidth();
		cx = ClientWidth / 2;
		cy = ClientHeight / 2;

		Timer timer = new Timer();
		class GameTask extends TimerTask {
			@Override
			public void run(){
			}
		}

		GameTask task = new GameTask();
		stage.setOnCloseRequest(event -> {
			if(task != null)task.cancel();
			if(timer != null)timer.cancel();
		});

		timer.schedule(task, 1000, 100);
		Toolkit.getDefaultToolkit().beep();

		Init();
		if(hand_mode){
			HandDraw();
		}
		else{
			Draw();
		}
	}
	void PreLineDraw(double e_x,double e_y,double s_x,double s_y){
		pre_line.setEndX(e_x);
		pre_line.setEndY(e_y);
		pre_line.setStartX(s_x);
		pre_line.setStartY(s_y);
	}
	void onKeyPressed(KeyEvent event) {
		if(event.getCode()==KeyCode.ESCAPE ){
			Escape();
		}
	}
	public void HandDraw(){

	}
	public void Init(){

		// 鉄板の描写
		Line under_line = new Line(WIDTH,200 + HEIGHT/2,0,200 + HEIGHT/2);
		under_line.setStroke(Color.GRAY);
		under_line.setStrokeWidth(3);
		root.getChildren().add(under_line);

		Line over_line = new Line(WIDTH,-200 + HEIGHT/2,0,-200 + HEIGHT/2);
		over_line.setStroke(Color.GRAY);
		over_line.setStrokeWidth(3);
		root.getChildren().add(over_line);

		// 最大可動域の描写
		Line left_line = new Line(
				origin_x - 600,200 + origin_y,
				origin_x - 600,-200 + origin_y);
		left_line.setStroke(Color.GRAY);
		left_line.setStrokeWidth(3);
		root.getChildren().add(left_line);

		// ロボットアーム根本の描写
		Circle arm = new Circle(origin_x,origin_y,200, Color.DARKGRAY);
		root.getChildren().add(arm);

		// タイトルの描写
		Image title_tmp = new Image(Paths.get("etc/title.png").toUri().toString());
		ImageView title = new ImageView(title_tmp);
		title.setFitHeight(200);
		title.setFitWidth(500);
		title.relocate( -20 , -25 );
		root.getChildren().add(title);

		// 操作説明の描写
		Image text_img = new Image(Paths.get("etc/text_kakou.png").toUri().toString());
		ImageView text = new ImageView(text_img);
		text.setFitHeight(200*1.2);
		text.setFitWidth(300*1.2);
		text.relocate( 530 , 0 );
		root.getChildren().add(text);

		Image []illust_tmp = {
				new Image(Paths.get("etc/board.png").toUri().toString()),
				new Image(Paths.get("etc/book.png").toUri().toString()),
				new Image(Paths.get("etc/hasami.png").toUri().toString()),
				new Image(Paths.get("etc/mannen.png").toUri().toString()),
				new Image(Paths.get("etc/konpasu.png").toUri().toString()),
				new Image(Paths.get("etc/osipin.png").toUri().toString()),
				new Image(Paths.get("etc/pare.png").toUri().toString()),
				new Image(Paths.get("etc/cal.png").toUri().toString()),
		};
		double []illust_x = {
				60,
				50,
				300,
				730,
				570,
				760,
				580,
				85,
		};
		double []illust_y = {
				440,
				170,
				480,
				470,
				405,
				250,
				270,
				320,
		};
		for(int i = 0; i < 8 ; i++){
			ImageView illust_img = new ImageView(illust_tmp[i]);
			illust_img.setFitHeight(80);
			illust_img.setFitWidth(80);
			illust_img.relocate(illust_x[i],illust_y[i]);
			root.getChildren().add(illust_img);
		}

		// A4用紙の描写
//		ArrayList<Line> A4_line = new ArrayList<Line>();
		Line a4_line1 = new Line(
				A4_center_x + A4_WIDTH/2 ,A4_center_y + A4_HEIGHT/2 ,
				A4_center_x + A4_WIDTH/2 ,A4_center_y - A4_HEIGHT/2);
		Line a4_line2 = new Line(
				A4_center_x + A4_WIDTH/2 ,A4_center_y + A4_HEIGHT/2 ,
				A4_center_x - A4_WIDTH/2 ,A4_center_y + A4_HEIGHT/2);
		Line a4_line3 = new Line(
				A4_center_x - A4_WIDTH/2 ,A4_center_y + A4_HEIGHT/2 ,
				A4_center_x - A4_WIDTH/2 ,A4_center_y - A4_HEIGHT/2);
		Line a4_line4 = new Line(
				A4_center_x - A4_WIDTH/2 ,A4_center_y - A4_HEIGHT/2 ,
				A4_center_x + A4_WIDTH/2 ,A4_center_y - A4_HEIGHT/2);
		a4_line1.setStroke(Color.RED);
		a4_line2.setStroke(Color.RED);
		a4_line3.setStroke(Color.RED);
		a4_line4.setStroke(Color.RED);
		a4_line1.setStrokeWidth(3);
		a4_line2.setStrokeWidth(3);
		a4_line3.setStrokeWidth(3);
		a4_line4.setStrokeWidth(3);
		root.getChildren().add(a4_line1);
		root.getChildren().add(a4_line2);
		root.getChildren().add(a4_line3);
		root.getChildren().add(a4_line4);
/*
		for(int i = 0; i < A4_line.size(); i++){
			A4_line.get(i).setStroke(Color.GRAY);
			A4_line.get(i).setStrokeWidth(3);
//			root.getChildren().add(A4_line.get(i));
		}
*/
		// A4の文字の描写
		Image A4_img = new Image(Paths.get("etc/A4.png").toUri().toString());
		ImageView A4 = new ImageView(A4_img);
		A4.setFitHeight(20);
		A4.setFitWidth(60);
		A4.relocate( 320 , 450 );
		root.getChildren().add(A4);

		// パレットの描写
		Image pale_img1 = new Image(Paths.get("etc/pale1.png").toUri().toString());
		ImageView pale1 = new ImageView(pale_img1);
		pale1.setFitHeight(60);
		pale1.setFitWidth(60);
		pale1.relocate( 225 , 160 );
		root.getChildren().add(pale1);

		Image pale_img2 = new Image(Paths.get("etc/pale2.png").toUri().toString());
		ImageView pale2 = new ImageView(pale_img2);
		pale2.setFitHeight(60);
		pale2.setFitWidth(60);
		pale2.relocate( 225 , 160 + 70 );
		root.getChildren().add(pale2);

		Image pale_img3 = new Image(Paths.get("etc/pale3.png").toUri().toString());
		ImageView pale3 = new ImageView(pale_img3);
		pale3.setFitHeight(60);
		pale3.setFitWidth(60);
		pale3.relocate( 225 , 160 + 140 );
		root.getChildren().add(pale3);

	}
	public void Escape(){
		pre_line.setStroke(null);
		now_line.MoveUp();
		now_line.first_call = true;
		stream.Comment("***********************************************");
		now_line.setRandColor();
	}
	public void Draw(){
		Hitohude b1 = new Hitohude(Color.BLUE);
		stream.Comment("BLUE1");
		b1.Move(-15, 425);
		b1.Move(-15, 455);
		b1.Move( 20, 455);
		b1.Move( 20, 425);
		b1.Move( 25, 425);
		b1.Move( 25, 375);
		b1.Move(-20, 375);
		b1.Move(-20, 425);
		b1.Move( 20, 425);
		b1.MoveUp();

		Hitohude b2 = new Hitohude(Color.BLUE);
		stream.Comment("BLUE2");
		b2.Move( -15, 365);
		b2.Move( -20, 365);
		b2.Move( -20, 345);
		b2.Move( -15, 345);
		b2.MoveUp();

		Hitohude b3 = new Hitohude(Color.BLUE);
		stream.Comment("BLUE3");
		b3.Move( -5, 365);
		b3.Move( 0, 365);
		b3.Move( 0, 345);
		b3.Move( -5, 345);
		b3.MoveUp();

		Hitohude b4 = new Hitohude(Color.BLUE);
		stream.Comment("BLUE4");
		b4.Move( 10, 365);
		b4.Move( 5, 365);
		b4.Move( 5, 345);
		b4.Move( 10, 345);
		b4.MoveUp();

		Hitohude b5 = new Hitohude(Color.BLUE);
		stream.Comment("BLUE5");
		b5.Move( 20, 365);
		b5.Move( 25, 365);
		b5.Move( 25, 345);
		b5.Move( 20, 345);
		b5.MoveUp();

//*****************************************************
		Hitohude y1 = new Hitohude(Color.YELLOWGREEN);
		stream.Comment("YELLOW1");
		y1.Move( -10, 455);
		y1.Move( -10, 445);
		y1.Move(  15, 445);
		y1.Move(  15, 455);
		y1.MoveUp();

		Hitohude y2 = new Hitohude(Color.YELLOWGREEN);
		stream.Comment("YELLOW2");
		y2.Move( -10, 440);
		y2.Move( -10, 430);
		y2.Move(  -5, 430);
		y2.Move(  -5, 440);
		y2.Move( -10, 440);
		y2.MoveUp();

		Hitohude y3 = new Hitohude(Color.YELLOWGREEN);
		stream.Comment("YELLOW3");
		y3.Move( 10, 440);
		y3.Move( 10, 430);
		y3.Move( 15, 430);
		y3.Move( 15, 440);
		y3.Move( 10, 440);
		y3.MoveUp();

		Hitohude y4 = new Hitohude(Color.YELLOWGREEN);
		stream.Comment("YELLOW4");
		y4.Move( -15, 375);
		y4.Move( -15, 360);
		y4.Move(  -5, 360);
		y4.Move(  -5, 375);
		y4.MoveUp();

		Hitohude y5 = new Hitohude(Color.YELLOWGREEN);
		stream.Comment("YELLOW5");
		y5.Move( 10, 375);
		y5.Move( 10, 360);
		y5.Move( 20, 360);
		y5.Move( 20, 375);
		y5.MoveUp();
//*****************************************************

		Hitohude r1 = new Hitohude(Color.RED);
		stream.Comment("RED1");
		r1.Move( -20, 420);
		r1.Move( -35, 420);
		r1.Move( -35, 405);
		r1.Move( -20, 405);
		r1.MoveUp();

		Hitohude r2 = new Hitohude(Color.RED);
		stream.Comment("RED2");
		r2.Move( 25, 420);
		r2.Move( 40, 420);
		r2.Move( 40, 405);
		r2.Move( 25, 405);
		r2.MoveUp();

		Hitohude r3 = new Hitohude(Color.RED);
		stream.Comment("RED3");
		r3.Move( -35, 405);
		r3.Move( -35, 375);
		r3.Move( -25, 375);
		r3.Move( -25, 405);
		r3.MoveUp();

		Hitohude r4 = new Hitohude(Color.RED);
		stream.Comment("RED4");
		r4.Move( 40, 405);
		r4.Move( 40, 375);
		r4.Move( 30, 375);
		r4.Move( 30, 405);
		r4.MoveUp();

		Hitohude r5 = new Hitohude(Color.RED);
		stream.Comment("RED5");
		r5.Move( -35, 375);
		r5.Move( -35, 360);
		r5.Move( -30, 360);
		r5.Move( -30, 375);
		r5.MoveUp();

		Hitohude r6 = new Hitohude(Color.RED);
		stream.Comment("RED6");
		r6.Move( 40, 375);
		r6.Move( 40, 360);
		r6.Move( 35, 360);
		r6.Move( 35, 375);
		r6.MoveUp();

		Hitohude r7 = new Hitohude(Color.RED);
		stream.Comment("RED7");
		r7.Move( -15, 340);
		r7.Move( -15, 355);
		r7.Move(  -5, 355);
		r7.Move(  -5, 340);
		r7.Move(  -0, 340);
		r7.Move(  -0, 335);
		r7.Move( -20, 335);
		r7.Move( -20, 340);
		r7.Move(  -5, 340);
		r7.MoveUp();

		Hitohude r8 = new Hitohude(Color.RED);
		stream.Comment("RED8");
		r8.Move( 10, 340);
		r8.Move( 10, 355);
		r8.Move( 20, 355);
		r8.Move( 20, 340);
		r8.Move( 25, 340);
		r8.Move( 25, 335);
		r8.Move(  5, 335);
		r8.Move(  5, 340);
		r8.Move( 20, 340);
		r8.MoveUp();
	}
	public class Hitohude{
		ArrayList<Point> dot = new ArrayList<Point>();
		Point now_dot = new Point();
		Point past_dot = new Point();
		Point pure_now_dot = new Point();
		boolean first_call = true;
		Color color;
		Color []palet0 = {
			color = color.rgb(108,155,210),
			color = color.rgb(121,107,175),
			color = color.rgb(186,121,177),
			color = color.rgb(238,135,180),
			color = color.rgb(239,133,140),
		};
		Color []palet1 = {
			color = color.rgb(255,246,127),
			color = color.rgb(193,219,129),
			color = color.rgb(105,189,131),
			color = color.rgb(97,193,190),
			color = color.rgb(84,195,241),
		};
		Color []palet2 = {
			color = color.rgb(128,0,115),
			color = color.rgb(198,0,111),
			color = color.rgb(199,0,68),
			color = color.rgb(239,132,92),
			color = color.rgb(249,194,112),
		};

		public Color getColor(){return color;}
		public void setRandColor(){
			switch(color_type){
			case 0: // 暖色
				color = palet0[rnd.nextInt(5)];
				break;
			case 1: // 淡い
				color = palet1[rnd.nextInt(5)];
				break;
			case 2: // 濃い
				color = palet2[rnd.nextInt(5)];
				break;
			default:
	    		switch(rnd.nextInt(10)){
	    		case 0:
	    			color = color.rgb(0,128,128);
	    			break;
	    		case 1:
	    			color = color.BLUE;
	    			break;
	    		case 2:
	    			color = color.ORANGE;
	    			break;
	    		case 3:
	    			color = color.GREEN;
	    			break;
	    		case 4:
	    			color = color.CYAN;
	    			break;
	    		case 5:
	    			color = color.PINK;
	    			break;
	    		case 6:
	    			color = color.GRAY;
	    			break;
	    		case 7:
	    			color = color.YELLOWGREEN;
	    			break;
	    		case 8:
	    			color = color.MAGENTA;
	    			break;
	    		case 9:
	    			color = color.VIOLET;
	    			break;
	    		default:
	    			color = color.SALMON;
	    		}
			}
		};
		public Hitohude(Color c){
			color = c;
		}
		public void MoveUp(){
			stream.MoveWrite(pure_now_dot.x,pure_now_dot.y,UP); // アームを最後の入力の真上に移動
			if(hand_mode == true){
				Circle plot = new Circle(now_dot.x,now_dot.y,2,color);
				root.getChildren().add(plot);
			}
		}
		public void Move(int x,int y){
			// x,y値の操作
			past_dot.x = now_dot.x;
			past_dot.y = now_dot.y;
			now_dot.setLocation(-y + origin_x,-x + origin_y);
			pure_now_dot.x = x;
			pure_now_dot.y = y;

			// 画面外に描写でエラー吐かせる
			if(now_dot.x < 0 || WIDTH < now_dot.x || now_dot.y < 0 || HEIGHT < now_dot.y){
			    Text    t       = new Text(
			    		100,
			    		50,
			    		"エラーしてますよ"
			    );
//			    t.setFont(new Font(20));
			    t.setFill(Color.RED);
			    t.setWrappingWidth( 700.0 );
			    t.setUnderline( true );
			    root.getChildren().add( t );

			}
			// 初めの点では描写を行わない
			if(first_call == true){
				first_call = false;
				if(hand_mode == true){
					Circle plot = new Circle(now_dot.x,now_dot.y,2,color);
					root.getChildren().add(plot);
				}
				stream.MoveWrite(x,y,UP); // アームを次に書く点の上に移動
				stream.MoveWrite(x,y,DOWN); // アームを下に下ろす
				return;
			}
			Line line = new Line(past_dot.x,past_dot.y,now_dot.x,now_dot.y);
			line.setStroke(color);
			line.setStrokeWidth(1);
			stream.MoveWrite(x,y,DOWN); // アームを平面に移動して書く

			root.getChildren().add(line);

//	        GraphicsContext gc = canvas.getGraphicsContext2D();
//			gc.setFill(Color.BLUE);
//			gc.fillRect(now_dot.x,now_dot.y,20,20);

		}
	}
	class streamTest{
		public streamTest(){
			ClearText();
//			InitText();
		}
		public void ClearText(){ // 前回の自動生成プログラムの初期化
			try{
				File file = new File(file_name);
			    if (checkBeforeWritefile(file)){
			    	FileWriter filewriter = new FileWriter(file);

			    	filewriter.close();
		        }else{
			    	System.out.println("ファイルに書き込めません");
			    }
			}catch(IOException e){
				System.out.println(e);
			}
		}
		public void Comment(String s){
			  try{
				  File file = new File(file_name);
			      if (checkBeforeWritefile(file)){
						//PrintWriterオブジェクトを作成（追記モード）
						//FileWriterオブジェクトをBufferedWriterでラッピングし、
						//PrintWriterのファイルへの出力をバッファリングします
						PrintWriter pw = new PrintWriter(
							new BufferedWriter(new FileWriter(file, true))
						);

						pw.print("' ");
						pw.println(s);

						//PrintWriterオブジェクトをクローズ
						pw.close();
			      }else{
			    	  System.out.println("ファイルに書き込めません");
			      }
		      }catch(IOException e){
			      System.out.println(e);
			  }
		}
		public void MoveWrite(double x,double y,int state){
			  try{
				  File file = new File(file_name);
			      if (checkBeforeWritefile(file)){
						//PrintWriterオブジェクトを作成（追記モード）
						//FileWriterオブジェクトをBufferedWriterでラッピングし、
						//PrintWriterのファイルへの出力をバッファリングします
						PrintWriter pw = new PrintWriter(
							new BufferedWriter(new FileWriter(file, true))
						);

						//文字列を出力
// 記入例
//		MOVE P,@0 	(-40,390,250,90,-90,0,5)
						pw.print("	MOVE P,@0(");
						pw.print(x);
						pw.print(",");
						pw.print(y);
						pw.print(",");
						pw.print(state==DOWN?137+47:240); // TODO 高さの設定
						pw.println(",0,180,0,5)");

						//PrintWriterオブジェクトをクローズ
						pw.close();
			      }else{
			    	  System.out.println("ファイルに書き込めません");
			    	  // TODO ポップアップウィンドウにする
			      }
		      }catch(IOException e){
			      System.out.println(e);
			  }
		  }
		private void InitText(){
			try{
				File file = new File(file_name);
			    if (checkBeforeWritefile(file)){
					//PrintWriterオブジェクトを作成（追記モード）
					//FileWriterオブジェクトをBufferedWriterでラッピングし、
					//PrintWriterのファイルへの出力をバッファリングします
					PrintWriter pw = new PrintWriter(
						new BufferedWriter(new FileWriter(file, true))
					);

					//文字列を出力
					pw.print("'!TITLE ");
					pw.println("PROGRAM PRO2 ' mainの意味"
					);
/*
'!TITLE "<タイトル>"
PROGRAM PRO2 ' mainの意味
	TAKEARM ' 制御権取得
	SPEED 30 ' 1 ~ 100 実際の速度は外部速度x内部速度

'		DEPART P,@0 100

'		SET IO[64]
'		RESET IO[65]
'		DELAY 500

'		SET IO[65]
'		RESET IO[64]
'		DELAY 500

'		MOVE 			(x,y,z, ...)
'		基準は(500,0,200or250)
'		MOVE P,@0 	(500,0,200,90,-90,0,5)
*/
					//PrintWriterオブジェクトをクローズ
					pw.close();
				}
			}
			catch(IOException e){
				System.out.println(e);
			}
		}
		private boolean checkBeforeWritefile(File file){
			if (file.exists()){
				if (file.isFile() && file.canWrite()){
					return true;
				}
			}
			return false;
		}
	}
	public static void main(String[] args) {
	  	launch(args);
	}

}
