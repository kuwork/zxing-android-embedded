package com.ajb.merchants.model.Map;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jerry on 16/3/10.
 */
public class MapBean implements Serializable {

    /**
     * id :
     * imgUrl :
     * parkName : 掌厅车场
     * grid : 30
     * gridWidth : 39
     * gridHeight : 51
     * mapWidth : 1148
     * mapHeight : 1510
     * walls : [{"startX":0,"startY":0,"w":8,"h":2},{"startX":0,"startY":0,"w":3,"h":36},{"startX":20,"startY":0,"w":11,"h":7},{"startX":11,"startY":11,"w":1,"h":22},{"startX":16,"startY":11,"w":1,"h":26},{"startX":20,"startY":10,"w":2,"h":24},{"startX":26,"startY":10,"w":3,"h":21},{"startX":33,"startY":10,"w":2,"h":19},{"startX":0,"startY":35,"w":16,"h":1},{"startX":0,"startY":41,"w":39,"h":9}]
     * parkings : [{"startX":3,"startY":3,"w":5,"h":3},{"startX":3,"startY":6,"w":3,"h":30},{"startX":4,"startY":38,"w":13,"h":3},{"startX":24,"startY":34,"w":3,"h":7},{"startX":27,"startY":36,"w":3,"h":2},{"startX":29,"startY":38,"w":7,"h":3},{"startX":36,"startY":34,"w":3,"h":4},{"startX":0,"startY":42,"w":39,"h":9}]
     * qrcodes : [{"startX":36,"startY":34,"w":3,"h":4}]
     */

    private String id;
    private String imgUrl;
    private String parkName;
    private int grid;
    private int gridWidth;
    private int gridHeight;
    private int mapWidth;
    private int mapHeight;
    /**
     * startX : 0
     * startY : 0
     * w : 8
     * h : 2
     */

    private List<MapBlock> walls;
    /**
     * startX : 3
     * startY : 3
     * w : 5
     * h : 3
     */

    private List<MapParking> parkings;
    /**
     * startX : 36
     * startY : 34
     * w : 3
     * h : 4
     */

    private List<QrBlock> qrcodes;
    private int[][] blocks;

    public void setId(String id) {
        this.id = id;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public void setGrid(int grid) {
        this.grid = grid;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public void setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
    }

    public void setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
    }

    public void setWalls(List<MapBlock> walls) {
        this.walls = walls;
    }

    public void setParkings(List<MapParking> parkings) {
        this.parkings = parkings;
    }

    public void setQrcodes(List<QrBlock> qrcodes) {
        this.qrcodes = qrcodes;
    }

    public String getId() {
        return id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getParkName() {
        return parkName;
    }

    public int getGrid() {
        return grid;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public List<MapBlock> getWalls() {
        return walls;
    }

    public List<MapParking> getParkings() {
        return parkings;
    }

    public List<QrBlock> getQrcodes() {
        return qrcodes;
    }


    public int[][] buildBlock() {
        blocks = new int[gridHeight][gridWidth];
        for (int i = 0; i < walls.size(); i++) {
            MapBlock block = walls.get(i);
            for (int j = 0; j < block.getH(); j++) {
                for (int k = 0; k < block.getW(); k++) {
                    blocks[block.getStartY() + j][block.getStartX() + k] = 1;
                }
            }
        }
        for (int i = 0; i < parkings.size(); i++) {
            MapBlock block = parkings.get(i);
            for (int j = 0; j < block.getH(); j++) {
                for (int k = 0; k < block.getW(); k++) {
                    blocks[block.getStartY() + j][block.getStartX() + k] = 1;
                }
            }
        }
        return blocks;
    }
}
