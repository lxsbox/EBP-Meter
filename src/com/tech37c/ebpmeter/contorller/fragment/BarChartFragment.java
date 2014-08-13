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

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.communication.IOnBarClickedListener;
import org.eazegraph.lib.communication.IOnPointFocusedListener;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.contorller.ChartFragment;
import com.tech37c.ebpmeter.model.BusinessHandler;

public class BarChartFragment extends ChartFragment {

    public BarChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        mBarChart = (BarChart) view.findViewById(R.id.barchart);

        mBarChart.setOnBarClickedListener(new IOnBarClickedListener() {
            @Override
            public void onBarClicked(int _Position) {
                Log.d("BarChart", "Position: " + _Position);
            }
        });

        loadData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBarChart.startAnimation();
    }

    @Override
    public void restartAnimation() {
        mBarChart.startAnimation();
    }

    
    private void loadData() {
		BusinessHandler handler = new BusinessHandler(getActivity());// 初始化业务处理对象
		List list = handler.queryByDay(10);
		
		Map highMap = (LinkedHashMap)list.get(0);
		for (Iterator it = highMap.keySet().iterator(); it.hasNext();) {
			Object obj = (String) it.next();
			String dateTime = (String) obj;
			String highX = dateTime.substring(5, 10);
			mBarChart.addBar(new BarModel((Integer)highMap.get(obj), getRandomColor()));
		}
	    
//		Map lowMap = (LinkedHashMap) list.get(1);
//		for (Iterator it = lowMap.keySet().iterator(); it.hasNext();) {
//			Object obj = (String) it.next();
//			String dateTime = (String) obj;
//			String lowX = dateTime.substring(5, 10);
//			mBarChart.addBar(new BarModel((Integer)lowMap.get(obj), 0xFF123456));
//		}
	}

    private int getRandomColor() {
    	int[] a = {0xFF123456,0xFF343456,0xFF563456,0xFF873F56,0xFF56B7F1,0xFF343456,0xFF1FF4AC,0xFF1BA4E6};
    	int i = (int)(a.length*Math.random());
    	return a[i];
    }
    
    private BarChart mBarChart;
}
