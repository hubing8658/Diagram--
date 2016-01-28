
package com.iss.diagram;

import java.util.ArrayList;

import com.iss.diagram.DiagramView.IDiagramData;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DiagramView dv = (DiagramView) findViewById(R.id.dv);
        ArrayList<IDiagramData> datas = new ArrayList<>();
        datas.add(new SpeedEntity(140.6F, "爱奇艺", 0xFFfbe396));
        datas.add(new SpeedEntity(130.6F, "腾迅视频", 0xFF9fdb9f));
        datas.add(new SpeedEntity(90.6F, "腾迅QQ", 0xFF6ecdb3));
        datas.add(new SpeedEntity(60.6F, "淘宝", 0xFF60bbb7));
        datas.add(new SpeedEntity(40.6F, "微信", 0xFFf98db3));
        datas.add(new SpeedEntity(30.6F, "其它1", 0xFFffadad));
        datas.add(new SpeedEntity(20.6F, "其它2", 0xFFBBFFFF));
        dv.setDiagramData(datas);
    }
    
}
