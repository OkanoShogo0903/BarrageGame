package sample;

import java.awt.Point;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class NewGame extends Application{
	Random rnd = new Random( Instant.now().getNano());
	double init_time = System.currentTimeMillis();
	int KeyDeration1 = 0;
	int KeyDeration2 = 0;
	double boss_health=1;
	final int WIDTH = 1000;
	final int HEIGHT = 650;
	final double LEFT_LIM = 190; // side bar position
	final double RIGHT_LIM = WIDTH - 190;
	ArrayList<ImageView> health_list = new ArrayList<ImageView>();
	Calendar cal = Calendar.getInstance(TimeZone.getDefault());
	AnchorPane center = new AnchorPane();
	TimerTask task = null;
	Timer grayTimer = null;
	double mouse_x;
	double mouse_y;

    public class TimerControlle{
		// setLoopでloop_timeを記憶させ、CheckLoopでループ時間が経過していたらtrueを返し、それ以外でfalseを返す仕様
		private double loop_time = 0;
		private int	 loop_count = 0;
		private double last_time = System.currentTimeMillis();

		public void setLoopMs(double tmp){loop_time = tmp;};
		public void TimerReset(){last_time = System.currentTimeMillis();};
		public boolean isLoopCheck(){
			if((System.currentTimeMillis() - last_time) > loop_time){
				last_time = System.currentTimeMillis();
				return true;
			}
			return false;
		}
		public void resetLoopCount(){loop_count = 0;}
		public int LoopCount(){
			if((System.currentTimeMillis() - last_time) > loop_time){
				last_time = System.currentTimeMillis();
				loop_count++;
			}
			return loop_count;
		}
	}
//***キャラクターやアイテムの総括*********************************************************************************
	public class CharacterController {
		int stage_num = 0;
		Player player = new Player();
		HealthBar health_bar = new 	HealthBar();
		ItemControlle item = new ItemControlle();
		ArrayList<Mob1> char_list = new ArrayList<Mob1>();
		ArrayList<Boss> boss_list = new ArrayList<Boss>();
		ArrayList<Mob2> st1_char1_list = new ArrayList<Mob2>();

		TimerControlle mob_pop = new TimerControlle();
		TimerControlle stage1_pop = new TimerControlle();
		int tmp = 0;
//		TimerControlle game_time = new TimerControlle(); // ゲームスタートからの時間測定
		int pop_count = 0;

		public CharacterController(){
//		create player
			center.getChildren().add(player);

// mobの湧き速度設定
			mob_pop.setLoopMs(3300);
			stage1_pop.setLoopMs(1000);
// 試験的にボスを出す、あとでStageControlleに移植
/*
			Boss boss = new Boss();
			boss_list.add(boss);
			center.getChildren().add(boss);
*/
		}
		public void StageControlle(){
// mobを湧かせる
			switch(stage_num){
			case 0:// STAGE1
				if(pop_count < 1){
					if(mob_pop.isLoopCheck()){
						Mob1 new_char = new Mob1();
						char_list.add(new_char);
						center.getChildren().add(new_char);
						pop_count++;
					}
				}
				/*
				if(stage1_pop.isLoopCheck()){
					Mob2 new_char = new Mob2();
					st1_char1_list.add(new_char);
					center.getChildren().add(new_char);
				}
				*/
				/*
				if(mob_pop.isLoopCheck()){
					tmp++;
					if(tmp > 20){
						Boss boss = new Boss();
						boss_list.add(boss);
						center.getChildren().add(boss);
					}
				}
				*/
				break;
			case 1:
				break;
			case 2:
				break;
			}
		}
		public void CreateBall(){
			// すべてのインスタンスにボールの生成関数を呼ぶ
			for(int i = 0; i < char_list.size(); i++){
				if(char_list.get(i).isTrueDeath() == false){ // キャラクターが生きているときは弾幕生成
					char_list.get(i).CreateBall();
				}
			}
			/*
			for(int i = 0; i < boss_list.size(); i++){
				if(boss_list.get(i).isTrueDeath() == false){
					boss_list.get(i).CreateBall();
				}
			}
			*/
			player.CreateBall();
			item.CreateItem();
		}
		public void Move(){
			for(int i = 0; i < char_list.size(); i++){
				char_list.get(i).Move();
			}
			for(int i = 0; i < boss_list.size(); i++){
				boss_list.get(i).Move();
			}
			for(int i = 0; i < st1_char1_list.size(); i++){
				st1_char1_list.get(i).Move();
			}

			player.Motion();
			item.Move();
			health_bar.HealthTrans();
		}
		public void DeathDecision(){ // キャラクターの生死判定
			// 死亡はリストから削除する
			for(int i = 0; i < char_list.size(); i++){
				if(char_list.get(i).isTrueDeath()){
//					char_list.remove(i);
				}
			}
			for(int i = 0; i < boss_list.size(); i++){
				if(boss_list.get(i).isTrueDeath()){
//					boss_list.remove(i);
//					center.clearConstraints(boss_list.remove(i));
				}
			}
//			player.Motion();
		}
	}
//***エフェクト*********************************************************************************
	public class Effect extends Ball{
		TimerControlle image_timer = new TimerControlle();
		int eff_count = 0;
		double rnd_x = 0;
		double rnd_y = 0;

		Image []set_image = {
		};
		Image []damage_image = {
			new Image(Paths.get("e1.png").toUri().toString()),
			new Image(Paths.get("e2.png").toUri().toString()),
			new Image(Paths.get("e3.png").toUri().toString()),
			new Image(Paths.get("e4.png").toUri().toString()),
			new Image(Paths.get("e5.png").toUri().toString()),
		};
		Image []item_image = {
			new Image(Paths.get("u1.png").toUri().toString()),
			new Image(Paths.get("u2.png").toUri().toString()),
			new Image(Paths.get("u3.png").toUri().toString()),
			new Image(Paths.get("u4.png").toUri().toString()),
			new Image(Paths.get("u5.png").toUri().toString()),
			new Image(Paths.get("u6.png").toUri().toString()),
			new Image(Paths.get("u7.png").toUri().toString()),
			new Image(Paths.get("u8.png").toUri().toString()),
			new Image(Paths.get("u9.png").toUri().toString()),
			new Image(Paths.get("u10.png").toUri().toString()),
		};
		Image []beam_image = {
				new Image(Paths.get("ice01.png").toUri().toString()),
				new Image(Paths.get("ice02.png").toUri().toString()),
				new Image(Paths.get("ice03.png").toUri().toString()),
				new Image(Paths.get("ice04.png").toUri().toString()),
				new Image(Paths.get("ice05.png").toUri().toString()),
			};

		public Effect(double x,double y,int type){
			super(x,y);
			setFill(null);

			switch(type){
			case 0: // 弾のヒット
				setRadius(30);
				image_timer.setLoopMs(100);
				rnd_x = ( 100*rnd.nextInt(1000)/1000 ) - 50;
				rnd_y = ( 100*rnd.nextInt(1000)/1000 ) - 50;
				set_image = damage_image;
				break;
			case 1: // アイテム取得
				setRadius(90);
				image_timer.setLoopMs(100);
				set_image = item_image;
				break;
			case 2: // ビーム
				setRadius(50);
				image_timer.setLoopMs(50);
				rnd_x = ( 100*rnd.nextInt(1000)/1000 ) - 50;
				rnd_y = ( 100*rnd.nextInt(1000)/1000 ) - 50;
				set_image = beam_image;
				break;
			}
		}
		public void MoveEffect(double x,double y){
			if(eff_count <= set_image.length - 1){
				setImage( set_image[eff_count] );
				if(image_timer.isLoopCheck()){
					eff_count++;
				}
			}
			else{
				setFill(null);
			}
			Move(rnd_x + x,rnd_y + y);
		}
	}
//***プレイヤー*********************************************************************************
	public class Player extends Character{
		double cx = WIDTH/ 2.0;
		double cy = HEIGHT - 90.0;
		double dx = 3;
		double RADIUS = 20.0f;
		double SIDE_RADIUS = RADIUS + 20;
		final int SIDE_BALL_NUM = 5;
		double ball_deg = 1;
		boolean hit_flag = false;
		boolean beam_flag = true;
		int level = 0;
		double health = 10;
		public void LevelUp(){
			ItemAction();
			level++;
		}

		TimerControlle image_timer = new TimerControlle();
		TimerControlle hit_timer   = new TimerControlle();
		TimerControlle shot_timer1 = new TimerControlle();
		TimerControlle shot_timer2 = new TimerControlle();
		TimerControlle shot_timer3 = new TimerControlle();
		TimerControlle beam_hit_timer = new TimerControlle();

		Image []damage_image = {
				new Image(Paths.get("e1.png").toUri().toString()),
				new Image(Paths.get("e2.png").toUri().toString()),
				new Image(Paths.get("e3.png").toUri().toString()),
				new Image(Paths.get("e4.png").toUri().toString()),
				new Image(Paths.get("e5.png").toUri().toString()),
			};

		public double getX(){return cx;};
		public double getY(){return cy;};

		ArrayList<PlayerBall1> ball_list1 = new ArrayList<PlayerBall1>();
		ArrayList<PlayerBall2> ball_list2 = new ArrayList<PlayerBall2>();
		ArrayList<PlayerBall3> ball_list3 = new ArrayList<PlayerBall3>();
		ArrayList<Ball> side_list = new ArrayList<Ball>();
		Image[] ball_img = {
				new Image(Paths.get("shot3_r.png").toUri().toString()),
				new Image(Paths.get("star01.png").toUri().toString()),
		};
		Image[] beam_img = {
				new Image(Paths.get("b1.png").toUri().toString()),
				new Image(Paths.get("b2.png").toUri().toString()),
				new Image(Paths.get("b3.png").toUri().toString()),
				new Image(Paths.get("b4.png").toUri().toString()),
				new Image(Paths.get("b5.png").toUri().toString()),
		};
		Image[] beam_head_img = {
				new Image(Paths.get("b1_head.png").toUri().toString()),
				new Image(Paths.get("b2_head.png").toUri().toString()),
				new Image(Paths.get("b3_head.png").toUri().toString()),
				new Image(Paths.get("b4_head.png").toUri().toString()),
				new Image(Paths.get("b5_head.png").toUri().toString()),
		};
		Image side_img = new Image(Paths.get("knife.png").toUri().toString());
    	Point mousePoint = new Point();

		Image []set_image = {
					new Image(Paths.get("remi_l1.png").toUri().toString()),
					new Image(Paths.get("remi_l3.png").toUri().toString()),
					new Image(Paths.get("remi_l2.png").toUri().toString()),
					new Image(Paths.get("remi_r1.png").toUri().toString()),
					new Image(Paths.get("remi_r3.png").toUri().toString()),
					new Image(Paths.get("remi_r2.png").toUri().toString()),
					new Image(Paths.get("remi_c1.png").toUri().toString()),
					new Image(Paths.get("remi_c3.png").toUri().toString()),
					new Image(Paths.get("remi_c2.png").toUri().toString())
				};

		// プレイヤーだけ別で設定
		public Player(){
			super(WIDTH/ 2.0,HEIGHT - 90.0);
			setRadius(RADIUS);
			setCenterX(cx);
			setCenterY(cy);
			setFill(new ImagePattern(set_image[7]));
			image_timer.setLoopMs(500);
	    	hit_timer.setLoopMs(200);
	    	shot_timer1.setLoopMs(150);
	    	shot_timer2.setLoopMs(150);
	    	shot_timer3.setLoopMs(1000);
	    	beam_hit_timer.setLoopMs(100);
			CreateSideBall();
		}
		public void setHitFlag(boolean tf){hit_flag = tf;}
		public boolean getHitFlag(){return hit_flag;}
		public void Motion(){
			int image_count = 0;
			int count = 0;
//			rnd.nextInt(3) // 0~2

			// 羽ばたかせるアニメーションの処理
			// 4コマの動作で1周期にする
			// 下げる時の動作と上げる時の高さは同じとみなす
			switch(KeyDeration1){
				case -1: // 左向き
					image_count = 0;
					dx = -4;
					dy = 0;
					break;
				case 1: // 右向き
					image_count = 3;
					dx = 4;
					dy = 0;
					break;
				case -2: // 下向き
					dx = 0;
					dy = 4;
					break;
				case 2: // 上向き
					dx = 0;
					dy = -4;
					break;
				default: // 正面
					dx = 0;
					dy = 0;
					image_count = 6;
			}
			count = image_timer.LoopCount()%4;
			if(count == 3)count = 1;
			image_count += count;

			if(image_count >= 9)image_count = 0;
			setFill(new ImagePattern(set_image[image_count]));
/*
	    	PointerInfo pi = MouseInfo.getPointerInfo();
	    	mousePoint = pi.getLocation(); //mousePointはPointクラスのインスタンス
	    	mouse_x = mousePoint.getX() - 280;
	    	mouse_y = mousePoint.getY() - 90;
			if(mouse_x > cx)cx+=2;
			else if(mouse_x < cx)cx+=-2;
			if(mouse_y > cy)cy+=2;
			else if(mouse_y < cy)cy+=-2;
*/

			// 画面端行けない処理
		    if (cx < RADIUS + 2){
		    	if(dx < 0)dx = 0;
		    }
		    if(cx > WIDTH - RADIUS * 2.0 + 4) {
		    	if(dx > 0)dx = 0;
		    }

		    // ダメージくらってるときの自キャラ点滅処理
		    if(hit_flag == true){
		    	// mobでタイマーをリセットする
		    	if((hit_timer.LoopCount()%2) == 0){ // 点滅させる
			    	setFill(null);
		    	}
		    	if(hit_timer.LoopCount() == 10){	// 無敵時間終わり
		    		hit_flag = false;
		    		hit_timer.resetLoopCount();
		    	}
		    }

			cx += dx;
			cy += dy;
			setCenterX(cx);
			setCenterY(cy);

			for(int i = 0; i < ball_list1.size(); i++){
				ball_list1.get(i).Move();
				ball_list1.get(i).PlayerBallHitCheck(40);
//				if(ball_list1.get(i).getActive() == false){ball_list1.remove(i);}
			}
			for(int i = 0; i < ball_list2.size(); i++){
				ball_list2.get(i).Move();
				ball_list2.get(i).PlayerBallHitCheck(40);
//				if(ball_list2.get(i).getActive() == false){}
			}
			for(int i = 0; i < ball_list3.size(); i++){
				ball_list3.get(i).Move();
			}
			if(ball_list3.size() != 0){
				if(beam_hit_timer.isLoopCheck()){ // ビームは重すぎるので、軽量化する
					ball_list3.get(0).PlayerBeamHitCheck(80);
					ball_list3.get(11).PlayerBeamHitCheck(80);
				}
			}
			for(int i = 0; i < side_list.size(); i++){
				// r center_x center_y 初期角度
				side_list.get(i).Move(SIDE_RADIUS,10,cx,cy,i * 360/SIDE_BALL_NUM);
				side_list.get(i).PlayerBallHitCheck(40);
//				double dr = side_list.get(side_list.size()-1).getCr();
			}
			for(int i = 0; i < eff_list.size(); i++){
				eff_list.get(i).MoveEffect(cx,cy);
			}
		}
		public void CreateBall(){
			if(level >= 3 && beam_flag == true){
				beam_flag = false;
				if(shot_timer3.isLoopCheck()){
					// right
					for(int i = 0;i < 11 ; i++){
						PlayerBall3 right = new PlayerBall3(cx + 100,cy+30-(55*i),i==0?0:1,false,i);
						ball_list3.add(right);
						center.getChildren().add(right);
					}
					// left
					for(int i = 0;i < 11 ; i++){
						PlayerBall3 left = new PlayerBall3(cx - 100,cy+30-(55*i),i==0?0:1,true,i);
						ball_list3.add(left);
						center.getChildren().add(left);
					}
				}
			}
			if(level >= 2){
				if(shot_timer2.isLoopCheck()){
					PlayerBall2 ball2 = new PlayerBall2(cx,cy);
					ball2.setDxDy( rnd.nextInt(2)==0?-10:10, - (30 + level * 10));
					ball_list2.add(ball2);
					center.getChildren().add(ball2);
				}
			}

			if(level >= 1){
				if(shot_timer1.isLoopCheck()){
					PlayerBall1 new_ball = new PlayerBall1(cx,cy);
					new_ball.setDxDy( rnd.nextInt(2)==0?-10:10, - (25 + level * 20));
					ball_list1.add(new_ball);
					center.getChildren().add(new_ball);
				}
			}
			if(level >= 0){
				if(shot_timer1.isLoopCheck()){
					PlayerBall1 new_ball = new PlayerBall1(cx,cy);
					new_ball.setDxDy( rnd.nextInt(1000)/1000 , - (25 + level * 20));
					ball_list1.add(new_ball);
					center.getChildren().add(new_ball);
				}
			}
		}
		public void CreateSideBall(){
			for(int i=0;i<SIDE_BALL_NUM;i++){
				Ball new_ball = new Ball(cx + SIDE_RADIUS * Math.cos(Math.toRadians(i * 360/SIDE_BALL_NUM)),cy + SIDE_RADIUS * Math.sin(Math.toRadians(i * 360/SIDE_BALL_NUM)));
				new_ball.setDxDy(0 ,0 );
				new_ball.setDr(3);
				new_ball.setSize(5);
				new_ball.setImage(side_img);
				side_list.add(new_ball);
				center.getChildren().add(new_ball);
				side_list.get(side_list.size()-1).setRadius(10);
				side_list.get(side_list.size()-1).setRotateDr(5);
			}
		}
		public class PlayerBall1 extends Ball{
			public PlayerBall1(double x,double y){
				super(x,y);
				setImage(ball_img[0]);
				setRadius(8);
//				setFill(new ImagePattern(ball_img));
//				setRadius(20.0f);
//				setRotate(90); // deg
			}
		}
		public class PlayerBall2 extends Ball{
			public PlayerBall2(double x,double y){
				super(x,y);
//				setFill(new ImagePattern());
				setImage(ball_img[1]);
				setRadius(8);
			}
		}
		public class PlayerBall3 extends Ball{
			int anime_count = rnd.nextInt(5);
			int type;
			int create_num;
			boolean left_beam;
			public PlayerBall3(double x,double y,int t,boolean flag,int n){
				super(x,y);
				type = t;
				create_num = n;
				left_beam = flag;
				setRadius(40.0f);
			}
			@Override
			public void Move(){
				anime_count++;

				if(type == 0){
					setImage( beam_head_img[ anime_count%(beam_head_img.length) ]);
				}
				else{
					setImage( beam_img[ anime_count%(beam_head_img.length) ]);
				}

				// active = false;
				if(left_beam == true){
					setCenterX(controller.player.cx + 100);
					setCenterY(controller.player.cy+30-(55*create_num));
				}
				else{
					setCenterX(controller.player.cx - 100);
					setCenterY(controller.player.cy+30-(55*create_num));
				}

			}
		}

		/*
public void BoundCheck(){
//		自機キャラクターの当たり判定
			for(int i = 0; i < ball_list.size(); i++){
			}
		}
		*/
//		HealthDecrease
	}
//***ボスクラス*********************************************************************************
	public class Boss extends Character{
		ArrayList<BossBall1> ball_list1 = new ArrayList<BossBall1>();
		ArrayList<BossBall2> ball_list2 = new ArrayList<BossBall2>();
		ArrayList<BossBeam> beam_list = new ArrayList<BossBeam>();
		TimerControlle shot1_pop = new TimerControlle();
		TimerControlle shot2_pop = new TimerControlle();
		TimerControlle beam_pop = new TimerControlle();
		Image shot1 = new Image(Paths.get("BossShot1.png").toUri().toString());
		Image shot2 = new Image(Paths.get("BossShot2.png").toUri().toString());
		Image beam = new Image(Paths.get("BeamSample.png").toUri().toString());

		Image char_img = new Image(Paths.get("dot_marisa.png").toUri().toString());

		public Boss(){
			super(WIDTH/2,HEIGHT/2);
			health = 100;
			setRadius(50.0f);
			setImage(char_img);
			dx = 5;

			shot1_pop.setLoopMs(900);
			shot2_pop.setLoopMs(2000);
			beam_pop.setLoopMs(4000);
		}
		public void CreateBall(){
			// タイマー周りに使いやすい関数を入れて、弾幕生成部のコード負担を減らす
			if(shot1_pop.isLoopCheck()){
				// x座標15単位の感覚で弾幕を放つ
				for(int shot_x = 10; shot_x < WIDTH -10 ; shot_x += 45){ // 弾幕の生成時に画面端から10は幅を開ける
					BossBall1 new_ball = new BossBall1(shot_x + (rnd.nextInt(200)/10) - 10 , 10); // 弾幕生成のx成分にプラマイ10の範囲で誤差を設ける
					ball_list1.add(new_ball);
					center.getChildren().add(new_ball);
				}
			}
			if(shot2_pop.isLoopCheck()){
				for(int i=0;i<3;i++){
					BossBall2 new_ball = new BossBall2(cx,cy);
					new_ball.setDxDy( 2*(i - 1), 3);
					new_ball.setRotate( 180 - 5*(i - 1));
					ball_list2.add(new_ball);
					center.getChildren().add(new_ball);
				}
			}
			if(beam_pop.isLoopCheck()){
				for(int beam_y = -200; beam_y < 0;beam_y += 15 ){
					BossBeam new_ball = new BossBeam(cx,beam_y);
					beam_list.add(new_ball);
					center.getChildren().add(new_ball);
				}
			}
		}
		public void Move(){
			// TODO ボスの動きを多彩にする
			// TODO ボスの体力ゲージを出す

			// TODO ボスの体力によって攻撃パターンが変わる

			double diff = System.currentTimeMillis() - init_time;
			double theta = Math.toRadians((diff/1000)*90); // 4sで360度回転する

			cx = cx_init + 10 * (theta - Math.sin(theta));
			cy = cy_init + 10 * (1 - Math.cos(theta));

			// TODO +-40degぐらいで左右に回転させてキャラクターに動きを作る
			setCenterX(cx);
			setCenterY(cy);
			for(int i = 0; i < ball_list1.size(); i++){
				ball_list1.get(i).Move();
				ball_list1.get(i).EnemyBallHitCheck(4);
//				if(ball_list1.get(i).getActive() == false){ball_list1.remove(i);}
			}
			for(int i = 0; i < ball_list2.size(); i++){
				ball_list2.get(i).Move();
				ball_list2.get(i).EnemyBallHitCheck(15);
//				if(ball_list2.get(i).getActive() == false){ball_list2.remove(i);}
			}
			for(int i = 0; i < beam_list.size(); i++){
				beam_list.get(i).Move();
				beam_list.get(i).EnemyBeamHitCheck(8,15); // ビームの当たり判定の幅x,y
			}
			for(int i = 0; i < eff_list.size(); i++){
				eff_list.get(i).MoveEffect(cx,cy);
			}
		}
		// ボス用の特殊ダメージアクション
		@Override
		public void DamagedAction(){
			/*
			setFill(null);
			// TODO アニメーションで爆発とかつけたい
			Image img = new Image( new File( "hanabi2sharp.gif" ).toURI().toString() );
			ImageView imgView = new ImageView();
			imgView.setImage(img);
			center.getChildren().add(imgView);
*/
		}
		public class BossBall1 extends Ball{
			// BossShot1
			// 	当たり判定が小さくて早い弾を真下にばらまく
			//	ほぼ常に展開
			public BossBall1(double x,double y){
				super(x,y);
				setFill(new ImagePattern(shot1));
				setRadius(5.0f);
				setRotate(180);
				dy = 5;
			}
		}
		public class BossBall2 extends Ball{
			// BossShot2
			//	遅くて大きめの弾幕を3連射ぐらいしてくる
			// 	3秒に1回ぐらい打つ
			public BossBall2(double x,double y){
				super(x,y);
				setFill(new ImagePattern(shot2));
				setRadius(20.0f);
//				setRotate(180);
				dy = 3;
			}
			// 動作は工夫したい
			// Move()
		}
		public class BossBeam extends Ball{
			// BeamSample
			// 	あんまり考えてない
			public BossBeam(double x,double y){
				super(x,y);
				setFill(new ImagePattern(beam));
				setRadius(20.0f);
				setRotate(90); // deg
				dy = 5;
			}
		}

	}
//***Mob**********************************************************************************
	public class Mob2 extends Character{
		Image char_img = new Image(Paths.get("car.png").toUri().toString());
		double diff = System.currentTimeMillis() - init_time;

		public Mob2(){
			super(rnd.nextInt(2) == 0 ? LEFT_LIM : WIDTH , rnd.nextInt(HEIGHT));
			health = 3;
			setRadius(50.0f);
			setImage(char_img);
			if(cx == WIDTH)dx = -10; // 左端から生成されたら、右に行く
			else dx = 10;
		}
		public void Move(){
			// Mob2の動作
			cx += dx;

			setCenterX(cx);
			setCenterY(cy);

			EnemyBallHitCheck(30);

		}
	}
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public class Mob1 extends Character{
		boolean auto_flag = false;
		double circle_x[] = {
				50,
				-20,
				-60
		};
		double circle_y[] = {
				40,
				-60,
				70
		};
		ArrayList<MobBall> ball_list = new ArrayList<MobBall>();
		TimerControlle shot_pop1 = new TimerControlle();
		TimerControlle shot_pop2 = new TimerControlle();
		Image[] circle_img = {
				new Image(Paths.get("circle.png").toUri().toString()),
				new Image(Paths.get("jin_conn.png").toUri().toString()),
				/*
				new Image(Paths.get("jin2.png").toUri().toString()),
				new Image(Paths.get("m4s.png").toUri().toString()),
				new Image(Paths.get("m6s.png").toUri().toString()),
				new Image(Paths.get("m9_1s.png").toUri().toString()),
				*/
		};
		Image char_img = new Image(Paths.get("yumekou.png").toUri().toString());
		Image[] ball_img = {
				new Image(Paths.get("shot1_green.png").toUri().toString()),
				new Image(Paths.get("shot1_blue.png").toUri().toString()),
				new Image(Paths.get("shot1_y.png").toUri().toString()),
		};
		Ball circle1 = new Ball(cx,cy);
		Ball circle2 = new Ball(cx,cy);
//		Ball circle3 = new Ball(cx,cy);
//		Ball circle4 = new Ball(cx,cy);
//		Ball circle5 = new Ball(cx,cy);
//		Ball circle6 = new Ball(cx,cy);

		public Mob1(){
			super(WIDTH/2,50); // キャラクターの設定
			health = 300;
			setRadius(50.0f);
			setImage(char_img);
			dx = 2;
			dy =0;

			shot_pop1.setLoopMs(300); // タイマーのセット
			shot_pop2.setLoopMs(400);

			center.getChildren().add(circle1); // サークルの設定
			circle1.setImage(circle_img[0]);
			circle1.setSize(65);
			circle1.setRotateDr(3);

			center.getChildren().add(circle2); // サークルの設定
			circle2.setImage(circle_img[1]);
			circle2.setSize(140);
			circle2.setRotateDr(-0.5);
/*
			center.getChildren().add(circle3); // サークルの設定
			circle3.setFill(null);
			circle3.setSize(150);
			circle3.setRotateDr(2);

			center.getChildren().add(circle4); // サークルの設定
			circle4.setImage(circle_img[3]);
			circle4.setSize(40);
			circle4.setRotateDr(6);

			center.getChildren().add(circle5); // サークルの設定
			circle5.setImage(circle_img[4]);
			circle5.setSize(40);
			circle5.setRotateDr(-8);

			center.getChildren().add(circle6); // サークルの設定
			circle6.setImage(circle_img[5]);
			circle6.setSize(40);
			circle6.setRotateDr(10);
*/
		}
		public void CreateBall(){
			// shot1 create

			if(shot_pop1.isLoopCheck()){
				Image set_img = ball_img[ rnd.nextInt(3) ]; // ランダムに色を付ける
//				Image set_img = ball_img[0];
				double deg = rnd.nextInt(90);
				for(int num = 0; num < 8 ;num++){
					MobBall new_ball = new MobBall(cx, cy);
					new_ball.setImage(set_img);
					new_ball.setSize(7);
					new_ball.setInitTheta(45 * num + deg);
					ball_list.add(new_ball);
					center.getChildren().add(new_ball);
				}
			}
			/*
			// shot2 create
			if(shot_pop2.isLoopCheck()){
				for(int num = 0;num < 4;num++){
					MobBall new_ball = new MobBall(cx, cy);
					new_ball.setImage(shot2);
					new_ball.setSize(7);
					ball_list.add(new_ball);
					center.getChildren().add(new_ball);
				}
			}
			*/
		}
		public void Move(){
			double diff = System.currentTimeMillis() - init_time;

			// mob1の体力でゲームの終わりを判定する
			boss_health = health;

			// Mob1の動作
//			cy += dy;
			//4sで360度回転する
//			cy = cy_init + 30*Math.sin(Math.toRadians((diff/1000)*90));

			if(KeyDeration2!=0){
				auto_flag = true;
			}
			if(auto_flag == true){ // 手動で動かす時用
				switch(KeyDeration2){
				case -1: // 左向き
					dx = -4;
					break;
				case 1: // 右向き
					dx = 4;
					break;
				case 2: // オートに戻す
					dx = 2;
					auto_flag = false;
					break;
				default: // 正面
					dx = 0;
				}
			}
			else{ // オートで動かす時用
				if(cx < LEFT_LIM + 200 && dx < 0 ){
					dx = -dx;
				}
				if(RIGHT_LIM - 200 < cx && dx > 0 ){
					dx = -dx;
				}
			}

			if (cx > WIDTH/2 + 200) { // 左右の境界に衝突
		    	dx = -dx;
		    }
			if (cx < WIDTH/2 -200)dx = -dx;


			cx += dx;
			setCenterX(cx);
			setCenterY(cy);

			// 弾幕の動作
			for(int i = 0; i < ball_list.size(); i++){
				double theta = Math.toRadians( (diff/1000) * 15 ); // 1sで15度回転する
				// x = R cos(θ+initθ)
				// y = R sin(θ+initθ)
//				double R = 60*(ball_list.get(i).getCreatedTimeMs()/1000); // 進む距離/second
				double R = ball_list.get(i).getCr();
				ball_list.get(i).setCr(R + 2.5); // 呼び出されるたびに2ずつ進む
				double set_x = R * Math.cos(theta + Math.toRadians(ball_list.get(i).init_theta) ) + ball_list.get(i).cx_init; // thetaはラジアン
				double set_y = R * Math.sin(theta + Math.toRadians(ball_list.get(i).init_theta) ) + ball_list.get(i).cy_init;
				ball_list.get(i).Move(set_x,set_y);
				ball_list.get(i).EnemyBallHitCheck(8);
				if(ball_list.get(i).getActive() == false){ball_list.set(i,null);} // 使わなくなったらnullを入れる
			}
			ball_list.removeAll(Collections.singleton(null));

			// サークルの動作
			circle1.Move(cx,cy);
			circle2.Move(cx,cy);
//			circle3.Move(cx,cy);
			/*
			for(int i=0;i<3;i++){
				double dist;
				double diff_x;
				double diff_y;
//				do{ 		// 歯車は敵キャラから一定距離以内をランダムに線形移動
					do{
						diff_x = 3*((rnd.nextInt(100)/100) - 0.5);
						diff_y = 3*((rnd.nextInt(100)/100) - 0.5);
						dist = DistanceCalc(cx + circle_x[i] + diff_x,cy + circle_y[i] + diff_y);
//						if(cy + circle_y[i] + diff_y < 0)continue;
					}while(dist > 100);
//				}while( dist < 100);
				circle_x[i]+= diff_x;
				circle_y[i]+= diff_y;

				switch(i){
				case 0:
					circle4.Move(cx + circle_x[0],cy + circle_y[0]);
					break;
				case 1:
					circle5.Move(cx + circle_x[1],cy + circle_y[1]);
					break;
				case 2:
					circle6.Move(cx + circle_x[2],cy + circle_y[2]);
					break;
				}
			}
			*/
//			if(health <100){
//				circle4.Move(cx + circle_x[0],cy + circle_y[0]);
//				circle5.Move(cx + circle_x[1],cy + circle_y[1]);
//				circle6.Move(cx + circle_x[2],cy + circle_y[2]);
//			}
			for(int i = 0; i < eff_list.size(); i++){
				eff_list.get(i).MoveEffect(cx,cy);
			}
		}
		public class MobBall extends Ball{
			public MobBall(double x,double y){
				super(x,y);
//				dx = rnd.nextInt(1000)/100 - 5;
//				dy = 3;
			}
		}
	}
//***Characterクラス*********************************************************************************
	public class Character extends Ball{
		double health;
//		double cx = WIDTH/ 2.0;
//		double cy = 40.0;
//		double cx = 40.0;
//		double cy = 30.0;
//		double dx = 0;
//		double dy = 0;
//		int RADIUS = 30;
		// charimgが設定されていなかったらデフォルトで真っ黒に塗りつぶす初期設定
		Image char_img = null;
		Image tmp_img;
		int create_num = 0;
		ArrayList<Effect> eff_list = new ArrayList<Effect>();
		public double getCx(){return cx;}
		public double getCy(){return cy;}
		public void setImage(Image tmp_image){
			setFill(new ImagePattern(tmp_image));
		}
		public void HealthIncrease(int add){health += add;}
		public void HealthDecrease(int decrease){health += -decrease;}
		public boolean isTrueDeath(){
			if(health <= 0)return true;
			return false;
		}
		public void SetPosition(double x,double y){
			cx = x;
			cy = y;
		}
		public Character(double x,double y){
			cx_init = cx;
			cy_init = cy;
			cx = x;
			cy = y;
			setCenterX(cx);
			setCenterY(cy);
		}
		// モブ用の通常ダメージアクション
		public void BeamDamagedAction(){
			Effect new_eff = new Effect(cx,cy,2);
			eff_list.add(new_eff);
			center.getChildren().add(new_eff);
		}
		public void DamagedAction(){
			Effect new_eff = new Effect(cx,cy,0);
			eff_list.add(new_eff);
			center.getChildren().add(new_eff);
		}
		public void ItemAction(){
			Effect new_eff = new Effect(cx,cy,1);
			eff_list.add(new_eff);
			center.getChildren().add(new_eff);
		}
	}
//***アイテム関連*********************************************************************************

    public class ItemControlle {
    	double pop_x = 0;
    	double pop_y = 20;
		ArrayList<Item> item_list = new ArrayList<Item>();
		TimerControlle item_pop_timer = new TimerControlle();
		Image []item_image = {
			new Image(Paths.get("item1.png").toUri().toString()),
			new Image(Paths.get("item2.png").toUri().toString()),
			new Image(Paths.get("item3.png").toUri().toString())
		};

		public ItemControlle(){
			item_pop_timer.setLoopMs(3000); // 一個目のアイテムポップ周期の設定
		}
		public void CreateItem(){
			// アイテムポップの時間的な処理実装部分
			// 数秒に１回　ランダムにアイテムを落とす
			if(item_pop_timer.isLoopCheck() == false)return;
			item_pop_timer.TimerReset();
			item_pop_timer.setLoopMs(rnd.nextInt(3000) + 1000); // 一個目以降のアイテムポップ周期の設定

			// アイテムの生成
			// 当たり判定用に、敵弾幕判定を付ける
			do{
				pop_x = rnd.nextInt(WIDTH - 40) + 20;
			}while(pop_x < LEFT_LIM + 50 || RIGHT_LIM - 50 < pop_x );
			Item new_item = new Item(pop_x,pop_y);
			item_list.add(new_item);
			center.getChildren().add(new_item);
//			cx = WIDTH * rnd.nextInt(100)/100;
		}
		public void Move(){
			for(int i = 0; i < item_list.size(); i++){
				item_list.get(i).Move();
				item_list.get(i).HitCheck();
			}
		}
	    public class Item extends Ball{
    		int item_num = rnd.nextInt(3);
    		boolean item_acitve = true;
	    	public Item(double cx,double cy){
	    		super(cx,cy);
	    		dx = 0;
	    		dy = 2.3;
				setRadius(10);
				setImage(item_image[item_num]); // アイテムイメージの貼り付け
	    	}
	    	public void HitCheck(){
	    		if(item_acitve == false)return;
		     	double player_dist = Math.sqrt(Math.pow(controller.player.getX() - this.cx,2) + Math.pow(controller.player.getY() - this.cy,2));

				if (player_dist < 30){     // プレイヤーとの衝突 アイテムの吸い付き大きめに設定
					// TODO 近づいたキャラクターにアイテムが吸い込まれる動作を追加
					item_acitve = false;
					setFill(null); // アイテムを消す
					controller.player.LevelUp();
				}
	    	}
	    }
    }
//***HPバー*********************************************************************************
	public class HealthBar{
		ArrayList<Block> block_list = new ArrayList<Block>();
		public HealthBar(){ // 体力バーの作成
			double x = LEFT_LIM + 30;
			double y = -15;
			int type;
			double threshold;
			for(int i = 0; i < 60 ;i++){
				threshold = i*(300/60);
				if(i <15)type = 0;
				else if(i < 30)type = 1;
				else type = 2;

				Block hp = new Block(x -25 + i*10 , y , type , threshold);
				block_list.add(hp);
			}
		}
		public void HealthTrans(){
			for(int i = 0; i < block_list.size(); i++){
				block_list.get(i).Vanish();
			}
		}
		public class Block{
			TimerControlle trans = new TimerControlle();
			Image[] img = {
					new Image(Paths.get("h1.png").toUri().toString()),
					new Image(Paths.get("h2.png").toUri().toString()),
					new Image(Paths.get("h3.png").toUri().toString()),
				};
			double vanish;
			int type;
			int loop_count = 0;
			ImageView img_view;
			public Block(double x,double y,int t,double thre){
				type = t;
				img_view = new ImageView(img[type]);
//				img_view.setImage(img[type]);
				img_view.setFitWidth(10);
				img_view.setFitHeight(20);
				img_view.relocate( x,y );
				center.getChildren().add(img_view);

				vanish = thre;
				trans.setLoopMs(300);
			}
			public void Vanish(){ // 体力バーが消える時の点滅処理
				if(loop_count >= 4){img_view.setImage(null);return;}
				if(boss_health > vanish)return;

				if(trans.isLoopCheck()){
					if(loop_count%2 == 0)img_view.setImage(img[type]);
					else img_view.setImage(null);
					loop_count++;
				}
			}
		}
	}

//***Ballの親クラス*********************************************************************************
    public class Ball extends Circle{
		double cx; // ボールの座標
		double cy;
		double cx_init = 0;
		double cy_init = 0;
		double cr = 0; // 移動角度
		double c_rotate; // 回転
		double dx = 0; // ボールの進む量
		double dy = 0;
		double dr = 0; // 移動角度の量
		double d_rotate; // 回転量
		double init_theta = 0;
		public void setCr(double c){cr = c;};
		public void setDr(double d){dr = d;};
		public double getCr(){return cr;};
		public double getDr(){return dr;};
		public void setInitTheta(double t){init_theta = t;};
//		boolean enemy_shot = true;
//		public void EnemyShotFalse(){enemy_shot = false;};
		boolean active = true;
		public boolean getActive(){return active;}
		int damage = 1;
		boolean go_rotate = false;

		final int RADIUS = 20;
		public void setDxDy(double x,double y){dx = x;dy = y;}
		public void setImage(Image tmp_image){
			setFill(new ImagePattern(tmp_image));
		}
		public void setRotateDr(double d){
			d_rotate = d;
			go_rotate = true;
		}
		public void setSize(double size){
			setRadius(size);
		}
		public Ball(double x,double y){
			setRadius(5.0f);
//			start_time = System.currentTimeMillis();
			cx_init = x;
			cy_init = y;
//			setFitWidth(10.0);
//			setPreserveRatio(true);
			cx = x;
			cy = y;
			setCenterX(cx);
			setCenterY(cy);
		}
		public Ball(){
			super();
//			start_time = System.currentTimeMillis();
		}
		public void Move(){
			/*
			if (cx < RADIUS + 2 || cx > WIDTH - RADIUS * 2.0 + 4) { // 左右の境界に衝突
		    	dx = -dx;
		    }
		    */
			cx = cx + dx;
		    cy = cy + dy;
		    if(go_rotate)setRotate(c_rotate += d_rotate);

		    if(cx < 0 || WIDTH < cx ||  cy < -40 || HEIGHT < cy){ // 画面外に出たらリストから外すように申請する
		    	active = false;
		    }

			setCenterX(cx);
			setCenterY(cy);
		}
		public void Move(double x,double y){
			cx = x;
			cy = y;
		    if(go_rotate)setRotate(c_rotate += d_rotate);
		    if(cx < 0 || WIDTH < cx ||  cy < -40 || HEIGHT < cy){ // 画面外に出たらリストから外すように申請する
		    	active = false;
		    }
			setCenterX(cx);
			setCenterY(cy);
		}
		public void Move(double r,double omega,double center_x ,double center_y,double init_cr){ // 円形に動くよ

			cr += dr;

			cx = center_x + r * Math.cos(Math.toRadians(init_cr + cr));
			cy = center_y + r * Math.sin(Math.toRadians(init_cr + cr));

		    if(cx < 0 || WIDTH < cx ||  cy < -40 || HEIGHT < cy){ // 画面外に出たらリストから外すように申請する
		    	active = false;
		    }

			// 絶対時間に対応して動くようにしたい
		    if(go_rotate)setRotate(c_rotate += d_rotate);
			setCenterX(cx);
			setCenterY(cy);
		}
		public void EnemyBallHitCheck(double threshold){ // 敵が射つボールの着弾
			// 味方と敵で当たり判定の関数を分けてフレンドリーファイア回避。自分の弾に被弾しない仕様
		    // ボールとプレイヤーの距離
		    // sqrtが√、powが乗算
    		double player_dist = Math.sqrt(Math.pow(controller.player.getX() - cx,2) + Math.pow(controller.player.getY() - cy,2));

			if (player_dist < threshold){     // 敵の弾がプレイヤーに衝突
				if(controller.player.getHitFlag() == false){
					controller.player.health--;
					setFill(null); // ボールを消す
					controller.player.DamagedAction(); // ダメージアニメ再生
					for(int n = 0; n < 10 ; n++){
						if(health_list.get(n).getImage()!=null){
							health_list.get(n).setImage(null);
							break;
						}
					}

					active = false;
//					Toolkit.getDefaultToolkit().beep();
					// 当たりフラグを立てて、自キャラを点滅させてダメージを表現するためのタイマーリセット
					controller.player.hit_timer.TimerReset();
					controller.player.setHitFlag(true);
				}
			}
		}
		public void EnemyBeamHitCheck(double threshold_x,double threshold_y){ // 敵が射つビームの着弾
			// ビームの幅を自分で設定?
    		double diff_x = controller.player.getX() - cx;
    		if(diff_x < 0)diff_x = -1 * diff_x;
    		double diff_y = controller.player.getY() - cy;
    		if(diff_y < 0)diff_y = -1 * diff_y;

    		if( diff_x < threshold_x ){
    			if( diff_y < threshold_y){
    				// 当たった
					controller.player.hit_timer.TimerReset();
					controller.player.setHitFlag(true);
    			}
    		}
		}
		public void PlayerBallHitCheck(double threshold){ // プレイヤーが射つボールの着弾
			double dist;
			for(int i = 0; i < controller.char_list.size() ; i++){
				dist = DistanceCalc(controller.char_list.get(i).getCx(),controller.char_list.get(i).getCy());
				if(dist < threshold){
					controller.char_list.get(i).HealthDecrease(damage);
					controller.char_list.get(i).DamagedAction();
					setFill(null);
				}
			}
			for(int i = 0; i < controller.boss_list.size() ; i++){
				dist = DistanceCalc(controller.boss_list.get(i).getCx(),controller.boss_list.get(i).getCy());
				if(dist < threshold){
					controller.boss_list.get(i).HealthDecrease(damage);
					controller.boss_list.get(i).DamagedAction();
					setFill(null);
				}
			}
			/*
			for(int i = 0; i < controller.st1_char1_list.size() ; i++){
				dist = DistanceCalc(controller.st1_char1_list.get(i).getCx(),controller.st1_char1_list.get(i).getCy());
				if(dist < threshold){
					controller.st1_char1_list.get(i).HealthDecrease(damage);
					controller.st1_char1_list.get(i).DamagedAction();
					setFill(null);
				}
			}
			*/
		}
		public void PlayerBeamHitCheck(double threshold_x){ // プレイヤーのビーム
			double dist;
			for(int i = 0; i < controller.char_list.size() ; i++){
				dist = cx - controller.char_list.get(i).cx;
				if(dist<0)dist = -1 * dist;
				if(dist < threshold_x){
					controller.char_list.get(i).HealthDecrease(damage);
					controller.char_list.get(i).BeamDamagedAction();
				}
			}
		}
		public double DistanceCalc(double dist_x,double dist_y){
			double diff_x = dist_x - cx;;
			double diff_y = dist_y - cy;;
			if(diff_x < 0)diff_x = -diff_x;
			if(diff_y < 0)diff_y = -diff_y;
			return Math.sqrt( Math.pow(diff_x,2) + Math.pow(diff_y,2));
		}
	}
//------------------------------------------
//    public class aaa implements ActionListener{
//    }
//--------------------------------------------------------------------------------------------------------
    //Cicle.setFill
	//Circle ball[] = new Circle[10];
//	Ball ball = new Ball();
//	CharacterController controller = new CharacterController();
	// Circle ball = new Circle(0, 20, RADIUS, Color.BLUE);
	CharacterController controller = new CharacterController();
//	Rectangle racket = new Rectangle();
	double y0Client = 0; // 上辺のクライアントアンド座標
	Timeline timeline = null;
	int point = 0;

	public void start(Stage stage) throws Exception {
    stage.setTitle("じゃゔぁ で げーむ を つくってみたよ");
	stage.initStyle(StageStyle.UTILITY);
//	center.getChildren().add(new (Ball));

// ラケット
/*
	racket.setWidth(35);
	racket.setHeight(5);
	racket.setFill(Color.BLACK);*/

	 initGame();
	 // メニューバーとメニュー項目を作成する
	MenuBar menuBar = new MenuBar();
	menuBar.setUseSystemMenuBar(true);
	Menu fileMenu = new Menu("ファイル");

	menuBar.getMenus().add(fileMenu);
	MenuItem mnuSente = new MenuItem("スタート");
	mnuSente.setOnAction(event -> {
		startGame(stage);
		// 指定したタスクが遅延の後に開始され、固定遅延実行を繰り返す
//	    grayTimer.schedule(task, 250, 10);
	});
	MenuItem mnuExit = new MenuItem("終了");
	mnuExit.setOnAction(event -> {
	 	cleanup(); Platform.exit();});
	Menu qb = new Menu("File");

	menuBar.getMenus().add(qb);
	MenuItem qbYes = new MenuItem("Start");
	qbYes.setOnAction(event -> startGame(stage));
	MenuItem qbNo = new MenuItem("No");
	qbNo.setOnAction(event -> {
	  	cleanup(); Platform.exit();});

	fileMenu.getItems().addAll(mnuSente, mnuExit);
	qb.getItems().addAll(qbYes, qbNo);
	stage.setOnCloseRequest(event -> cleanup()); // ウィンドウが閉じるとき
    BorderPane root = new BorderPane();
//    center.setBorder(new Border(new BorderStroke(Color.BLACK,
 //           BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT )));
    center.setFocusTraversable(true);
    center.setOnKeyPressed(event -> {
    	// プレイヤー
    	if(event.getCode() == KeyCode.LEFT){
    		KeyDeration1 = -1;
    	}
    	else if(event.getCode() == KeyCode.RIGHT){
    		KeyDeration1 = 1;
    	}
    	else if(event.getCode() == KeyCode.UP){
    		KeyDeration1 = 2;
    	}
    	else if(event.getCode() == KeyCode.DOWN){
    		KeyDeration1 = -2;
    	}

    	// ドリバー
    	if(event.getCode() == KeyCode.A){
    		KeyDeration2 = -1;
    	}
    	else if(event.getCode() == KeyCode.D){
    		KeyDeration2 = 1;
    	}
    	else if(event.getCode() == KeyCode.S){
    		KeyDeration2 = 2;
    	}
    });

    // バックグラウンドイメージを表示する
    Image img = new Image( Paths.get("zireiden.png").toUri().toString());
//    Image img = new Image( Paths.get("space.png").toUri().toString());
    Background bg = new Background(new BackgroundImage(img, null, null, null, null));
    center.setBackground(bg);

    root.setTop(menuBar);
    root.setCenter(center);
    stage.setScene(new Scene(root, WIDTH, HEIGHT));
    stage.show();
    stage.setOnCloseRequest(event -> cleanup());

/*
    JPanel p = new JPanel();
    JButton start_button = new JButton();
	ImageIcon bt_icon = new ImageIcon(Paths.get("gamestart.png").toUri().toString());
    start_button.setIcon(bt_icon);
    start_button.setSize(WIDTH/2, WIDTH/2);
    p.add(start_button);
*/
    //
// ゲーム説明
    /*
	Image img_tmp = new Image(Paths.get("gamestart.png").toUri().toString());
	ImageView start_img = new ImageView(img_tmp);
    start_img.relocate((WIDTH/2) - 150 , 120 + (HEIGHT/2));
	root.getChildren().add(start_img);
	// TODO ゲーム始まったら消す
	*/

// サイドバー作成部分
	Image left_tmp = new Image(Paths.get("left_bar.png").toUri().toString());
	ImageView left_img = new ImageView(left_tmp);
	left_img.setFitHeight(HEIGHT - 25);
	left_img.setFitWidth(LEFT_LIM);
	left_img.relocate( 0 , 25 );
	root.getChildren().add(left_img);

	Image right_tmp = new Image(Paths.get("right_bar.png").toUri().toString());
	ImageView right_img = new ImageView(right_tmp);
	right_img.setFitHeight(HEIGHT - 25);
	right_img.setFitWidth((WIDTH - RIGHT_LIM));
	right_img.relocate( RIGHT_LIM , 25 );
	root.getChildren().add(right_img);

	for(int num = 0; num<10 ; num++){
		Image tmp = new Image(Paths.get("tanni.png").toUri().toString());
		ImageView tanni_img = new ImageView(tmp);
		tanni_img.setFitHeight(45);
		tanni_img.setFitWidth(90);
	    tanni_img.relocate( 50 , 38 + num * 60.5 ); // 25はメニューバーの高さ
		root.getChildren().add(tanni_img);
		health_list.add(tanni_img);
	}

	// テキスト
	/*
    Text    t       = new Text(
    		500,
    		HEIGHT/2,
    		"ボールド、ITALIC、下線、文字寄せ"
    );
//    t.setX( RIGHT_LIM + (WIDTH - RIGHT_LIM)/2 );
    t.setFont(new Font(20));
    //    t.setFont( Font.font( "Meiryo UI" , FontWeight.BOLD  , FontPosture.ITALIC , 35 ) );
    t.setFont(Font.font ("Verdana", 20));
    t.setFill(Color.RED);
    t.setTextAlignment( TextAlignment.CENTER );
    t.setWrappingWidth( 700.0 );
    t.setUnderline( true );
    center.getChildren().add( t );
    */

}
 void startGame(Stage stage)
{
    initGame();
    timeline = new Timeline(
            new KeyFrame(Duration.millis(40),
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
            		// マウス位置の取得
                    // ボールを移動する
            		controller.StageControlle();
            		controller.CreateBall();
            		controller.Move();
            		controller.DeathDecision();

            		// ガベージコレクションの明示的呼び出し
            		System.gc();
            		// キー入力のリセット
        			KeyDeration1 = 0;
        			KeyDeration2 = 0;

		    		if(controller.player.health <= 0){gameEnd(stage);}
		    		if(boss_health <= 0){gameEnd(stage);}
                }
            }));
	timeline.setCycleCount(Timeline.INDEFINITE);
	timeline.setOnFinished(event -> gameEnd(stage));
/*
 // 乱数を初期化する
    Random rnd = new Random( Instant.now().getNano());
    dx = rnd.nextInt(10) - 5;
    if (dx == 0)
        dx = 1;
    dy = 7;
    cx = ball.getX();
    cy = ball.getY();
*/
    timeline.play();
}
void initGame()
{
//    racket.setX((WIDTH - racket.getWidth()) / 2);
//    racket.setY(250);
//    mob.setCenterX(WIDTH/ 2.0);
//    mob.setCenterY(40.0);

    point = 0;
}
 void gameEnd(Stage stage)
{
    timeline.stop();
    timeline = null;
    Alert dlg = new Alert(AlertType.INFORMATION);
    if(controller.player.health > 0){
    	dlg.setTitle("ゲームクリア！");
    	dlg.setHeaderText(String.format("ドリバーを倒したよ！"));
    }
    else{
        dlg.setTitle("ゲームオーバー！");
        dlg.setHeaderText(String.format("---------留年決定---------"));
    }
    dlg.initOwner(stage);
    dlg.show();
}
 void cleanup()
{
    // クリーンアップする
    if (timeline != null)
        timeline = null;
}
public static void main(String[] args) {
  	launch(args);
}
}
