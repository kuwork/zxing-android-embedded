package com.ajb.merchants.model.Map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.Gravity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AStar {
    private int[][] map;//地图(0可通过 1不可通过)
    private List<Node> openList;//开启列表
    private List<Node> closeList;//关闭列表  
    private final int COST_STRAIGHT = 10;//垂直方向或水平方向移动的路径评分  
    private final int COST_DIAGONAL = 14;//斜方向移动的路径评分  
    private int row;//行  
    private int column;//列

    public List<Node> getResultList() {
        return resultList;
    }

    private List<Node> resultList;

    public AStar(int[][] map, int row, int column) {
        this.map = map;
        this.row = row;
        this.column = column;
        openList = new ArrayList<Node>();
        closeList = new ArrayList<Node>();
    }

    //查找坐标（-1：错误，0：没找到，1：找到了）
    public int search(int x1, int y1, int x2, int y2) {
        if (x1 < 0 || x1 >= column || x2 < 0 || x2 >= column || y1 < 0 || y1 >= row || y2 < 0 || y2 >= row) {
            return -1;
        }
        if (map[y1][x1] == 1 || map[y2][x2] == 1) {
            return -1;
        }
        Node sNode = new Node(x1, y1, null);
        Node eNode = new Node(x2, y2, null);
        openList.add(sNode);
        resultList = search(sNode, eNode);
        if (resultList.size() == 0) {
            return 0;
        }
        for (Node node : resultList) {
            map[node.getY()][node.getX()] = -1;
        }
        Gson gson = new Gson();
        String s = gson.toJson(map);
        return 1;
    }

    //查找核心算法
    private List<Node> search(Node sNode, Node eNode) {
        List<Node> resultList = new ArrayList<Node>();
        boolean isFind = false;
        Node node = null;
        while (openList.size() > 0) {
            //取出开启列表中最低F值，即第一个存储的值的F为最低的  
            node = openList.get(0);
            //判断是否找到目标点  
            if (node.getX() == eNode.getX() && node.getY() == eNode.getY()) {
                isFind = true;
                break;
            }
            //上  
            if ((node.getY() - 1) >= 0) {
                checkPath(node.getX(), node.getY() - 1, node, eNode, COST_STRAIGHT);
            }
            //下  
            if ((node.getY() + 1) < row) {
                checkPath(node.getX(), node.getY() + 1, node, eNode, COST_STRAIGHT);
            }
            //左  
            if ((node.getX() - 1) >= 0) {
                checkPath(node.getX() - 1, node.getY(), node, eNode, COST_STRAIGHT);
            }
            //右  
            if ((node.getX() + 1) < column) {
                checkPath(node.getX() + 1, node.getY(), node, eNode, COST_STRAIGHT);
            }
            //左上  
            if ((node.getX() - 1) >= 0 && (node.getY() - 1) >= 0) {
                checkPath(node.getX() - 1, node.getY() - 1, node, eNode, COST_DIAGONAL);
            }
            //左下  
            if ((node.getX() - 1) >= 0 && (node.getY() + 1) < row) {
                checkPath(node.getX() - 1, node.getY() + 1, node, eNode, COST_DIAGONAL);
            }
            //右上  
            if ((node.getX() + 1) < column && (node.getY() - 1) >= 0) {
                checkPath(node.getX() + 1, node.getY() - 1, node, eNode, COST_DIAGONAL);
            }
            //右下  
            if ((node.getX() + 1) < column && (node.getY() + 1) < row) {
                checkPath(node.getX() + 1, node.getY() + 1, node, eNode, COST_DIAGONAL);
            }
            //从开启列表中删除  
            //添加到关闭列表中  
            closeList.add(openList.remove(0));
            //开启列表中排序，把F值最低的放到最底端            Collections.sort(openList, new NodeFComparator());  
        }
        if (isFind) {
            getPath(resultList, node);
        }
        return resultList;
    }

    //查询此路是否能走通
    private boolean checkPath(int x, int y, Node parentNode, Node eNode, int cost) {
        Node node = new Node(x, y, parentNode);
        //查找地图中是否能通过  
        if (map[y][x] == 1) {
            closeList.add(node);
            return false;
        }
        //查找关闭列表中是否存在  
        if (isListContains(closeList, x, y) != -1) {
            return false;
        }
        //查找开启列表中是否存在  
        int index = -1;
        if ((index = isListContains(openList, x, y)) != -1) {
            //G值是否更小，即是否更新G，F值  
            if ((parentNode.getG() + cost) < openList.get(index).getG()) {
                node.setParentNode(parentNode);
                countG(node, eNode, cost);
                countF(node);
                openList.set(index, node);
            }
        } else {
            //添加到开启列表中  
            node.setParentNode(parentNode);
            count(node, eNode, cost);
            openList.add(node);
        }
        return true;
    }

    //集合中是否包含某个元素(-1：没有找到，否则返回所在的索引)
    private int isListContains(List<Node> list, int x, int y) {
        for (int i = 0; i < list.size(); i++) {
            Node node = list.get(i);
            if (node.getX() == x && node.getY() == y) {
                return i;
            }
        }
        return -1;
    }

    //从终点往返回到起点
    private void getPath(List<Node> resultList, Node node) {
        if (node.getParentNode() != null) {
            getPath(resultList, node.getParentNode());
        }
        resultList.add(node);
    }

    //计算G,H,F值
    private void count(Node node, Node eNode, int cost) {
        countG(node, eNode, cost);
        countH(node, eNode);
        countF(eNode);
    }

    //计算G值  
    private void countG(Node node, Node eNode, int cost) {
        if (node.getParentNode() == null) {
            node.setG(cost);
        } else {
            node.setG(node.getParentNode().getG() + cost);
        }
    }

    //计算H值  
    private void countH(Node node, Node eNode) {
        node.setF(Math.abs(node.getX() - eNode.getX()) + Math.abs(node.getY() - eNode.getY()));
    }

    //计算F值  
    private void countF(Node node) {
        node.setF(node.getG() + node.getF());
    }


    /**
     * @param canvas  画布
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param timeStr 需要绘制的字符串
     * @param gravity 对齐方式,仅支持TOP,BOTTOM,CENTER_VERTICAL
     * @return 字体非空高度
     * @Title drawTime
     * @Description 绘制字符串
     * @author 陈国宏
     * @date 2014年10月15日 上午10:28:58
     */
    public static int drawText(Canvas canvas, TextPaint mTextPaint,
                        boolean isCalAgain, int left, int top, int right, int bottom,
                        String timeStr, int gravity) {
        int h;
        Paint.FontMetrics fm = null;
        if (isCalAgain) {
            fm = initTextPaint(mTextPaint, right - left, bottom - top, timeStr);
        } else {
            fm = mTextPaint.getFontMetrics();
        }
        h = (int) (Math.ceil(fm.descent - fm.ascent));// 获得字体高度
        switch (gravity) {
            case Gravity.TOP:
            case Gravity.TOP | Gravity.LEFT:
                canvas.drawText(timeStr, left, top - fm.ascent - h / 8,
                        mTextPaint);
                break;
            case Gravity.BOTTOM:
            case Gravity.BOTTOM | Gravity.LEFT:
                canvas.drawText(timeStr, left, bottom - fm.descent + h / 8,
                        mTextPaint);
                break;
            case Gravity.CENTER_VERTICAL:
            case Gravity.CENTER_VERTICAL | Gravity.LEFT:
                canvas.drawText(timeStr, left, (bottom + top) / 2 + h / 2
                        - fm.descent, mTextPaint);
                break;
            case Gravity.TOP | Gravity.RIGHT:
            case Gravity.RIGHT:
                canvas.drawText(
                        timeStr,
                        left + (right - left - mTextPaint.measureText(timeStr)),
                        top - fm.ascent - h / 8, mTextPaint);
                break;
            case Gravity.RIGHT | Gravity.CENTER_VERTICAL:
                canvas.drawText(
                        timeStr,
                        left + (right - left - mTextPaint.measureText(timeStr)),
                        (bottom + top) / 2 + h / 2 - fm.descent, mTextPaint);
                break;
            case Gravity.CENTER:
                canvas.drawText(timeStr,
                        left + (right - left - mTextPaint.measureText(timeStr))
                                / 2, (bottom + top) / 2 + h / 2 - fm.descent,
                        mTextPaint);
                break;
            default:
                canvas.drawText(timeStr, left, top - fm.ascent - h / 8,
                        mTextPaint);
                break;
        }
        return (int) -fm.ascent;
    }

    /**
     * @param mTextPaint
     * @param width
     * @param height
     * @param str
     * @return
     * @Title initTextPaint
     * @Description 最大化字体
     * @author 陈国宏
     * @date 2014年12月1日 下午3:41:53
     */
    private static Paint.FontMetrics initTextPaint(TextPaint mTextPaint, float width,
                                                   float height, String str) {
        mTextPaint.setTextSize(1f);
        String[] lines = str.trim().split("\n");
        float[] result = getTextSize(str, (Paint) mTextPaint, lines);
        float textSize = mTextPaint.getTextSize();
        while (result[0] <= width && result[1] <= height) {
            textSize = mTextPaint.getTextSize();
            mTextPaint.setTextSize(mTextPaint.getTextSize() + 1f);
            result = getTextSize(str, (Paint) mTextPaint, lines);
        }
        mTextPaint.setTextSize(textSize);
        return mTextPaint.getFontMetrics();
    }

    /**
     * @param str
     * @param paint
     * @param lines
     * @return
     * @Title getTextSize
     * @Description 获取字体的大小
     * @author 陈国宏
     * @date 2014年12月1日 下午3:42:17
     */
    private static float[] getTextSize(String str, Paint paint, String[] lines) {
        float[] max = new float[]{0, 1};
        int i = 0;
        char ch;
        String maxLine = lines[0];
        for (int j = 0; j < lines.length; j++) {
            if (lines[j].length() > maxLine.length()) {
                maxLine = lines[j];
            }
        }
        max[0] = paint.measureText(maxLine);
        Paint.FontMetrics fm = paint.getFontMetrics();
        max[1] = lines.length * (fm.descent - fm.ascent) * 6 / 8;
        return max;
    }
}