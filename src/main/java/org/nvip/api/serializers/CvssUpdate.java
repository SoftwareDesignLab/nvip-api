/**
 * Copyright 2023 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RSAT19CB0000020 awarded by the United
 * States Department of Homeland Security.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.nvip.api.serializers;

import org.nvip.entities.CvssUpdateRecord;

import lombok.*;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;

@Getter
public class CvssUpdate {
	private ArrayList<CvssUpdateRecord> cvssRecords = new ArrayList<>();
	
	public CvssUpdate(JSONArray cvssUpdateJSON) {
		for(int i = 0; i < cvssUpdateJSON.length(); i++) {
            JSONObject cvssRecordJSON = cvssUpdateJSON.getJSONObject(i);
            cvssRecords.add(new CvssUpdateRecord(cvssRecordJSON.getDouble("base_score"), cvssRecordJSON.getDouble("impact_score")));
        }
	}
}
