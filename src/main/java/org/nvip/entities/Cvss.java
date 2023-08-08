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

import org.json.JSONObject;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@JsonIgnoreProperties(value = "vulnerability")
public class Cvss {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name="cvss_id") 
    private int id;
    @ManyToOne @JoinColumn(name="cve_id", referencedColumnName = "cveId")
    Vulnerability vulnerability;
	private double baseScore;
	private double impactScore;
    @Basic private LocalDateTime createDate;
    private Integer userId;

    public Cvss(Vulnerability vulnerability, double baseScore, double impactScore) {
        this.baseScore = baseScore;
        this.impactScore = impactScore;
        this.vulnerability = vulnerability;
        this.userId = -1;
    }

	public Cvss(Vulnerability vulnerability, double baseScore, double impactScore, int userId) {
		this.baseScore = baseScore;
		this.impactScore = impactScore;
		this.vulnerability = vulnerability;
        this.userId = userId;
	}

    public Cvss(Vulnerability vulnerability, double baseScore, double impactScore, LocalDateTime createDate, int userId) {
        this.baseScore = baseScore;
        this.impactScore = impactScore;
        this.vulnerability = vulnerability;
        this.userId = userId;
        this.createDate = createDate;
    }
}