/**
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.nvip.entities.VdoUpdateRecord;

public class VdoUpdate {
	
	private ArrayList<VdoUpdateRecord> vdoRecords = new ArrayList<VdoUpdateRecord>();
	
	public VdoUpdate(JSONObject vdoUpdateJSON) {
				
		JSONArray vdoUpdates = vdoUpdateJSON.getJSONArray("vdoLabels");
		
		for (int i=0; i<vdoUpdates.length(); i++) {
			JSONObject vdoRecordJSON = vdoUpdates.getJSONObject(i);
			vdoRecords.add(new VdoUpdateRecord(vdoRecordJSON.getString("label"), vdoRecordJSON.getString("group"), vdoRecordJSON.getDouble("confidence")));
		}
	}

	public ArrayList<VdoUpdateRecord> getVdoRecords() {
		return vdoRecords;
	}
	
	

}