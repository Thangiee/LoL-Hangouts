package com.thangiee.lolhangouts.ui.settings;


/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.SwitchPreference;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Extension of {@link android.preference.SwitchPreference} that uses {@link android.support.v7.widget.SwitchCompat}
 * to offer the new {@link android.os.Build.VERSION_CODES#LOLLIPOP}.
 */
public class SwitchCompatPreference extends SwitchPreference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwitchCompatPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SwitchCompatPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SwitchCompatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchCompatPreference(Context context) {
        super(context, null);
    }

    private final Listener mListener2 = new Listener();

    private class Listener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!callChangeListener(isChecked)) {
                // Listener didn't like it, change it back.
                // CompoundButton will make sure we don't recurse.
                buttonView.setChecked(!isChecked);
                return;
            }

            SwitchCompatPreference.this.setChecked(isChecked);
        }
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        View checkableView = view.findViewById(android.R.id.toggle);
        if (checkableView != null && checkableView instanceof Checkable) {
            if (checkableView instanceof Switch) {
                final Switch switchView = (Switch) checkableView;
                switchView.setOnCheckedChangeListener(null);
            } else if (checkableView instanceof SwitchCompat) {
                final SwitchCompat switchView = (SwitchCompat) checkableView;
                switchView.setOnCheckedChangeListener(null);
            }

            ((Checkable) checkableView).setChecked(isChecked());

            if (checkableView instanceof Switch) {
                final Switch switchView = (Switch) checkableView;
                switchView.setTextOn(getSwitchTextOn());
                switchView.setTextOff(getSwitchTextOff());
                switchView.setOnCheckedChangeListener(mListener2);
            } else if (checkableView instanceof SwitchCompat) {
                final SwitchCompat switchView = (SwitchCompat) checkableView;
                switchView.setTextOn(getSwitchTextOn());
                switchView.setTextOff(getSwitchTextOff());
                switchView.setOnCheckedChangeListener(mListener2);
            }
        }

        syncSummaryView(view);
    }

    /**
     * Sync a summary view contained within view's sub-hierarchy with the correct summary text.
     * @param view View where a summary should be located
     */
    void syncSummaryView(View view) {
        // Sync the summary view
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        if (summaryView != null) {
            boolean useDefaultSummary = true;
            if (isChecked() && !TextUtils.isEmpty(getSummaryOn())) {
                summaryView.setText(getSummaryOn());
                useDefaultSummary = false;
            } else if (!isChecked() && !TextUtils.isEmpty(getSummaryOff())) {
                summaryView.setText(getSummaryOff());
                useDefaultSummary = false;
            }

            if (useDefaultSummary) {
                final CharSequence summary = getSummary();
                if (summary != null) {
                    summaryView.setText(summary);
                    useDefaultSummary = false;
                }
            }

            int newVisibility = View.GONE;
            if (!useDefaultSummary) {
                // Someone has written to it
                newVisibility = View.VISIBLE;
            }
            if (newVisibility != summaryView.getVisibility()) {
                summaryView.setVisibility(newVisibility);
            }
        }
    }
}
