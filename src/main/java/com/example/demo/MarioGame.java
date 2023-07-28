package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.Node;
import javafx.scene.text.Font;



public class MarioGame extends Application {
    private static final int MAP_WIDTH = 800;
    private static final int MAP_HEIGHT = 600;
    private static final int SPEED = 3;
    private static final double TRONG_LUC = 0.1;
    private static final double LUC_NHAY = 5;


    private int jumpAnimationCount = 0;
    private int walkAnimationCount = 0;
    private int dieAnimationCount = 0;
    private int sunAnimationCount = 0;
    private int monsterAnimationCount = 0;
    private boolean save_point_1=false;//false
    private double V_sun = 0;
    private String huong="right";
    private String huong_skunk;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean isJumping = false;
    private boolean die = false;
    private double VAN_TOC = 0;
    private boolean mat_troi_roi = false;


    private Label label;
    private Button button;
    private Button play_button;
    private Button help_button;

    private Button quit_button;
    private Button back_button;
    private Pane root;
    private ImageView back_ground;
    private ImageView mario;
    private Rectangle mat_dat;
    private Rectangle block1,block2,block3,block4,block5,block6,block7,block8,block9,block10,
                block11,block12,block13,block14,block15,block16,block17,block18,block19,block20;
    private ImageView trap1,trap3,trap4,trap5,trap6,trap7,trap8,trap9,trap10;
    private Rectangle trap2;
    private ImageView trap2_1;
    private ImageView spikes;

    private Label easy;
    private Label hard;
    private Label text_save_point;
    private ImageView save_point;

