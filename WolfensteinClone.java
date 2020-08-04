import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class WolfensteinClone extends JPanel implements ActionListener, KeyListener {
    int[] playerCoord = new int[]{250, 250};
    Double playerDirection = 0.0;
    Double playerVelocity = 0.0;
    Boolean playerMoving = false;
    Boolean playerMoveForward = true;
    int playerTurning = 0; //0 = not turning, 1 = clockwise, 2 = counterclockwise
    int tickCount = 1;
    //ArrayList<Double> rayCastCollisions = new ArrayList<>();
    ArrayList<RayCast> rayList = new ArrayList<>();
    ArrayList<Wall> wallList = new ArrayList<>();
    ArrayList<SolidWall> solidWallList = new ArrayList<>();
    ArrayList<Point> rayCastCollisions = new ArrayList<>();
    ArrayList<Integer> rayCastedWalls = new ArrayList<>();

    Boolean perspective3D = false;

    boolean gameState = true;

    public WolfensteinClone() {
        JFrame gt =new JFrame("DogenStein");
        gt.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gt.addKeyListener(this);
        gt.setVisible(true);
        gt.add(this);
        gt.setFocusable(true);
        gt.setFocusTraversalKeysEnabled(false);
        gt.setSize(516 ,539);
        gt.setPreferredSize(new Dimension(517, 540));
        gt.pack();

        /** rayList.add(new RayCast(-0.314159));
        rayList.add(new RayCast((-0.314159)/2));
        rayList.add(new RayCast(0));
        rayList.add(new RayCast((0.314159)/2));
        rayList.add(new RayCast(0.314159)); */

        for(int i = -30; i < 30; i += 1) {
            double tempRad = Math.toRadians(i);
            rayList.add(new RayCast(tempRad));
        }


    }

    public void playGame() {
        while(gameState = true) {
            gameTick();
        }
    }

    public void gameTick() {
        movePlayer();
        tickCount += 1;
        try{
            Thread.sleep(20);
        }
        catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public static boolean tickDelay(int frameDelay, int tick) {
        if(frameDelay == 0)
            return false;
        if(tick % frameDelay == 0)
            return true;
        else
            return false;
    }

    private void countTicker() {
        tickCount += 1;
        if(tickCount >= 500)
            tickCount = 1;
    }

    public void paintComponent(Graphics p) {
        super.paintComponent(p);
        if(perspective3D)
            draw3DGraphics(p);
        else
            draw2DGraphics(p);

    }

    public void draw2DGraphics(Graphics p) {
        repaint();
        //drawCellsInSight(p);
        p.setColor(Color.LIGHT_GRAY);
        drawGrid(p);
        p.setColor(Color.BLACK);
        p.drawOval(playerCoord[0] - 5, playerCoord[1] - 5, 10, 10);
        drawDirection(p);
        drawWalls(p);
        drawRayCastCollisions(p);

    }

    public void drawDirection(Graphics p){
        int endX = (int)Math.round(Math.cos(playerDirection) * 300);
        int endY = (int)Math.round(Math.sin(playerDirection) * 300);
        p.drawLine(playerCoord[0], playerCoord[1], endX+playerCoord[0], endY+playerCoord[1]);
    }

    public void drawGrid(Graphics p) {
        for(int i = 0; i < 500; i+= 50){
            for(int j = 0; j < 500; j += 50){
                p.drawRect(i, j, 50, 50);
            }
        }
    }

    public void drawWalls(Graphics p) {
        p.setColor(Color.RED);
        for(int i = 0; i < wallList.size(); i++){
            Wall tempWall = wallList.get(i);
                if(tempWall.isHorizontal())
                    p.drawLine(tempWall.getX(), tempWall.getY(), tempWall.getX() + 50, tempWall.getY());
                else
                    p.drawLine(tempWall.getX(), tempWall.getY(), tempWall.getX(), tempWall.getY() + 50);
        }

    }

    public void drawCellsInSight(Graphics p){
        RayCast mainRay = rayList.get(0);
        for(int i = 0; i < mainRay.overlappingCells.size(); i += 1) {
            p.setColor(Color.YELLOW);
            int tempX = (int)mainRay.overlappingCells.get(i).getX()*50;
            int tempY = (int)mainRay.overlappingCells.get(i).getY()*50;
            if(tempX >= 0 && tempY >= 0)
                p.fillRect(tempX, tempY, 50, 50);
        }
    }

    public void drawRayCastCollisions(Graphics p){
        p.setColor(Color.BLUE);
        int size = rayList.size();
        RayCast tempRay;
        for(int i = 0; i < size; i++ ){
            tempRay = rayList.get(i);
            if(tempRay.getWallCollision().getX() != -1337)
                p.drawOval((int)tempRay.getWallCollision().getX() - 2, (int)tempRay.getWallCollision().getY() - 2, 4, 4);
        }
    }

    public void draw3DGraphics(Graphics p) {
        //Calc bar width
        repaint();
        int numRay = rayCastedWalls.size();
        if(numRay == 0)
            return;
        int barWidth = 500/numRay;

        p.setColor(Color.GRAY);
        p.fillRect(0,0,500, 250);
        p.setColor(Color.LIGHT_GRAY);
        p.fillRect(0,250,500, 250);

        //Fix bar heights
        ArrayList<Integer> wallCastAdjusted = new ArrayList<>();
        for(int i = 0; i < numRay; i++) {
            int temp = 500 - rayCastedWalls.get(i);
            temp = temp*temp;
            temp = temp/500;
            if(temp == 500)
                temp = wallCastAdjusted.get(i-1);
            wallCastAdjusted.add(temp);
        }

        //The FINAL STEP I hope
        for(int i = 0; i < numRay; i++){
            Color tempColor = new Color(0, 100, wallCastAdjusted.get(i)/2);
            p.setColor(tempColor);
            //p.setColor(Color.BLUE);
            p.fillRect(i*barWidth,250-(wallCastAdjusted.get(i)/2), barWidth,  wallCastAdjusted.get(i));
        }
    }

    public void runGame() {
        movePlayer();
        while(gameState = true) {
            gameTick();
        }
    }

    public void updatePlayerLocation() {
        int xChangeRounded = (int)Math.floor(Math.cos(playerDirection)*playerVelocity);
        int yChangeRounded = (int)Math.floor(Math.sin(playerDirection)*playerVelocity);
        Double xDecimal = Math.cos(playerDirection)*playerVelocity - xChangeRounded;
        Double yDecimal = Math.sin(playerDirection)*playerVelocity - yChangeRounded;
        int xRecip = 0;
        int yRecip = 0;
        if(xDecimal != 0)
            xRecip = (int)Math.round(1 / xDecimal);
        if(yDecimal != 0)
            yRecip = (int)Math.round(1 / yDecimal);

        playerCoord[0] += xChangeRounded;
        playerCoord[1] += yChangeRounded;

        if(tickDelay(xRecip, tickCount))
            playerCoord[0] += 1;
        if(tickDelay(yRecip, tickCount))
            playerCoord[1] += 1;

        //rayList.get(0).updateRayDirection();
    }

    public void movePlayer() {
        playerWalk();
        playerTurn();
        updatePlayerLocation();
        rayCastCollisions.clear();
        for(int i = 0; i < rayList.size(); i++) {
            rayList.get(i).updateRay();
            rayCastCollisions.add(rayList.get(i).getWallCollisionCoord());
        }
        calcWallDistances();
    }

    public void calcWallDistances() {
        //Point primRayLoc = rayList.get(0).wallCollisionCoord;
        ArrayList<Point> rayCol = rayCastCollisions;
        //Getting distance for primary ray
        //int xDist = playerCoord[0] - (int)primRayLoc.getX();
        //int yDist = playerCoord[1] - (int)primRayLoc.getY();
        //int primRayDist = (int)Math.sqrt((xDist*xDist) +(yDist*yDist));


        //Getting distance for each ray
        ArrayList<Integer> distList = new ArrayList<>();
        RayCast tempRay;
        for(int i = 0; i < rayCol.size(); i++) {
            tempRay = rayList.get(i);
            if(tempRay.wallCollisionCoord.getX() != -1337) {
                int xDistTemp = playerCoord[0] - (int) tempRay.wallCollisionCoord.getX();
                int yDistTemp = playerCoord[1] - (int) tempRay.wallCollisionCoord.getY();
                int linearDistance = (int)Math.sqrt((xDistTemp*xDistTemp) +(yDistTemp*yDistTemp));
                double properDistance = Math.cos(tempRay.getRelAngle())*linearDistance;
                int properDistanceRounded = (int)Math.round(properDistance);
                distList.add(properDistanceRounded);
            }
            else{
                distList.add(0);
            }
        }
        rayCastedWalls = distList;
    }

    public void playerWalk() {
        if(!playerMoving) {
            if(playerVelocity > 0)
                playerVelocity -= 0.1;
            if(playerVelocity < 0)
                playerVelocity += 0.1;
        }
        else {
            if(playerMoveForward)
                if(playerVelocity < 3.0)
                    playerVelocity += 0.1;
            if(!playerMoveForward)
                if(playerVelocity > -3.0)
                    playerVelocity -= 0.1;
        }
        //if(playerVelocity < 0)
            //playerVelocity = 0.0;

    }

    public void playerTurn(){
        if(playerTurning == 1)
            playerDirection += (0.0628318);
        if(playerTurning == 2)
            playerDirection -= (0.0628318);
        if(playerDirection > (2*3.14159))
            playerDirection -= (2*3.14159);
        if(playerDirection < 0)
            playerDirection = (2*3.14159) + playerDirection;
    }

    public void addWall(int x, int y, boolean direction, int length) {
        for(int i = 0; i < length; i++) {
            if(direction)
                wallList.add(new Wall(x + (i*50), y, true));
            else
                wallList.add(new Wall(x, y+(i*50), false));
        }
    }

    public void addSolidWall(int x, int y) {
        SolidWall newWall = new SolidWall(x, y);
        solidWallList.add(newWall);
        ArrayList<Wall> walls = newWall.getWalls();
        for(int i = 0; i < 4; i++)
            wallList.add(walls.get(i));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP) {
            playerMoving = true;
            playerMoveForward = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN) {
            playerMoving = true;
            playerMoveForward = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT)
            playerTurning = 2;
        if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            playerTurning = 1;
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            if(perspective3D)
                perspective3D = false;
            else
                perspective3D = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)
            playerMoving = false;
        if(e.getKeyCode() == KeyEvent.VK_LEFT)
            playerTurning = 0;
        if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            playerTurning = 0;
    }

    class Wall{
        boolean Horizontal;
        int[] location;

        public Wall(int x, int y, boolean direction){
            Horizontal = direction;
            location = new int[]{x, y};
        }

        public Boolean isHorizontal(){
            return Horizontal;
        }

        public int getX(){
            return location[0];
        }

        public int getY(){
            return location[1];
        }
    }

    class RayCast {
        Double relativeDirection;
        Double absoluteDirection;
        Double raySlope;
        ArrayList<SolidWall> overlappingWalls = new ArrayList<>();
        //ArrayList<Integer> overlappingCells = new ArrayList<>();
        ArrayList<Point> overlappingCells = new ArrayList<>();
        int[] startingCell = new int[2];
        boolean slopeDirectionRight;
        Point wallCollisionCoord = new Point(-1337, -1337);

        public RayCast(double directionDiff) {
            int currentX = playerCoord[0];
            int currentY = playerCoord[1];
            int cellX = (currentX - (currentX % 50)) / 50;
            int cellY = (currentY - (currentY % 50)) / 50;
            slopeDirectionRight = true;
            startingCell[0] = cellX;
            startingCell[1] = cellY;
            relativeDirection = directionDiff;
            absoluteDirection = directionDiff + playerDirection;
            getRaySlope();
        }

        public void getRaySlope() {
            Double direction = absoluteDirection;
            Double xChange = Math.cos(direction);
            Double yChange = Math.sin(direction);
            Double xRecip = 1/xChange;
            raySlope = yChange * xRecip;
            if(absoluteDirection < (3.14159 *(1.5)) && absoluteDirection > (3.14159 / 2))
                slopeDirectionRight = false;
            else
                slopeDirectionRight = true;
        }

        public void  updateRayDirection(){
            absoluteDirection = playerDirection + relativeDirection;
        }


        public ArrayList<Point> getOverlappingCells() {
            overlappingCells.clear();
            int[] currentCellCoords = new int[2];
            double absX = playerCoord[0];
            double absY = playerCoord[1];
            int cellX = startingCell[0];
            int cellY = startingCell[1];
            Point newCell;
            for(int i = 0; i < 500; i++){
                if(slopeDirectionRight) {
                    absX = playerCoord[0] + i;
                    absY = (int) Math.round(playerCoord[1] + i*(raySlope));
                }
                else {
                    absX = playerCoord[0] - i;
                    absY = (int)Math.round(playerCoord[1] - (i*raySlope));
                }
                cellX = (int)(absX - (absX % 50)) / 50;
                cellY = (int)(absY - (absY % 50)) / 50;
                newCell = new Point(cellX, cellY);
                if(!overlappingCells.contains(newCell))
                    overlappingCells.add(newCell);
            }
            return overlappingCells;
        }

        public Point getWallCollision() {
            //Find the first cell that is a solid wall.
            int collideWallX = -99;
            int collideWallY = -99;
            for(int i = 0; i < overlappingCells.size(); i++){
                Point tempPoint = overlappingCells.get(i);
                int tempX = (int)tempPoint.getX();
                int tempY = (int)tempPoint.getY();
                for(int k = 0; k < solidWallList.size(); k++) {
                    if(solidWallList.get(k).getCellX() == tempX && solidWallList.get(k).getCellY() == tempY){
                        collideWallX = tempX * 50;
                        collideWallY = tempY * 50;
                        k = solidWallList.size();
                        i = overlappingCells.size();
                    }
                }
            }
            if(collideWallX == -99)
                return new Point(-1337, -1337);
            //Find which point at wall is collided.
            double collisionPointX = playerCoord[0];
            double collisionPointY = playerCoord[1];
            for(int i = 0; i < 500; i++) {
                collisionPointX += Math.cos(absoluteDirection);
                collisionPointY += Math.sin(absoluteDirection);
                if((collisionPointX >= collideWallX && collisionPointX <= collideWallX+50) && (collisionPointY >= collideWallY && collisionPointY <= collideWallY+50)) {
                    wallCollisionCoord = new Point((int) collisionPointX, (int) collisionPointY);
                    return wallCollisionCoord;
                }
            }
            wallCollisionCoord = new Point(-1337, -1337);
            return wallCollisionCoord;
        }

        public void updateRay() {
            updateRayDirection();
            getRaySlope();
            getOverlappingCells();
            getWallCollision();
        }

        public Point getWallCollisionCoord(){
            return wallCollisionCoord;
        }

        public double getRelAngle(){
            return relativeDirection;
        }


    }

    class SolidWall {
        Integer[] wallLocation = new Integer[2];
        ArrayList<Wall> sideWalls = new ArrayList<>();

        public SolidWall(int gridX, int gridY) {
            sideWalls.add(new Wall(gridX*50, gridY*50, true));
            sideWalls.add(new Wall(gridX*50, gridY*50 + 50, true));
            sideWalls.add(new Wall(gridX*50, gridY*50, false));
            sideWalls.add(new Wall(gridX*50+50, gridY*50, false));
            wallLocation[0] = gridX;
            wallLocation[1] = gridY;
        }

        public ArrayList<Wall> getWalls() {
            return sideWalls;
        }

        public int getCellX() {
            return wallLocation[0];
        }

        public int getCellY() {
            return wallLocation[1];
        }
    }

    public static void main(String[] args) {
        WolfensteinClone playing = new WolfensteinClone();
        playing.addSolidWall(3,2);
        playing.addSolidWall(4,2);
        playing.addSolidWall(5,2);
        playing.addSolidWall(6,2);
        playing.addSolidWall(6,3);
        for(int i = 0; i < 10; i++){
            playing.addSolidWall(i, 0);
            playing.addSolidWall(i, 9);
        }
        for(int i = 1; i < 10; i++){
            playing.addSolidWall(0, i);
            playing.addSolidWall(9, i);
        }
        playing.addSolidWall(6, 7);
        playing.addSolidWall(2, 8);
        playing.addSolidWall(2, 7);
        playing.addSolidWall(2, 6);





        playing.playGame();
    }
}


