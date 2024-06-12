package com.example.konnect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GraphView extends View {
    private Paint nodePaint;
    private Paint edgePaint;
    private Paint textPaint;
    private List<Node> nodes;
    private List<Edge> edges;
    private float nodeWidth = 200;  // Default node width
    private float nodeHeight = 100;  // Default node height

    private float nodeCornerRadius = 20;  // Default corner radius

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphView(Context context) {
        super(context);
        init();
    }

    private void init() {
        nodePaint = new Paint();
        nodePaint.setColor(Color.rgb(68, 100, 173));  // Default node color
        nodePaint.setStyle(Paint.Style.FILL);
        nodePaint.setTextAlign(Paint.Align.CENTER);

        edgePaint = new Paint();
        edgePaint.setColor(Color.BLACK);
        edgePaint.setStrokeWidth(5);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(48);
        textPaint.setTextAlign(Paint.Align.CENTER);

        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void setNodeColor(int color) {
        nodePaint.setColor(color);
        invalidate();
    }

    public void setNodeSize(float width, float height) {
        nodeWidth = width;
        nodeHeight = height;
        invalidate();
    }

    public void setNodeCornerRadius(float radius) {
        nodeCornerRadius = radius;
        invalidate();
    }

    public void addNode(String title, float x, float y) {
        nodes.add(new Node(title, x, y));
        invalidate();
    }

    public void addEdge(int startNodeIndex, int endNodeIndex) {
        edges.add(new Edge(startNodeIndex, endNodeIndex));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Edge edge : edges) {
            Node startNode = nodes.get(edge.startNodeIndex);
            Node endNode = nodes.get(edge.endNodeIndex);
            canvas.drawLine(startNode.x, startNode.y, endNode.x, endNode.y, edgePaint);
        }
        for (Node node : nodes) {
            canvas.drawRoundRect(node.x - nodeWidth / 2, node.y - nodeHeight / 2, node.x + nodeWidth / 2, node.y + nodeHeight / 2, nodeCornerRadius, nodeCornerRadius, nodePaint);
            canvas.drawText(node.title, node.x, node.y + (textPaint.getTextSize() / 4), textPaint);
        }
    }

    private static class Node {
        String title;
        float x, y;

        Node(String title, float x, float y) {
            this.title = title;
            this.x = x;
            this.y = y;
        }
    }

    private static class Edge {
        int startNodeIndex;
        int endNodeIndex;

        Edge(int startNodeIndex, int endNodeIndex) {
            this.startNodeIndex = startNodeIndex;
            this.endNodeIndex = endNodeIndex;
        }
    }
}
