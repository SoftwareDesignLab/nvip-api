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

import java.time.LocalDateTime;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Entity
@Table(name="vdocharacteristic")
@ToString
@Getter
@Setter
@JsonIgnoreProperties(value = "vulnerability")
public class VdoCharacteristic {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name="vdo_characteristic_id") private int id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name="cve_id", referencedColumnName = "cveId")
    Vulnerability vulnerability;

    private LocalDateTime createdDate;
	private String vdoLabel;
	private String vdoNounGroup;
    private double vdoConfidence;
    private Integer userId;

    public VdoCharacteristic(Vulnerability vulnerability, String vdoLabel, String vdoNounGroup, double vdoConfidence) {
        this.vulnerability = vulnerability;
        this.vdoLabel = vdoLabel;
        this.vdoNounGroup = vdoNounGroup;
        this.vdoConfidence = vdoConfidence;
    }

    public VdoCharacteristic(Vulnerability vulnerability, LocalDateTime createdDate, String vdoLabel, String vdoNounGroup, double vdoConfidence, int userId) {
        this.vulnerability = vulnerability;
        this.createdDate = createdDate;
        this.vdoLabel = vdoLabel;
        this.vdoNounGroup = vdoNounGroup;
        this.vdoConfidence = vdoConfidence;
        this.userId = userId;
    }
}
