package com.al.ivan.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class BaseActor extends Group {

    public int numberColor;
    public int hit;
    public boolean mBrick;
    public boolean brickHardStatus;
    protected boolean hindranceStatus;
    public boolean brickVisible;

    // переменные для метода leftRightMoving();
    private float timeGame;
    private float timeGameInterval;
    private boolean timeBool;
    private float timeSpeed;

    // Для сохранения анимации и связанных данных
    private Animation<TextureRegion> animation;
    private float elapsedTime;
    private boolean animationPaused;

    protected Vector2 velocityVec; // Переменные для скорости

    // Переменные для плавного ускорения и замедления
    private Vector2 accelerationVec;
    private float acceleration;

    private float maxSpeed; // максимальная скорость обьекта
    private float deceleration; // скорость замедления обьекта
    private Polygon boundaryPolygon; // переменная для прямоугольника вокруг Actor

    private static Rectangle worldBounds; // переменная для хранения границ игрового мира


    public BaseActor(float x, float y, Stage s) {
        super();
        setPosition(x, y);
        s.addActor(this);

        // Инициализация переменных для анимации
        animation = null;
        elapsedTime = 0;
        animationPaused = false;

        // Инициализация переменные для скорости
        velocityVec = new Vector2(0, 0);

        // Инициализация переменные для ускорения
        accelerationVec = new Vector2(0, 0);
        acceleration = 0;

        maxSpeed = 1000;
        deceleration = 0;

        timeGame = 0;
        timeGameInterval = MathUtils.random(0, 7);
        timeBool = false;
        timeSpeed = MathUtils.random(100, 200);

        numberColor = 4;
        brickVisible = true;

    }

    // метод, который возвращает размер экрана использующегося устройства в дюймах
    public static double getScreenSizeInches() {
        // Use the primary monitor as baseline
        // It would also be possible to get the monitor where the window is displayed
        Graphics.Monitor primary = Gdx.graphics.getPrimaryMonitor();
        Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode(primary);

        float dpi = 160 * Gdx.graphics.getDensity();
        float widthInches = displayMode.width / dpi;
        float heightInches = displayMode.height / dpi;

        //Use the pythagorean theorem to get the diagonal screen size
        return Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
    }
    // метод, который возвращает размер экрана использующегося устройства в дюймах(конец)

    public void centerAtPosition(float x, float y) {
        setPosition(x - this.getWidth() / 2, y - this.getHeight() / 2);
    }

    // начальная позиция в центре актера other
    public void centerAtActor(BaseActor other) {
        centerAtPosition(other.getX() + other.getWidth() / 2, other.getY() + other.getHeight() / 2);
    }

    // начальная позиция по оси X в центре актера other, а по оси Y в нижней точке актера other
    public void positionAtActor(BaseActor other) {
        centerAtPosition(other.getX() + other.getWidth() / 2, other.getY());
    }

    // Метод для настройки анимации
    public void setAnimation(Animation<TextureRegion> anim) {
        animation = anim;
        TextureRegion tr = animation.getKeyFrame(0);
        float w = tr.getRegionWidth();
        float h = tr.getRegionHeight();
        setSize(w,h);
        setOrigin(w/2, h/2);

        // создает прямоугольный многоугольник вокрук Actor
        if (boundaryPolygon == null)
            setBoundaryRectangle();
    }

    // Метод для изменения значения animationPaused
    public void setAnimationPaused(boolean pause) {
        animationPaused = pause;
    }

    //
    public void act(float dt) {
        super.act(dt);

        timeGame += dt;

        if (!animationPaused)
            elapsedTime += dt;
    }

    // Переопределенный метод рисования класса Actor
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);

        if (animation != null && isVisible())
            batch.draw(animation.getKeyFrame(elapsedTime), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(),
                    getScaleX(), getScaleY(), getRotation());
    }

    // Метод для создания анимации из отдельных файлов
    public Animation<TextureRegion> loadAnimationFromFiles(String[] fileNames, float frameDuration, boolean loop) {
        int fileCount = fileNames.length;
        Array<TextureRegion> textureArray = new Array<TextureRegion>();

        for (int n = 0; n < fileCount; n ++) {
            String fileName = fileNames[n];
            Texture texture = new Texture(Gdx.files.internal(fileName));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureArray.add(new TextureRegion(texture));
        }

        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);

        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (animation == null)
            setAnimation(anim);

        return anim;
    }

    // Метод для создания анимации из таблицы спрайтов
    public Animation<TextureRegion> loadAnimationFromSheet(String fileName, int rows, int cols, float frameDuration, boolean loop) {
        Texture texture = new Texture(Gdx.files.internal(fileName), true);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;

        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);

        Array<TextureRegion> textureArray = new Array<TextureRegion>();

        for (int r = 0; r < rows; r ++)
            for (int c = 0; c < cols; c ++)
                textureArray.add(temp[r][c]);

        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);

        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (animation == null)
            setAnimation(anim);

        return anim;
    }

    // Метод для отображения неподвижного изображения, используя однокадровую анимацию
    public Animation<TextureRegion>loadTexture(String fileName) {
        String[] fileNames = new String[1];
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }

    // Метод для проверки завершения анимации
    public boolean isAnimationFinished() {
        return animation.isAnimationFinished(elapsedTime);
    }

    // Методы для установки и получения угла и скорости движения обьекта Actor
    public void setSpeed(float speed) {
        if (velocityVec.len() == 0)
            velocityVec.set(speed, 0);
        else
            velocityVec.setLength(speed);
    }

    public float getSpeed() {
        return velocityVec.len();
    }

    // метод, который устанавливает направление движения актера
    public void setMotionAngle(float angle) {
        velocityVec.setAngle(angle);
    }

    public float getMotionAngle() {
        return velocityVec.angle();
    }
    // Конец методов для установки и получения угла и скорости движения обьекта Actor

    // Метод, чтобы узнать движется обьект или нет
    public boolean isMoving() {
        return (getSpeed() > 0);
    }

    // Метод, который движет обьект влево и право с разной скоростью и длинной
    public void leftRightMoving(float intervalTimeStart, float intervalTimeEnd, float speedStart, float speedEnd, float setAngleStart, float setAngleEnd) {
        if (timeGame > timeGameInterval && timeBool == false) {
            setSpeed(timeSpeed);
            setMotionAngle(setAngleStart);
            timeGame = 0;
            timeBool = true;
            timeGameInterval = MathUtils.random(intervalTimeStart, intervalTimeEnd);
            timeSpeed = MathUtils.random(speedStart, speedEnd);
        }
        if (timeGame > timeGameInterval && timeBool == true) {
            setSpeed(timeSpeed);
            setMotionAngle(setAngleEnd);
            timeGame = 0;
            timeBool = false;
            timeGameInterval = MathUtils.random(intervalTimeStart, intervalTimeEnd);
            timeSpeed = MathUtils.random(speedStart, speedEnd);
        }
    }

    // Метод для установления величины вектора ускорения
    public void setAcceleration(float acc) {
        acceleration = acc;
    }

    // Метод для ускорения в указанном направлении(принимает угол направления в качестве параметра)
    public void accelerateAtAngle(float angle) {
        this.accelerationVec.add((new Vector2(this.acceleration, 0.0F)).setAngle(angle));
    }

    // Метод, который ускоряет обьект в том направлении, в котором он находится в данный момент
    public void accelerateForward() {
        accelerateAtAngle(getRotation());
    }

    // Метод, для установки максимального значения скорости
    public void setMaxSpeed(float ms) {
        maxSpeed = ms;
    }

    // Метод, для получения максимального значения скорости
    public float getMaxSpeed() {
        return maxSpeed;
    }

    // Метод, для установки скорости замедления
    public void setDeceleration(float dec) {
        deceleration = dec;
    }

    // Метод, который вычисляет максимальную скорость, ускорение, направление, угол поворота и соответствующим образом обнавляет положение Actor
    // Метод обрабатывает следующие значения
    // 1. Регулирует вектор скорости на основе вектора ускорения
    // 2. Если обьект не ускоряется, он должен применить величину замедления к текущей скорости
    // 3. Убеждается, что скорость не превышает максимальное значение
    // 4. Регулирует положение обьекта на основе вектора скорости
    // 5. Сбрасывает вектор ускорения
    // Заставляет двигаться обьект в какую-то сторону непрерывно и под углом
    public void applyPhysics(float dt) {
        velocityVec.add(this.accelerationVec.x * dt, accelerationVec.y * dt);
        float speed = getSpeed();
        if (accelerationVec.len() == 0) {
            speed -= this.deceleration * dt;
        }

        speed = MathUtils.clamp(speed, 0, maxSpeed);
        setSpeed(speed);
        moveBy(this.velocityVec.x * dt, velocityVec.y * dt);
        accelerationVec.set(0, 0);
    }

    // Метод, который создает прямоугольник вокрук Actor
    public void setBoundaryRectangle() {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0,0, w,0, w,h, 0,h};
        boundaryPolygon = new Polygon(vertices);
    }

    // Метод, который создает прямоугольный многоугольник вокрук Actor
    public void setBoundaryPolygon(int numSides) {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = new float[2 * numSides];

        for(int i = 0; i < numSides; ++i) {
            float angle = (float)i * 6.28F / (float)numSides;
            vertices[2 * i] = w / 2.0F * MathUtils.cos(angle) + w / 2.0F;
            vertices[2 * i + 1] = h / 2.0F * MathUtils.sin(angle) + h / 2.0F;
        }

        boundaryPolygon = new Polygon(vertices);
    }

    // Метод, который возвращает прямоугольный многоугольник вокрук Actor
    public Polygon getBoundaryPolygon() {
        boundaryPolygon.setPosition(getX(), getY());
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }

    // Метод, который проверяет перекрытие двух полигонов
    public boolean overlaps(BaseActor other) {
        Polygon poly1 = getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return false;

        return Intersector.overlapConvexPolygons(poly1, poly2);
    }

    // Метод, для изменения прозрачности обьекта Actor
    public void setOpacity(float opacity) {
        getColor().a = opacity;
    }

    // Метод возвращает направление, в котором был перемещен актер(если было перекрытие)(позволяет сталкивать обьекты)
    public Vector2 preventOverlap(BaseActor other) {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return null;

        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

        if (!polygonOverlap)
            return null;

        this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);

        return mtv.normal;
    }

    //
    public static ArrayList<BaseActor> getList(Stage stage, String className) {
        ArrayList<BaseActor> list = new ArrayList<BaseActor>();

        Class theClass = null;
        try {
            theClass = Class.forName(className);
        } catch (Exception error) {
            error.printStackTrace();
        }

        for (Actor a : stage.getActors()){
            if (theClass.isInstance(a))
                list.add((BaseActor)a);
        }
        return list;
    }

    // Метод, который определяет, кеолько экземпляров обьекта определенного типа остается в данный момент времени
    public static int count(Stage stage, String className) {
        return getList(stage, className).size();
    }

    // Метод, который сохраняет размер игрового мира непосредственно из числовых значений
    public static void setWorldBounds(float width, float height) {
        worldBounds = new Rectangle(0, 0, width, height);
    }

    // Метод, который сохраняет размер игрового мира непосредственно из числовых значений
    // Этод метод для игры RectangleDestroyer
    public void setWorldBoundsDestroyer(float width, float height) {
        worldBounds = new Rectangle(0, Gdx.graphics.getHeight()/2f, width, height);
    }

    // Метод, который сохраняет размер игрового мира на основе актера(например, актера, отображающее фоновое изображение)
    public static void setWorldBounds(BaseActor ba) {
        setWorldBounds(ba.getWidth(), ba.getHeight());
    }

    // Метод, который узнает размер игрового мира
    public static Rectangle getWorldBounds() {
        return worldBounds;
    }


    // Метод, который держит актера в прямоугольной области, определяемой границами мира
    public void boundToWorld() {
        if (getX() < 0)
            setX(0);
        if (getX() + getWidth() > worldBounds.width)
            setX(worldBounds.width - getWidth());
        if (getY() < 0)
            setY(0);
        if (getY() + getHeight() > worldBounds.height)
            setY(worldBounds.height - getHeight());
    }

    // Метод с помощью которого если обьект выйдет за границу экрана с одной стороны, то
    public void wrapAroundWorld() {
        if (getX() + getWidth() < 0)
            setX(worldBounds.width);
        if (getX() > worldBounds.width)
            setX( - getWidth());
        if (getY() + getHeight() < 0)
            setY(worldBounds.height);
        if (getY() > worldBounds.height)
            setY( - getHeight());
    }

    // Метод, который привязывает камеру к определенной области и перемещает ее в случае, когда мир больше экрана
    public void alignCamera() {
        Camera cam = this.getStage().getCamera();
        Viewport v = this.getStage().getViewport();

        // центрирование камеры
        cam.position.set(this.getX() + this.getOriginX(), this.getY() + this.getOriginY(), 0);

        cam.position.x = MathUtils.clamp(cam.position.x, cam.viewportWidth / 2, worldBounds.width - cam.viewportWidth / 2);
        cam.position.y = MathUtils.clamp(cam.position.y, cam.viewportHeight / 2, worldBounds.height - cam.viewportHeight / 2);
        cam.update();
    }

    //Способ чтобы определить, находятся ли два актера «близко».
    // Чтобы реализовать это, нужно временно увеличить ограничивающий многоугольник одного из актеров, а затем проверить наложение.
    // Вы можете легче всего увеличить многоугольник, установив его масштаб (в обоих направлениях x и y).
    // Последующие вычисления предполагают, что актер еще не масштабирован ни в одном направлении.
    // Например, если у актера есть ширина и высота 50 пикселей, и вы хотите определить, находится ли другой актер в пределах 10 пикселей,
    // вам необходимо увеличить каждое из измерений на 20 пикселей (по 10 пикселей с каждой стороны),
    // что составляет с коэффициентом масштабирования (50 + 20) / 50 = 1,4.
    // Это вычисление выполняется с помощью следующего метода.
    public boolean isWithinDistance(float distance, BaseActor other) {
        Polygon poly1 = this.getBoundaryPolygon();
        float scaleX = (this.getWidth() + 2 * distance) / this.getWidth();
        float scaleY = (this.getHeight() + 2 * distance) / this.getHeight();
        poly1.setScale(scaleX, scaleY);
        Polygon poly2 = other.getBoundaryPolygon();
        if ( !poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()) )
            return false;

        return Intersector.overlapConvexPolygons( poly1, poly2 );
    }

    // метод, с помощью которого происходит отскакивание под противоположным углом
    public void bounceOff(BaseActor other) {
        Vector2 v = this.preventOverlap(other);
        if ( Math.abs(v.x) >= Math.abs(v.y) ){ // horizontal bounce
            this.velocityVec.x *= -1;
        } else { // vertical bounce
            this.velocityVec.y *= -1;
        }
    }

    // метод, который скрывает, но не удаляет актера
    // если false - то скрывается
    public void visibleActor(boolean b, boolean overlaps) {
        setVisible(b);
        brickVisible = overlaps;
    }

}