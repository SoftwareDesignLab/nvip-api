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
package org.nvip.entities;


import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.List;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
@Table(name="vdocharacteristic")
@ToString
@Getter
@Setter
@JsonIgnoreProperties(value = "vulnerability")
public class VdoCharacteristic {
	@Id int id;
	@Transient String cveId;
	@Transient private String vdoLabel;
	private double vdoConfidence;
	@Transient private String vdoNounGroup;

	@ToString.Exclude
//	@ManyToOne(fetch = FetchType.EAGER)
	@ManyToOne
	@JoinColumn(name="cve_id", referencedColumnName = "cveId")
	Vulnerability vulnerability;

	@ToString.Exclude
//	@ManyToOne(fetch = FetchType.EAGER)
	@ManyToOne
	@JoinColumn(name="vdo_label_id")
	VdoLabel vdoLabels;

	@ToString.Exclude
//	@ManyToOne(fetch = FetchType.EAGER)
	@ManyToOne
	@JoinColumn(name="vdo_noun_group_id")
	VDOgroup vdoGroup;

	public VdoCharacteristic(String cveId, String vdoLabel, double vdoConfidence, String vdoNounGroup) {
		this.cveId = cveId;
		this.vdoLabel = vdoLabel;
		this.vdoConfidence = vdoConfidence;
		this.vdoNounGroup = vdoNounGroup;

		this.vdoGroup = new VDOgroup(vdoNounGroup, null, null);
	}
}
