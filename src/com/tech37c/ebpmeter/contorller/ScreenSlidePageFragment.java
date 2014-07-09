/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tech37c.ebpmeter.contorller;


import com.tech37c.ebpmeter.R;
import com.tech37c.ebpmeter.contorller.calling.ContactsPickerActivity;
import com.tech37c.ebpmeter.model.BaseDAO;
import com.tech37c.ebpmeter.model.BusinessHandler;
import com.tech37c.ebpmeter.model.RecordPOJO;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 * <p>This class is used by the {@link CardFlipActivity} and {@link
 * ScreenSlideActivity} samples.</p>
 */
public class ScreenSlidePageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    public static final String Dev_Type = "Dev_Type";
    public static final String Dev_ID = "Dev_ID";
    public static final String User_ID = "User_ID";
    public static final String Measure_Time = "Measure_Time";
    public static final String HBP = "HBP";
    public static final String LBP = "LBP";
    public static final String Beat = "Beat";
    protected ImageButton contactsButton;

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    private RecordPOJO record = new RecordPOJO();
    

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber, RecordPOJO record) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(Dev_Type, record.getDev_Type());
        args.putString(Dev_ID, record.getDev_ID());
        args.putString(User_ID, record.getUser_ID());
        args.putString(Measure_Time, record.getMeasure_Time());
        args.putString(HBP, record.getHBP());
        args.putString(LBP, record.getLBP());
        args.putString(Beat, record.getBeat());
        
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        record.setBeat(getArguments().getString(Beat));
        record.setDev_ID(getArguments().getString(Dev_ID));
        record.setDev_Type(getArguments().getString(Dev_Type));
        record.setHBP(getArguments().getString(HBP));
        record.setLBP(getArguments().getString(LBP));
        record.setMeasure_Time(getArguments().getString(Measure_Time));
        record.setUser_ID(getArguments().getString(User_ID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_main_content, container, false);
        // Set the title view to show the page number.
        ((TextView) rootView.findViewById(R.id.meter_user)).setText(record.getUser_ID());
        ((TextView) rootView.findViewById(R.id.checking_time)).setText(record.getMeasure_Time());
        ((TextView) rootView.findViewById(R.id.high_value)).setText(record.getHBP());
        ((TextView) rootView.findViewById(R.id.low_value)).setText(record.getLBP());
        ((TextView) rootView.findViewById(R.id.heart_beat)).setText(record.getBeat());
//        		getString(R.string.title_template_step, mPageNumber + 1));
        
        
        contactsButton = (ImageButton)rootView.findViewById(R.id.give_call);
		// add button listener
        contactsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				Intent contactPicker = new Intent(getActivity(), ContactsPickerActivity.class);
//				startActivity(contactPicker);
				
				Intent callIntent = new Intent(Intent.ACTION_DIAL);
				callIntent.setData(Uri.parse("tel:"+ "13812345678"));
				startActivity(callIntent);
			}
		});
        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
