/**
 *
 *   Copyright (C) 2014 Paul Cech
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.tech37c.ebpmeter.contorller.fragment;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.communication.IOnPointFocusedListener;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.contorller.ChartFragment;
import com.tech37c.ebpmeter.model.BusinessHandler;

public class CubicValueLineChartFragment extends ChartFragment {


    public CubicValueLineChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cubic_value_line_chart, container, false);
        mCubicValueLineChart = (ValueLineChart) view.findViewById(R.id.cubiclinechart);
        loadData();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mCubicValueLineChart.startAnimation();
    }

    @Override
    public void restartAnimation() {
        mCubicValueLineChart.startAnimation();
    }

    private void loadData() {
		BusinessHandler handler = new BusinessHandler(getActivity());// 初始化业务处理对象
		List list = handler.queryByDay(30);
		
		ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);
		Map highMap = (LinkedHashMap)list.get(0);
		int idx = 0;
		String latestMonth = "";
		for (Iterator it = highMap.keySet().iterator(); it.hasNext();) {
			Object obj = (String) it.next();
			String dateTime = (String) obj;
			
			String day ="";
			if(!dateTime.substring(5, 7).equals(latestMonth)&&idx!=0) {
				series.getSeries().get(idx-1).setLegendLabel(latestMonth+ "月");
			}
//			series.addPoint(new ValueLinePoint(highX + "日", (Integer)highMap.get(obj)));
			series.addPoint(new ValueLinePoint(day, (Integer)highMap.get(obj)));
			latestMonth = dateTime.substring(5, 7);
			idx++;
		}
		mCubicValueLineChart.addSeries(series);
		mCubicValueLineChart.addStandardValue(135);
		mCubicValueLineChart.setOnPointFocusedListener(new IOnPointFocusedListener() {
            public void onPointFocused(int _PointPos) {
                Log.d("Test", "Pos: " + _PointPos);
            }
        });
	    
	    
//	    ValueLineSeries series2 = new ValueLineSeries();
//	    series2.setColor(0xFFB3F7B1);
//		Map lowMap = (LinkedHashMap) list.get(1);
//		for (Iterator it = lowMap.keySet().iterator(); it.hasNext();) {
//			Object obj = (String) it.next();
//			String dateTime = (String) obj;
//			String lowX = dateTime.substring(5, 10);
//			series2.addPoint(new ValueLinePoint(lowX + "日", (Integer)lowMap.get(obj)));
//		}
//		chart2.addSeries(series2);
//		chart2.addStandardValue(80);
//		chart2.setOnPointFocusedListener(new IOnPointFocusedListener() {
//            public void onPointFocused(int _PointPos) {
//                Log.d("Test", "Pos: " + _PointPos);
//            }
//        });
	}
    
    private boolean isLastMonth(Map map, String month) {
    	
    	
    	return true;
    }

    private ValueLineChart mCubicValueLineChart;
}
