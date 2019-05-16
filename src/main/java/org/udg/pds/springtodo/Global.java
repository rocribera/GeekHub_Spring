package org.udg.pds.springtodo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.service.*;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@Service
public class Global {
    private MinioClient minioClient;

    private Logger logger = LoggerFactory.getLogger(Global.class);

    @Autowired
    private
    UserService userService;

    @Autowired
    private
    GameService gameService;

    @Autowired
    private
    CategoryService categoryService;

    @Autowired
    private
    PostService postService;

    @Value("${todospring.minio.url:}")
    private String minioURL;

    @Value("${todospring.minio.access-key:}")
    private String minioAccessKey;

    @Value("${todospring.minio.secret-key:}")
    private String minioSecretKey;

    @Value("${todospring.minio.bucket:}")
    private String minioBucket;

    @Value("${todospring.base-url:#{null}}")
    private String BASE_URL;

    @Value("${todospring.base-port:8080}")
    private String BASE_PORT;


    @PostConstruct
    void init() {

        logger.info("Starting Minio connection ...");
        try {
            minioClient = new MinioClient(minioURL, minioAccessKey, minioSecretKey);
        } catch (Exception e) {
            logger.warn("Cannot initialize minio service with url:" + minioURL + ", access-key:" + minioAccessKey + ", secret-key:" + minioSecretKey);
        }

        if (minioBucket == null) {
            logger.warn("Cannot initialize minio bucket: " + minioBucket);
            minioClient = null;
        }

        if (BASE_URL == null) BASE_URL = "http://localhost";
        BASE_URL += ":" + BASE_PORT;


        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream("src\\main\\resources\\android-pds19-3c-firebase-adminsdk-l76yv-1789b3d812.json");


            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initData();
    }

    private void initData() {

        logger.info("Starting populating database ...");
        User user = userService.register("usuari", "usuari@hotmail.com", "123456");


        Category shooter = categoryService.createCategory("Shooter");
        Category big_map = categoryService.createCategory("Big map");
        Category online = categoryService.createCategory("Online");
        Game game1 = gameService.createGame("Battlefield","https://images.g2a.com/newlayout/270x270/1x1x0/0a80471a22bc/590b27175bafe324f0665b43","Big map with a lot of chaos");
        gameService.addCategory(game1.getId(), new ArrayList<Long>() {{
            add(shooter.getId());
            add(big_map.getId());
            add(online.getId());
        }});
        Game game2 = gameService.createGame("Modern Warfare 2","https://hb.imgix.net/46de552d47f87b55668da538d92e4505299370e8.jpg?auto=compress,format&fit=crop&h=353&w=616&s=aac61545579dae3a98edd5c63db41002", "360 sniper");
        gameService.addCategory(game2.getId(), new ArrayList<Long>() {{
            add(shooter.getId());
        }});
        //User 1
        userService.addGame(user.getId(),game2.getId());
        Post post1 = postService.createPost("Sniper 2v2 Rust",true,"I search a new Captain Price",user.getId(),game2.getId());
        postService.createPost("Sniper 4v4 Terminal",true,"Come on come on let's go",user.getId(),game2.getId());
        userService.updateProfile(user.getId(),"","A user like the others","https://i.imgur.com/qw72OSB.png");
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }

    public String getMinioBucket() {
        return minioBucket;
    }

    public String getBaseURL() {
        return BASE_URL;
    }
}