    private Rectangle block_start1, block_start2, block_start3, block_start4;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, MAP_WIDTH, -MAP_HEIGHT);


        new_game();
        UI();
        scene.setOnKeyPressed(event -> {
            if (!die)
            {
                switch (event.getCode()) {
                    case LEFT:
                        moveLeft = true;
                        huong = "left";
                        break;
                    case RIGHT:
                        moveRight = true;
                        huong = "right";
                        break;
                    case UP:
                        if (!isJumping && check_va_cham() != 0)
                        {
                            // Bắt đầu nhảy bằng cách đặt vận tốc y âm (lực đẩy lên)
                            VAN_TOC = -LUC_NHAY;
                            isJumping = true;
                        }
                        break;
                }
            }
            //new game
            if (event.getCode() == KeyCode.N)
            {
                new_game();
                if (save_point_1)
                {
                    root.getChildren().remove(save_point);
                    for (Node child : root.getChildren()) {
                        if (child != mario)
                        {
                            child.setTranslateX(child.getTranslateX() - save_point.getTranslateX() + 300);
                        }
                    }
                    mario.setTranslateY(save_point.getTranslateY() - 50);
                }
            }
            if (event.getCode() == KeyCode.ESCAPE)
            {
                save_point_1=false;
                new_game();
                UI();
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case LEFT:
                    moveLeft = false;
                    break;
                case RIGHT:
                    moveRight = false;
                    break;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                kiem_tra_su_song();
                update();
                renderGame();

                play_button.setOnMouseClicked(event -> {
                    save_point_1=false;
                    new_game();
                });

                help_button.setOnMouseClicked(event -> {
                    back_ground.setImage(new Image(getClass().getResource("/anh_render/help.png").toExternalForm()));
                    back_ground.setFitWidth(800);
                    back_ground.setFitHeight(600);
                    root.getChildren().add(mat_dat);
                    root.getChildren().remove(play_button);
                    root.getChildren().remove(help_button);
                    root.getChildren().remove(quit_button);

                    root.getChildren().add(back_button);
                });

                back_button.setOnMouseClicked(event -> {
                    new_game();
                    UI();
                });

                quit_button.setOnMouseClicked(event -> {
                    primaryStage.close();
                });

                button.setOnMouseClicked(event -> {
                    if (!button.isPressed()) {
                        new_game();
                        if (save_point_1)
                        {
                            root.getChildren().remove(save_point);
                            for (Node child : root.getChildren()) {
                                if (child != mario)
                                {
                                    child.setTranslateX(child.getTranslateX() - save_point.getTranslateX() + 300);
                                }
                            }
                            mario.setTranslateY(save_point.getTranslateY()-50);
                        }
                    }
                });
            }
        };
        gameLoop.start();
    }

    private void new_game()
    {
        jumpAnimationCount = 0;
        walkAnimationCount = 0;
        dieAnimationCount = 0;
        sunAnimationCount = 0;
        monsterAnimationCount = 0;
        V_sun = 3;
        huong="right";
        moveLeft = false;
        moveRight = false;
        isJumping = false;
        die = false;
        VAN_TOC = 0;
        mat_troi_roi = false;
        root.getChildren().clear();

        back_ground = new ImageView(new Image(getClass().getResource("/anh_render/map.png").toExternalForm()));
        back_ground.setTranslateX(-1000);
        back_ground.setTranslateY(0);
        root.getChildren().add(back_ground);

        mario = new ImageView(new Image(getClass().getResource("/anh_render/walk_left_1.png").toExternalForm()));
        mario.setFitWidth(56);
        mario.setFitHeight(50);
        mario.setTranslateX(300);
        mario.setTranslateY(300);
        root.getChildren().add(mario);

        mat_dat = new Rectangle(1500, 0, Color.BROWN);
        mat_dat.setTranslateX(0);
        mat_dat.setTranslateY(600);
//        root.getChildren().add(mat_dat);

        block1 = new Rectangle(400, 100, Color.GREEN);
        block1.setTranslateX(1500);
        block1.setTranslateY(500);
        root.getChildren().add(block1);

        block2 = new Rectangle(300, 100, Color.GREEN);
        block2.setTranslateX(2100);
        block2.setTranslateY(500);
        root.getChildren().add(block2);

        block3 = new Rectangle(600, 100, Color.GREEN);
        block3.setTranslateX(2700);
        block3.setTranslateY(500);
        root.getChildren().add(block3);

        block4 = new Rectangle(300, 0, Color.GREEN);
        block4.setTranslateX(2400);
        block4.setTranslateY(600);
        root.getChildren().add(block4);

        block5 = new Rectangle(401, 25, Color.GREEN);
        block5.setTranslateX(3500);
        block5.setTranslateY(400);
        root.getChildren().add(block5);

        block6 = new Rectangle(0, 450, Color.GREEN);
        block6.setTranslateX(3900);
        block6.setTranslateY(0);
        root.getChildren().add(block6);

        block7 = new Rectangle(200, 25, Color.GREEN);
        block7.setTranslateX(3900);
        block7.setTranslateY(400);
        root.getChildren().add(block7);

        block8 = new Rectangle(200, 50, Color.GREEN);
        block8.setTranslateX(3500);
        block8.setTranslateY(550);
        root.getChildren().add(block8);

        block9 = new Rectangle(1700, 50, Color.GREEN);
        block9.setTranslateX(4000);
        block9.setTranslateY(550);
        root.getChildren().add(block9);

        block10 = new Rectangle(30, 0, Color.GREEN);
        block10.setTranslateX(3700);
        block10.setTranslateY(600);
        root.getChildren().add(block10);

        block11 = new Rectangle(300, 51, Color.GREEN);
        block11.setTranslateX(4300);
        block11.setTranslateY(500);
        root.getChildren().add(block11);

        block12 = new Rectangle(800, 30, Color.GREEN);
        block12.setTranslateX(4700);
        block12.setTranslateY(400);
        root.getChildren().add(block12);

        trap1 = new ImageView(new Image(getClass().getResource("/anh_render/sun_1.png").toExternalForm()));
        trap1.setFitWidth(100);
        trap1.setFitHeight(100);
        trap1.setTranslateX(1700);
        trap1.setTranslateY(200);
        root.getChildren().add(trap1);

        trap2_1 = new ImageView(new Image(getClass().getResource("/anh_render/flower.png").toExternalForm()));
        trap2_1.setFitWidth(50);
        trap2_1.setFitHeight(50);
        trap2_1.setTranslateX(2525);
        trap2_1.setTranslateY(750);
        root.getChildren().add(trap2_1);

        trap2 = new Rectangle(0,50);
        trap2.setTranslateX(2550);
        trap2.setTranslateY(350);
        root.getChildren().add(trap2);

        trap3 = new ImageView(new Image(getClass().getResource("/anh_render/monster_right_1.png").toExternalForm()));
        trap3.setFitWidth(86);
        trap3.setFitHeight(50);
        trap3.setTranslateX(2901);
        trap3.setTranslateY(450);
        root.getChildren().add(trap3);

        trap4 = new ImageView(new Image(getClass().getResource("/anh_render/sun_1.png").toExternalForm()));
        trap4.setFitWidth(100);
        trap4.setFitHeight(100);
        trap4.setTranslateX(4400);
        trap4.setTranslateY(200);
        root.getChildren().add(trap4);

        trap5 = new ImageView(new Image(getClass().getResource("/anh_render/skunk_left_1.png").toExternalForm()));
        trap5.setFitWidth(79);
        trap5.setFitHeight(50);
        trap5.setTranslateX(5400);
        trap5.setTranslateY(350);
        root.getChildren().add(trap5);

        spikes = new ImageView(new Image(getClass().getResource("/anh_render/spikes.png").toExternalForm()));
        spikes.setFitWidth(900);
        spikes.setFitHeight(35);
        spikes.setTranslateX(4600);
        spikes.setTranslateY(515);
        root.getChildren().add(spikes);

        save_point = new ImageView(new Image(getClass().getResource("/anh_render/save_point.png").toExternalForm()));
        save_point.setFitWidth(50);
        save_point.setFitHeight(50);
        save_point.setTranslateX(2800);//2800
        save_point.setTranslateY(450);
        root.getChildren().add(save_point);

        easy = new Label("easy");
        easy.setFont(Font.font("Arial", 20));
        easy.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        easy.setTranslateX(3500);
        easy.setTranslateY(401);
        root.getChildren().add(easy);

        hard = new Label("hard");
        hard.setFont(Font.font("Arial", 20));
        hard.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        hard.setTranslateX(3500);
        hard.setTranslateY(560);
        root.getChildren().add(hard);

        text_save_point = new Label("save point");
        text_save_point.setFont(Font.font("Arial", 20));
        text_save_point.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        text_save_point.setTranslateX(2770);
        text_save_point.setTranslateY(510);
        root.getChildren().add(text_save_point);

        label = new Label("YOU DIED");
        label.setFont(Font.font("Arial", 100));
        label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        label.setTranslateX(180);
        label.setTranslateY(150);

        button = new Button("NEW GAME");
        button.setFont(Font.font("Arial", 40));
        button.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 30px;");
        button.setPrefWidth(300);
        button.setPrefHeight(100);
        button.setTranslateX(250);
        button.setTranslateY(350);

        back_button = new Button("BACK");
        back_button.setFont(Font.font("Arial", 30));
        back_button.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 30px;");
        back_button.setPrefWidth(150);
        back_button.setPrefHeight(60);
        back_button.setTranslateX(0);
        back_button.setTranslateY(0);


        block_start1 = new Rectangle(200,300,Color.BROWN);
        block_start1.setTranslateX(200);
        block_start1.setTranslateY(500);
        root.getChildren().add(block_start1);

        block_start2 = new Rectangle(100,100,Color.RED);
        block_start2.setTranslateX(600);
        block_start2.setTranslateY(500);
        root.getChildren().add(block_start2);

        block_start3 = new Rectangle(100,200,Color.RED);
        block_start3.setTranslateX(900);
        block_start3.setTranslateY(400);
        root.getChildren().add(block_start3);

        block_start4 = new Rectangle(100,300,Color.RED);
        block_start4.setTranslateX(1200);
        block_start4.setTranslateY(300);
        root.getChildren().add(block_start4);
    }
    private void UI()
    {
        root.getChildren().clear();

        back_ground = new ImageView(new Image(getClass().getResource("/anh_render/UI.png").toExternalForm()));
        back_ground.setTranslateX(0);
        back_ground.setTranslateY(0);
        root.getChildren().add(back_ground);

        play_button = new Button("PLAY");
        play_button.setFont(Font.font("Arial", 40));
        play_button.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 30px;");
        play_button.setPrefWidth(300);
        play_button.setPrefHeight(100);
        play_button.setTranslateX(250);
        play_button.setTranslateY(100);
        root.getChildren().add(play_button);

        help_button = new Button("HELP");
        help_button.setFont(Font.font("Arial", 40));
        help_button.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 30px;");
        help_button.setPrefWidth(300);
        help_button.setPrefHeight(100);
        help_button.setTranslateX(250);
        help_button.setTranslateY(250);
        root.getChildren().add(help_button);

        quit_button = new Button("QUIT");
        quit_button.setFont(Font.font("Arial", 40));
        quit_button.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 30px;");
        quit_button.setPrefWidth(300);
        quit_button.setPrefHeight(100);
        quit_button.setTranslateX(250);
        quit_button.setTranslateY(400);
        root.getChildren().add(quit_button);

    }
    private void update()
    {
        // Di chuyển các đối tượng dựa trên hướng di chuyển
        if (moveLeft) {
            updateMap("left");
        }
        if (moveRight) {
            updateMap("right");
        }

        // Kiểm tra và xử lý trạng thái nhảy
        if (isJumping) {
            // Áp dụng lực hấp dẫn để nhân vật rơi xuống
            VAN_TOC += TRONG_LUC;
            mario.setTranslateY(mario.getTranslateY() + VAN_TOC);

            // Kiểm tra va chạm với các vật thể khác
            for (Node child : root.getChildren()) {
                if (child != mario && child != back_ground)
                {
                    if ((mario.getBoundsInParent().intersects(child.getBoundsInParent()) && VAN_TOC>0)||
                            mario.getBoundsInParent().intersects(child.getBoundsInParent()) && VAN_TOC<0 &&
                                    mario.getTranslateY() + mario.getFitHeight() > child.getTranslateY()) {
                        // Nếu nhân vật chạm vào vật thể, dừng nhảy và đặt vị trí y tại đáy vật thể
                        isJumping = false;
                        VAN_TOC = 0;
                        if (die == false && VAN_TOC>0)
                        {
                            mario.setTranslateY(child.getTranslateY() - mario.getFitHeight());
                        }
                        jumpAnimationCount = 0;
                        break;
                    }
                }
            }
        } else {
            VAN_TOC += TRONG_LUC;
            // Kiểm tra va chạm với các vật thể khác
            for (Node child : root.getChildren()) {
                if (child != mario && child != back_ground)
                {
                    if (mario.getBoundsInParent().intersects(child.getBoundsInParent())) {
                        // Nếu nhân vật chạm vào vật thể, dừng nhảy và đặt vị trí y tại đáy vật thể
                        if (mario.getTranslateY() + mario.getFitHeight() < child.getTranslateY() + 10)
                        {
                            VAN_TOC = 0;
                            if (check_va_cham()==1) mario.setTranslateY(child.getTranslateY()- mario.getFitHeight());
                        }
                        else
                        {
                            if (check_va_cham()==2) VAN_TOC=0;
                            if (moveLeft) updateMap("right");
                            if (moveRight) updateMap("left");
                        }
                        break;
                    }
                }
            }
            mario.setTranslateY(mario.getTranslateY() + VAN_TOC);
        }
    }


    private void updateMap(String direction)
    {
        switch (direction)
        {
            case "left":
                mario.setTranslateX(mario.getTranslateX()-SPEED);
                break;
            case "right":
                mario.setTranslateX(mario.getTranslateX()+SPEED);
                break;
        }



        if (check_va_cham() < 2 && !(check_va_cham() == 1 && isJumping))
        {
            // Di chuyển các đối tượng khác trên bản đồ ngược lại với hướng di chuyển của nhân vật
            for (Node child : root.getChildren()) {
                if (child != mario )
                {
                    child.setTranslateX(child.getTranslateX() + (direction.equals("left") ? SPEED : -SPEED));
                }
            }
        }
        kiem_tra_su_song();
        if (die == false) {
            if (direction == "left") mario.setTranslateX(mario.getTranslateX() + SPEED);
            else mario.setTranslateX(mario.getTranslateX() - SPEED);
        }


        // Kiểm tra giới hạn màn hình để không di chuyển quá xa khỏi màn hình
        // ...

    }

    private void kiem_tra_su_song()
    {
        //rơi -> chết
        if (mario.getTranslateY() > 600)
        {
            die = true;
            VAN_TOC=0;
            dieAnimationCount=301;
        }
        if (mario.getBoundsInParent().intersects(spikes.getBoundsInParent()) && mario.getTranslateX() < spikes.getTranslateX()+30)
        {
            die = true;
        }
        //va chạm vật thể -> chết
        if (    mario.getBoundsInParent().intersects(trap1.getBoundsInParent())||
                mario.getBoundsInParent().intersects(trap2.getBoundsInParent())||
                mario.getBoundsInParent().intersects(trap3.getBoundsInParent())||
                mario.getBoundsInParent().intersects(trap4.getBoundsInParent())||
                mario.getBoundsInParent().intersects(trap5.getBoundsInParent()) )
        {
            die = true;
            VAN_TOC=0;
        }
        if (mario.getBoundsInParent().intersects(save_point.getBoundsInParent()))
        {
            save_point_1=true;
            root.getChildren().remove(save_point);
        }
    }

    private int check_va_cham()
    {
        int check=0;
        // Kiểm tra va chạm với các vật thể khác
        for (Node child : root.getChildren()) {
            if (child != mario && child != back_ground)
            {
                if (mario.getBoundsInParent().intersects(child.getBoundsInParent())) {
                    // Nếu nhân vật chạm vào vật thể, dừng nhảy và đặt vị trí y tại đáy vật thể
                    check += 1;
                }
            }
        }
        return check;

    }
    private void die_Animation()
    {
        if (!root.getChildren().contains(label) && !root.getChildren().contains(button) && dieAnimationCount>300) {
            // Nếu không có, thêm label vào root
            root.getChildren().addAll(label, button);
        }
        if (dieAnimationCount > 0 && dieAnimationCount < 100)
        {
            mario.setImage(new Image(getClass().getResource("/anh_render/die_" + huong + "_1.png").toExternalForm()));
        } else if (dieAnimationCount >= 100 && dieAnimationCount < 200) {
            mario.setImage(new Image(getClass().getResource("/anh_render/die_" + huong + "_2.png").toExternalForm()));
        } else if (dieAnimationCount >= 200 && dieAnimationCount < 300){
            mario.setImage(new Image(getClass().getResource("/anh_render/die_" + huong + "_3.png").toExternalForm()));
        } else {
            mario.setImage(new Image(getClass().getResource("/anh_render/die_" + huong + "_4.png").toExternalForm()));
        }
        dieAnimationCount++;
    }
    private void jump_Animation()
    {
        if (jumpAnimationCount > 0 && jumpAnimationCount < 10)
        {
            mario.setImage(new Image(getClass().getResource("/anh_render/jump_" + huong + "_1.png").toExternalForm()));
        } else if (jumpAnimationCount >= 10 && jumpAnimationCount < 50) {
            mario.setImage(new Image(getClass().getResource("/anh_render/jump_" + huong + "_2.png").toExternalForm()));
        } else {
            mario.setImage(new Image(getClass().getResource("/anh_render/jump_" + huong + "_3.png").toExternalForm()));
        }
        // Tăng giá trị của jumpAnimationCount để chuyển đổi hình ảnh nhảy
        jumpAnimationCount++;
    }
    private void walk_Animation()
    {
        if (walkAnimationCount > 0 && walkAnimationCount < 20)
        {
            mario.setImage(new Image(getClass().getResource("/anh_render/walk_" + huong + "_2.png").toExternalForm()));
        }else if (walkAnimationCount >= 20 && walkAnimationCount < 40) {
            mario.setImage(new Image(getClass().getResource("/anh_render/walk_" + huong + "_3.png").toExternalForm()));
        }else if (walkAnimationCount >= 40 && walkAnimationCount < 60) {
            mario.setImage(new Image(getClass().getResource("/anh_render/walk_" + huong + "_4.png").toExternalForm()));
        }else if (walkAnimationCount >= 60 && walkAnimationCount < 80) {
            mario.setImage(new Image(getClass().getResource("/anh_render/walk_" + huong + "_1.png").toExternalForm()));
        }else{
            walkAnimationCount=0;
        }
        walkAnimationCount++;
    }
    private void skunk_animation(ImageView x)
    {
        if (walkAnimationCount > 0 && walkAnimationCount <= 20)
        {
            x.setImage(new Image(getClass().getResource("/anh_render/skunk_" + huong_skunk + "_2.png").toExternalForm()));
        }else if (walkAnimationCount > 20 && walkAnimationCount <= 40) {
            x.setImage(new Image(getClass().getResource("/anh_render/skunk_" + huong_skunk + "_3.png").toExternalForm()));
        }else if (walkAnimationCount > 40 && walkAnimationCount <= 60) {
            x.setImage(new Image(getClass().getResource("/anh_render/skunk_" + huong_skunk + "_4.png").toExternalForm()));
        }else if (walkAnimationCount > 60 && walkAnimationCount <= 80) {
            x.setImage(new Image(getClass().getResource("/anh_render/skunk_" + huong_skunk + "_1.png").toExternalForm()));
        }
    }
    private void sun_Animation(ImageView x)
    {
        if (sunAnimationCount > 0 && sunAnimationCount < 20)
        {
            x.setImage(new Image(getClass().getResource("/anh_render/sun_4.png").toExternalForm()));
        }else if (sunAnimationCount >= 20 && sunAnimationCount < 40) {
            x.setImage(new Image(getClass().getResource("/anh_render/sun_5.png").toExternalForm()));
        }else if (sunAnimationCount >= 40 && sunAnimationCount < 60) {
            x.setImage(new Image(getClass().getResource("/anh_render/sun_6.png").toExternalForm()));
        }else if (sunAnimationCount >= 60 && sunAnimationCount < 80) {
            x.setImage(new Image(getClass().getResource("/anh_render/sun_7.png").toExternalForm()));
        }else if (sunAnimationCount >= 80 && sunAnimationCount < 100) {
            x.setImage(new Image(getClass().getResource("/anh_render/sun_8.png").toExternalForm()));
        }else if (sunAnimationCount >= 100 && sunAnimationCount < 120) {
            x.setImage(new Image(getClass().getResource("/anh_render/sun_9.png").toExternalForm()));
        }else{
            sunAnimationCount=0;
        }
        sunAnimationCount++;

    }
    private void monster_Animation()
    {
        if (monsterAnimationCount > 0 && monsterAnimationCount<314)
        {
            if (monsterAnimationCount % 60>=0 && monsterAnimationCount % 60<20)
            {
                trap3.setImage(new Image(getClass().getResource("/anh_render/monster_right_1.png").toExternalForm()));
                trap3.setTranslateX(trap3.getTranslateX()+1);
            }
            if (monsterAnimationCount % 60>=20 && monsterAnimationCount % 60<40)
            {
                trap3.setImage(new Image(getClass().getResource("/anh_render/monster_right_2.png").toExternalForm()));
                trap3.setTranslateX(trap3.getTranslateX()+1);
            }
            if (monsterAnimationCount % 60>=40 && monsterAnimationCount % 60<60)
            {
                trap3.setImage(new Image(getClass().getResource("/anh_render/monster_right_3.png").toExternalForm()));
                trap3.setTranslateX(trap3.getTranslateX()+1);
            }
        }
        if (monsterAnimationCount > 314 && monsterAnimationCount<628)
        {
            if (monsterAnimationCount % 60>=0 && monsterAnimationCount % 60<20)
            {
                trap3.setImage(new Image(getClass().getResource("/anh_render/monster_left_1.png").toExternalForm()));
                trap3.setTranslateX(trap3.getTranslateX()-1);
            }
            if (monsterAnimationCount % 60>=20 && monsterAnimationCount % 60<40)
            {
                trap3.setImage(new Image(getClass().getResource("/anh_render/monster_left_2.png").toExternalForm()));
                trap3.setTranslateX(trap3.getTranslateX()-1);
            }
            if (monsterAnimationCount % 60>=40 && monsterAnimationCount % 60<60)
            {
                trap3.setImage(new Image(getClass().getResource("/anh_render/monster_left_3.png").toExternalForm()));
                trap3.setTranslateX(trap3.getTranslateX()-1);
            }
        }
        if (monsterAnimationCount>628) monsterAnimationCount=0;
        monsterAnimationCount++;
    }


    private void renderGame()
    {
        if (mario.getTranslateX() > trap1.getTranslateX()-20) mat_troi_roi = true;
        if (mat_troi_roi)
        {
            V_sun += TRONG_LUC;
            //mặt trời rơi
            trap1.setTranslateY(trap1.getTranslateY()+V_sun);
        }

        if (mario.getTranslateX() > block1.getTranslateX()+300 && mario.getBoundsInParent().intersects(block1.getBoundsInParent())){
            block1.setWidth(300);
        }

        if (mario.getTranslateX() > block5.getTranslateX()+200 && mario.getTranslateY() < block5.getTranslateY() && mario.getBoundsInParent().intersects(block5.getBoundsInParent())){
            block5.setWidth(200);
        }

        if (mario.getBoundsInParent().intersects(block10.getBoundsInParent()))
        {
            block10.setWidth(300);
        }

        if (mario.getBoundsInParent().intersects(trap2.getBoundsInParent()))
        {
            trap2_1.setTranslateY(350);
        }

        if (mario.getTranslateX() > block12.getTranslateX() && mario.getTranslateX() < block12.getTranslateX() + 800)
        {
            trap5.setTranslateX(2 * block12.getTranslateX() + 800 - mario.getTranslateX());
            if (mario.getTranslateY() < block12.getTranslateY())
            {
                trap5.setTranslateY(mario.getTranslateY());
            }
            if (huong == "left") huong_skunk = "right";
            else huong_skunk = "left";
            trap5.setImage(new Image(getClass().getResource("/anh_render/skunk_" + huong_skunk + "_1.png").toExternalForm()));
        }
        else
        {
            trap5.setTranslateY(350);
        }




        monster_Animation();
        sun_Animation(trap1);
        sun_Animation(trap4);
//        skunk_animation(trap5);

        if (die)
        {
            moveLeft=false;moveRight=false;isJumping=false;
            die_Animation();
        }
        else
        {
            if (isJumping)
            {
                jump_Animation();
            }
            else
            {
                if (!moveLeft && !moveRight)
                {
                    mario.setImage(new Image(getClass().getResource("/anh_render/walk_" + huong + "_1.png").toExternalForm()));
                    walkAnimationCount=0;
                }
                else
                {
                    walk_Animation();
                }
            }
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
