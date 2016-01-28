
package com.iss.diagram;

import com.iss.diagram.DiagramView.IDiagramData;

/**
 * @author hubing
 * @version 1.0.0 2016-1-15
 */

public class SpeedEntity implements IDiagramData {

    private float speed;
    private String softName;
    private float percent;
    private int color;
    
    public SpeedEntity(float speed, String softName, int color) {
        super();
        this.speed = speed;
        this.softName = softName;
        this.color = color;
    }

    @Override
    public float getPercentAttrValue() {
        return speed;
    }

    @Override
    public String getShowName() {
        return softName;
    }

    @Override
    public void setPercent(float percent) {
        this.percent = percent;
    }

    @Override
    public float getPercent() {
        return percent;
    }

    @Override
    public int getPercentColor() {
        return color;
    }

}
