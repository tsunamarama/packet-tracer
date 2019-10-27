import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Michael Johnson
 */
public class PacketGrapher extends JPanel {
    private static final long serialVersionUID = 1L;
    private Double WIDTH = 900.0;
    private Double HEIGHT = 250.0;
    private Point graphPoint = new Point(50, 275);
    private Double BYTE_INCR = 200000.0;
    private Double SECOND_INCR = 50.0;
    private Double byteTicks;
    private Double secondTicks;
    private Double maxByteTicks;
    private Double maxSecondTicks;
    private Double relativeSingleByte;
    private Double relativeSingleSecond;
    private Integer maxBytes;
    private Integer maxSeconds;
    private LinkedHashMap<Integer, Integer> graphData;
    private boolean defaultGraph = true;

    /**
     * Constructs a <code>PacketGrapher</code> object with default values.
     * @author Michael Johnson
     */
    public PacketGrapher() {
    }

    /**
     * Replaces the current graphing data <code>graphData</code> with the given data set.
     * @param graphData the <code>LinkedHashMap</code> that is to be used to draw the graph
     */
    public void updateData(LinkedHashMap<Integer, Integer> graphData) {
        this.graphData = graphData;
        defaultGraph = false;
        setupGraphParameters();
        repaint();
    }

    private void drawAxes(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Volume [bytes]", 4, 16);
        g.drawString("Time [s]", 455, 325);
        g.drawLine(50, 25, 50, 275);
        g.drawLine(50, 275, 950, 275);
    }
    
    private void drawTicks(Graphics g) {
        String label;
        double secondIncrement = 0.0;
        double byteIncrement = 0.0;
        double defaultIncrement = 0.0;
        if (!defaultGraph) {
            for (int i = 0; i <= maxSecondTicks; i += SECOND_INCR) {
                g.drawLine(graphPoint.x + (int) secondIncrement, graphPoint.y, graphPoint.x + (int) secondIncrement, graphPoint.y + 5);
                label = String.format("%d", i);
                g.drawString(label, graphPoint.x + (int) secondIncrement - 10, graphPoint.y + 20);
                secondIncrement += relativeSingleSecond * SECOND_INCR;
            }
            for (int i = 0; i <= maxByteTicks; i += BYTE_INCR) {
                g.drawLine(graphPoint.x, graphPoint.y - (int) byteIncrement, graphPoint.x - 5, graphPoint.y - (int) byteIncrement);
                if (i != 0) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawLine(graphPoint.x, graphPoint.y - (int) byteIncrement, graphPoint.x + 900, graphPoint.y - (int) byteIncrement);
                    g.setColor(Color.BLACK);
                }
                if (i >= 1000000) {
                    label = String.format("%.1fM", i / 1000000.0);
                } else if(i >= 1000) {
                    label = String.format("%dK", i / 1000);
                } else {
                    label = String.format("%d", i);
                }
                g.drawString(label, graphPoint.x - 45, 5 + graphPoint.y - (int) byteIncrement);
                byteIncrement += relativeSingleByte * BYTE_INCR;
            }
        } else {
            for (int i = 0; i <= 650; i += SECOND_INCR) {
                g.drawLine(graphPoint.x + (int) defaultIncrement, graphPoint.y, graphPoint.x + (int) defaultIncrement, graphPoint.y + 5);
                label = String.format("%d", i);
                g.drawString(label, graphPoint.x + (int) defaultIncrement - 10, graphPoint.y + 20);
                defaultIncrement += (900.0 / 650.0) * SECOND_INCR;
            }
        }
    }

    private void drawGraph(Graphics g) {
        if (!defaultGraph) {
            g.setColor(Color.BLUE);
            for (Map.Entry<Integer, Integer> entry: graphData.entrySet()) {
                Integer seconds = entry.getKey();
                Integer bytes = entry.getValue();
                g.drawLine((int) (graphPoint.x + (relativeSingleSecond * seconds)), graphPoint.y, (int) (graphPoint.x + (relativeSingleSecond * seconds)), (graphPoint.y - (int) (relativeSingleByte * bytes)));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAxes(g);
        drawTicks(g);
        drawGraph(g);
    }

    private void setupGraphParameters() {
        maxBytes = getMaxValue(1);
        setIncrement();
        maxSeconds = getMaxValue(-1);
        byteTicks = tickCalc(maxBytes, BYTE_INCR);
        secondTicks = tickCalc(maxSeconds, SECOND_INCR);
        maxByteTicks = setMaxTicks(byteTicks, BYTE_INCR);
        maxSecondTicks = setMaxTicks(secondTicks, SECOND_INCR);
        relativeSingleByte = setRelativeSingleValue(maxByteTicks, HEIGHT);
        relativeSingleSecond = setRelativeSingleValue(maxSecondTicks, WIDTH);
    }

    private Integer getMaxValue(Integer option) {
        Integer result = 0;
        if (option.equals(1)) {
            result = Collections.max(graphData.entrySet(), Map.Entry.comparingByValue()).getValue();
        } else if (option.equals(-1)) {
            result = Collections.max(graphData.entrySet(), Map.Entry.comparingByKey()).getKey();
        }
        return result;
    }

    private Double setMaxTicks(Double tickType, Double incr) {
        return tickType * incr;
    }

    private Double tickCalc(int maxVal, double incr) {
        Double ticks = maxVal / incr;
        double result = Math.ceil(ticks);
        if (result < 4.0 && incr == BYTE_INCR) {
            result = 4.0;
        } else if (result > 10.0 && incr == BYTE_INCR) {
            result = 10.0;
        } else if (result < 8.0 && incr == SECOND_INCR) {
            result = 8.0;
        } else if (result > 24.0 && incr == SECOND_INCR) {
            result = 24.0;
        }
        return result;
    }

    private Double setRelativeSingleValue(Double maxVal, Double max) {
        return max / maxVal;
    }

    private void setIncrement() {
        if (maxBytes >= 200000) {
            BYTE_INCR = 200000.0;
        } else if (maxBytes >= 20000) {
            BYTE_INCR = 20000.0;
        } else if (maxBytes >= 2000) {
            BYTE_INCR = 2000.0;
        } else if (maxBytes >= 200) {
            BYTE_INCR = 200.0;
        } else if (maxBytes >= 20) {
            BYTE_INCR = 20.0;
        } else {
            BYTE_INCR = 2.0;
        }
    }
}
